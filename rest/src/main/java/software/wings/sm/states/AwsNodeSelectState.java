package software.wings.sm.states;

import static java.util.stream.Collectors.toList;
import static software.wings.api.ServiceInstanceIdsParam.ServiceInstanceIdsParamBuilder.aServiceInstanceIdsParam;
import static software.wings.sm.ExecutionResponse.Builder.anExecutionResponse;

import com.google.common.collect.ImmutableMap;

import com.github.reinert.jjschema.Attributes;
import org.mongodb.morphia.annotations.Transient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.wings.api.PhaseElement;
import software.wings.beans.ServiceInstance;
import software.wings.common.Constants;
import software.wings.service.intfc.InfrastructureMappingService;
import software.wings.sm.ContextElementType;
import software.wings.sm.ExecutionContext;
import software.wings.sm.ExecutionContextImpl;
import software.wings.sm.ExecutionResponse;
import software.wings.sm.State;
import software.wings.sm.StateType;

import java.util.List;
import javax.inject.Inject;

/**
 * Created by rishi on 1/12/17.
 */
public class AwsNodeSelectState extends State {
  private static final Logger logger = LoggerFactory.getLogger(AwsNodeSelectState.class);

  @Attributes(title = "Number of instances") private int instanceCount;

  @Attributes(title = "Select specific hosts?") private boolean specificHosts;
  private List<String> hostNames;

  @Attributes(title = "Provision Nodes?") private boolean provisionNode;
  @Attributes(title = "Host specification (Launch Configuration Name)") private String launcherConfigName;

  @Inject @Transient private InfrastructureMappingService infrastructureMappingService;

  /**
   * Instantiates a new Aws node select state.
   *
   * @param name the name
   */
  public AwsNodeSelectState(String name) {
    super(name, StateType.AWS_NODE_SELECT.name());
  }

  @Override
  public ExecutionResponse execute(ExecutionContext context) {
    String appId = ((ExecutionContextImpl) context).getApp().getUuid();
    String envId = ((ExecutionContextImpl) context).getEnv().getUuid();

    PhaseElement phaseElement = context.getContextElement(ContextElementType.PARAM, Constants.PHASE_PARAM);
    String serviceId = phaseElement.getServiceElement().getUuid();
    String computeProviderId = phaseElement.getComputeProviderId();

    logger.info("serviceId : {}, computeProviderId: {}", serviceId, computeProviderId);

    List<ServiceInstance> serviceInstances;

    if (provisionNode) {
      serviceInstances = infrastructureMappingService.provisionNodes(
          appId, serviceId, envId, computeProviderId, launcherConfigName, instanceCount);
    } else {
      if (specificHosts) {
        serviceInstances = infrastructureMappingService.selectServiceInstances(appId, serviceId, envId,
            computeProviderId, ImmutableMap.of("specificHosts", specificHosts, "hostNames", hostNames));
      } else {
        serviceInstances = infrastructureMappingService.selectServiceInstances(appId, serviceId, envId,
            computeProviderId, ImmutableMap.of("specificHosts", specificHosts, "instanceCount", instanceCount));
      }
    }
    List<String> serviceInstancesIds = serviceInstances.stream().map(ServiceInstance::getUuid).collect(toList());
    return anExecutionResponse()
        .addElement(aServiceInstanceIdsParam().withInstanceIds(serviceInstancesIds).withServiceId(serviceId).build())
        .build();
  }

  @Override
  public void handleAbortEvent(ExecutionContext context) {}

  /**
   * Gets instance count.
   *
   * @return the instance count
   */
  public int getInstanceCount() {
    return instanceCount;
  }

  /**
   * Sets instance count.
   *
   * @param instanceCount the instance count
   */
  public void setInstanceCount(int instanceCount) {
    this.instanceCount = instanceCount;
  }

  /**
   * Is specific hosts boolean.
   *
   * @return the boolean
   */
  public boolean isSpecificHosts() {
    return specificHosts;
  }

  /**
   * Sets specific hosts.
   *
   * @param specificHosts the specific hosts
   */
  public void setSpecificHosts(boolean specificHosts) {
    this.specificHosts = specificHosts;
  }

  /**
   * Gets host names.
   *
   * @return the host names
   */
  public List<String> getHostNames() {
    return hostNames;
  }

  /**
   * Sets host names.
   *
   * @param hostNames the host names
   */
  public void setHostNames(List<String> hostNames) {
    this.hostNames = hostNames;
  }

  /**
   * Is provision node boolean.
   *
   * @return the boolean
   */
  public boolean isProvisionNode() {
    return provisionNode;
  }

  /**
   * Sets provision node.
   *
   * @param provisionNode the provision node
   */
  public void setProvisionNode(boolean provisionNode) {
    this.provisionNode = provisionNode;
  }

  /**
   * Gets launcher config name.
   *
   * @return the launcher config name
   */
  public String getLauncherConfigName() {
    return launcherConfigName;
  }

  /**
   * Sets launcher config name.
   *
   * @param launcherConfigName the launcher config name
   */
  public void setLauncherConfigName(String launcherConfigName) {
    this.launcherConfigName = launcherConfigName;
  }
}
