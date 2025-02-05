/*
 * Copyright 2021 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.gitsync.functor;

import static io.harness.annotations.dev.HarnessTeam.DX;

import io.harness.annotations.dev.OwnedBy;
import io.harness.gitsync.beans.NGPersistentEntity;
import io.harness.gitsync.beans.YamlDTO;

@FunctionalInterface
@OwnedBy(DX)
public interface NgDtoToEntityFunctor<Y extends YamlDTO, E extends NGPersistentEntity> {
  E apply(Y yaml);
}
