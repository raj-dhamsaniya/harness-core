// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: io/harness/event/payloads/ecs_messages.proto

package io.harness.event.payloads;

/**
 * Protobuf type {@code io.harness.event.payloads.EcsTaskInfo}
 */
@javax.annotation.Generated(value = "protoc", comments = "annotations:EcsTaskInfo.java.pb.meta")
public final class EcsTaskInfo extends com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:io.harness.event.payloads.EcsTaskInfo)
    EcsTaskInfoOrBuilder {
  private static final long serialVersionUID = 0L;
  // Use EcsTaskInfo.newBuilder() to construct.
  private EcsTaskInfo(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private EcsTaskInfo() {}

  @java.
  lang.Override
  public final com.google.protobuf.UnknownFieldSet getUnknownFields() {
    return this.unknownFields;
  }
  private EcsTaskInfo(
      com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    int mutable_bitField0_ = 0;
    com.google.protobuf.UnknownFieldSet.Builder unknownFields = com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 10: {
            io.harness.event.payloads.EcsTaskDescription.Builder subBuilder = null;
            if (ecsTaskDescription_ != null) {
              subBuilder = ecsTaskDescription_.toBuilder();
            }
            ecsTaskDescription_ =
                input.readMessage(io.harness.event.payloads.EcsTaskDescription.parser(), extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom(ecsTaskDescription_);
              ecsTaskDescription_ = subBuilder.buildPartial();
            }

            break;
          }
          case 18: {
            io.harness.event.payloads.ReservedResource.Builder subBuilder = null;
            if (ecsTaskResource_ != null) {
              subBuilder = ecsTaskResource_.toBuilder();
            }
            ecsTaskResource_ =
                input.readMessage(io.harness.event.payloads.ReservedResource.parser(), extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom(ecsTaskResource_);
              ecsTaskResource_ = subBuilder.buildPartial();
            }

            break;
          }
          default: {
            if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
    return io.harness.event.payloads.EcsMessages.internal_static_io_harness_event_payloads_EcsTaskInfo_descriptor;
  }

  @java.
  lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
    return io.harness.event.payloads.EcsMessages
        .internal_static_io_harness_event_payloads_EcsTaskInfo_fieldAccessorTable.ensureFieldAccessorsInitialized(
            io.harness.event.payloads.EcsTaskInfo.class, io.harness.event.payloads.EcsTaskInfo.Builder.class);
  }

  public static final int ECS_TASK_DESCRIPTION_FIELD_NUMBER = 1;
  private io.harness.event.payloads.EcsTaskDescription ecsTaskDescription_;
  /**
   * <code>.io.harness.event.payloads.EcsTaskDescription ecs_task_description = 1;</code>
   */
  public boolean hasEcsTaskDescription() {
    return ecsTaskDescription_ != null;
  }
  /**
   * <code>.io.harness.event.payloads.EcsTaskDescription ecs_task_description = 1;</code>
   */
  public io.harness.event.payloads.EcsTaskDescription getEcsTaskDescription() {
    return ecsTaskDescription_ == null ? io.harness.event.payloads.EcsTaskDescription.getDefaultInstance()
                                       : ecsTaskDescription_;
  }
  /**
   * <code>.io.harness.event.payloads.EcsTaskDescription ecs_task_description = 1;</code>
   */
  public io.harness.event.payloads.EcsTaskDescriptionOrBuilder getEcsTaskDescriptionOrBuilder() {
    return getEcsTaskDescription();
  }

  public static final int ECS_TASK_RESOURCE_FIELD_NUMBER = 2;
  private io.harness.event.payloads.ReservedResource ecsTaskResource_;
  /**
   * <code>.io.harness.event.payloads.ReservedResource ecs_task_resource = 2;</code>
   */
  public boolean hasEcsTaskResource() {
    return ecsTaskResource_ != null;
  }
  /**
   * <code>.io.harness.event.payloads.ReservedResource ecs_task_resource = 2;</code>
   */
  public io.harness.event.payloads.ReservedResource getEcsTaskResource() {
    return ecsTaskResource_ == null ? io.harness.event.payloads.ReservedResource.getDefaultInstance()
                                    : ecsTaskResource_;
  }
  /**
   * <code>.io.harness.event.payloads.ReservedResource ecs_task_resource = 2;</code>
   */
  public io.harness.event.payloads.ReservedResourceOrBuilder getEcsTaskResourceOrBuilder() {
    return getEcsTaskResource();
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1)
      return true;
    if (isInitialized == 0)
      return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output) throws java.io.IOException {
    if (ecsTaskDescription_ != null) {
      output.writeMessage(1, getEcsTaskDescription());
    }
    if (ecsTaskResource_ != null) {
      output.writeMessage(2, getEcsTaskResource());
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1)
      return size;

    size = 0;
    if (ecsTaskDescription_ != null) {
      size += com.google.protobuf.CodedOutputStream.computeMessageSize(1, getEcsTaskDescription());
    }
    if (ecsTaskResource_ != null) {
      size += com.google.protobuf.CodedOutputStream.computeMessageSize(2, getEcsTaskResource());
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof io.harness.event.payloads.EcsTaskInfo)) {
      return super.equals(obj);
    }
    io.harness.event.payloads.EcsTaskInfo other = (io.harness.event.payloads.EcsTaskInfo) obj;

    if (hasEcsTaskDescription() != other.hasEcsTaskDescription())
      return false;
    if (hasEcsTaskDescription()) {
      if (!getEcsTaskDescription().equals(other.getEcsTaskDescription()))
        return false;
    }
    if (hasEcsTaskResource() != other.hasEcsTaskResource())
      return false;
    if (hasEcsTaskResource()) {
      if (!getEcsTaskResource().equals(other.getEcsTaskResource()))
        return false;
    }
    if (!unknownFields.equals(other.unknownFields))
      return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    if (hasEcsTaskDescription()) {
      hash = (37 * hash) + ECS_TASK_DESCRIPTION_FIELD_NUMBER;
      hash = (53 * hash) + getEcsTaskDescription().hashCode();
    }
    if (hasEcsTaskResource()) {
      hash = (37 * hash) + ECS_TASK_RESOURCE_FIELD_NUMBER;
      hash = (53 * hash) + getEcsTaskResource().hashCode();
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static io.harness.event.payloads.EcsTaskInfo parseFrom(java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static io.harness.event.payloads.EcsTaskInfo parseFrom(
      java.nio.ByteBuffer data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static io.harness.event.payloads.EcsTaskInfo parseFrom(com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static io.harness.event.payloads.EcsTaskInfo parseFrom(
      com.google.protobuf.ByteString data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static io.harness.event.payloads.EcsTaskInfo parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static io.harness.event.payloads.EcsTaskInfo parseFrom(
      byte[] data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static io.harness.event.payloads.EcsTaskInfo parseFrom(java.io.InputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }
  public static io.harness.event.payloads.EcsTaskInfo parseFrom(java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static io.harness.event.payloads.EcsTaskInfo parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
  }
  public static io.harness.event.payloads.EcsTaskInfo parseDelimitedFrom(java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static io.harness.event.payloads.EcsTaskInfo parseFrom(com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }
  public static io.harness.event.payloads.EcsTaskInfo parseFrom(com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() {
    return newBuilder();
  }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(io.harness.event.payloads.EcsTaskInfo prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code io.harness.event.payloads.EcsTaskInfo}
   */
  public static final class Builder extends com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:io.harness.event.payloads.EcsTaskInfo)
      io.harness.event.payloads.EcsTaskInfoOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
      return io.harness.event.payloads.EcsMessages.internal_static_io_harness_event_payloads_EcsTaskInfo_descriptor;
    }

    @java.
    lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return io.harness.event.payloads.EcsMessages
          .internal_static_io_harness_event_payloads_EcsTaskInfo_fieldAccessorTable.ensureFieldAccessorsInitialized(
              io.harness.event.payloads.EcsTaskInfo.class, io.harness.event.payloads.EcsTaskInfo.Builder.class);
    }

    // Construct using io.harness.event.payloads.EcsTaskInfo.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders) {
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      if (ecsTaskDescriptionBuilder_ == null) {
        ecsTaskDescription_ = null;
      } else {
        ecsTaskDescription_ = null;
        ecsTaskDescriptionBuilder_ = null;
      }
      if (ecsTaskResourceBuilder_ == null) {
        ecsTaskResource_ = null;
      } else {
        ecsTaskResource_ = null;
        ecsTaskResourceBuilder_ = null;
      }
      return this;
    }

    @java.
    lang.Override
    public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
      return io.harness.event.payloads.EcsMessages.internal_static_io_harness_event_payloads_EcsTaskInfo_descriptor;
    }

    @java.
    lang.Override
    public io.harness.event.payloads.EcsTaskInfo getDefaultInstanceForType() {
      return io.harness.event.payloads.EcsTaskInfo.getDefaultInstance();
    }

    @java.
    lang.Override
    public io.harness.event.payloads.EcsTaskInfo build() {
      io.harness.event.payloads.EcsTaskInfo result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.
    lang.Override
    public io.harness.event.payloads.EcsTaskInfo buildPartial() {
      io.harness.event.payloads.EcsTaskInfo result = new io.harness.event.payloads.EcsTaskInfo(this);
      if (ecsTaskDescriptionBuilder_ == null) {
        result.ecsTaskDescription_ = ecsTaskDescription_;
      } else {
        result.ecsTaskDescription_ = ecsTaskDescriptionBuilder_.build();
      }
      if (ecsTaskResourceBuilder_ == null) {
        result.ecsTaskResource_ = ecsTaskResource_;
      } else {
        result.ecsTaskResource_ = ecsTaskResourceBuilder_.build();
      }
      onBuilt();
      return result;
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }
    @java.lang.Override
    public Builder setField(com.google.protobuf.Descriptors.FieldDescriptor field, java.lang.Object value) {
      return super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field, int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(com.google.protobuf.Descriptors.FieldDescriptor field, java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof io.harness.event.payloads.EcsTaskInfo) {
        return mergeFrom((io.harness.event.payloads.EcsTaskInfo) other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(io.harness.event.payloads.EcsTaskInfo other) {
      if (other == io.harness.event.payloads.EcsTaskInfo.getDefaultInstance())
        return this;
      if (other.hasEcsTaskDescription()) {
        mergeEcsTaskDescription(other.getEcsTaskDescription());
      }
      if (other.hasEcsTaskResource()) {
        mergeEcsTaskResource(other.getEcsTaskResource());
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
      io.harness.event.payloads.EcsTaskInfo parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (io.harness.event.payloads.EcsTaskInfo) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private io.harness.event.payloads.EcsTaskDescription ecsTaskDescription_;
    private com.google.protobuf.SingleFieldBuilderV3<io.harness.event.payloads.EcsTaskDescription,
        io.harness.event.payloads.EcsTaskDescription.Builder, io.harness.event.payloads.EcsTaskDescriptionOrBuilder>
        ecsTaskDescriptionBuilder_;
    /**
     * <code>.io.harness.event.payloads.EcsTaskDescription ecs_task_description = 1;</code>
     */
    public boolean hasEcsTaskDescription() {
      return ecsTaskDescriptionBuilder_ != null || ecsTaskDescription_ != null;
    }
    /**
     * <code>.io.harness.event.payloads.EcsTaskDescription ecs_task_description = 1;</code>
     */
    public io.harness.event.payloads.EcsTaskDescription getEcsTaskDescription() {
      if (ecsTaskDescriptionBuilder_ == null) {
        return ecsTaskDescription_ == null ? io.harness.event.payloads.EcsTaskDescription.getDefaultInstance()
                                           : ecsTaskDescription_;
      } else {
        return ecsTaskDescriptionBuilder_.getMessage();
      }
    }
    /**
     * <code>.io.harness.event.payloads.EcsTaskDescription ecs_task_description = 1;</code>
     */
    public Builder setEcsTaskDescription(io.harness.event.payloads.EcsTaskDescription value) {
      if (ecsTaskDescriptionBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ecsTaskDescription_ = value;
        onChanged();
      } else {
        ecsTaskDescriptionBuilder_.setMessage(value);
      }

      return this;
    }
    /**
     * <code>.io.harness.event.payloads.EcsTaskDescription ecs_task_description = 1;</code>
     */
    public Builder setEcsTaskDescription(io.harness.event.payloads.EcsTaskDescription.Builder builderForValue) {
      if (ecsTaskDescriptionBuilder_ == null) {
        ecsTaskDescription_ = builderForValue.build();
        onChanged();
      } else {
        ecsTaskDescriptionBuilder_.setMessage(builderForValue.build());
      }

      return this;
    }
    /**
     * <code>.io.harness.event.payloads.EcsTaskDescription ecs_task_description = 1;</code>
     */
    public Builder mergeEcsTaskDescription(io.harness.event.payloads.EcsTaskDescription value) {
      if (ecsTaskDescriptionBuilder_ == null) {
        if (ecsTaskDescription_ != null) {
          ecsTaskDescription_ = io.harness.event.payloads.EcsTaskDescription.newBuilder(ecsTaskDescription_)
                                    .mergeFrom(value)
                                    .buildPartial();
        } else {
          ecsTaskDescription_ = value;
        }
        onChanged();
      } else {
        ecsTaskDescriptionBuilder_.mergeFrom(value);
      }

      return this;
    }
    /**
     * <code>.io.harness.event.payloads.EcsTaskDescription ecs_task_description = 1;</code>
     */
    public Builder clearEcsTaskDescription() {
      if (ecsTaskDescriptionBuilder_ == null) {
        ecsTaskDescription_ = null;
        onChanged();
      } else {
        ecsTaskDescription_ = null;
        ecsTaskDescriptionBuilder_ = null;
      }

      return this;
    }
    /**
     * <code>.io.harness.event.payloads.EcsTaskDescription ecs_task_description = 1;</code>
     */
    public io.harness.event.payloads.EcsTaskDescription.Builder getEcsTaskDescriptionBuilder() {
      onChanged();
      return getEcsTaskDescriptionFieldBuilder().getBuilder();
    }
    /**
     * <code>.io.harness.event.payloads.EcsTaskDescription ecs_task_description = 1;</code>
     */
    public io.harness.event.payloads.EcsTaskDescriptionOrBuilder getEcsTaskDescriptionOrBuilder() {
      if (ecsTaskDescriptionBuilder_ != null) {
        return ecsTaskDescriptionBuilder_.getMessageOrBuilder();
      } else {
        return ecsTaskDescription_ == null ? io.harness.event.payloads.EcsTaskDescription.getDefaultInstance()
                                           : ecsTaskDescription_;
      }
    }
    /**
     * <code>.io.harness.event.payloads.EcsTaskDescription ecs_task_description = 1;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<io.harness.event.payloads.EcsTaskDescription,
        io.harness.event.payloads.EcsTaskDescription.Builder, io.harness.event.payloads.EcsTaskDescriptionOrBuilder>
    getEcsTaskDescriptionFieldBuilder() {
      if (ecsTaskDescriptionBuilder_ == null) {
        ecsTaskDescriptionBuilder_ =
            new com.google.protobuf.SingleFieldBuilderV3<io.harness.event.payloads.EcsTaskDescription,
                io.harness.event.payloads.EcsTaskDescription.Builder,
                io.harness.event.payloads.EcsTaskDescriptionOrBuilder>(
                getEcsTaskDescription(), getParentForChildren(), isClean());
        ecsTaskDescription_ = null;
      }
      return ecsTaskDescriptionBuilder_;
    }

    private io.harness.event.payloads.ReservedResource ecsTaskResource_;
    private com.google.protobuf.SingleFieldBuilderV3<io.harness.event.payloads.ReservedResource,
        io.harness.event.payloads.ReservedResource.Builder, io.harness.event.payloads.ReservedResourceOrBuilder>
        ecsTaskResourceBuilder_;
    /**
     * <code>.io.harness.event.payloads.ReservedResource ecs_task_resource = 2;</code>
     */
    public boolean hasEcsTaskResource() {
      return ecsTaskResourceBuilder_ != null || ecsTaskResource_ != null;
    }
    /**
     * <code>.io.harness.event.payloads.ReservedResource ecs_task_resource = 2;</code>
     */
    public io.harness.event.payloads.ReservedResource getEcsTaskResource() {
      if (ecsTaskResourceBuilder_ == null) {
        return ecsTaskResource_ == null ? io.harness.event.payloads.ReservedResource.getDefaultInstance()
                                        : ecsTaskResource_;
      } else {
        return ecsTaskResourceBuilder_.getMessage();
      }
    }
    /**
     * <code>.io.harness.event.payloads.ReservedResource ecs_task_resource = 2;</code>
     */
    public Builder setEcsTaskResource(io.harness.event.payloads.ReservedResource value) {
      if (ecsTaskResourceBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ecsTaskResource_ = value;
        onChanged();
      } else {
        ecsTaskResourceBuilder_.setMessage(value);
      }

      return this;
    }
    /**
     * <code>.io.harness.event.payloads.ReservedResource ecs_task_resource = 2;</code>
     */
    public Builder setEcsTaskResource(io.harness.event.payloads.ReservedResource.Builder builderForValue) {
      if (ecsTaskResourceBuilder_ == null) {
        ecsTaskResource_ = builderForValue.build();
        onChanged();
      } else {
        ecsTaskResourceBuilder_.setMessage(builderForValue.build());
      }

      return this;
    }
    /**
     * <code>.io.harness.event.payloads.ReservedResource ecs_task_resource = 2;</code>
     */
    public Builder mergeEcsTaskResource(io.harness.event.payloads.ReservedResource value) {
      if (ecsTaskResourceBuilder_ == null) {
        if (ecsTaskResource_ != null) {
          ecsTaskResource_ =
              io.harness.event.payloads.ReservedResource.newBuilder(ecsTaskResource_).mergeFrom(value).buildPartial();
        } else {
          ecsTaskResource_ = value;
        }
        onChanged();
      } else {
        ecsTaskResourceBuilder_.mergeFrom(value);
      }

      return this;
    }
    /**
     * <code>.io.harness.event.payloads.ReservedResource ecs_task_resource = 2;</code>
     */
    public Builder clearEcsTaskResource() {
      if (ecsTaskResourceBuilder_ == null) {
        ecsTaskResource_ = null;
        onChanged();
      } else {
        ecsTaskResource_ = null;
        ecsTaskResourceBuilder_ = null;
      }

      return this;
    }
    /**
     * <code>.io.harness.event.payloads.ReservedResource ecs_task_resource = 2;</code>
     */
    public io.harness.event.payloads.ReservedResource.Builder getEcsTaskResourceBuilder() {
      onChanged();
      return getEcsTaskResourceFieldBuilder().getBuilder();
    }
    /**
     * <code>.io.harness.event.payloads.ReservedResource ecs_task_resource = 2;</code>
     */
    public io.harness.event.payloads.ReservedResourceOrBuilder getEcsTaskResourceOrBuilder() {
      if (ecsTaskResourceBuilder_ != null) {
        return ecsTaskResourceBuilder_.getMessageOrBuilder();
      } else {
        return ecsTaskResource_ == null ? io.harness.event.payloads.ReservedResource.getDefaultInstance()
                                        : ecsTaskResource_;
      }
    }
    /**
     * <code>.io.harness.event.payloads.ReservedResource ecs_task_resource = 2;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<io.harness.event.payloads.ReservedResource,
        io.harness.event.payloads.ReservedResource.Builder, io.harness.event.payloads.ReservedResourceOrBuilder>
    getEcsTaskResourceFieldBuilder() {
      if (ecsTaskResourceBuilder_ == null) {
        ecsTaskResourceBuilder_ =
            new com.google.protobuf.SingleFieldBuilderV3<io.harness.event.payloads.ReservedResource,
                io.harness.event.payloads.ReservedResource.Builder,
                io.harness.event.payloads.ReservedResourceOrBuilder>(
                getEcsTaskResource(), getParentForChildren(), isClean());
        ecsTaskResource_ = null;
      }
      return ecsTaskResourceBuilder_;
    }
    @java.lang.Override
    public final Builder setUnknownFields(final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }

    // @@protoc_insertion_point(builder_scope:io.harness.event.payloads.EcsTaskInfo)
  }

  // @@protoc_insertion_point(class_scope:io.harness.event.payloads.EcsTaskInfo)
  private static final io.harness.event.payloads.EcsTaskInfo DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new io.harness.event.payloads.EcsTaskInfo();
  }

  public static io.harness.event.payloads.EcsTaskInfo getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<EcsTaskInfo> PARSER =
      new com.google.protobuf.AbstractParser<EcsTaskInfo>() {
        @java.lang.Override
        public EcsTaskInfo parsePartialFrom(
            com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
          return new EcsTaskInfo(input, extensionRegistry);
        }
      };

  public static com.google.protobuf.Parser<EcsTaskInfo> parser() {
    return PARSER;
  }

  @java.
  lang.Override
  public com.google.protobuf.Parser<EcsTaskInfo> getParserForType() {
    return PARSER;
  }

  @java.
  lang.Override
  public io.harness.event.payloads.EcsTaskInfo getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }
}
