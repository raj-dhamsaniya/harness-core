/*
 * Copyright 2020 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Shield 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/06/PolyForm-Shield-1.0.0.txt.
 */

package io.harness.delegate.task.k8s;

import io.harness.annotation.RecasterAlias;
import io.harness.annotations.dev.CodePulse;
import io.harness.annotations.dev.HarnessModuleComponent;
import io.harness.annotations.dev.ProductModule;
import io.harness.delegate.beans.connector.k8Connector.KubernetesClusterConfigDTO;
import io.harness.security.encryption.EncryptedDataDetail;
import io.harness.taskcontext.infra.DirectK8sInfraContext;
import io.harness.taskcontext.infra.InfraContext;

import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@RecasterAlias("io.harness.delegate.task.k8s.DirectK8sInfraDelegateConfig")
@CodePulse(module = ProductModule.CDS, unitCoverageRequired = false, components = {HarnessModuleComponent.CDS_K8S})
public class DirectK8sInfraDelegateConfig implements K8sInfraDelegateConfig {
  String namespace;
  KubernetesClusterConfigDTO kubernetesClusterConfigDTO;
  List<EncryptedDataDetail> encryptionDataDetails;
  boolean useSocketCapability;

  @Override
  public boolean useSocketCapability() {
    return useSocketCapability;
  }

  @Override
  public InfraContext toInfraContext(String delegateId) {
    return DirectK8sInfraContext.builder().delegateId(delegateId).build();
  }
}
