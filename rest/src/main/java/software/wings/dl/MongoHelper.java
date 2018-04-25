package software.wings.dl;

import static io.harness.data.structure.EmptyPredicate.isEmpty;
import static io.harness.data.structure.EmptyPredicate.isNotEmpty;
import static io.harness.govern.Switch.unhandled;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static software.wings.beans.SearchFilter.Operator.AND;
import static software.wings.beans.SearchFilter.Operator.EQ;
import static software.wings.beans.SearchFilter.Operator.EXISTS;
import static software.wings.beans.SearchFilter.Operator.NOT_EXISTS;
import static software.wings.beans.SearchFilter.Operator.OR;
import static software.wings.beans.SortOrder.OrderType.DESC;

import com.google.common.base.Preconditions;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.DatastoreImpl;
import org.mongodb.morphia.mapping.MappedClass;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Criteria;
import org.mongodb.morphia.query.FieldEnd;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.wings.beans.ErrorCode;
import software.wings.beans.SearchFilter;
import software.wings.beans.SearchFilter.Operator;
import software.wings.beans.SortOrder;
import software.wings.beans.SortOrder.OrderType;
import software.wings.exception.InvalidRequestException;
import software.wings.exception.WingsException;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class MongoHelper.
 */
public class MongoHelper {
  private static final Logger logger = LoggerFactory.getLogger(MongoHelper.class);

  /**
   * Query page request.
   *
   * @param <T>       the generic type
   * @param datastore the datastore
   * @param cls       the cls
   * @param req       the req
   * @return the page response
   */
  public static <T> PageResponse<T> queryPageRequest(
      Datastore datastore, Class<T> cls, PageRequest<T> req, boolean disableValidation) {
    Query q = datastore.createQuery(cls);
    if (disableValidation) {
      q.disableValidation();
    }

    Mapper mapper = ((DatastoreImpl) datastore).getMapper();
    MappedClass mappedClass = mapper.addMappedClass(cls);
    q = MongoHelper.applyPageRequest(q, req, mappedClass, mapper);

    PageResponse<T> response = new PageResponse<>(req);

    if (req.getOptions() == null || req.getOptions().contains(PageRequest.Option.COUNT)) {
      long total = q.count();
      response.setTotal(total);
    }

    if (req.getOptions() == null || req.getOptions().contains(PageRequest.Option.LIST)) {
      q.offset(req.getStart());
      if (PageRequest.UNLIMITED.equals(req.getLimit())) {
        q.limit(PageRequest.DEFAULT_UNLIMITED);
      } else {
        q.limit(req.getPageSize());
      }
      List<T> list = q.asList();
      response.setResponse(list);
    }

    return response;
  }

  /**
   * Get count.
   *
   * @param <T>       the generic type
   * @param datastore the datastore
   * @param cls       the cls
   * @return the page response
   */
  public static <T> long getCount(Datastore datastore, Class<T> cls, PageRequest<T> req) {
    Query q = datastore.createQuery(cls);

    Mapper mapper = ((DatastoreImpl) datastore).getMapper();
    MappedClass mappedClass = mapper.addMappedClass(cls);
    q = MongoHelper.applyPageRequest(q, req, mappedClass, mapper);

    return q.count();
  }

