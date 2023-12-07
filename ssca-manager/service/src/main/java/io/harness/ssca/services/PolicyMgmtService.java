/*
 * Copyright 2023 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.ssca.services;

import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;
import io.harness.ssca.beans.OpaPolicyEvaluationResult;
import io.harness.ssca.entities.NormalizedSBOMComponentEntity;

import java.util.List;

@OwnedBy(HarnessTeam.SSCA)
public interface PolicyMgmtService {
  OpaPolicyEvaluationResult evaluate(String accountId, String orgIdentifier, String projectIdentifier,
      List<String> policySetRef, List<NormalizedSBOMComponentEntity> normalizedSBOMComponentEntities);
}
