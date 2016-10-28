package software.wings.service.impl;

import static java.util.Arrays.asList;
import static software.wings.beans.PipelineExecution.Builder.aPipelineExecution;

import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import software.wings.beans.Pipeline;
import software.wings.beans.PipelineExecution;
import software.wings.beans.PipelineStageExecution;
import software.wings.beans.WorkflowExecution;
import software.wings.beans.artifact.Artifact;
import software.wings.dl.PageRequest;
import software.wings.dl.PageResponse;
import software.wings.dl.WingsPersistence;
import software.wings.service.intfc.ArtifactService;
import software.wings.service.intfc.PipelineService;
import software.wings.service.intfc.WorkflowExecutionService;
import software.wings.service.intfc.WorkflowService;
import software.wings.sm.ExecutionStatus;

import javax.inject.Inject;

/**
 * Created by anubhaw on 10/26/16.
 */
public class PipelineServiceImpl implements PipelineService {
  @Inject private WorkflowExecutionService workflowExecutionService;
  @Inject private ArtifactService artifactService;
  @Inject private WingsPersistence wingsPersistence;
  @Inject private WorkflowService workflowService;

  @Override
  public PageResponse<PipelineExecution> listPipelineExecutions(PageRequest<PipelineExecution> pageRequest) {
    return wingsPersistence.query(PipelineExecution.class, pageRequest);
  }

  @Override
  public void updatePipelineExecutionData(
      String appId, String pipelineExecutionId, WorkflowExecution workflowExecution) {
    Query<PipelineExecution> query = wingsPersistence.createQuery(PipelineExecution.class)
                                         .field("appId")
                                         .equal(appId)
                                         .field("workflowExecutionId")
                                         .equal(pipelineExecutionId);
    UpdateOperations<PipelineExecution> operations =
        wingsPersistence.createUpdateOperations(PipelineExecution.class)
            .add("pipelineStageExecutions", new PipelineStageExecution(asList(workflowExecution)));
    wingsPersistence.update(query, operations);
  }

  @Override
  public void updatePipelineExecutionData(String appId, String pipelineExecutionId, Artifact artifact) {
    Query<PipelineExecution> query = wingsPersistence.createQuery(PipelineExecution.class)
                                         .field("appId")
                                         .equal(appId)
                                         .field("workflowExecutionId")
                                         .equal(pipelineExecutionId);
    UpdateOperations<PipelineExecution> operations = wingsPersistence.createUpdateOperations(PipelineExecution.class)
                                                         .set("artifactId", artifact.getUuid())
                                                         .set("artifactName", artifact.getDisplayName());
    wingsPersistence.update(query, operations);
  }

  @Override
  public WorkflowExecution execute(String appId, String pipelineId) {
    WorkflowExecution workflowExecution = workflowExecutionService.triggerPipelineExecution(appId, pipelineId);
    Pipeline pipeline = wingsPersistence.get(Pipeline.class, appId, pipelineId);
    PipelineExecution pipelineExecution = aPipelineExecution()
                                              .withAppId(appId)
                                              .withPipelineId(pipelineId)
                                              .withPipeline(pipeline)
                                              .withWorkflowExecutionId(workflowExecution.getUuid())
                                              .withStatus(workflowExecution.getStatus())
                                              .build();
    wingsPersistence.save(pipelineExecution);
    return workflowExecution;
  }

  @Override
  public void updatePipelineExecutionData(String appId, String workflowExecutionId, ExecutionStatus status) {
    Query<PipelineExecution> query = wingsPersistence.createQuery(PipelineExecution.class)
                                         .field("appId")
                                         .equal(appId)
                                         .field("workflowExecutionId")
                                         .equal(workflowExecutionId);
    UpdateOperations<PipelineExecution> operations =
        wingsPersistence.createUpdateOperations(PipelineExecution.class).set("status", status);
    wingsPersistence.update(query, operations);
  }
}
