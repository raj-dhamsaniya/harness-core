package software.wings.service.impl.trigger;

import static io.harness.exception.WingsException.USER;
import static software.wings.utils.Validator.equalCheck;
import static software.wings.utils.Validator.notNullCheck;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.harness.beans.PageRequest;
import io.harness.beans.PageResponse;
import io.harness.exception.InvalidRequestException;
import lombok.extern.slf4j.Slf4j;
import software.wings.beans.Event;
import software.wings.beans.artifact.Artifact;
import software.wings.beans.trigger.Condition;
import software.wings.beans.trigger.Condition.Type;
import software.wings.beans.trigger.DeploymentTrigger;
import software.wings.dl.WingsPersistence;
import software.wings.service.intfc.AppService;
import software.wings.service.intfc.ArtifactStreamService;
import software.wings.service.intfc.PipelineService;
import software.wings.service.intfc.WorkflowService;
import software.wings.service.intfc.trigger.DeploymentTriggerService;
import software.wings.service.intfc.yaml.YamlPushService;
import software.wings.utils.Validator;

import java.util.List;
import java.util.Map;
import javax.validation.executable.ValidateOnExecution;

@Singleton
@ValidateOnExecution
@Slf4j
public class DeploymentTriggerServiceImpl implements DeploymentTriggerService {
  @Inject private transient Map<String, TriggerProcessor> triggerProcessorMapBinder;
  @Inject private transient WingsPersistence wingsPersistence;
  @Inject private transient PipelineService pipelineService;
  @Inject private transient WorkflowService workflowService;
  @Inject private transient YamlPushService yamlPushService;
  @Inject private transient ArtifactStreamService artifactStreamService;
  @Inject private transient DeploymentTriggerServiceHelper triggerServiceHelper;
  @Inject private transient AppService appService;

  @Override
  public DeploymentTrigger save(DeploymentTrigger trigger) {
    validateTrigger(trigger);

    String uuid = Validator.duplicateCheck(() -> wingsPersistence.save(trigger), "name", trigger.getName());
    return get(trigger.getAppId(), uuid);
    // Todo Uncomment once YAML support is added  actionsAfterTriggerSave(deploymentTrigger);
  }

  @Override
  public DeploymentTrigger update(DeploymentTrigger trigger) {
    DeploymentTrigger existingTrigger =
        wingsPersistence.getWithAppId(DeploymentTrigger.class, trigger.getAppId(), trigger.getUuid());
    notNullCheck("Trigger was deleted ", existingTrigger, USER);
    equalCheck(trigger.getAction().getActionType(), existingTrigger.getAction().getActionType());

    validateTrigger(trigger);
    String uuid = Validator.duplicateCheck(() -> wingsPersistence.save(trigger), "name", trigger.getName());
    return get(trigger.getAppId(), uuid);
    // Todo Uncomment once YAML support is added actionsAfterTriggerUpdate(existingTrigger, deploymentTrigger);
  }

  @Override
  public void delete(String appId, String triggerId) {
    DeploymentTrigger deploymentTrigger = get(appId, triggerId);
    notNullCheck("Trigger not exist ", triggerId, USER);
    // Todo Uncomment once YAML support is added  actionsAfterTriggerDelete(deploymentTrigger);
    // Do we have to prune tag links ?
    wingsPersistence.delete(DeploymentTrigger.class, triggerId);
  }

  @Override
  public DeploymentTrigger get(String appId, String triggerId) {
    DeploymentTrigger deploymentTrigger = wingsPersistence.getWithAppId(DeploymentTrigger.class, appId, triggerId);
    notNullCheck("Trigger not exist ", triggerId, USER);
    TriggerProcessor triggerProcessor = obtainTriggerProcessor(deploymentTrigger);
    triggerProcessor.transformTriggerConditionRead(deploymentTrigger);
    triggerProcessor.transformTriggerActionRead(deploymentTrigger);
    return deploymentTrigger;
  }

  @Override
  public PageResponse<DeploymentTrigger> list(PageRequest<DeploymentTrigger> pageRequest) {
    return wingsPersistence.query(DeploymentTrigger.class, pageRequest);
  }

  @Override
  public void triggerExecutionPostArtifactCollectionAsync(
      String accountId, String appId, String artifactStreamId, List<Artifact> artifacts) {
    ArtifactTriggerProcessor triggerProcessor =
        (ArtifactTriggerProcessor) triggerProcessorMapBinder.get(Type.NEW_ARTIFACT.name());

    triggerProcessor.executeTriggerOnEvent(appId,
        ArtifactTriggerProcessor.ArtifactTriggerExecutionParams.builder()
            .artifactStreamId(artifactStreamId)
            .collectedArtifacts(artifacts)
            .build());
  }

  private void validateTrigger(DeploymentTrigger trigger) {
    TriggerProcessor triggerProcessor = obtainTriggerProcessor(trigger);

    triggerProcessor.validateTriggerConditionSetup(trigger);
    triggerProcessor.validateTriggerActionSetup(trigger);
  }

  private TriggerProcessor obtainTriggerProcessor(DeploymentTrigger deploymentTrigger) {
    return triggerProcessorMapBinder.get(obtainTriggerConditionType(deploymentTrigger.getCondition()));
  }

  private String obtainTriggerConditionType(Condition condition) {
    if (condition.getType().equals(Type.NEW_ARTIFACT)) {
      return condition.getType().name();
    }
    throw new InvalidRequestException("Invalid Trigger Condition for trigger " + condition.getType().name(), USER);
  }

  void actionsAfterTriggerRead(DeploymentTrigger existingTrigger, DeploymentTrigger updatedTrigger) {
    String accountId = appService.getAccountIdByAppId(updatedTrigger.getAppId());

    boolean isRename = !existingTrigger.getName().equals(updatedTrigger.getName());
    yamlPushService.pushYamlChangeSet(accountId, existingTrigger, updatedTrigger, Event.Type.UPDATE, false, isRename);
  }

  void actionsAfterTriggerDelete(DeploymentTrigger savedTrigger) {
    String accountId = appService.getAccountIdByAppId(savedTrigger.getAppId());
    yamlPushService.pushYamlChangeSet(accountId, null, savedTrigger, Event.Type.DELETE, false, false);
  }

  void actionsAfterTriggerSave(DeploymentTrigger savedTrigger) {
    String accountId = appService.getAccountIdByAppId(savedTrigger.getAppId());

    yamlPushService.pushYamlChangeSet(accountId, null, savedTrigger, Event.Type.CREATE, false, false);
  }

  void actionsAfterTriggerUpdate(DeploymentTrigger existingTrigger, DeploymentTrigger updatedTrigger) {
    String accountId = appService.getAccountIdByAppId(updatedTrigger.getAppId());

    boolean isRename = !existingTrigger.getName().equals(updatedTrigger.getName());
    yamlPushService.pushYamlChangeSet(accountId, existingTrigger, updatedTrigger, Event.Type.UPDATE, false, isRename);
  }
}
