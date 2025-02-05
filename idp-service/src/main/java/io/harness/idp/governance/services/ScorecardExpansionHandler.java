/*
 * Copyright 2023 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.idp.governance.services;

import static io.harness.data.structure.EmptyPredicate.isEmpty;
import static io.harness.idp.common.CommonUtils.truncateEntityName;
import static io.harness.idp.common.JacksonUtils.convert;
import static io.harness.remote.client.NGRestUtils.getGeneralResponse;
import static io.harness.remote.client.NGRestUtils.getResponse;

import static java.lang.String.format;

import io.harness.cdstage.remote.CDStageConfigClient;
import io.harness.clients.BackstageResourceClient;
import io.harness.idp.backstage.beans.BackstageCatalogEntityTypes;
import io.harness.idp.backstage.entities.BackstageCatalogEntity;
import io.harness.idp.governance.beans.ScorecardExpandedValue;
import io.harness.idp.governance.beans.ServiceScorecards;
import io.harness.idp.governance.beans.ServiceScorecardsMapper;
import io.harness.idp.namespace.service.NamespaceService;
import io.harness.idp.scorecard.scores.service.ScoreService;
import io.harness.ng.core.dto.CDStageMetaDataDTO;
import io.harness.ng.core.dto.CdDeployStageMetadataRequestDTO;
import io.harness.pms.contracts.governance.ExpansionPlacementStrategy;
import io.harness.pms.contracts.governance.ExpansionRequestMetadata;
import io.harness.pms.sdk.core.governance.ExpandedValue;
import io.harness.pms.sdk.core.governance.ExpansionResponse;
import io.harness.pms.sdk.core.governance.JsonExpansionHandler;
import io.harness.pms.yaml.YAMLFieldNameConstants;
import io.harness.pms.yaml.YamlNode;
import io.harness.spec.server.idp.v1.model.ScorecardSummaryInfo;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScorecardExpansionHandler implements JsonExpansionHandler {
  private static final String COMPONENT = "component";
  private static final ObjectMapper mapper =
      new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  private static final String CATALOG_API = "%s/idp/api/catalog/entities?%s";
  @Inject BackstageResourceClient backstageResourceClient;
  @Inject ScoreService scoreService;
  @Inject CDStageConfigClient cdStageConfigClient;
  @Inject NamespaceService namespaceService;

  @Override
  public ExpansionResponse expand(JsonNode fieldValue, ExpansionRequestMetadata metadata, String fqn) {
    String accountId = metadata.getAccountId();
    try {
      namespaceService.getNamespaceForAccountIdentifier(accountId);
    } catch (Exception e) {
      log.info(e.getMessage());
      return ExpansionResponse.builder().success(false).errorMessage(e.getMessage()).build();
    }
    String orgId = metadata.getOrgId();
    String projectId = metadata.getProjectId();
    String stageIdentifier = fieldValue.get("identifier").asText();
    String pipeline = metadata.getYaml().toStringUtf8();
    log.info(format("Process started for IDP Scorecard expansion for stage: [%s], account: [%s], project:[%s]",
        stageIdentifier, accountId, projectId));
    CDStageMetaDataDTO cdStageMetaDataDTO = getCDStageResponse(stageIdentifier, pipeline);
    if (cdStageMetaDataDTO == null || isInvalidResponse(cdStageMetaDataDTO)) {
      String errorMessage =
          format("Could not fetch ServiceRef and EnvironmentRef for stage: [%s], account: [%s], project:[%s]",
              stageIdentifier, accountId, projectId);
      log.error(errorMessage);
      return ExpansionResponse.builder().success(false).errorMessage(errorMessage).build();
    }
    if (isEmpty(cdStageMetaDataDTO.getServiceEnvRefList())) {
      cdStageMetaDataDTO = setServiceEnvRef(cdStageMetaDataDTO);
    }

    Map<String, List<ServiceScorecards>> serviceScores = new HashMap<>();
    for (CDStageMetaDataDTO.ServiceEnvRef serviceEnvRef : cdStageMetaDataDTO.getServiceEnvRefList()) {
      String serviceId = serviceEnvRef.getServiceRef();
      String filter = "filter=kind=" + COMPONENT + ",metadata.annotations.harness.io/cd-serviceId=" + serviceId;
      String url = String.format(CATALOG_API, accountId, filter);
      log.info("Making backstage API request {}: ", url);
      try {
        Object entitiesResponse = getGeneralResponse(backstageResourceClient.getCatalogEntities(url));
        List<BackstageCatalogEntity> entities = convert(mapper, entitiesResponse, BackstageCatalogEntity.class);
        String uuid = getUUId(entities, orgId, projectId);
        if (uuid == null) {
          continue;
        }
        log.info("Matching backstage entity: " + uuid);
        List<ScorecardSummaryInfo> scorecardSummaryInfos = scoreService.getScoresSummaryForAnEntity(accountId, uuid);
        serviceScores.put(serviceId, ServiceScorecardsMapper.toDTO(scorecardSummaryInfos));
      } catch (Exception e) {
        log.error(format("Error while fetch catalog details for account = [%s], serviceId = [%s], error = [%s]",
                      accountId, serviceId, e.getMessage()),
            e);
      }
    }

    if (isEmpty(serviceScores)) {
      String errorMessage = "Could not find matching backstage entity or scores for given service(s)";
      log.info(errorMessage);
      return ExpansionResponse.builder().success(false).errorMessage(errorMessage).build();
    }

    ExpandedValue value = ScorecardExpandedValue.builder().serviceScores(serviceScores).build();
    log.info("Scorecard Expand json value: " + value.toJson());
    return ExpansionResponse.builder()
        .success(true)
        .key(value.getKey())
        .value(value)
        .fqn(fqn + YamlNode.PATH_SEP + YAMLFieldNameConstants.SPEC)
        .placement(ExpansionPlacementStrategy.APPEND)
        .build();
  }

  private CDStageMetaDataDTO getCDStageResponse(String stageIdentifier, String pipeline) {
    try {
      return getResponse(cdStageConfigClient.getCDStageMetaData(
          CdDeployStageMetadataRequestDTO.builder().stageIdentifier(stageIdentifier).pipelineYaml(pipeline).build()));
    } catch (Exception e) {
      String errorMessage = format(
          "Exception occurred while fetching service and environment reference for stage: [%s]", stageIdentifier);
      log.error(errorMessage, e);
      return null;
    }
  }

  private boolean isInvalidResponse(CDStageMetaDataDTO cdStageMetaDataDTO) {
    return isEmpty(cdStageMetaDataDTO.getServiceEnvRefList())
        && (Objects.isNull(cdStageMetaDataDTO.getServiceRef())
            || Objects.isNull(cdStageMetaDataDTO.getEnvironmentRef()));
  }

  private CDStageMetaDataDTO setServiceEnvRef(CDStageMetaDataDTO cdStageMetaDataDTO) {
    return CDStageMetaDataDTO.builder()
        .environmentRef(cdStageMetaDataDTO.getEnvironmentRef())
        .serviceRef(cdStageMetaDataDTO.getServiceRef())
        .serviceEnvRef(CDStageMetaDataDTO.ServiceEnvRef.builder()
                           .environmentRef(cdStageMetaDataDTO.getEnvironmentRef())
                           .serviceRef(cdStageMetaDataDTO.getServiceRef())
                           .build())
        .build();
  }

  private String getUUId(List<BackstageCatalogEntity> entities, String orgId, String projectId) {
    if (entities.size() == 1) {
      return entities.get(0).getMetadata().getUid();
    } else if (entities.size() > 1) {
      BackstageCatalogEntity catalogEntity = entities.stream()
                                                 .filter(entity
                                                     -> (BackstageCatalogEntityTypes.getEntityDomain(entity) != null
                                                            && Objects.equals(truncateEntityName(orgId),
                                                                BackstageCatalogEntityTypes.getEntityDomain(entity)))
                                                         && Objects.equals(truncateEntityName(projectId),
                                                             BackstageCatalogEntityTypes.getEntitySystem(entity)))
                                                 .findFirst()
                                                 .orElse(null);
      if (catalogEntity != null) {
        return catalogEntity.getMetadata().getUid();
      }
    }
    return null;
  }
}
