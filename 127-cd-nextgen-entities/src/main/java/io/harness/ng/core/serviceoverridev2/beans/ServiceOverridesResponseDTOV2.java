/*
 * Copyright 2023 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.ng.core.serviceoverridev2.beans;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import io.harness.annotations.dev.CodePulse;
import io.harness.annotations.dev.HarnessModuleComponent;
import io.harness.annotations.dev.ProductModule;
import io.harness.gitsync.beans.StoreType;
import io.harness.gitx.EntityGitInfo;
import io.harness.ng.core.template.CacheResponseMetadataDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@CodePulse(module = ProductModule.CDS, unitCoverageRequired = false,
    components = {HarnessModuleComponent.CDS_SERVICE_ENVIRONMENT})
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(NON_NULL)
@Schema(
    name = "ServiceOverrideResponseV2", description = "This is the Service Override Response entity defined in Harness")

public class ServiceOverridesResponseDTOV2 {
  @NonNull String identifier;
  String accountId;
  String orgIdentifier;
  String projectIdentifier;
  @NonNull String environmentRef;
  String serviceRef;
  String infraIdentifier;
  String clusterIdentifier;
  @NonNull ServiceOverridesType type;
  @NonNull ServiceOverridesSpec spec;
  boolean isNewlyCreated;
  String yamlInternal;
  // this is for onboarding override v2 yaml version
  String yaml;

  @Schema(hidden = true) EntityGitInfo entityGitInfo;
  @Schema(hidden = true) String connectorRef;
  @Schema(hidden = true) StoreType storeType;
  @Schema(hidden = true) String fallbackBranch;
  @Schema(hidden = true) CacheResponseMetadataDTO cacheResponseMetadataDTO;
}
