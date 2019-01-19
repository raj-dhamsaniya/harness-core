package io.harness.event.model;

public interface EventConstants {
  String ACCOUNT_ID = "ACCOUNT_ID";
  String ACCOUNT_NAME = "ACCOUNT_NAME";
  String WORKFLOW_EXECUTION_STATUS = "WORKFLOW_EXECUTION_STATUS"; // ABORTED,FAILED,SUCCEEDED etc
  String WORKFLOW_TYPE = "WORKFLOW_TYPE"; // AUTOMATIC / MANUAL
  String WORKFLOW_DURATION = "WORKFLOW_DURATION";
  String AUTOMATIC_WORKFLOW_TYPE = "AUTOMATIC";
  String MANUAL_WORKFLOW_TYPE = "MANUAL";
  String USER_LOGGED_IN = "USER_LOGGED_IN";
  String SETUP_DATA_TYPE = "SETUP_DATA_TYPE";
  String INSTANCE_COUNT_TYPE = "INSTANCE_COUNT_TYPE";
  String NUMBER_OF_APPLICATIONS = "NUMBER_OF_APPLICATIONS";
  String NUMBER_OF_WORKFLOWS = "NUMBER_OF_WORKFLOWS";
  String NUMBER_OF_ENVIRONMENTS = "NUMBER_OF_ENVIRONMENTS";
  String NUMBER_OF_SERVICES = "NUMBER_OF_SERVICES";
  String NUMBER_OF_PIPELINES = "NUMBER_OF_PIPELINES";
  String NUMBER_OF_TRIGGERS = "NUMBER_OF_TRIGGERS";
  String VERIFICATION_TYPE = "VERIFICATION_TYPE";
  String VERIFICATION_247_CONFIGURED = "VERIFICATION_247_CONFIGURED";

  String LOG_ML_FEEDBACKTYPE = "LOG_ML_FEEDBACK_TYPE";
  String VERIFICATION_STATE_TYPE = "VERIFICATION_STATE_TYPE";
  String APPLICATION_ID = "APPLICATION_ID";
  String APPLICATION_NAME = "APPLICATION_NAME";
  String WORKFLOW_ID = "WORKFLOW_ID";
  String WORKFLOW_NAME = "WORKFLOW_NAME";
  String SERVICE_ID = "SERVICE_ID";
  String SERVICE_NAME = "SERVICE_NAME";
  String ENVIRONMENT_ID = "ENVIRONMENT_ID";
  String ENVIRONMENT_NAME = "ENVIRONMENT_NAME";
  String INSTANCE_COUNT_TOTAL = "TOTAL";
  String INSTANCE_COUNT_GLOBAL_TOTAL = "GLOBAL_TOTAL";
  String INSTANCE_COUNT_NINETY_FIVE_PERCENTILE = "95 PERCENTILE";
}
