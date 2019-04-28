package software.wings.service.impl.notifications;

import static io.harness.data.structure.EmptyPredicate.isEmpty;
import static java.util.stream.Collectors.toList;

import com.google.inject.Inject;

import io.harness.exception.ExceptionUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.wings.beans.Notification;
import software.wings.beans.User;
import software.wings.beans.notification.NotificationSettings;
import software.wings.beans.security.UserGroup;
import software.wings.service.intfc.AccountService;
import software.wings.service.intfc.NotificationSetupService;
import software.wings.service.intfc.UserService;

import java.util.List;

public class UserGroupBasedDispatcher implements NotificationDispatcher<UserGroup> {
  private static final Logger log = LoggerFactory.getLogger(UserGroupBasedDispatcher.class);

  @Inject private NotificationSetupService notificationSetupService;
  @Inject private EmailDispatcher emailDispatcher;
  @Inject private SlackMessageDispatcher slackMessageDispatcher;
  @Inject private UserService userService;
  @Inject private AccountService accountService;

  @Override
  public void dispatch(List<Notification> notifications, UserGroup userGroup) {
    if (isEmpty(notifications)) {
      return;
    }

    if (null == userGroup.getNotificationSettings()) {
      log.info("Notification Settings is null for User Group. No message will be sent. userGroup={} accountId={}",
          userGroup.getName(), userGroup.getAccountId());
      return;
    }

    NotificationSettings notificationSettings = userGroup.getNotificationSettings();

    // if `isUseIndividualEmails` is true, then notify all "members" of group
    if (notificationSettings.isUseIndividualEmails()) {
      List<String> emails =
          userGroup.getMembers().stream().filter(User::isEmailVerified).map(User::getEmail).collect(toList());

      log.info("[isUseIndividualEmails=true] Dispatching notifications to all the users of userGroup. uuid={} name={}",
          userGroup.getUuid(), userGroup.getName());
      emailDispatcher.dispatch(notifications, emails);
    }

    String accountId = notifications.get(0).getAccountId();

    boolean isCommunityAccount = accountService.isCommunityAccount(accountId);
    if (isCommunityAccount) {
      log.info("Slack Configuration will be ignored since it's a community account. accountId={}", accountId);
    } else if (null != userGroup.getSlackConfig()) {
      try {
        slackMessageDispatcher.dispatch(notifications, userGroup.getSlackConfig());
      } catch (Exception e) {
        log.error(ExceptionUtils.getMessage(e));
      }
    }

    if (CollectionUtils.isNotEmpty(userGroup.getEmailAddresses())) {
      try {
        emailDispatcher.dispatch(notifications, userGroup.getEmailAddresses());
      } catch (Exception e) {
        log.error(ExceptionUtils.getMessage(e));
      }
    }
  }

  @Override
  public EmailDispatcher getEmailDispatcher() {
    return emailDispatcher;
  }

  @Override
  public SlackMessageDispatcher getSlackDispatcher() {
    return slackMessageDispatcher;
  }

  @Override
  public Logger logger() {
    return log;
  }
}
