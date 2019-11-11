package io.harness.event.handler.impl.segment;

import static io.harness.data.structure.EmptyPredicate.isEmpty;
import static io.harness.data.structure.EmptyPredicate.isNotEmpty;
import static io.harness.event.handler.impl.Constants.ACCOUNT_ID;
import static io.harness.event.handler.impl.Constants.CUSTOM_EVENT_NAME;
import static io.harness.event.handler.impl.Constants.EMAIL_ID;
import static io.harness.event.handler.impl.Constants.ORIGINAL_TIMESTAMP_NAME;
import static io.harness.event.handler.impl.Constants.USER_NAME;
import static io.harness.event.model.EventType.COMMUNITY_TO_ESSENTIALS;
import static io.harness.event.model.EventType.COMMUNITY_TO_PAID;
import static io.harness.event.model.EventType.COMPLETE_USER_REGISTRATION;
import static io.harness.event.model.EventType.CUSTOM;
import static io.harness.event.model.EventType.ESSENTIALS_TO_PAID;
import static io.harness.event.model.EventType.FIRST_DELEGATE_REGISTERED;
import static io.harness.event.model.EventType.FIRST_DEPLOYMENT_EXECUTED;
import static io.harness.event.model.EventType.FIRST_ROLLED_BACK_DEPLOYMENT;
import static io.harness.event.model.EventType.FIRST_VERIFIED_DEPLOYMENT;
import static io.harness.event.model.EventType.FIRST_WORKFLOW_CREATED;
import static io.harness.event.model.EventType.JOIN_ACCOUNT_REQUEST;
import static io.harness.event.model.EventType.LICENSE_UPDATE;
import static io.harness.event.model.EventType.NEW_TRIAL_SIGNUP;
import static io.harness.event.model.EventType.PAID_TO_ESSENTIALS;
import static io.harness.event.model.EventType.SETUP_2FA;
import static io.harness.event.model.EventType.SETUP_CV_24X7;
import static io.harness.event.model.EventType.SETUP_IP_WHITELISTING;
import static io.harness.event.model.EventType.SETUP_RBAC;
import static io.harness.event.model.EventType.SETUP_SSO;
import static io.harness.event.model.EventType.TECH_STACK;
import static io.harness.event.model.EventType.TRIAL_TO_COMMUNITY;
import static io.harness.event.model.EventType.TRIAL_TO_ESSENTIALS;
import static io.harness.event.model.EventType.TRIAL_TO_PAID;
import static io.harness.event.model.EventType.USER_INVITED_FROM_EXISTING_ACCOUNT;
import static io.harness.exception.WingsException.USER;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.harness.event.handler.EventHandler;
import io.harness.event.handler.impl.Utils;
import io.harness.event.handler.segment.SegmentConfig;
import io.harness.event.listener.EventListener;
import io.harness.event.model.Event;
import io.harness.event.model.EventData;
import io.harness.event.model.EventType;
import io.harness.exception.WingsException;
import io.harness.lock.AcquiredLock;
import io.harness.lock.PersistentLocker;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import software.wings.beans.Account;
import software.wings.beans.User;
import software.wings.service.intfc.AccountService;
import software.wings.service.intfc.UserService;
import software.wings.utils.Validator;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
@Slf4j
public class SegmentHandler implements EventHandler {
  private SegmentConfig segmentConfig;
  @Inject private SegmentHelper segmentHelper;
  @Inject private AccountService accountService;
  @Inject private UserService userService;
  @Inject private Utils utils;
  @Inject private PersistentLocker persistentLocker;

  @UtilityClass
  public static final class Keys {
    public static final String NATERO = "Natero";
    public static final String SALESFORCE = "Salesforce";
    public static final String GROUP_ID = "groupId";
  }

  @Inject
  public SegmentHandler(SegmentConfig segmentConfig, EventListener eventListener) {
    this.segmentConfig = segmentConfig;
    if (segmentConfig != null) {
      // segment API url format take from https://segment.com/docs/sources/server/http/
      boolean validApiUrl = StringUtils.contains(segmentConfig.getUrl(), "https://api.segment.io");
      if (isSegmentEnabled() && validApiUrl) {
        registerEventHandlers(eventListener);
      }
    }
  }

  private void registerEventHandlers(EventListener eventListener) {
    eventListener.registerEventHandler(this,
        Sets.newHashSet(USER_INVITED_FROM_EXISTING_ACCOUNT, COMPLETE_USER_REGISTRATION, FIRST_DELEGATE_REGISTERED,
            FIRST_WORKFLOW_CREATED, FIRST_DEPLOYMENT_EXECUTED, FIRST_VERIFIED_DEPLOYMENT, FIRST_ROLLED_BACK_DEPLOYMENT,
            SETUP_CV_24X7, SETUP_2FA, SETUP_SSO, SETUP_IP_WHITELISTING, SETUP_RBAC, TRIAL_TO_PAID, TRIAL_TO_COMMUNITY,
            COMMUNITY_TO_PAID, COMMUNITY_TO_ESSENTIALS, ESSENTIALS_TO_PAID, PAID_TO_ESSENTIALS, TRIAL_TO_ESSENTIALS,
            NEW_TRIAL_SIGNUP, LICENSE_UPDATE, JOIN_ACCOUNT_REQUEST, CUSTOM, TECH_STACK));
  }

