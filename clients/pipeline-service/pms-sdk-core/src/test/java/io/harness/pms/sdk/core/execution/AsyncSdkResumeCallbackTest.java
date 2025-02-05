/*
 * Copyright 2021 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.pms.sdk.core.execution;

import static io.harness.pms.sdk.PmsSdkModuleUtils.CORE_EXECUTOR_NAME;
import static io.harness.pms.sdk.core.execution.AsyncSdkResumeCallback.CDS_REMOVE_RESUME_EVENT_FOR_ASYNC_AND_ASYNCCHAIN_MODE;
import static io.harness.rule.OwnerRule.SAHIL;
import static io.harness.rule.OwnerRule.YUVRAJ;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;
import io.harness.category.element.UnitTests;
import io.harness.pms.contracts.ambiance.Ambiance;
import io.harness.pms.contracts.ambiance.Level;
import io.harness.pms.contracts.execution.AsyncChainExecutableResponse;
import io.harness.pms.contracts.execution.AsyncExecutableResponse;
import io.harness.pms.contracts.execution.ExecutableResponse;
import io.harness.pms.contracts.plan.ExecutionMetadata;
import io.harness.pms.sdk.core.PmsSdkCoreTestBase;
import io.harness.pms.sdk.core.execution.events.node.resume.NodeResumeEventHandler;
import io.harness.pms.sdk.core.steps.io.ResponseDataMapper;
import io.harness.rule.Owner;

import com.google.inject.name.Named;
import com.google.protobuf.ByteString;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;

@OwnedBy(HarnessTeam.PIPELINE)
public class AsyncSdkResumeCallbackTest extends PmsSdkCoreTestBase {
  private static String NODE_EXECUTION_ID = "nodeId";
  private static String PLAN_EXECUTION_ID = "planExecutionId";

  @Mock SdkNodeExecutionService sdkNodeExecutionService;
  @Mock @Named(CORE_EXECUTOR_NAME) ExecutorService executorService;
  @Mock NodeResumeEventHandler nodeResumeEventHandler;
  @Mock ResponseDataMapper responseDataMapper;
  AsyncSdkResumeCallback asyncSdkResumeCallback;
  Ambiance ambiance;

  @Before
  public void setup() {
    ambiance = Ambiance.newBuilder()
                   .setPlanExecutionId(PLAN_EXECUTION_ID)
                   .addLevels(Level.newBuilder().setRuntimeId(NODE_EXECUTION_ID).build())
                   .build();
    asyncSdkResumeCallback = AsyncSdkResumeCallback.builder()
                                 .sdkNodeExecutionService(sdkNodeExecutionService)
                                 .ambianceBytes(ambiance.toByteArray())
                                 .build();
  }

  @Test
  @Owner(developers = SAHIL)
  @Category(UnitTests.class)
  public void testNotify() {
    asyncSdkResumeCallback.notify(new HashMap<>());
    verify(sdkNodeExecutionService).resumeNodeExecution(ambiance, new HashMap<>(), false);
  }

  @Test
  @Owner(developers = SAHIL)
  @Category(UnitTests.class)
  public void testNotifyError() {
    asyncSdkResumeCallback.notifyError(new HashMap<>());
    verify(sdkNodeExecutionService).resumeNodeExecution(ambiance, new HashMap<>(), true);
  }

  @Test
  @Owner(developers = YUVRAJ)
  @Category(UnitTests.class)
  public void testNotifyWithFFEnabledAndNullExecutableResponse() {
    Ambiance ambiance1 =
        Ambiance.newBuilder()
            .setPlanExecutionId(PLAN_EXECUTION_ID)
            .addLevels(Level.newBuilder().setRuntimeId(NODE_EXECUTION_ID).build())
            .setMetadata(ExecutionMetadata.newBuilder()
                             .putFeatureFlagToValueMap(CDS_REMOVE_RESUME_EVENT_FOR_ASYNC_AND_ASYNCCHAIN_MODE, true)
                             .build())
            .build();
    AsyncSdkResumeCallback asyncSdkResumeCallback1 = AsyncSdkResumeCallback.builder()
                                                         .sdkNodeExecutionService(sdkNodeExecutionService)
                                                         .ambianceBytes(ambiance1.toByteArray())
                                                         .build();
    asyncSdkResumeCallback1.notify(new HashMap<>());
    verify(sdkNodeExecutionService).resumeNodeExecution(ambiance1, new HashMap<>(), false);
  }

  @Test
  @Owner(developers = YUVRAJ)
  @Category(UnitTests.class)
  public void testNotifyErrorWithSkipSdkResumeEventForAsyncChain() {
    Ambiance ambiance1 =
        Ambiance.newBuilder()
            .setPlanExecutionId(PLAN_EXECUTION_ID)
            .addLevels(Level.newBuilder().setRuntimeId(NODE_EXECUTION_ID).setNodeType("PLAN_NODE").build())
            .setMetadata(ExecutionMetadata.newBuilder()
                             .putFeatureFlagToValueMap(CDS_REMOVE_RESUME_EVENT_FOR_ASYNC_AND_ASYNCCHAIN_MODE, true)
                             .build())
            .build();
    ExecutableResponse executableResponse =
        ExecutableResponse.newBuilder()
            .setAsyncChain(AsyncChainExecutableResponse.newBuilder().setChainEnd(false).build())
            .build();
    byte[] resolvedStepParameters = ByteString.copyFromUtf8("stepParameters").toByteArray();
    AsyncSdkResumeCallback asyncSdkResumeCallback1 = AsyncSdkResumeCallback.builder()
                                                         .sdkNodeExecutionService(sdkNodeExecutionService)
                                                         .responseDataMapper(responseDataMapper)
                                                         .nodeResumeEventHandler(nodeResumeEventHandler)
                                                         .executorService(executorService)
                                                         .ambianceBytes(ambiance1.toByteArray())
                                                         .executableResponseBytes(executableResponse.toByteArray())
                                                         .resolvedStepParameters(resolvedStepParameters)
                                                         .build();
    doReturn(new HashMap<>()).when(responseDataMapper).toResponseDataProtoV2(any());
    asyncSdkResumeCallback1.notify(new HashMap<>());
    verify(executorService, times(1)).submit(any(Runnable.class));
  }

  @Test
  @Owner(developers = YUVRAJ)
  @Category(UnitTests.class)
  public void testNotifyErrorWithSkipSdkResumeEventForAsync() {
    Ambiance ambiance1 =
        Ambiance.newBuilder()
            .setPlanExecutionId(PLAN_EXECUTION_ID)
            .addLevels(Level.newBuilder().setRuntimeId(NODE_EXECUTION_ID).setNodeType("PLAN_NODE").build())
            .setMetadata(ExecutionMetadata.newBuilder()
                             .putFeatureFlagToValueMap(CDS_REMOVE_RESUME_EVENT_FOR_ASYNC_AND_ASYNCCHAIN_MODE, true)
                             .build())
            .build();
    ExecutableResponse executableResponse =
        ExecutableResponse.newBuilder().setAsync(AsyncExecutableResponse.newBuilder().build()).build();
    byte[] resolvedStepParameters = ByteString.copyFromUtf8("stepParameters").toByteArray();
    AsyncSdkResumeCallback asyncSdkResumeCallback1 = AsyncSdkResumeCallback.builder()
                                                         .sdkNodeExecutionService(sdkNodeExecutionService)
                                                         .responseDataMapper(responseDataMapper)
                                                         .nodeResumeEventHandler(nodeResumeEventHandler)
                                                         .executorService(executorService)
                                                         .ambianceBytes(ambiance1.toByteArray())
                                                         .executableResponseBytes(executableResponse.toByteArray())
                                                         .resolvedStepParameters(resolvedStepParameters)
                                                         .build();
    doReturn(new HashMap<>()).when(responseDataMapper).toResponseDataProtoV2(any());
    asyncSdkResumeCallback1.notify(new HashMap<>());
    verify(executorService, times(1)).submit(any(Runnable.class));
  }
}
