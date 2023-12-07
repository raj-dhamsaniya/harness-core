/*
 * Copyright 2023 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.ssca.services.drift;

import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;
import io.harness.ssca.beans.drift.ComponentDriftResults;
import io.harness.ssca.beans.drift.ComponentDriftStatus;

import org.springframework.data.domain.Pageable;

@OwnedBy(HarnessTeam.SSCA)
public interface SbomDriftService {
  void calculateAndStoreComponentDrift(
      String accountId, String orgId, String projectId, String artifactId, String baseTag, String tag);

  ComponentDriftResults getComponentDriftsByArtifactId(String accountId, String orgId, String projectId,
      String artifactId, String baseTag, String tag, ComponentDriftStatus status, Pageable pageable);
}
