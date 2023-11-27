/*
 * Copyright 2021 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Shield 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/06/PolyForm-Shield-1.0.0.txt.
 */

package io.harness.ng.core;

import static io.harness.annotations.dev.HarnessTeam.PL;

import io.harness.annotations.dev.OwnedBy;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.TypeAlias;

@OwnedBy(PL)
@Getter
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("org")
@TypeAlias("OrgScope")
public class OrgScope extends ResourceScope {
  @NotEmpty String accountIdentifier;
  @NotEmpty String orgIdentifier;

  public OrgScope(String accountIdentifier, String orgIdentifier) {
    super("org", accountIdentifier);
    this.accountIdentifier = accountIdentifier;
    this.orgIdentifier = orgIdentifier;
  }
}
