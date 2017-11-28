package software.wings.app;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static software.wings.common.Constants.DELEGATE_SYNC_CACHE;
import static software.wings.waitnotify.ErrorNotifyResponseData.Builder.anErrorNotifyResponseData;

import org.atmosphere.cpr.BroadcasterFactory;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.wings.beans.DelegateTask;
import software.wings.beans.DelegateTask.Status;
import software.wings.dl.WingsPersistence;
import software.wings.lock.PersistentLocker;
import software.wings.utils.CacheHelper;
import software.wings.waitnotify.WaitNotifyEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.cache.Cache;
import javax.cache.Caching;
import javax.inject.Inject;

/**
 * Scheduled Task to look for finished WaitInstances and send messages to NotifyEventQueue.
 *
 * @author Rishi
 */
public class DelegateQueueTask implements Runnable {
  @Inject private WingsPersistence wingsPersistence;

  @Inject private PersistentLocker persistentLocker;

  @Inject private BroadcasterFactory broadcasterFactory;

  @Inject private WaitNotifyEngine waitNotifyEngine;

  @Inject private CacheHelper cacheHelper;

  private final Logger logger = LoggerFactory.getLogger(getClass());

  /* (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    boolean lockAcquired = false;
    try {
      lockAcquired = persistentLocker.acquireLock(DelegateQueueTask.class, DelegateQueueTask.class.getName());
      if (!lockAcquired) {
        log().warn("Persistent lock could not be acquired for the DelegateQueue");
        return;
      }

      // Release tasks acquired by delegate but not started execution. Introduce "ACQUIRED" status may be ?
      Query<DelegateTask> releaseLongQueuedTasks =
          wingsPersistence.createQuery(DelegateTask.class)
              .field("status")
              .equal(Status.QUEUED)
              .field("delegateId")
              .exists()
              .field("lastUpdatedAt")
              .lessThan(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(5));

      wingsPersistence.update(
          releaseLongQueuedTasks, wingsPersistence.createUpdateOperations(DelegateTask.class).unset("delegateId"));

      // Find tasks which are timed out and update their status to FAILED.
      List<DelegateTask> longRunningTimedOutTasks = new ArrayList<>();
      try {
        longRunningTimedOutTasks = wingsPersistence.createQuery(DelegateTask.class)
                                       .field("status")
                                       .equal(Status.STARTED)
                                       .asList()
                                       .stream()
                                       .filter(DelegateTask::isTimedOut)
                                       .collect(Collectors.toList());
      } catch (com.esotericsoftware.kryo.KryoException kryo) {
        logger.warn("Delegate task schema backwards incompatibilty", kryo);
        for (Key<DelegateTask> key :
            wingsPersistence.createQuery(DelegateTask.class).field("status").equal(Status.STARTED).asKeyList()) {
          try {
            wingsPersistence.get(DelegateTask.class, key.getId().toString());
          } catch (com.esotericsoftware.kryo.KryoException ex) {
            wingsPersistence.delete(DelegateTask.class, key.getId().toString());
          }
        }
      }

      if (longRunningTimedOutTasks.size() > 0) {
        logger.info("Found {} long running tasks, to be killed", longRunningTimedOutTasks.size());
        longRunningTimedOutTasks.forEach(delegateTask -> {

          Query<DelegateTask> updateQuery = wingsPersistence.createQuery(DelegateTask.class)
                                                .field("status")
                                                .equal(Status.STARTED)
                                                .field(Mapper.ID_KEY)
                                                .equal(delegateTask.getUuid());
          UpdateOperations<DelegateTask> updateOperations =
              wingsPersistence.createUpdateOperations(DelegateTask.class).set("status", Status.ERROR);

          DelegateTask updatedDelegateTask =
              wingsPersistence.getDatastore().findAndModify(updateQuery, updateOperations);

          if (updatedDelegateTask != null) {
            logger.info("Long running delegate task [{}] is terminated", updatedDelegateTask.getUuid());
            waitNotifyEngine.notify(updatedDelegateTask.getWaitId(),
                anErrorNotifyResponseData()
                    .withErrorMessage("Delegate timeout. Delegate ID: " + updatedDelegateTask.getDelegateId())
                    .build());
          } else {
            logger.error("Delegate task [{}] could not be updated", delegateTask.getUuid());
            // more error handling here.
          }
        });
      }
      // Re-broadcast queued sync tasks not picked up by any Delegate
      Cache<String, DelegateTask> delegateSyncCache =
          cacheHelper.getCache(DELEGATE_SYNC_CACHE, String.class, DelegateTask.class);
      try {
        delegateSyncCache.forEach(stringDelegateTaskEntry -> {
          try {
            DelegateTask syncDelegateTask = stringDelegateTaskEntry.getValue();
            if (syncDelegateTask.getStatus().equals(Status.QUEUED) && syncDelegateTask.getDelegateId() == null) {
              logger.info("Re-broadcast queued sync task [{}]", syncDelegateTask.getUuid());
              broadcasterFactory.lookup("/stream/delegate/" + syncDelegateTask.getAccountId(), true)
                  .broadcast(syncDelegateTask);
            }
          } catch (Exception ex) {
            logger.error("Could not fetch delegate task from queue ", ex);
            logger.warn("Remove Delegate task [{}] from cache", stringDelegateTaskEntry.getKey());
            Caching.getCache(DELEGATE_SYNC_CACHE, String.class, DelegateTask.class)
                .remove(stringDelegateTaskEntry.getKey());
          }
        });
      } catch (Exception e) {
        delegateSyncCache.clear();
      }

      List<DelegateTask> unassignedTasks = wingsPersistence.createQuery(DelegateTask.class)
                                               .field("status")
                                               .equal(DelegateTask.Status.QUEUED)
                                               .field("delegateId")
                                               .doesNotExist()
                                               .asList();

      if (isNotEmpty(unassignedTasks)) {
        unassignedTasks.forEach(delegateTask -> {
          logger.info("Re-broadcast queued async task [{}]", delegateTask.getUuid());
          broadcasterFactory.lookup("/stream/delegate/" + delegateTask.getAccountId(), true).broadcast(delegateTask);
        });
      }

    } catch (Exception exception) {
      log().error("Error seen in the Notifier call", exception);
    } finally {
      if (lockAcquired) {
        persistentLocker.releaseLock(DelegateQueueTask.class, DelegateQueueTask.class.getName());
      }
    }
  }

  private Logger log() {
    return LoggerFactory.getLogger(getClass());
  }
}
