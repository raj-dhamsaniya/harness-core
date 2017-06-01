package software.wings.integration.appdynamics;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static software.wings.beans.SettingAttribute.Builder.aSettingAttribute;
import static software.wings.dl.PageRequest.Builder.aPageRequest;

import com.google.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mongodb.morphia.query.Query;
import software.wings.beans.AppDynamicsConfig.Builder;
import software.wings.beans.RestResponse;
import software.wings.beans.SearchFilter.Operator;
import software.wings.beans.SettingAttribute;
import software.wings.beans.SettingAttribute.Category;
import software.wings.beans.SortOrder.OrderType;
import software.wings.dl.PageRequest;
import software.wings.dl.PageResponse;
import software.wings.integration.BaseIntegrationTest;
import software.wings.metrics.BucketData;
import software.wings.metrics.RiskLevel;
import software.wings.service.impl.appdynamics.AppdynamicsApplication;
import software.wings.service.impl.appdynamics.AppdynamicsBusinessTransaction;
import software.wings.service.impl.appdynamics.AppdynamicsMetric;
import software.wings.service.impl.appdynamics.AppdynamicsMetricData;
import software.wings.service.impl.appdynamics.AppdynamicsMetricDataRecord;
import software.wings.service.impl.appdynamics.AppdynamicsMetricDataValue;
import software.wings.service.impl.appdynamics.AppdynamicsNode;
import software.wings.service.impl.appdynamics.AppdynamicsTier;
import software.wings.service.intfc.appdynamics.AppdynamicsService;
import software.wings.utils.JsonUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

/**
 * Created by rsingh on 5/11/17.
 */
public class AppdynamicsIntegrationTest extends BaseIntegrationTest {
  @Inject private AppdynamicsService appdynamicsService;

  final long METRIC_ID = 4L;
  final String METRIC_NAME = "Average Response Time (ms)";

  @Before
  public void setUp() throws Exception {
    loginAdminUser();
    deleteAllDocuments(Arrays.asList(SettingAttribute.class));
    SettingAttribute appdSettingAttribute =
        aSettingAttribute()
            .withCategory(Category.CONNECTOR)
            .withName("AppDynamics")
            .withAccountId(accountId)
            .withValue(Builder.anAppDynamicsConfig()
                           .withControllerUrl("https://wings251.saas.appdynamics.com/controller")
                           .withUsername("appd-user")
                           .withAccountname("wings251")
                           .withPassword("5PdEYf9H".toCharArray())
                           .withAccountId(accountId)
                           .build())
            .build();
    wingsPersistence.saveAndGet(SettingAttribute.class, appdSettingAttribute);
  }

  @Test
  public void testGetAllApplications() throws Exception {
    final List<SettingAttribute> appdynamicsSettings =
        settingsService.getGlobalSettingAttributesByType(accountId, "APP_DYNAMICS");
    Assert.assertEquals(1, appdynamicsSettings.size());

    // get all applications
    WebTarget target = client.target(API_BASE
        + "/appdynamics/applications?settingId=" + appdynamicsSettings.get(0).getUuid() + "&accountId=" + accountId);
    RestResponse<List<AppdynamicsApplication>> restResponse =
        getRequestBuilderWithAuthHeader(target).get(new GenericType<RestResponse<List<AppdynamicsApplication>>>() {});

    Assert.assertEquals(0, restResponse.getResponseMessages().size());
    Assert.assertTrue(restResponse.getResource().size() > 0);

    for (AppdynamicsApplication app : restResponse.getResource()) {
      Assert.assertTrue(app.getId() > 0);
      Assert.assertFalse(StringUtils.isBlank(app.getName()));
    }
  }

