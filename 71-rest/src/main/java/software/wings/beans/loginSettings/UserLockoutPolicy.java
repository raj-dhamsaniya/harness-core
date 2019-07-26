package software.wings.beans.loginSettings;

import io.harness.annotation.HarnessExportableEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import software.wings.beans.security.UserGroup;

import java.util.List;

@HarnessExportableEntity
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserLockoutPolicy {
  boolean enableLockoutPolicy;
  int numberOfFailedAttemptsBeforeLockout;
  int lockOutPeriod;
  boolean notifyUser;
  List<UserGroup> userGroupsToNotify;
}
