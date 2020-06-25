package software.wings.delegatetasks.aws.ecs.ecstaskhandler;

import static io.harness.delegate.command.CommandExecutionResult.CommandExecutionStatus.SUCCESS;
import static io.harness.rule.OwnerRule.ARVIND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static software.wings.beans.command.EcsSetupParams.EcsSetupParamsBuilder.anEcsSetupParams;
import static software.wings.utils.WingsTestConstants.SERVICE_ID;

import com.google.inject.Inject;

import com.amazonaws.services.ecs.model.TaskDefinition;
import com.amazonaws.services.elasticloadbalancingv2.model.Action;
import com.amazonaws.services.elasticloadbalancingv2.model.Listener;
import com.amazonaws.services.elasticloadbalancingv2.model.TargetGroup;
import io.harness.category.element.UnitTests;
import io.harness.delegate.command.CommandExecutionResult;
import io.harness.delegate.task.aws.AwsElbListener;
import io.harness.rule.Owner;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import software.wings.WingsBaseTest;
import software.wings.beans.command.ExecutionLogCallback;
import software.wings.cloudprovider.aws.EcsContainerService;
import software.wings.delegatetasks.DelegateFileManager;
import software.wings.delegatetasks.DelegateLogService;
import software.wings.helpers.ext.ecs.request.EcsBGServiceSetupRequest;
import software.wings.helpers.ext.ecs.request.EcsServiceSetupRequest;
import software.wings.helpers.ext.ecs.response.EcsCommandExecutionResponse;
import software.wings.service.impl.AwsHelperService;
import software.wings.service.intfc.aws.delegate.AwsElbHelperServiceDelegate;
import software.wings.service.intfc.security.EncryptionService;

import java.util.Collections;
import java.util.Optional;

public class EcsBlueGreenSetupCommandHandlerTest extends WingsBaseTest {
  private final Listener forwardListener =
      new Listener().withDefaultActions(new Action().withTargetGroupArn("arn").withType("forward"));
  private final Listener nonForwardListener =
      new Listener().withDefaultActions(new Action().withTargetGroupArn("arn").withType("default"));
  @InjectMocks @Inject private EcsBlueGreenSetupCommandHandler handler;
  @Mock private AwsHelperService mockAwsHelperService;
  @Mock private EcsSetupCommandTaskHelper mockEcsSetupCommandTaskHelper;
  @Mock private AwsElbHelperServiceDelegate mockAwsElbHelperServiceDelegate;
  @Mock private EcsContainerService mockEcsContainerService;
  @Mock private DelegateFileManager mockDelegateFileManager;
  @Mock private EncryptionService mockEncryptionService;
  @Mock private DelegateLogService mockDelegateLogService;

  @Test
  @Owner(developers = ARVIND)
  @Category(UnitTests.class)
  public void testExecuteTaskInternalFailure() {
    ExecutionLogCallback mockCallback = mock(ExecutionLogCallback.class);
    EcsServiceSetupRequest request = EcsServiceSetupRequest.builder().build();
    EcsCommandExecutionResponse response = handler.executeTaskInternal(request, null, mockCallback);
    assertThat(response).isNotNull();
    assertThat(response.getCommandExecutionStatus()).isEqualTo(CommandExecutionResult.CommandExecutionStatus.FAILURE);
    assertThat(response.getEcsCommandResponse().getOutput())
        .isEqualTo("Invalid Request Type: Expected was : EcsBGServiceSetupRequest");
    assertThat(response.getEcsCommandResponse().getCommandExecutionStatus())
        .isEqualTo(CommandExecutionResult.CommandExecutionStatus.FAILURE);
  }

  @Test
  @Owner(developers = ARVIND)
  @Category(UnitTests.class)
  public void testExecute_NoActionSetForListenerForwardTargetGroup() {
    ExecutionLogCallback mockCallback = mock(ExecutionLogCallback.class);
    EcsBGServiceSetupRequest request =
        EcsBGServiceSetupRequest.builder().ecsSetupParams(anEcsSetupParams().build()).build();

    doReturn(nonForwardListener)
        .when(mockAwsElbHelperServiceDelegate)
        .getElbListener(any(), any(), anyString(), anyString());

    EcsCommandExecutionResponse response = handler.executeTaskInternal(request, null, mockCallback);
    assertThat(response).isNotNull();
    assertThat(response.getCommandExecutionStatus()).isEqualTo(CommandExecutionResult.CommandExecutionStatus.FAILURE);
    assertThat(response.getEcsCommandResponse().getCommandExecutionStatus())
        .isEqualTo(CommandExecutionResult.CommandExecutionStatus.FAILURE);
  }