  @Test
  public void testGetAllTiers() throws Exception {
    final List<SettingAttribute> appdynamicsSettings =
        settingsService.getGlobalSettingAttributesByType(accountId, "APP_DYNAMICS");
    Assert.assertEquals(1, appdynamicsSettings.size());

    // get all applications
    WebTarget target = client.target(API_BASE
        + "/appdynamics/applications?settingId=" + appdynamicsSettings.get(0).getUuid() + "&accountId=" + accountId);
    RestResponse<List<AppdynamicsApplication>> restResponse =
        getRequestBuilderWithAuthHeader(target).get(new GenericType<RestResponse<List<AppdynamicsApplication>>>() {});

    long appId = 0;

    for (AppdynamicsApplication application : restResponse.getResource()) {
      if (application.getName().equalsIgnoreCase("MyApp")) {
        appId = application.getId();
        break;
      }
    }

    Assert.assertTrue("could not find MyApp application in appdynamics", appId > 0);
    WebTarget btTarget = client.target(API_BASE + "/appdynamics/tiers?settingId=" + appdynamicsSettings.get(0).getUuid()
        + "&accountId=" + accountId + "&appdynamicsAppId=" + appId);
    RestResponse<List<AppdynamicsTier>> tierRestResponse =
        getRequestBuilderWithAuthHeader(btTarget).get(new GenericType<RestResponse<List<AppdynamicsTier>>>() {});
    Assert.assertTrue(tierRestResponse.getResource().size() > 0);

    for (AppdynamicsTier tier : tierRestResponse.getResource()) {
      Assert.assertTrue(tier.getId() > 0);
      Assert.assertFalse(StringUtils.isBlank(tier.getName()));
      Assert.assertFalse(StringUtils.isBlank(tier.getType()));
      Assert.assertFalse(StringUtils.isBlank(tier.getAgentType()));
      Assert.assertTrue(tier.getNumberOfNodes() > 0);
    }
  }

  @Test
  public void testGetAllNodes() throws Exception {
    final List<SettingAttribute> appdynamicsSettings =
        settingsService.getGlobalSettingAttributesByType(accountId, "APP_DYNAMICS");
    Assert.assertEquals(1, appdynamicsSettings.size());

    // get all applications
    WebTarget target = client.target(API_BASE
        + "/appdynamics/applications?settingId=" + appdynamicsSettings.get(0).getUuid() + "&accountId=" + accountId);
    RestResponse<List<AppdynamicsApplication>> restResponse =
        getRequestBuilderWithAuthHeader(target).get(new GenericType<RestResponse<List<AppdynamicsApplication>>>() {});

    long appId = 0;

    for (AppdynamicsApplication application : restResponse.getResource()) {
      if (application.getName().equalsIgnoreCase("MyApp")) {
        appId = application.getId();
        break;
      }
    }

    Assert.assertTrue("could not find MyApp application in appdynamics", appId > 0);
    WebTarget btTarget = client.target(API_BASE + "/appdynamics/tiers?settingId=" + appdynamicsSettings.get(0).getUuid()
        + "&accountId=" + accountId + "&appdynamicsAppId=" + appId);
    RestResponse<List<AppdynamicsTier>> tierRestResponse =
        getRequestBuilderWithAuthHeader(btTarget).get(new GenericType<RestResponse<List<AppdynamicsTier>>>() {});
    Assert.assertTrue(tierRestResponse.getResource().size() > 0);

    for (AppdynamicsTier tier : tierRestResponse.getResource()) {
      Assert.assertTrue(tier.getId() > 0);
      Assert.assertFalse(StringUtils.isBlank(tier.getName()));
      Assert.assertFalse(StringUtils.isBlank(tier.getType()));
      Assert.assertFalse(StringUtils.isBlank(tier.getAgentType()));
      Assert.assertTrue(tier.getNumberOfNodes() > 0);

      WebTarget nodeTarget =
          client.target(API_BASE + "/appdynamics/nodes?settingId=" + appdynamicsSettings.get(0).getUuid()
              + "&accountId=" + accountId + "&appdynamicsAppId=" + appId + "&tierId=" + tier.getId());

      RestResponse<List<AppdynamicsNode>> nodeRestResponse =
          getRequestBuilderWithAuthHeader(nodeTarget).get(new GenericType<RestResponse<List<AppdynamicsNode>>>() {});

      Assert.assertTrue(nodeRestResponse.getResource().size() > 0);
      for (AppdynamicsNode node : nodeRestResponse.getResource()) {
        Assert.assertTrue(node.getId() > 0);
        Assert.assertFalse(StringUtils.isBlank(node.getName()));
        Assert.assertFalse(StringUtils.isBlank(node.getType()));
        Assert.assertTrue(node.getTierId() > 0);
        Assert.assertFalse(StringUtils.isBlank(node.getTierName()));
        Assert.assertTrue(node.getMachineId() > 0);
        Assert.assertFalse(StringUtils.isBlank(node.getMachineName()));
        Assert.assertFalse(StringUtils.isBlank(node.getMachineOSType()));
        Assert.assertFalse(StringUtils.isBlank(node.getAppAgentVersion()));
        Assert.assertFalse(StringUtils.isBlank(node.getAgentType()));
        Assert.assertTrue(node.getIpAddresses().size() > 0);

        Assert.assertTrue(node.getIpAddresses().containsKey("ipAddresses"));
        Assert.assertTrue(node.getIpAddresses().get("ipAddresses").size() > 0);
      }
    }
  }