  public boolean isSegmentEnabled() {
    if (segmentConfig == null) {
      return false;
    }
    return segmentConfig.isEnabled();
  }

  @Override
  public void handleEvent(Event event) {
    if (event == null) {
      logger.error("Event is null");
      return;
    }
    EventType eventType = event.getEventType();
    if (eventType == null) {
      logger.error("Event type is null");
      return;
    }
    EventData eventData = event.getEventData();
    if (eventData == null) {
      logger.error("Event data is null");
      return;
    }
    Map<String, String> properties = eventData.getProperties();
    if (isEmpty(properties)) {
      logger.error("Event data properties are null");
      return;
    }

    boolean validApiUrl = StringUtils.contains(segmentConfig.getUrl(), "https://api.segment.io");
    if (!isSegmentEnabled() || !validApiUrl) {
      logger.info("Segment not enabled or incorrect URL. Config: {}", segmentConfig);
    }

    try {
      if (NEW_TRIAL_SIGNUP.equals(eventType) || JOIN_ACCOUNT_REQUEST.equals(eventType)) {
        String email = properties.get(EMAIL_ID);
        if (isEmpty(email)) {
          logger.error("User email is empty");
          return;
        }

        String identity = reportIdentity(properties.get(USER_NAME), email);
        if (isNotEmpty(identity)) {
          reportTrackEvent(eventType, Arrays.asList(identity));
        } else {
          logger.error("identity is null");
        }
        return;
      }

      String accountId = properties.get(ACCOUNT_ID);
      if (isEmpty(accountId)) {
        logger.error("Account is empty");
        return;
      }

      Account account = accountService.get(accountId);
      Validator.notNullCheck("Account is null for accountId:" + accountId, account);

      switch (eventType) {
        case COMMUNITY_TO_PAID:
        case COMMUNITY_TO_ESSENTIALS:
        case TRIAL_TO_COMMUNITY:
        case TRIAL_TO_PAID:
        case ESSENTIALS_TO_PAID:
        case PAID_TO_ESSENTIALS:
        case TRIAL_TO_ESSENTIALS:
        case FIRST_DELEGATE_REGISTERED:
          reportToAllUsers(account, eventType);
          return;
        case LICENSE_UPDATE:
          updateAllUsersInSegment(account);
          return;
        default:
          break;
      }

      User user = utils.getUser(properties);
      if (user == null) {
        return;
      }

      switch (eventType) {
        case COMPLETE_USER_REGISTRATION:
          // In case of sso based signup, we only send complete user registration.
          // But we have a special requirement for sending NEW_TRIAL_SIGNUP to segment for easier tracking.
          if (isNotEmpty(user.getOauthProvider())) {
            reportTrackEvent(account, EventType.NEW_TRIAL_SIGNUP.name(), user, null);
          }
          reportTrackEvent(account, eventType.name(), user, null);
          break;
        case CUSTOM:
          reportTrackEvent(account, properties.get(CUSTOM_EVENT_NAME), user, properties, null);
          break;
        case TECH_STACK:
          properties.put(ORIGINAL_TIMESTAMP_NAME, String.valueOf(System.currentTimeMillis()));
          reportTrackEvent(account, eventType.name(), user, properties, null);
          break;
        default:
          reportTrackEvent(account, eventType.name(), user, null);
          break;
      }

    } catch (Exception ex) {
      logger.error("Error while sending event to segment for event {}", eventType, ex);
    }
  }

  private void reportToAllUsers(Account account, EventType eventType) {
    List<User> usersOfAccount = userService.getUsersOfAccount(account.getUuid());
    if (isEmpty(usersOfAccount)) {
      return;
    }

    List<String> IdentityList =
        usersOfAccount.stream().map(user -> user.getSegmentIdentity()).collect(Collectors.toList());
    reportTrackEvent(eventType, IdentityList);
  }

  private void updateAllUsersInSegment(Account account) {
    List<User> usersOfAccount = userService.getUsersOfAccount(account.getUuid());
    if (isEmpty(usersOfAccount)) {
      return;
    }

    usersOfAccount.stream().filter(user -> isNotEmpty(user.getSegmentIdentity())).forEach(user -> {
      try {
        reportIdentity(account, user, false);
      } catch (URISyntaxException e) {
        logger.error("Error while updating license to all users in segment", e);
      }
    });
  }

