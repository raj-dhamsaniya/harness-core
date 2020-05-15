package software.wings.graphql.datafetcher.billing;

import static io.harness.rule.OwnerRule.ROHIT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import io.harness.category.element.UnitTests;
import io.harness.ccm.billing.graphql.CloudBillingAggregate;
import io.harness.ccm.billing.graphql.CloudBillingFilter;
import io.harness.ccm.billing.graphql.CloudBillingGroupBy;
import io.harness.ccm.billing.graphql.CloudBillingIdFilter;
import io.harness.ccm.billing.graphql.CloudBillingTimeFilter;
import io.harness.ccm.billing.graphql.CloudEntityGroupBy;
import io.harness.ccm.billing.preaggregated.PreAggregateBillingServiceImpl;
import io.harness.ccm.billing.preaggregated.PreAggregateBillingTimeSeriesStatsDTO;
import io.harness.rule.Owner;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import software.wings.graphql.datafetcher.AbstractDataFetcherTest;
import software.wings.graphql.schema.type.aggregation.QLData;
import software.wings.graphql.schema.type.aggregation.QLIdOperator;
import software.wings.graphql.schema.type.aggregation.QLTimeOperator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CloudTimeSeriesStatsDataFetcherTest extends AbstractDataFetcherTest {
  @Mock CloudBillingHelper cloudBillingHelper;
  @Mock PreAggregateBillingServiceImpl preAggregateBillingService;
  @InjectMocks CloudTimeSeriesStatsDataFetcher cloudTimeSeriesStatsDataFetcher;

  private static final String COST = "unblendedCost";
  private static final String DISCOUNT = "awsBlendedCost";
  private static final String SERVICE = "service";
  private static final String LINKED_ACCOUNT = "linkedAccount";
  private static final String USAGE_TYPE = "usageType";
  private static final String INSTANCE_TYPE = "instanceType";
  private static final String CLOUD_PROVIDER = "AWS";

  private List<CloudBillingAggregate> cloudBillingAggregates = new ArrayList<>();
  private List<CloudBillingFilter> filters = new ArrayList<>();
  private List<CloudBillingGroupBy> groupBy = new ArrayList<>();

  @Before
  public void setup() {
    cloudBillingAggregates.add(getBillingAggregate(COST));
    cloudBillingAggregates.add(getBillingAggregate(DISCOUNT));
    filters.addAll(Arrays.asList(getStartTimeAwsBillingFilter(0L), getServiceAwsFilter(new String[] {SERVICE}),
        getLinkedAccountsAwsFilter(new String[] {LINKED_ACCOUNT}), getUsageTypeAwsFilter(new String[] {USAGE_TYPE}),
        getRegionFilter(new String[] {INSTANCE_TYPE}), getCloudProviderFilter(new String[] {CLOUD_PROVIDER}),
        getInstanceTypeAwsFilter(new String[] {INSTANCE_TYPE})));

    groupBy.addAll(Arrays.asList(getServiceGroupBy(), getLinkedAccountsGroupBy(), getInstanceTypeGroupBy(),
        getUsageTypeGroupBy(), getRegionGroupBy()));

    when(preAggregateBillingService.getPreAggregateBillingTimeSeriesStats(
             anyList(), anyList(), anyList(), anyList(), any()))
        .thenReturn(PreAggregateBillingTimeSeriesStatsDTO.builder().build());
    when(cloudBillingHelper.getCloudProviderTableName(anyString())).thenReturn("CLOUD_PROVIDER_TABLE_NAME");
  }

  @Test
  @Owner(developers = ROHIT)
  @Category(UnitTests.class)
  public void testTimeSeriesDataFetcher() {
    QLData data =
        cloudTimeSeriesStatsDataFetcher.fetch(ACCOUNT1_ID, cloudBillingAggregates, filters, groupBy, null, 5, 0);
    assertThat(data).isEqualTo(PreAggregateBillingTimeSeriesStatsDTO.builder().build());
  }

  private CloudBillingAggregate getBillingAggregate(String columnName) {
    return CloudBillingAggregate.builder().operationType(QLCCMAggregateOperation.SUM).columnName(columnName).build();
  }

  private CloudBillingFilter getStartTimeAwsBillingFilter(Long filterTime) {
    CloudBillingFilter cloudBillingFilter = new CloudBillingFilter();
    cloudBillingFilter.setPreAggregatedTableStartTime(
        CloudBillingTimeFilter.builder().operator(QLTimeOperator.AFTER).value(filterTime).build());
    return cloudBillingFilter;
  }

  private CloudBillingFilter getServiceAwsFilter(String[] service) {
    CloudBillingFilter cloudBillingFilter = new CloudBillingFilter();
    cloudBillingFilter.setAwsService(
        CloudBillingIdFilter.builder().operator(QLIdOperator.EQUALS).values(service).build());
    return cloudBillingFilter;
  }

  private CloudBillingFilter getLinkedAccountsAwsFilter(String[] linkedAccounts) {
    CloudBillingFilter cloudBillingFilter = new CloudBillingFilter();
    cloudBillingFilter.setAwsLinkedAccount(
        CloudBillingIdFilter.builder().operator(QLIdOperator.IN).values(linkedAccounts).build());
    return cloudBillingFilter;
  }

  private CloudBillingFilter getUsageTypeAwsFilter(String[] usageType) {
    CloudBillingFilter cloudBillingFilter = new CloudBillingFilter();
    cloudBillingFilter.setAwsUsageType(
        CloudBillingIdFilter.builder().operator(QLIdOperator.NOT_IN).values(usageType).build());
    return cloudBillingFilter;
  }

  private CloudBillingFilter getInstanceTypeAwsFilter(String[] instanceType) {
    CloudBillingFilter cloudBillingFilter = new CloudBillingFilter();
    cloudBillingFilter.setAwsInstanceType(
        CloudBillingIdFilter.builder().operator(QLIdOperator.NOT_NULL).values(instanceType).build());
    return cloudBillingFilter;
  }

  private CloudBillingFilter getRegionFilter(String[] region) {
    CloudBillingFilter cloudBillingFilter = new CloudBillingFilter();
    cloudBillingFilter.setRegion(CloudBillingIdFilter.builder().operator(QLIdOperator.EQUALS).values(region).build());
    return cloudBillingFilter;
  }

  private CloudBillingFilter getCloudProviderFilter(String[] cloudProvider) {
    CloudBillingFilter cloudBillingFilter = new CloudBillingFilter();
    cloudBillingFilter.setCloudProvider(
        CloudBillingIdFilter.builder().operator(QLIdOperator.IN).values(cloudProvider).build());
    return cloudBillingFilter;
  }

  private CloudBillingGroupBy getServiceGroupBy() {
    CloudBillingGroupBy cloudBillingGroupBy = new CloudBillingGroupBy();
    cloudBillingGroupBy.setEntityGroupBy(CloudEntityGroupBy.awsService);
    return cloudBillingGroupBy;
  }

  private CloudBillingGroupBy getLinkedAccountsGroupBy() {
    CloudBillingGroupBy cloudBillingGroupBy = new CloudBillingGroupBy();
    cloudBillingGroupBy.setEntityGroupBy(CloudEntityGroupBy.awsLinkedAccount);
    return cloudBillingGroupBy;
  }

  private CloudBillingGroupBy getUsageTypeGroupBy() {
    CloudBillingGroupBy cloudBillingGroupBy = new CloudBillingGroupBy();
    cloudBillingGroupBy.setEntityGroupBy(CloudEntityGroupBy.awsUsageType);
    return cloudBillingGroupBy;
  }

  private CloudBillingGroupBy getInstanceTypeGroupBy() {
    CloudBillingGroupBy cloudBillingGroupBy = new CloudBillingGroupBy();
    cloudBillingGroupBy.setEntityGroupBy(CloudEntityGroupBy.awsInstanceType);
    return cloudBillingGroupBy;
  }

  private CloudBillingGroupBy getRegionGroupBy() {
    CloudBillingGroupBy cloudBillingGroupBy = new CloudBillingGroupBy();
    cloudBillingGroupBy.setEntityGroupBy(CloudEntityGroupBy.region);
    return cloudBillingGroupBy;
  }
}