  @Test
  public void testGetAllBusinessTransactions() throws Exception {
    final List<SettingAttribute> appdynamicsSettings =
        settingsService.getGlobalSettingAttributesByType(accountId, "APP_DYNAMICS");
    Assert.assertEquals(1, appdynamicsSettings.size());

    // get all applications
    WebTarget target = client.target(API_BASE
        + "/appdynamics/applications?settingId=" + appdynamicsSettings.get(0).getUuid() + "&accountId=" + accountId);
    RestResponse<List<AppdynamicsApplication>> restResponse =
        getRequestBuilderWithAuthHeader(target).get(new GenericType<RestResponse<List<AppdynamicsApplication>>>() {});

    long appId = 0;

    for (AppdynamicsApplication application : restResponse.getResource()) {
      if (application.getName().equalsIgnoreCase("MyApp")) {
        appId = application.getId();
        break;
      }
    }

    Assert.assertTrue("could not find MyApp application in appdynamics", appId > 0);
    WebTarget btTarget = client.target(API_BASE + "/appdynamics/business-transactions?settingId="
        + appdynamicsSettings.get(0).getUuid() + "&accountId=" + accountId + "&appdynamicsAppId=" + appId);
    RestResponse<List<AppdynamicsBusinessTransaction>> btRestResponse = getRequestBuilderWithAuthHeader(btTarget).get(
        new GenericType<RestResponse<List<AppdynamicsBusinessTransaction>>>() {});
    Assert.assertTrue(btRestResponse.getResource().size() > 0);

    for (AppdynamicsBusinessTransaction bt : btRestResponse.getResource()) {
      Assert.assertTrue(bt.getId() > 0);
      Assert.assertTrue(bt.getTierId() > 0);
      Assert.assertFalse(StringUtils.isBlank(bt.getName()));
      Assert.assertFalse(StringUtils.isBlank(bt.getEntryPointType()));
      Assert.assertFalse(StringUtils.isBlank(bt.getInternalName()));
      Assert.assertFalse(StringUtils.isBlank(bt.getTierName()));
      Assert.assertFalse(StringUtils.isBlank(bt.getInternalName()));
    }
  }