  @Test
  @Owner(developers = ARVIND)
  @Category(UnitTests.class)
  public void testExecute_StageListenerArn() {
    ExecutionLogCallback mockCallback = mock(ExecutionLogCallback.class);
    EcsBGServiceSetupRequest request = EcsBGServiceSetupRequest.builder()
                                           .ecsSetupParams(anEcsSetupParams().withStageListenerArn("arn").build())
                                           .build();

    doReturn(forwardListener)
        .when(mockAwsElbHelperServiceDelegate)
        .getElbListener(any(), any(), anyString(), anyString());

    linearClosure(mockCallback, request);
  }

  @Test
  @Owner(developers = ARVIND)
  @Category(UnitTests.class)
  public void testExecute_NoStageListenerArn_WrongTargetGroupArn() {
    ExecutionLogCallback mockCallback = mock(ExecutionLogCallback.class);
    EcsBGServiceSetupRequest request =
        EcsBGServiceSetupRequest.builder().ecsSetupParams(anEcsSetupParams().withTargetGroupArn("arn").build()).build();

    doReturn(forwardListener)
        .when(mockAwsElbHelperServiceDelegate)
        .getElbListener(any(), any(), anyString(), anyString());

    doReturn(Optional.empty())
        .when(mockAwsElbHelperServiceDelegate)
        .getTargetGroup(any(), any(), anyString(), anyString());

    EcsCommandExecutionResponse response = handler.executeTaskInternal(request, null, mockCallback);
    assertThat(response).isNotNull();
    assertThat(response.getCommandExecutionStatus()).isEqualTo(CommandExecutionResult.CommandExecutionStatus.FAILURE);
    assertThat(response.getEcsCommandResponse().getCommandExecutionStatus())
        .isEqualTo(CommandExecutionResult.CommandExecutionStatus.FAILURE);
  }

  @Test
  @Owner(developers = ARVIND)
  @Category(UnitTests.class)
  public void testExecute_NoStageListenerArn_TargetGroupArn() {
    ExecutionLogCallback mockCallback = mock(ExecutionLogCallback.class);
    EcsBGServiceSetupRequest request =
        EcsBGServiceSetupRequest.builder().ecsSetupParams(anEcsSetupParams().withTargetGroupArn("arn").build()).build();

    doReturn(forwardListener)
        .when(mockAwsElbHelperServiceDelegate)
        .getElbListener(any(), any(), anyString(), anyString());

    doReturn(Optional.of(new TargetGroup()))
        .when(mockAwsElbHelperServiceDelegate)
        .getTargetGroup(any(), any(), anyString(), anyString());

    linearClosure(mockCallback, request);
  }

  @Test
  @Owner(developers = ARVIND)
  @Category(UnitTests.class)
  public void testExecute_NoStageListenerArn_NoTargetGroupArn_Clone_ProdListenerTrue() {
    ExecutionLogCallback mockCallback = mock(ExecutionLogCallback.class);
    EcsBGServiceSetupRequest request = EcsBGServiceSetupRequest.builder()
                                           .ecsSetupParams(anEcsSetupParams()
                                                               .withRegion("region-1")
                                                               .withLoadBalancerName("lb-1")
                                                               .withStageListenerPort("8080")
                                                               .withTargetGroupArn2("arn2")
                                                               .build())
                                           .build();

    doReturn(forwardListener)
        .when(mockAwsElbHelperServiceDelegate)
        .getElbListener(any(), any(), anyString(), anyString());

    doReturn(Collections.singletonList(AwsElbListener.builder().port(8080).build()))
        .when(mockAwsElbHelperServiceDelegate)
        .getElbListenersForLoadBalaner(any(), any(), anyString(), anyString());

    linearClosure(mockCallback, request);

    verify(mockAwsElbHelperServiceDelegate, times(2)).getElbListener(any(), any(), anyString(), anyString());
    verify(mockEcsSetupCommandTaskHelper).getTargetGroupForDefaultAction(any(), any());
  }