  /**
   * Apply page request.
   *
   * @param <T>         the generic type
   * @param query       the query
   * @param req         the req
   * @param mappedClass the mapped class
   * @param mapper      the mapper
   * @return the query
   */
  public static <T> Query<T> applyPageRequest(
      Query<T> query, PageRequest<T> req, MappedClass mappedClass, Mapper mapper) {
    if (req == null) {
      return query;
    }

    Preconditions.checkNotNull(query, "Query cannot be null");
    req.populateFilters(mappedClass, mapper);

    if (req.getFilters() != null) {
      for (SearchFilter filter : req.getFilters()) {
        if (filter == null || filter.getOp() == null) {
          continue;
        }

        if (filter.getOp() == OR || filter.getOp() == AND) {
          List<Criteria> criterias = new ArrayList<>();
          for (Object opFilter : filter.getFieldValues()) {
            if (!(opFilter instanceof SearchFilter)) {
              logger.error("OR/AND operator can only be used with SearchFiter values");
              throw new WingsException(ErrorCode.DEFAULT_ERROR_CODE);
            }
            SearchFilter opSearchFilter = (SearchFilter) opFilter;
            criterias.add(applyOperator(query.criteria(opSearchFilter.getFieldName()), opSearchFilter));
          }

          if (filter.getOp() == OR) {
            query.or(criterias.toArray(new Criteria[0]));
          } else {
            query.and(criterias.toArray(new Criteria[0]));
          }
        } else {
          FieldEnd<? extends Query<T>> fieldEnd = query.field(filter.getFieldName());
          query = applyOperator(fieldEnd, filter);
        }
      }
    }

    if (req.getOrders() != null) {
      // Add default sorting if none present
      if (req.getOrders().isEmpty()) {
        SortOrder sortOrder = new SortOrder();
        sortOrder.setFieldName("createdAt");
        sortOrder.setOrderType(OrderType.DESC);
        req.addOrder(sortOrder);
      }

      query.order(req.getOrders()
                      .stream()
                      .map(so -> (DESC.equals(so.getOrderType())) ? "-" + so.getFieldName() : so.getFieldName())
                      .collect(joining(", ")));
    }

    List<String> fieldsIncluded = req.getFieldsIncluded();
    List<String> fieldsExcluded = req.getFieldsExcluded();

    if (isNotEmpty(fieldsIncluded)) {
      query.retrievedFields(true, fieldsIncluded.toArray(new String[0]));
    } else if (isNotEmpty(fieldsExcluded)) {
      query.retrievedFields(false, fieldsExcluded.toArray(new String[0]));
    }

    return query;
  }

  private static <T> T applyOperator(FieldEnd<T> fieldEnd, SearchFilter filter) {
    if (!(filter.getOp() == EXISTS || filter.getOp() == NOT_EXISTS) && isEmpty(filter.getFieldValues())) {
      throw new InvalidRequestException("Unspecified fieldValue for search");
    }
    Operator op = filter.getOp();
    if (op == null) {
      op = EQ;
    }
    switch (op) {
      case LT:
        return fieldEnd.lessThan(filter.getFieldValues()[0]);

      case GT:
        return fieldEnd.greaterThan(filter.getFieldValues()[0]);

      case GE:
        return fieldEnd.greaterThanOrEq(filter.getFieldValues()[0]);

      case EQ:
        return fieldEnd.equal(filter.getFieldValues()[0]);

      case NOT_EQ:
        return fieldEnd.notEqual(filter.getFieldValues()[0]);

      case CONTAINS:
        return fieldEnd.containsIgnoreCase(String.valueOf(filter.getFieldValues()[0]));

      case STARTS_WITH:
        return fieldEnd.startsWithIgnoreCase(String.valueOf(filter.getFieldValues()[0]));

      case HAS:
        return fieldEnd.hasAnyOf(asList(filter.getFieldValues()));

      case IN:
        return fieldEnd.hasAnyOf(asList(filter.getFieldValues()));

      case NOT_IN:
        return fieldEnd.hasNoneOf(asList(filter.getFieldValues()));

      case EXISTS:
        return fieldEnd.exists();

      case NOT_EXISTS:
        return fieldEnd.doesNotExist();

      default:
        unhandled(op);
    }
    return null;
  }

  /**
   * Sets the unset.
   *
   * @param <T>   the generic type
   * @param ops   the ops
   * @param field the field
   * @param value the value
   * @return the update operations
   */
  public static <T> UpdateOperations<T> setUnset(UpdateOperations<T> ops, String field, Object value) {
    if (value == null || (value instanceof String && isBlank((String) value))) {
      return ops.unset(field);
    } else {
      return ops.set(field, value);
    }
  }
}