  @Test
  public void testGetAllTierBTMetrics() throws Exception {
    final List<SettingAttribute> appdynamicsSettings =
        settingsService.getGlobalSettingAttributesByType(accountId, "APP_DYNAMICS");
    Assert.assertEquals(1, appdynamicsSettings.size());

    // get all applications
    WebTarget target = client.target(API_BASE
        + "/appdynamics/applications?settingId=" + appdynamicsSettings.get(0).getUuid() + "&accountId=" + accountId);
    RestResponse<List<AppdynamicsApplication>> restResponse =
        getRequestBuilderWithAuthHeader(target).get(new GenericType<RestResponse<List<AppdynamicsApplication>>>() {});

    long appId = 0;

    for (AppdynamicsApplication application : restResponse.getResource()) {
      if (application.getName().equalsIgnoreCase("MyApp")) {
        appId = application.getId();
        break;
      }
    }

    Assert.assertTrue("could not find MyApp application in appdynamics", appId > 0);
    WebTarget btTarget = client.target(API_BASE + "/appdynamics/tiers?settingId=" + appdynamicsSettings.get(0).getUuid()
        + "&accountId=" + accountId + "&appdynamicsAppId=" + appId);
    RestResponse<List<AppdynamicsTier>> tierRestResponse =
        getRequestBuilderWithAuthHeader(btTarget).get(new GenericType<RestResponse<List<AppdynamicsTier>>>() {});
    Assert.assertTrue(tierRestResponse.getResource().size() > 0);

    for (AppdynamicsTier tier : tierRestResponse.getResource()) {
      WebTarget btMetricsTarget =
          client.target(API_BASE + "/appdynamics/tier-bt-metrics?settingId=" + appdynamicsSettings.get(0).getUuid()
              + "&accountId=" + accountId + "&appdynamicsAppId=" + appId + "&tierId=" + tier.getId());
      RestResponse<List<AppdynamicsMetric>> tierBTMResponse =
          getRequestBuilderWithAuthHeader(btMetricsTarget)
              .get(new GenericType<RestResponse<List<AppdynamicsMetric>>>() {});

      List<AppdynamicsMetric> btMetrics = tierBTMResponse.getResource();
      Assert.assertTrue(btMetrics.size() > 0);

      for (AppdynamicsMetric btMetric : btMetrics) {
        Assert.assertFalse(StringUtils.isBlank(btMetric.getName()));
        Assert.assertTrue("failed for " + btMetric.getName(), btMetric.getChildMetrices().size() > 0);

        for (AppdynamicsMetric leafMetric : btMetric.getChildMetrices()) {
          Assert.assertFalse(StringUtils.isBlank(leafMetric.getName()));
          Assert.assertEquals(
              "failed for " + btMetric.getName() + "|" + leafMetric.getName(), 0, leafMetric.getChildMetrices().size());
        }
      }
    }
  }

