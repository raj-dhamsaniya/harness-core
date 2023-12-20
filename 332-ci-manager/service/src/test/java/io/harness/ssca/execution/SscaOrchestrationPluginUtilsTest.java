/*
 * Copyright 2023 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.ssca.execution;

import static io.harness.rule.OwnerRule.INDER;
import static io.harness.ssca.execution.SscaOrchestrationPluginUtils.getSscaOrchestrationSecretVars;
import static io.harness.ssca.execution.orchestration.SscaOrchestrationStepPluginUtils.COSIGN_PASSWORD;
import static io.harness.ssca.execution.orchestration.SscaOrchestrationStepPluginUtils.COSIGN_PRIVATE_KEY;
import static io.harness.ssca.execution.orchestration.SscaOrchestrationStepPluginUtils.PLUGIN_BASE64_SECRET;
import static io.harness.ssca.execution.orchestration.SscaOrchestrationStepPluginUtils.PLUGIN_FORMAT;
import static io.harness.ssca.execution.orchestration.SscaOrchestrationStepPluginUtils.PLUGIN_MODE;
import static io.harness.ssca.execution.orchestration.SscaOrchestrationStepPluginUtils.PLUGIN_SBOMDESTINATION;
import static io.harness.ssca.execution.orchestration.SscaOrchestrationStepPluginUtils.PLUGIN_SBOMSOURCE;
import static io.harness.ssca.execution.orchestration.SscaOrchestrationStepPluginUtils.PLUGIN_SBOM_DRIFT;
import static io.harness.ssca.execution.orchestration.SscaOrchestrationStepPluginUtils.PLUGIN_TOOL;
import static io.harness.ssca.execution.orchestration.SscaOrchestrationStepPluginUtils.PLUGIN_TYPE;
import static io.harness.ssca.execution.orchestration.SscaOrchestrationStepPluginUtils.SKIP_NORMALISATION;

import static org.assertj.core.api.Assertions.assertThat;

import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;
import io.harness.beans.sweepingoutputs.StageInfraDetails.Type;
import io.harness.category.element.UnitTests;
import io.harness.ci.execution.integrationstage.K8InitializeTaskUtils;
import io.harness.ci.executionplan.CIExecutionTestBase;
import io.harness.encryption.Scope;
import io.harness.encryption.SecretRefData;
import io.harness.ngsettings.client.remote.NGSettingsClient;
import io.harness.pms.contracts.ambiance.Ambiance;
import io.harness.pms.yaml.ParameterField;
import io.harness.rule.Owner;
import io.harness.ssca.beans.Attestation;
import io.harness.ssca.beans.attestation.AttestationType;
import io.harness.ssca.beans.attestation.CosignAttestation;
import io.harness.ssca.beans.ingestion.SbomFile;
import io.harness.ssca.beans.mode.SbomModeType;
import io.harness.ssca.beans.sbomDrift.SbomDrift;
import io.harness.ssca.beans.sbomDrift.SbomDriftBase;
import io.harness.ssca.beans.source.ImageSbomSource;
import io.harness.ssca.beans.source.SbomSource;
import io.harness.ssca.beans.source.SbomSourceType;
import io.harness.ssca.beans.stepinfo.SscaOrchestrationStepInfo;
import io.harness.ssca.beans.tools.SbomOrchestrationTool;
import io.harness.ssca.beans.tools.SbomOrchestrationToolType;
import io.harness.ssca.beans.tools.syft.SyftSbomOrchestration;
import io.harness.ssca.beans.tools.syft.SyftSbomOrchestration.SyftOrchestrationFormat;
import io.harness.yaml.core.variables.NGVariableType;
import io.harness.yaml.core.variables.SecretNGVariable;
import io.harness.yaml.utils.NGVariablesUtils;

import com.google.inject.Inject;
import java.util.Map;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;

@OwnedBy(HarnessTeam.SSCA)
public class SscaOrchestrationPluginUtilsTest extends CIExecutionTestBase {
  @Inject private SscaOrchestrationPluginUtils sscaOrchestrationPluginUtils;

  @Mock private K8InitializeTaskUtils k8InitializeTaskUtils;

  @Mock private NGSettingsClient settingsClient;

  @Test
  @Owner(developers = INDER)
  @Category(UnitTests.class)
  public void testGetSscaOrcherstrationStepEnvVariables() {
    SscaOrchestrationStepInfo stepInfo =
        SscaOrchestrationStepInfo.builder()
            .tool(SbomOrchestrationTool.builder()
                      .type(SbomOrchestrationToolType.SYFT)
                      .sbomOrchestrationSpec(
                          SyftSbomOrchestration.builder().format(SyftOrchestrationFormat.SPDX_JSON).build())
                      .build())
            .source(SbomSource.builder()
                        .type(SbomSourceType.IMAGE)
                        .sbomSourceSpec(ImageSbomSource.builder()
                                            .image(ParameterField.createValueField("image:tag"))
                                            .connector(ParameterField.createValueField("conn1"))
                                            .build())
                        .build())
            .sbomDrift(SbomDrift.builder().base(SbomDriftBase.BASELINE).build())
            .build();
    Ambiance ambiance = Ambiance.newBuilder()
                            .putSetupAbstractions("accountId", "accountId")
                            .putSetupAbstractions("orgIdentifier", "orgIdentifier")
                            .putSetupAbstractions("projectIdentifier", "projectIdentifier")
                            .build();

    Map<String, String> sscaEnvVarMap =
        sscaOrchestrationPluginUtils.getSscaOrchestrationStepEnvVariables(stepInfo, "id1", ambiance, Type.K8);
    assertThat(sscaEnvVarMap).isNotNull().isNotEmpty();
    assertThat(sscaEnvVarMap).hasSize(12);
    assertThat(sscaEnvVarMap.get(PLUGIN_MODE)).isEqualTo(SbomModeType.GENERATION.toString());
    assertThat(sscaEnvVarMap.get(PLUGIN_TOOL)).isEqualTo(SbomOrchestrationToolType.SYFT.toString());
    assertThat(sscaEnvVarMap.get(PLUGIN_FORMAT)).isEqualTo(SyftOrchestrationFormat.SPDX_JSON.toString());
    assertThat(sscaEnvVarMap.get(PLUGIN_SBOMSOURCE)).isEqualTo("image:tag");
    assertThat(sscaEnvVarMap.get(PLUGIN_TYPE)).isEqualTo("Orchestrate");
    assertThat(sscaEnvVarMap.get(PLUGIN_SBOMDESTINATION)).isEqualTo("harness/sbom");
    assertThat(sscaEnvVarMap.get(SKIP_NORMALISATION)).isEqualTo("true");
    assertThat(sscaEnvVarMap.get(PLUGIN_SBOM_DRIFT)).isEqualTo(SbomDriftBase.BASELINE.toString());
  }

  @Test
  @Owner(developers = INDER)
  @Category(UnitTests.class)
  public void testGetSscaOrchestrationSecretVars() {
    SscaOrchestrationStepInfo stepInfo =
        SscaOrchestrationStepInfo.builder()
            .attestation(Attestation.builder()
                             .type(AttestationType.COSIGN)
                             .attestationSpec(CosignAttestation.builder().password("test").privateKey("key").build())
                             .build())
            .build();

    Map<String, SecretNGVariable> secretNGVariableMap = getSscaOrchestrationSecretVars(stepInfo);
    assertThat(secretNGVariableMap).isNotNull().isNotEmpty().hasSize(2);
    assertThat(secretNGVariableMap.get(COSIGN_PASSWORD)).isNotNull();
    SecretNGVariable variable = secretNGVariableMap.get(COSIGN_PASSWORD);
    assertThat(variable.getType()).isEqualTo(NGVariableType.SECRET);
    assertThat(variable.getName()).isEqualTo(COSIGN_PASSWORD);
    assertThat(variable.getValue()).isNotNull();
    SecretRefData secretRefData = variable.getValue().getValue();
    assertThat(secretRefData).isNotNull();
    assertThat(secretRefData.getScope()).isEqualTo(Scope.PROJECT);
    assertThat(secretRefData.toSecretRefStringValue()).isEqualTo("test");

    SscaOrchestrationStepInfo accountLevelSecretStepInfo =
        SscaOrchestrationStepInfo.builder()
            .attestation(
                Attestation.builder()
                    .type(AttestationType.COSIGN)
                    .attestationSpec(CosignAttestation.builder().password("account.test").privateKey("key").build())
                    .build())
            .build();
    Map<String, SecretNGVariable> secretVariableMap1 = getSscaOrchestrationSecretVars(accountLevelSecretStepInfo);
    assertThat(secretVariableMap1).isNotEmpty().hasSize(2);
    assertThat(secretVariableMap1.get(COSIGN_PASSWORD)).isNotNull();
    assertThat(secretVariableMap1.get(COSIGN_PASSWORD).getValue().getValue()).isNotNull();
    assertThat(secretVariableMap1.get(COSIGN_PASSWORD).getValue().getValue().getScope()).isEqualTo(Scope.ACCOUNT);
    assertThat(secretVariableMap1.get(COSIGN_PASSWORD).getValue().getValue().getIdentifier()).isEqualTo("test");
  }

  @Test
  @Owner(developers = INDER)
  @Category(UnitTests.class)
  public void testGetSscaOrcherstrationStepEnvVariablesForVM() {
    long expressionFunctorToken = 12345;
    SscaOrchestrationStepInfo stepInfo =
        SscaOrchestrationStepInfo.builder()
            .mode(SbomModeType.GENERATION)
            .tool(SbomOrchestrationTool.builder()
                      .type(SbomOrchestrationToolType.SYFT)
                      .sbomOrchestrationSpec(
                          SyftSbomOrchestration.builder().format(SyftOrchestrationFormat.SPDX_JSON).build())
                      .build())
            .source(SbomSource.builder()
                        .type(SbomSourceType.IMAGE)
                        .sbomSourceSpec(ImageSbomSource.builder()
                                            .image(ParameterField.createValueField("image:tag"))
                                            .connector(ParameterField.createValueField("conn1"))
                                            .build())
                        .build())
            .attestation(
                Attestation.builder()
                    .type(AttestationType.COSIGN)
                    .attestationSpec(CosignAttestation.builder().password("account.test").privateKey("key").build())
                    .build())
            .build();
    Ambiance ambiance = Ambiance.newBuilder()
                            .putSetupAbstractions("accountId", "accountId")
                            .putSetupAbstractions("orgIdentifier", "orgIdentifier")
                            .putSetupAbstractions("projectIdentifier", "projectIdentifier")
                            .setExpressionFunctorToken(expressionFunctorToken)
                            .build();
    Map<String, String> sscaEnvVarMap =
        sscaOrchestrationPluginUtils.getSscaOrchestrationStepEnvVariables(stepInfo, "id1", ambiance, Type.VM);
    assertThat(sscaEnvVarMap).isNotNull().isNotEmpty();
    assertThat(sscaEnvVarMap).hasSize(13);
    assertThat(sscaEnvVarMap.get(PLUGIN_TOOL)).isEqualTo(SbomOrchestrationToolType.SYFT.toString());
    assertThat(sscaEnvVarMap.get(PLUGIN_FORMAT)).isEqualTo(SyftOrchestrationFormat.SPDX_JSON.toString());
    assertThat(sscaEnvVarMap.get(PLUGIN_SBOMSOURCE)).isEqualTo("image:tag");
    assertThat(sscaEnvVarMap.get(PLUGIN_TYPE)).isEqualTo("Orchestrate");
    assertThat(sscaEnvVarMap.get(PLUGIN_MODE)).isEqualTo("generation");
    assertThat(sscaEnvVarMap.get(PLUGIN_SBOMDESTINATION)).isEqualTo("harness/sbom");
    assertThat(sscaEnvVarMap.get(SKIP_NORMALISATION)).isEqualTo("true");
    assertThat(sscaEnvVarMap.get(PLUGIN_BASE64_SECRET)).isEqualTo("false");
    assertThat(sscaEnvVarMap.get(COSIGN_PASSWORD))
        .isEqualTo(NGVariablesUtils.fetchSecretExpressionWithExpressionToken("account.test", expressionFunctorToken));
    assertThat(sscaEnvVarMap.get(COSIGN_PRIVATE_KEY))
        .isEqualTo(NGVariablesUtils.fetchSecretExpressionWithExpressionToken("key", expressionFunctorToken));
  }

  @Test
  @Owner(developers = INDER)
  @Category(UnitTests.class)
  public void testGetSscaOrcherstrationIngestionStepEnvVariables() {
    SscaOrchestrationStepInfo stepInfo =
        SscaOrchestrationStepInfo.builder()
            .mode(SbomModeType.INGESTION)
            .ingestion(SbomFile.builder().file(ParameterField.createValueField("/path/to/sbom")).build())
            .source(SbomSource.builder()
                        .type(SbomSourceType.IMAGE)
                        .sbomSourceSpec(ImageSbomSource.builder()
                                            .image(ParameterField.createValueField("image:tag"))
                                            .connector(ParameterField.createValueField("conn1"))
                                            .build())
                        .build())
            .build();
    Ambiance ambiance = Ambiance.newBuilder()
                            .putSetupAbstractions("accountId", "accountId")
                            .putSetupAbstractions("orgIdentifier", "orgIdentifier")
                            .putSetupAbstractions("projectIdentifier", "projectIdentifier")
                            .build();
    Map<String, String> sscaEnvVarMap =
        sscaOrchestrationPluginUtils.getSscaOrchestrationStepEnvVariables(stepInfo, "id1", ambiance, Type.K8);
    assertThat(sscaEnvVarMap).isNotNull().isNotEmpty();
    assertThat(sscaEnvVarMap).hasSize(9);
    assertThat(sscaEnvVarMap.get(PLUGIN_MODE)).isEqualTo(SbomModeType.INGESTION.toString());
    assertThat(sscaEnvVarMap.get(PLUGIN_SBOMSOURCE)).isEqualTo("image:tag");
    assertThat(sscaEnvVarMap.get(PLUGIN_TYPE)).isEqualTo("Orchestrate");
    assertThat(sscaEnvVarMap.get(PLUGIN_SBOMDESTINATION)).isEqualTo("/path/to/sbom");
    assertThat(sscaEnvVarMap.get(SKIP_NORMALISATION)).isEqualTo("true");
    assertThat(sscaEnvVarMap.get(PLUGIN_BASE64_SECRET)).isEqualTo("false");

    k8InitializeTaskUtils.removeEnvVarsWithSecretRef(sscaEnvVarMap);
    assertThat(sscaEnvVarMap).hasSize(9);
  }
}
