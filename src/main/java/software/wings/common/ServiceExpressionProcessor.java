/**
 *
 */
package software.wings.common;

import org.apache.commons.lang3.ArrayUtils;
import org.modelmapper.ModelMapper;
import software.wings.api.ServiceElement;
import software.wings.beans.SearchFilter;
import software.wings.beans.SearchFilter.Operator;
import software.wings.beans.Service;
import software.wings.dl.PageRequest;
import software.wings.dl.PageRequest.Builder;
import software.wings.service.intfc.ServiceResourceService;
import software.wings.sm.ExecutionContext;
import software.wings.sm.ExecutionContextImpl;
import software.wings.sm.ExpressionProcessor;
import software.wings.utils.Misc;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Rishi
 */
public class ServiceExpressionProcessor implements ExpressionProcessor {
  static final String EXPRESSION_START_PATTERN = "services()";
  private static final String SERVICE_EXPR_PROCESSOR = "serviceExpressionProcessor";
  private ServiceResourceService serviceResourceService;
  private String appId;

  private String[] serviceNames;

  public ServiceExpressionProcessor(ExecutionContext context, ServiceResourceService serviceResourceService) {
    this.serviceResourceService = serviceResourceService;
    ExecutionContextImpl contextImpl = (ExecutionContextImpl) context;
    // retrieve appId from the context
    this.appId = contextImpl.getStateExecutionInstance().getAppId();
  }

  @Override
  public String getPrefixObjectName() {
    return SERVICE_EXPR_PROCESSOR;
  }

  @Override
  public String normalizeExpression(String expression) {
    if (expression == null || !expression.startsWith(EXPRESSION_START_PATTERN)) {
      return null;
    }
    expression = SERVICE_EXPR_PROCESSOR + "." + expression;
    if (!expression.endsWith(".list()")) {
      expression = expression + ".list()";
    }
    return expression;
  }

  public ServiceExpressionProcessor services(String... serviceNames) {
    this.serviceNames = serviceNames;
    return this;
  }

  public ServiceExpressionProcessor withNames(String... serviceNames) {
    this.serviceNames = serviceNames;
    return this;
  }

  public List<ServiceElement> list() {
    Builder pageRequest =
        PageRequest.Builder.aPageRequest()
            .withLimit(PageRequest.UNLIMITED)
            .addFilter(SearchFilter.Builder.aSearchFilter().withField("appId", Operator.EQ, appId).build());
    List<Service> services = null;

    if (ArrayUtils.isEmpty(serviceNames)) {
      services = serviceResourceService.list(pageRequest.build());
    } else if (Misc.isWildCharPresent(serviceNames)) {
      services = serviceResourceService.list(pageRequest.build());
      services = matchingServices(services, serviceNames);
    } else {
      pageRequest.addFilter(SearchFilter.Builder.aSearchFilter().withField("name", Operator.IN, serviceNames).build());
      services = serviceResourceService.list(pageRequest.build());
    }

    return convertToServiceElements(services);
  }

  List<Service> matchingServices(List<Service> services, String... names) {
    List<Pattern> patterns = new ArrayList<>();
    for (String name : names) {
      patterns.add(Pattern.compile(name.replaceAll("\\" + Constants.WILD_CHAR, "." + Constants.WILD_CHAR)));
    }

    List<Service> matchingServices = new ArrayList<>();
    for (Service service : services) {
      for (Pattern pattern : patterns) {
        Matcher matcher = pattern.matcher(service.getName());
        if (matcher.matches()) {
          matchingServices.add(service);
          break;
        }
      }
    }
    return matchingServices;
  }

  private List<ServiceElement> convertToServiceElements(List<Service> services) {
    ModelMapper mm = new ModelMapper();

    List<ServiceElement> elements = new ArrayList<>();
    for (Service service : services) {
      ServiceElement element = new ServiceElement();
      mm.map(service, element);
      elements.add(element);
    }

    return elements;
  }
}
