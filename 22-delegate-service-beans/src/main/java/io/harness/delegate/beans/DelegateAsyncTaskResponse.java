package io.harness.delegate.beans;

import io.harness.mongo.index.FdTtlIndex;
import io.harness.persistence.PersistentEntity;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.FieldNameConstants;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.time.OffsetDateTime;
import java.util.Date;

@Value
@Builder
@Entity(value = "!!!custom_delegateAsyncTaskResponses", noClassnameStored = true)
@FieldNameConstants(innerTypeName = "DelegateAsyncTaskResponseKeys")
public class DelegateAsyncTaskResponse implements PersistentEntity {
  @Id private String uuid;
  private byte[] responseData;
  private long lastProcessingAttempt;

  @FdTtlIndex @Builder.Default private Date validUntil = Date.from(OffsetDateTime.now().plusHours(2).toInstant());
}
