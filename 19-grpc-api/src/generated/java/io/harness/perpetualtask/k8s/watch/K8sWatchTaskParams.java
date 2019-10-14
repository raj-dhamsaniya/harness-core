// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: io/harness/perpetualtask/k8s.watch/k8s_watch.proto

package io.harness.perpetualtask.k8s.watch;

/**
 * Protobuf type {@code io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams}
 */
@javax.annotation.Generated(value = "protoc", comments = "annotations:K8sWatchTaskParams.java.pb.meta")
public final class K8sWatchTaskParams extends com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams)
    K8sWatchTaskParamsOrBuilder {
  private static final long serialVersionUID = 0L;
  // Use K8sWatchTaskParams.newBuilder() to construct.
  private K8sWatchTaskParams(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private K8sWatchTaskParams() {
    cloudProviderId_ = "";
    k8SClusterConfig_ = com.google.protobuf.ByteString.EMPTY;
    k8SResourceKind_ = "";
  }

  @java.
  lang.Override
  public final com.google.protobuf.UnknownFieldSet getUnknownFields() {
    return this.unknownFields;
  }
  private K8sWatchTaskParams(
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
            java.lang.String s = input.readStringRequireUtf8();

            cloudProviderId_ = s;
            break;
          }
          case 18: {
            k8SClusterConfig_ = input.readBytes();
            break;
          }
          case 26: {
            java.lang.String s = input.readStringRequireUtf8();

            k8SResourceKind_ = s;
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
    return io.harness.perpetualtask.k8s.watch.K8SWatch
        .internal_static_io_harness_perpetualtask_k8s_watch_K8sWatchTaskParams_descriptor;
  }

  @java.
  lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
    return io.harness.perpetualtask.k8s.watch.K8SWatch
        .internal_static_io_harness_perpetualtask_k8s_watch_K8sWatchTaskParams_fieldAccessorTable
        .ensureFieldAccessorsInitialized(io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams.class,
            io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams.Builder.class);
  }

  public static final int CLOUD_PROVIDER_ID_FIELD_NUMBER = 1;
  private volatile java.lang.Object cloudProviderId_;
  /**
   * <code>string cloud_provider_id = 1;</code>
   */
  public java.lang.String getCloudProviderId() {
    java.lang.Object ref = cloudProviderId_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      cloudProviderId_ = s;
      return s;
    }
  }
  /**
   * <code>string cloud_provider_id = 1;</code>
   */
  public com.google.protobuf.ByteString getCloudProviderIdBytes() {
    java.lang.Object ref = cloudProviderId_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
      cloudProviderId_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int K8S_CLUSTER_CONFIG_FIELD_NUMBER = 2;
  private com.google.protobuf.ByteString k8SClusterConfig_;
  /**
   * <code>bytes k8s_cluster_config = 2;</code>
   */
  public com.google.protobuf.ByteString getK8SClusterConfig() {
    return k8SClusterConfig_;
  }

  public static final int K8S_RESOURCE_KIND_FIELD_NUMBER = 3;
  private volatile java.lang.Object k8SResourceKind_;
  /**
   * <code>string k8s_resource_kind = 3;</code>
   */
  public java.lang.String getK8SResourceKind() {
    java.lang.Object ref = k8SResourceKind_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      k8SResourceKind_ = s;
      return s;
    }
  }
  /**
   * <code>string k8s_resource_kind = 3;</code>
   */
  public com.google.protobuf.ByteString getK8SResourceKindBytes() {
    java.lang.Object ref = k8SResourceKind_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
      k8SResourceKind_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
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
    if (!getCloudProviderIdBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 1, cloudProviderId_);
    }
    if (!k8SClusterConfig_.isEmpty()) {
      output.writeBytes(2, k8SClusterConfig_);
    }
    if (!getK8SResourceKindBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 3, k8SResourceKind_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1)
      return size;

    size = 0;
    if (!getCloudProviderIdBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, cloudProviderId_);
    }
    if (!k8SClusterConfig_.isEmpty()) {
      size += com.google.protobuf.CodedOutputStream.computeBytesSize(2, k8SClusterConfig_);
    }
    if (!getK8SResourceKindBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, k8SResourceKind_);
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
    if (!(obj instanceof io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams)) {
      return super.equals(obj);
    }
    io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams other =
        (io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams) obj;

    if (!getCloudProviderId().equals(other.getCloudProviderId()))
      return false;
    if (!getK8SClusterConfig().equals(other.getK8SClusterConfig()))
      return false;
    if (!getK8SResourceKind().equals(other.getK8SResourceKind()))
      return false;
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
    hash = (37 * hash) + CLOUD_PROVIDER_ID_FIELD_NUMBER;
    hash = (53 * hash) + getCloudProviderId().hashCode();
    hash = (37 * hash) + K8S_CLUSTER_CONFIG_FIELD_NUMBER;
    hash = (53 * hash) + getK8SClusterConfig().hashCode();
    hash = (37 * hash) + K8S_RESOURCE_KIND_FIELD_NUMBER;
    hash = (53 * hash) + getK8SResourceKind().hashCode();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams parseFrom(java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams parseFrom(
      java.nio.ByteBuffer data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams parseFrom(com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams parseFrom(
      com.google.protobuf.ByteString data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams parseFrom(
      byte[] data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }
  public static io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams parseFrom(java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
  }
  public static io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams parseDelimitedFrom(java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams parseFrom(
      com.google.protobuf.CodedInputStream input) throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input);
  }
  public static io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams parseFrom(
      com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() {
    return newBuilder();
  }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams prototype) {
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
   * Protobuf type {@code io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams}
   */
  public static final class Builder extends com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams)
      io.harness.perpetualtask.k8s.watch.K8sWatchTaskParamsOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
      return io.harness.perpetualtask.k8s.watch.K8SWatch
          .internal_static_io_harness_perpetualtask_k8s_watch_K8sWatchTaskParams_descriptor;
    }

    @java.
    lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return io.harness.perpetualtask.k8s.watch.K8SWatch
          .internal_static_io_harness_perpetualtask_k8s_watch_K8sWatchTaskParams_fieldAccessorTable
          .ensureFieldAccessorsInitialized(io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams.class,
              io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams.Builder.class);
    }

    // Construct using io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams.newBuilder()
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
      cloudProviderId_ = "";

      k8SClusterConfig_ = com.google.protobuf.ByteString.EMPTY;

      k8SResourceKind_ = "";

      return this;
    }

    @java.
    lang.Override
    public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
      return io.harness.perpetualtask.k8s.watch.K8SWatch
          .internal_static_io_harness_perpetualtask_k8s_watch_K8sWatchTaskParams_descriptor;
    }

    @java.
    lang.Override
    public io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams getDefaultInstanceForType() {
      return io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams.getDefaultInstance();
    }

    @java.
    lang.Override
    public io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams build() {
      io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.
    lang.Override
    public io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams buildPartial() {
      io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams result =
          new io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams(this);
      result.cloudProviderId_ = cloudProviderId_;
      result.k8SClusterConfig_ = k8SClusterConfig_;
      result.k8SResourceKind_ = k8SResourceKind_;
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
      if (other instanceof io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams) {
        return mergeFrom((io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams) other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams other) {
      if (other == io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams.getDefaultInstance())
        return this;
      if (!other.getCloudProviderId().isEmpty()) {
        cloudProviderId_ = other.cloudProviderId_;
        onChanged();
      }
      if (other.getK8SClusterConfig() != com.google.protobuf.ByteString.EMPTY) {
        setK8SClusterConfig(other.getK8SClusterConfig());
      }
      if (!other.getK8SResourceKind().isEmpty()) {
        k8SResourceKind_ = other.k8SResourceKind_;
        onChanged();
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
      io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private java.lang.Object cloudProviderId_ = "";
    /**
     * <code>string cloud_provider_id = 1;</code>
     */
    public java.lang.String getCloudProviderId() {
      java.lang.Object ref = cloudProviderId_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        cloudProviderId_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string cloud_provider_id = 1;</code>
     */
    public com.google.protobuf.ByteString getCloudProviderIdBytes() {
      java.lang.Object ref = cloudProviderId_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
        cloudProviderId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string cloud_provider_id = 1;</code>
     */
    public Builder setCloudProviderId(java.lang.String value) {
      if (value == null) {
        throw new NullPointerException();
      }

      cloudProviderId_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string cloud_provider_id = 1;</code>
     */
    public Builder clearCloudProviderId() {
      cloudProviderId_ = getDefaultInstance().getCloudProviderId();
      onChanged();
      return this;
    }
    /**
     * <code>string cloud_provider_id = 1;</code>
     */
    public Builder setCloudProviderIdBytes(com.google.protobuf.ByteString value) {
      if (value == null) {
        throw new NullPointerException();
      }
      checkByteStringIsUtf8(value);

      cloudProviderId_ = value;
      onChanged();
      return this;
    }

    private com.google.protobuf.ByteString k8SClusterConfig_ = com.google.protobuf.ByteString.EMPTY;
    /**
     * <code>bytes k8s_cluster_config = 2;</code>
     */
    public com.google.protobuf.ByteString getK8SClusterConfig() {
      return k8SClusterConfig_;
    }
    /**
     * <code>bytes k8s_cluster_config = 2;</code>
     */
    public Builder setK8SClusterConfig(com.google.protobuf.ByteString value) {
      if (value == null) {
        throw new NullPointerException();
      }

      k8SClusterConfig_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>bytes k8s_cluster_config = 2;</code>
     */
    public Builder clearK8SClusterConfig() {
      k8SClusterConfig_ = getDefaultInstance().getK8SClusterConfig();
      onChanged();
      return this;
    }

    private java.lang.Object k8SResourceKind_ = "";
    /**
     * <code>string k8s_resource_kind = 3;</code>
     */
    public java.lang.String getK8SResourceKind() {
      java.lang.Object ref = k8SResourceKind_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        k8SResourceKind_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string k8s_resource_kind = 3;</code>
     */
    public com.google.protobuf.ByteString getK8SResourceKindBytes() {
      java.lang.Object ref = k8SResourceKind_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
        k8SResourceKind_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string k8s_resource_kind = 3;</code>
     */
    public Builder setK8SResourceKind(java.lang.String value) {
      if (value == null) {
        throw new NullPointerException();
      }

      k8SResourceKind_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string k8s_resource_kind = 3;</code>
     */
    public Builder clearK8SResourceKind() {
      k8SResourceKind_ = getDefaultInstance().getK8SResourceKind();
      onChanged();
      return this;
    }
    /**
     * <code>string k8s_resource_kind = 3;</code>
     */
    public Builder setK8SResourceKindBytes(com.google.protobuf.ByteString value) {
      if (value == null) {
        throw new NullPointerException();
      }
      checkByteStringIsUtf8(value);

      k8SResourceKind_ = value;
      onChanged();
      return this;
    }
    @java.lang.Override
    public final Builder setUnknownFields(final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }

    // @@protoc_insertion_point(builder_scope:io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams)
  }

  // @@protoc_insertion_point(class_scope:io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams)
  private static final io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams();
  }

  public static io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<K8sWatchTaskParams> PARSER =
      new com.google.protobuf.AbstractParser<K8sWatchTaskParams>() {
        @java.lang.Override
        public K8sWatchTaskParams parsePartialFrom(
            com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
          return new K8sWatchTaskParams(input, extensionRegistry);
        }
      };

  public static com.google.protobuf.Parser<K8sWatchTaskParams> parser() {
    return PARSER;
  }

  @java.
  lang.Override
  public com.google.protobuf.Parser<K8sWatchTaskParams> getParserForType() {
    return PARSER;
  }

  @java.
  lang.Override
  public io.harness.perpetualtask.k8s.watch.K8sWatchTaskParams getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }
}