  public String reportIdentity(Account account, User user, boolean wait) throws URISyntaxException {
    String userInviteUrl = utils.getUserInviteUrl(user.getEmail(), account);
    String identity = segmentHelper.createOrUpdateIdentity(
        user.getUuid(), user.getEmail(), user.getName(), account, userInviteUrl, user.getOauthProvider());
    if (!identity.equals(user.getSegmentIdentity())) {
      updateUserIdentity(user, identity);
    }

    if (wait) {
      // Sleeping for 10 secs just to give the marketo system time to update.
      // Marketo doesn't handle real-time changes very well.
      // If the track event is reported with the newly created identity,
      // marketo rejects the track request
      try {
        Thread.sleep(10000);
      } catch (InterruptedException ex) {
        logger.warn("Exception while waiting 10 seconds for segment to catchup");
      }
    }
    return identity;
  }

  public String reportIdentity(String userName, String email) throws IOException {
    return segmentHelper.createOrUpdateIdentity(null, email, userName, null, null, null);
  }

  private User updateUserEvents(User user, String event) {
    try (AcquiredLock lock =
             persistentLocker.waitToAcquireLock(user.getEmail(), Duration.ofMinutes(2), Duration.ofMinutes(4))) {
      User latestUser = userService.getUserFromCacheOrDB(user.getUuid());
      Set<String> reportedSegmentTracks = latestUser.getReportedSegmentTracks();
      if (reportedSegmentTracks == null) {
        reportedSegmentTracks = new HashSet<>();
      }
      reportedSegmentTracks.add(event);
      latestUser.setReportedSegmentTracks(reportedSegmentTracks);
      return userService.update(latestUser);
    }
  }

  @VisibleForTesting
  public User updateUserIdentity(User user, String segmentIdentity) {
    if (isEmpty(segmentIdentity)) {
      return user;
    }

    try (AcquiredLock lock =
             persistentLocker.waitToAcquireLock(user.getEmail(), Duration.ofMinutes(2), Duration.ofMinutes(4))) {
      User latestUser = userService.getUserFromCacheOrDB(user.getUuid());
      if (latestUser != null) {
        latestUser.setSegmentIdentity(segmentIdentity);
        return userService.update(latestUser);
      } else {
        throw new WingsException("Invalid user for the given id:" + user.getUuid(), USER);
      }
    }
  }

  public boolean reportTrackEvent(EventType eventType, List<String> identityList) {
    logger.info("Reporting track for event {} with leads {}", eventType, identityList);
    if (isEmpty(identityList)) {
      logger.error("No identities reported for event {}", eventType);
      return false;
    }
    Map<String, String> properties = new HashMap<>();
    properties.put("original_timestamp", String.valueOf(System.currentTimeMillis()));
    segmentHelper.reportTrackEvent(identityList, eventType.name(), properties);
    logger.info("Reported track for event {} with leads {}", eventType, identityList);
    return true;
  }

  public void reportTrackEvent(Account account, String event, User user, Map<String, Boolean> integrations)
      throws IOException, URISyntaxException {
    reportTrackEvent(account, event, user, null, integrations);
  }

  public void reportTrackEvent(Account account, String event, User user, Map<String, String> properties,
      Map<String, Boolean> integrations) throws URISyntaxException {
    String userId = user.getUuid();
    String identity = user.getSegmentIdentity();
    logger.info("Reporting track for event {} with lead {}", event, userId);
    if (isEmpty(identity) || !identity.equals(userId)) {
      identity = reportIdentity(account, user, true);
      if (isEmpty(identity)) {
        logger.error("Invalid identity id reported for user {}", userId);
        return;
      }

      // Getting the latest copy since we had a sleep of 10 seconds.
      user = userService.getUserFromCacheOrDB(userId);
    }

    if (properties == null) {
      properties = new HashMap<>();
      properties.put("original_timestamp", String.valueOf(System.currentTimeMillis()));
    }

    boolean reported = segmentHelper.reportTrackEvent(identity, event, properties, integrations);
    if (reported) {
      updateUserEvents(user, event);
    }
    logger.info("Reported track for event {} with lead {}", event, userId);
  }

  public void reportTrackEvent(Account account, String event, String userId, Map<String, String> properties,
      Map<String, Boolean> integrations) throws URISyntaxException {
    String identity;
    User user = null;
    if (isNotEmpty(userId)) {
      user = userService.get(userId);
      if (user == null) {
        logger.warn("User is null. Skipping reporting of track event {}", event);
        return;
      }
      identity = user.getSegmentIdentity();
      logger.info("Reporting track for event {} with lead {}", event, userId);
      if (isEmpty(identity) || !identity.equals(userId)) {
        identity = reportIdentity(account, user, true);
        if (isEmpty(identity)) {
          logger.error("Invalid identity id reported for user {}", userId);
          return;
        }

        // Getting the latest copy since we had a sleep of 10 seconds.
        user = userService.getUserFromCacheOrDB(userId);
      }
    } else {
      identity = "system-" + account.getUuid();
    }

    if (properties == null) {
      properties = new HashMap<>();
      properties.put("original_timestamp", String.valueOf(System.currentTimeMillis()));
    }

    boolean reported = segmentHelper.reportTrackEvent(identity, event, properties, integrations);
    if (user != null && reported) {
      updateUserEvents(user, event);
    }
    logger.info("Reported track for event {} with lead {}", event, identity);
  }
}