  @Test
  @Ignore
  public void testGetBTMetricData() throws Exception {
    final List<SettingAttribute> appdynamicsSettings =
        settingsService.getGlobalSettingAttributesByType(accountId, "APP_DYNAMICS");
    Assert.assertEquals(1, appdynamicsSettings.size());

    // get all applications
    WebTarget target = client.target(API_BASE
        + "/appdynamics/applications?settingId=" + appdynamicsSettings.get(0).getUuid() + "&accountId=" + accountId);
    RestResponse<List<AppdynamicsApplication>> restResponse =
        getRequestBuilderWithAuthHeader(target).get(new GenericType<RestResponse<List<AppdynamicsApplication>>>() {});

    long appId = 0;

    for (AppdynamicsApplication application : restResponse.getResource()) {
      if (application.getName().equalsIgnoreCase("MyApp")) {
        appId = application.getId();
        break;
      }
    }

    Assert.assertTrue("could not find MyApp application in appdynamics", appId > 0);
    WebTarget btTarget = client.target(API_BASE + "/appdynamics/tiers?settingId=" + appdynamicsSettings.get(0).getUuid()
        + "&accountId=" + accountId + "&appdynamicsAppId=" + appId);
    RestResponse<List<AppdynamicsTier>> tierRestResponse =
        getRequestBuilderWithAuthHeader(btTarget).get(new GenericType<RestResponse<List<AppdynamicsTier>>>() {});
    Assert.assertTrue(tierRestResponse.getResource().size() > 0);

    for (AppdynamicsTier tier : tierRestResponse.getResource()) {
      WebTarget btMetricsTarget =
          client.target(API_BASE + "/appdynamics/tier-bt-metrics?settingId=" + appdynamicsSettings.get(0).getUuid()
              + "&accountId=" + accountId + "&appdynamicsAppId=" + appId + "&tierId=" + tier.getId());
      RestResponse<List<AppdynamicsMetric>> tierBTMResponse =
          getRequestBuilderWithAuthHeader(btMetricsTarget)
              .get(new GenericType<RestResponse<List<AppdynamicsMetric>>>() {});

      List<AppdynamicsMetric> btMetrics = tierBTMResponse.getResource();
      Assert.assertTrue(btMetrics.size() > 0);

      for (AppdynamicsMetric btMetric : btMetrics) {
        Assert.assertFalse(StringUtils.isBlank(btMetric.getName()));
        Assert.assertTrue("failed for " + btMetric.getName(), btMetric.getChildMetrices().size() > 0);

        final String url = API_BASE + "/appdynamics/get-metric-data?settingId=" + appdynamicsSettings.get(0).getUuid()
            + "&accountId=" + accountId + "&appdynamicsAppId=" + appId + "&tierId=" + tier.getId() + "&btName="
            + btMetric.getName() + "&startTime=" + (System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(5))
            + "&endTime=" + System.currentTimeMillis();
        WebTarget btMetricsDataTarget = client.target(url.replaceAll(" ", "%20"));
        RestResponse<List<AppdynamicsMetricData>> tierBTMDataResponse =
            getRequestBuilderWithAuthHeader(btMetricsDataTarget)
                .get(new GenericType<RestResponse<List<AppdynamicsMetricData>>>() {});
        System.out.println(JsonUtils.asJson(tierBTMDataResponse.getResource()));
      }
    }
  }

