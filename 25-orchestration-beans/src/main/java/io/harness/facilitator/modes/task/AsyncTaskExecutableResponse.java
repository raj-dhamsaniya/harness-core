package io.harness.facilitator.modes.task;

import static io.harness.annotations.dev.HarnessTeam.CDC;

import io.harness.annotations.Redesign;
import io.harness.annotations.dev.OwnedBy;
import io.harness.facilitator.modes.TaskExecutableResponse;
import lombok.Builder;
import lombok.Value;

@OwnedBy(CDC)
@Redesign
@Value
@Builder
public class AsyncTaskExecutableResponse implements TaskExecutableResponse {
  String taskId;
  String taskIdentifier;
  String taskType;
}