  @Test
  @Owner(developers = ARVIND)
  @Category(UnitTests.class)
  public void testExecute_NoStageListenerArn_NoTargetGroupArn_Clone_ProdListenerFalse_StageTGTrue() {
    ExecutionLogCallback mockCallback = mock(ExecutionLogCallback.class);
    EcsBGServiceSetupRequest request = EcsBGServiceSetupRequest.builder()
                                           .ecsSetupParams(anEcsSetupParams()
                                                               .withRegion("region-1")
                                                               .withLoadBalancerName("lb-1")
                                                               .withStageListenerPort("8080")
                                                               .withTargetGroupArn2("arn2")
                                                               .build())
                                           .build();

    doReturn(forwardListener)
        .when(mockAwsElbHelperServiceDelegate)
        .getElbListener(any(), any(), anyString(), anyString());

    doReturn(Collections.singletonList(AwsElbListener.builder().port(80).build()))
        .when(mockAwsElbHelperServiceDelegate)
        .getElbListenersForLoadBalaner(any(), any(), anyString(), anyString());

    doReturn(Optional.of(new TargetGroup()))
        .when(mockAwsElbHelperServiceDelegate)
        .getTargetGroup(any(), any(), anyString(), anyString());

    doReturn(Optional.of(new TargetGroup()))
        .when(mockAwsElbHelperServiceDelegate)
        .getTargetGroupByName(any(), any(), anyString(), anyString());

    doReturn(forwardListener)
        .when(mockAwsElbHelperServiceDelegate)
        .createStageListener(any(), any(), anyString(), anyString(), anyInt(), anyString());

    linearClosure(mockCallback, request);

    verify(mockAwsElbHelperServiceDelegate).getElbListener(any(), any(), anyString(), anyString());
    verify(mockAwsElbHelperServiceDelegate)
        .createStageListener(any(), any(), anyString(), anyString(), anyInt(), anyString());
  }

  @Test
  @Owner(developers = ARVIND)
  @Category(UnitTests.class)
  public void testExecute_NoStageListenerArn_NoTargetGroupArn_Clone_ProdListenerFalse_StageTGFalse() {
    ExecutionLogCallback mockCallback = mock(ExecutionLogCallback.class);
    EcsBGServiceSetupRequest request = EcsBGServiceSetupRequest.builder()
                                           .ecsSetupParams(anEcsSetupParams()
                                                               .withRegion("region-1")
                                                               .withLoadBalancerName("lb-1")
                                                               .withStageListenerPort("8080")
                                                               .withTargetGroupArn2("arn2")
                                                               .build())
                                           .build();

    doReturn(forwardListener)
        .when(mockAwsElbHelperServiceDelegate)
        .getElbListener(any(), any(), anyString(), anyString());

    doReturn(Collections.singletonList(AwsElbListener.builder().port(80).build()))
        .when(mockAwsElbHelperServiceDelegate)
        .getElbListenersForLoadBalaner(any(), any(), anyString(), anyString());

    doReturn(Optional.of(new TargetGroup()))
        .when(mockAwsElbHelperServiceDelegate)
        .getTargetGroup(any(), any(), anyString(), anyString());

    doReturn(new TargetGroup())
        .when(mockAwsElbHelperServiceDelegate)
        .cloneTargetGroup(any(), any(), anyString(), anyString(), anyString());

    doReturn(Optional.empty())
        .when(mockAwsElbHelperServiceDelegate)
        .getTargetGroupByName(any(), any(), anyString(), anyString());

    doReturn(forwardListener)
        .when(mockAwsElbHelperServiceDelegate)
        .createStageListener(any(), any(), anyString(), anyString(), anyInt(), anyString());

    linearClosure(mockCallback, request);

    verify(mockAwsElbHelperServiceDelegate).getElbListener(any(), any(), anyString(), anyString());
    verify(mockAwsElbHelperServiceDelegate)
        .createStageListener(any(), any(), anyString(), anyString(), anyInt(), anyString());
  }

  private void linearClosure(ExecutionLogCallback mockCallback, EcsBGServiceSetupRequest request) {
    doReturn(new TaskDefinition())
        .when(mockEcsSetupCommandTaskHelper)
        .createTaskDefinition(any(), any(), any(), any(), any(), any());

    doReturn(SERVICE_ID).when(mockEcsSetupCommandTaskHelper).createEcsService(any(), any(), any(), any(), any(), any());

    EcsCommandExecutionResponse response = handler.executeTaskInternal(request, null, mockCallback);
    verify(mockEcsSetupCommandTaskHelper).deleteExistingServicesOtherThanBlueVersion(any(), any(), any(), any());
    verify(mockEcsSetupCommandTaskHelper).createEcsService(any(), any(), any(), any(), any(), any());

    verify(mockEcsSetupCommandTaskHelper)
        .storeCurrentServiceNameAndCountInfo(eq(request.getAwsConfig()), any(), any(), any(), eq(SERVICE_ID));
    verify(mockEcsSetupCommandTaskHelper).backupAutoScalarConfig(any(), any(), any(), anyString(), any(), any());
    verify(mockEcsSetupCommandTaskHelper).logLoadBalancerInfo(any(), any());

    assertThat(response).isNotNull();
    assertThat(response.getCommandExecutionStatus()).isEqualTo(SUCCESS);
    assertThat(response.getEcsCommandResponse().getCommandExecutionStatus()).isEqualTo(SUCCESS);
  }
}