  @Test
  @Ignore
  public void testAppdynamicsPersistence() throws Exception {
    final List<SettingAttribute> appdynamicsSettings =
        settingsService.getGlobalSettingAttributesByType(accountId, "APP_DYNAMICS");
    Assert.assertEquals(1, appdynamicsSettings.size());
    String settingId = appdynamicsSettings.get(0).getUuid();
    WebTarget appTarget =
        client.target(API_BASE + "/appdynamics/applications?settingId=" + settingId + "&accountId=" + accountId);
    RestResponse<List<AppdynamicsApplication>> appRestResponse = getRequestBuilderWithAuthHeader(appTarget).get(
        new GenericType<RestResponse<List<AppdynamicsApplication>>>() {});
    AppdynamicsApplication app = appRestResponse.getResource().get(0);
    WebTarget btTarget = client.target(API_BASE + "/appdynamics/tiers?settingId=" + settingId
        + "&accountId=" + accountId + "&appdynamicsAppId=" + app.getId());
    RestResponse<List<AppdynamicsTier>> tierRestResponse =
        getRequestBuilderWithAuthHeader(btTarget).get(new GenericType<RestResponse<List<AppdynamicsTier>>>() {});
    AppdynamicsTier tier = tierRestResponse.getResource().get(0);
    WebTarget btMetricsTarget = client.target(API_BASE + "/appdynamics/tier-bt-metrics?settingId=" + settingId
        + "&accountId=" + accountId + "&appdynamicsAppId=" + app.getId() + "&tierId=" + tier.getId());
    RestResponse<List<AppdynamicsMetric>> tierBTMResponse =
        getRequestBuilderWithAuthHeader(btMetricsTarget).get(new GenericType<RestResponse<List<AppdynamicsMetric>>>() {
        });
    String btName = tierBTMResponse.getResource().get(0).getName();
    final AppdynamicsMetricDataValue METRIC_VALUE_1 = AppdynamicsMetricDataValue.Builder.anAppdynamicsMetricDataValue()
                                                          .withStartTimeInMillis(1495432894010L)
                                                          .withValue(100L)
                                                          .withMin(5L)
                                                          .withMax(200L)
                                                          .withCurrent(50L)
                                                          .withSum(910L)
                                                          .withCount(8L)
                                                          .withStandardDeviation(0.5d)
                                                          .withOccurrences(3)
                                                          .withUseRange(true)
                                                          .build();
    final AppdynamicsMetricDataValue METRIC_VALUE_2 = AppdynamicsMetricDataValue.Builder.anAppdynamicsMetricDataValue()
                                                          .withStartTimeInMillis(1495432954010L)
                                                          .withValue(200L)
                                                          .withMin(10L)
                                                          .withMax(400L)
                                                          .withCurrent(100L)
                                                          .withSum(1800L)
                                                          .withCount(16L)
                                                          .withStandardDeviation(0.25d)
                                                          .withOccurrences(6)
                                                          .withUseRange(true)
                                                          .build();
    final AppdynamicsMetricDataValue METRIC_VALUE_3 = AppdynamicsMetricDataValue.Builder.anAppdynamicsMetricDataValue()
                                                          .withStartTimeInMillis(1495433014010L)
                                                          .withValue(300L)
                                                          .withMin(15L)
                                                          .withMax(600L)
                                                          .withCurrent(150L)
                                                          .withSum(2700L)
                                                          .withCount(24L)
                                                          .withStandardDeviation(0.125d)
                                                          .withOccurrences(9)
                                                          .withUseRange(true)
                                                          .build();
    final AppdynamicsMetricData METRIC_DATA_1 =
        AppdynamicsMetricData.Builder.anAppdynamicsMetricsData()
            .withMetricName("BTM|BTs|BT:132632|Component:42159|" + METRIC_NAME)
            .withMetricId(METRIC_ID)
            .withMetricPath("Business Transaction Performance|Business Transactions|test-tier|" + btName
                + "|Individual Nodes|test-node|" + METRIC_NAME)
            .withFrequency("ONE_MIN")
            .withMetricValues(new AppdynamicsMetricDataValue[] {METRIC_VALUE_1, METRIC_VALUE_2})
            .build();
    final AppdynamicsMetricData METRIC_DATA_2 =
        AppdynamicsMetricData.Builder.anAppdynamicsMetricsData()
            .withMetricName("BTM|BTs|BT:132632|Component:42159|" + METRIC_NAME)
            .withMetricId(METRIC_ID)
            .withMetricPath("Business Transaction Performance|Business Transactions|test-tier|" + btName
                + "|Individual Nodes|test-node|" + METRIC_NAME)
            .withFrequency("ONE_MIN")
            .withMetricValues(new AppdynamicsMetricDataValue[] {METRIC_VALUE_2, METRIC_VALUE_3})
            .build();
    final List<AppdynamicsMetricDataRecord> METRIC_DATA_RECORDS_1 =
        AppdynamicsMetricDataRecord.generateDataRecords(accountId, app.getId(), tier.getId(), METRIC_DATA_1);

    // insert metric values 1 and 2 using the REST interface
    WebTarget target = client.target(API_BASE + "/appdynamics/save-metrics?accountId=" + accountId
        + "&appdynamicsAppId=" + app.getId() + "&tierId=" + tier.getId());
    RestResponse<Boolean> restResponse = getRequestBuilderWithAuthHeader(target).post(
        Entity.entity(Arrays.asList(METRIC_DATA_1), APPLICATION_JSON), new GenericType<RestResponse<Boolean>>() {});
    assert (restResponse.getResource());

    // query
    PageRequest.Builder requestBuilder = aPageRequest()
                                             .addFilter("accountId", Operator.EQ, accountId)
                                             .addFilter("appdAppId", Operator.EQ, app.getId())
                                             .addFilter("metricId", Operator.EQ, METRIC_ID)
                                             .addFilter("tierId", Operator.EQ, tier.getId())
                                             .addOrder("startTime", OrderType.ASC);
    PageResponse<AppdynamicsMetricDataRecord> response =
        wingsPersistence.query(AppdynamicsMetricDataRecord.class, requestBuilder.build());
    List<AppdynamicsMetricDataRecord> result = response.getResponse();
    Assert.assertEquals("result not correct length", 2, result.size());
    Assert.assertEquals("record 0 not equal", METRIC_DATA_RECORDS_1.get(0), result.get(0));
    Assert.assertEquals("record 1 not equal", METRIC_DATA_RECORDS_1.get(1), result.get(1));

    // insert metric values 2 and 3 using direct service call
    // this should result in 1, 2, and 3 all being stored
    appdynamicsService.saveMetricData(accountId, app.getId(), tier.getId(), Arrays.asList(METRIC_DATA_2));
    response = wingsPersistence.query(AppdynamicsMetricDataRecord.class, requestBuilder.build());
    result = response.getResponse();
    Assert.assertEquals("result not correct length", 3, result.size());

    // more specific query
    requestBuilder.addFilter("btName", Operator.EQ, btName)
        .addFilter("nodeName", Operator.EQ, "test-node")
        .addFilter("startTime", Operator.EQ, 1495432894010L);
    response = wingsPersistence.query(AppdynamicsMetricDataRecord.class, requestBuilder.build());
    result = response.getResponse();
    Assert.assertEquals("short result not correct length", 1, result.size());

    // generate metrics using the REST interface

    target = client.target(API_BASE + "/appdynamics/generate-metrics?settingId=" + settingId + "&accountId=" + accountId
        + "&appdynamicsAppId=" + app.getId() + "&tierId=" + tier.getId() + "&startTimeInMillis=1495432894010"
        + "&endTimeInMillis=1495433114010");
    //    RestResponse<Map<String, Map<String, BucketData>>> metricRestResponse =
    //        getRequestBuilderWithAuthHeader(target).post(Entity.entity(Arrays.asList(BT_NAME), APPLICATION_JSON), new
    //        GenericType<RestResponse<Map<String, Map<String, BucketData>>>>() {
    //        });
    RestResponse<Map<String, Map<String, BucketData>>> metricRestResponse = getRequestBuilderWithAuthHeader(target).get(
        new GenericType<RestResponse<Map<String, Map<String, BucketData>>>>() {});
    Map<String, Map<String, BucketData>> generatedMetrics = metricRestResponse.getResource();
    Assert.assertEquals(RiskLevel.HIGH, generatedMetrics.get(btName).get(METRIC_NAME).getRisk());
    Assert.assertEquals("100.0", generatedMetrics.get(btName).get(METRIC_NAME).getOldData().getDisplayValue());

    // delete
    Query<AppdynamicsMetricDataRecord> query = wingsPersistence.createQuery(AppdynamicsMetricDataRecord.class);
    query.filter("accountId = ", accountId)
        .filter("appdAppId = ", app.getId())
        .filter("metricId", METRIC_ID)
        .filter("tierId", tier.getId());
    boolean success = wingsPersistence.delete(query);
    assert (success);
    requestBuilder = aPageRequest()
                         .addFilter("accountId", Operator.EQ, accountId)
                         .addFilter("appdAppId", Operator.EQ, app.getId())
                         .addFilter("metricId", Operator.EQ, METRIC_ID)
                         .addFilter("tierId", Operator.EQ, tier.getId())
                         .addOrder("startTime", OrderType.ASC);
    response = wingsPersistence.query(AppdynamicsMetricDataRecord.class, requestBuilder.build());
    result = response.getResponse();
    Assert.assertEquals("result not correct length", 0, result.size());
  }
}
