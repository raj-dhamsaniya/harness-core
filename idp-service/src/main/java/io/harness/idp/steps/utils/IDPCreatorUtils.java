/*
 * Copyright 2021 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.idp.steps.utils;

import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;
import io.harness.idp.steps.Constants;

import com.google.common.collect.Sets;
import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
@OwnedBy(HarnessTeam.IDP)
public class IDPCreatorUtils {
  Set<String> supportedSteps =
      Sets.newHashSet("Run", "Plugin", "GitClone", "liteEngineTask", Constants.COOKIECUTTER, Constants.CREATE_REPO,
          Constants.DIRECT_PUSH, Constants.REGISTER_CATALOG, Constants.CREATE_CATALOG, Constants.SLACK_NOTIFY);
  public Set<String> getSupportedSteps() {
    return supportedSteps;
  }
}