package com.openapi.converter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openapi.converter.dto.openapi.ApiResponse;
import com.openapi.converter.dto.openapi.Components;
import com.openapi.converter.dto.openapi.Info;
import com.openapi.converter.dto.openapi.MediaType;
import com.openapi.converter.dto.openapi.Oauth2Flow;
import com.openapi.converter.dto.openapi.OpenAPI;
import com.openapi.converter.dto.openapi.Operation;
import com.openapi.converter.dto.openapi.PathItem;
import com.openapi.converter.dto.openapi.Schema;
import com.openapi.converter.dto.openapi.SecurityScheme;
import com.openapi.converter.exception.OperationNotSpecifiedException;
import com.openapi.converter.mapping.OpenApiMapper;
import com.openapi.converter.model.report.ApiResponseReport;
import com.openapi.converter.model.report.ComponentReport;
import com.openapi.converter.model.report.FieldReport;
import com.openapi.converter.model.report.MethodInfo;
import com.openapi.converter.model.report.OpenApiReport;
import com.openapi.converter.model.report.RequestBodyReport;
import com.openapi.converter.model.report.SchemaReport;
import com.openapi.converter.model.report.SecurityRequirementReport;
import com.openapi.converter.model.report.SecuritySchemaReport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.openapi.converter.util.Utils.getOperation;

/**
 * Open api report service.
 *
 * @author Roman batygin
 */
@Slf4j
@Service
public class OpenApiReportService {

    private static final String SLASH_SEPARATOR = "/";
    private static final String PASSWORD_GRANT = "password";
    private static final String IMPLICIT_GRANT = "implicit";
    private static final String AUTHORIZATION_CODE_GRANT = "authorization_code";
    private static final String CLIENT_CREDENTIALS_GRANT = "client_credentials";
    private static final int ALL_OF_SIZE = 2;
    private static final int CHILD_SCHEMA_IDX = 1;

    private final OpenApiMapper openApiMapper;
    private final ObjectMapper exampleObjectMapper;

    /**
     * Constructor with parameters.
     *
     * @param openApiMapper       - open api mapper
     * @param exampleObjectMapper example object mapper
     */
    public OpenApiReportService(OpenApiMapper openApiMapper,
                                @Qualifier("exampleObjectMapper") ObjectMapper exampleObjectMapper) {
        this.openApiMapper = openApiMapper;
        this.exampleObjectMapper = exampleObjectMapper;
    }

    /**
     * Builds open api report model.
     *
     * @param openAPI - open api schema
     * @return open api report model
     */
    public OpenApiReport buildReport(OpenAPI openAPI) {
        String title = Optional.ofNullable(openAPI.getInfo())
                .map(Info::getTitle)
                .orElse(null);
        log.info("Starting to build open api report [{}]", title);
        OpenApiReport openApiReport = openApiMapper.map(openAPI);
        var methods = buildMethodsReport(openAPI);
        var components = buildComponents(openAPI);
        var securitySchemes = buildSecuritySchemesReports(openAPI);
        openApiReport.setMethods(methods);
        openApiReport.setComponents(components);
        openApiReport.setSecuritySchemes(securitySchemes);
        log.info("Open api report [{}] has been built", title);
        return openApiReport;
    }

    private List<MethodInfo> buildMethodsReport(OpenAPI openAPI) {
        var paths = Optional.ofNullable(openAPI.getPaths()).orElse(Collections.emptyMap());
        var methods = paths.entrySet()
                .stream()
                .map(entry -> buildMethodInfo(entry, openAPI))
                .collect(Collectors.toList());
        log.info("[{}] methods report has been build", methods.size());
        return methods;
    }

    private List<ComponentReport> buildComponents(OpenAPI openAPI) {
        if (Optional.ofNullable(openAPI.getComponents()).isEmpty() ||
                CollectionUtils.isEmpty(openAPI.getComponents().getSchemas())) {
            return Collections.emptyList();
        }
        var componentReports = openAPI.getComponents().getSchemas()
                .entrySet()
                .stream()
                .map(entry -> buildComponentReport(entry.getKey(), entry.getValue(),
                        openAPI.getComponents().getSchemas()))
                .collect(Collectors.toList());
        log.info("[{}] components report has been built", componentReports.size());
        return componentReports;
    }

    @SuppressWarnings("unchecked")
    private ComponentReport buildComponentReport(String name, Schema schema, Map<String, Schema> schemas) {
        List<FieldReport> fields = buildFieldReports(schema, schemas);
        log.info("[{}] fields has been built for component [{}]", fields.size(), name);
        return ComponentReport.builder()
                .name(name)
                .description(schema.getDescription())
                .fields(fields)
                .build();
    }

    private List<FieldReport> buildSimpleFieldReports(Schema schema, List<String> requiredFields) {
        return schema.getProperties().entrySet()
                .stream()
                .map(entry -> buildFieldModel(entry.getKey(),
                        requiredFields.contains(entry.getKey()), entry.getValue()))
                .collect(Collectors.toList());
    }

    private List<FieldReport> buildSimpleFieldReports(Schema schema) {
        var requiredFields = Optional.ofNullable(schema.getRequired())
                .orElse(Collections.emptyList());
        return buildSimpleFieldReports(schema, requiredFields);
    }

    private List<FieldReport> buildAllOfFieldReports(Schema schema, Map<String, Schema> schemas) {
        Schema child = schema.getAllOf().get(CHILD_SCHEMA_IDX);
        List<FieldReport> fieldReports = newArrayList();
        Stack<Schema> parentSchemas = getAllParentSchema(schema, schemas);
        while (!parentSchemas.isEmpty()) {
            Schema next = parentSchemas.pop();
            var nextFields = buildSimpleFieldReports(next)
                    .stream()
                    .filter(fieldReport -> fieldReports.stream().noneMatch(
                            f -> f.getFieldName().equals(fieldReport.getFieldName())))
                    .collect(Collectors.toList());
            fieldReports.addAll(nextFields);
        }
        var requiredFields = Optional.ofNullable(schema.getRequired()).orElse(Collections.emptyList());
        var childFields = buildSimpleFieldReports(child, requiredFields);
        fieldReports.addAll(childFields);
        return fieldReports;
    }

    private Stack<Schema> getAllParentSchema(Schema schema, Map<String, Schema> schemas) {
        Schema current = schema.getAllOf().iterator().next();
        Stack<Schema> stack = new Stack<>();
        do {
            Schema nextParentSchema = getNextParentSchema(current, schemas);
            if (nextParentSchema != null) {
                stack.push(nextParentSchema);
            }
            if (!CollectionUtils.isEmpty(nextParentSchema.getAllOf()) &&
                    nextParentSchema.getAllOf().size() >= ALL_OF_SIZE) {
                current = nextParentSchema.getAllOf().iterator().next();
            } else {
                current = null;
            }
        } while (current != null);
        return stack;
    }

    private Schema getNextParentSchema(Schema schema, Map<String, Schema> schemas) {
        String schemaRef = StringUtils.substringAfterLast(schema.getRef(), SLASH_SEPARATOR);
        return schemas.entrySet()
                .stream()
                .filter(entry -> entry.getKey().equals(schemaRef))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);

    }

    private List<FieldReport> buildFieldReports(Schema schema, Map<String, Schema> schemas) {
        if (!CollectionUtils.isEmpty(schema.getProperties())) {
            return buildSimpleFieldReports(schema);
        } else if (!CollectionUtils.isEmpty(schema.getAllOf()) && schema.getAllOf().size() >= ALL_OF_SIZE) {
            return buildAllOfFieldReports(schema, schemas);
        } else {
            return Collections.emptyList();
        }
    }

    private FieldReport buildFieldModel(String fieldName, boolean required, Schema schema) {
        SchemaReport schemaReport = buildSchemaReport(schema);
        return FieldReport.builder()
                .fieldName(fieldName)
                .description(schemaReport.getDescription())
                .required(required)
                .schema(schemaReport)
                .build();
    }

    private SchemaReport buildSchemaReport(Schema schema) {
        return Optional.ofNullable(schema)
                .map(s -> {
                    SchemaReport schemaReport = openApiMapper.map(s);
                    var oneOfRefs = buildOneOfRefs(schema);
                    var itemsReport = buildSchemaItems(schema);
                    schemaReport.setOneOfRefs(oneOfRefs);
                    schemaReport.setItemsReport(itemsReport);
                    return schemaReport;
                }).orElse(null);
    }

    private List<SchemaReport> buildSchemaItems(Schema schema) {
        List<SchemaReport> schemaItemsReports = newArrayList();
        Schema items = schema.getItems();
        while (items != null) {
            var itemsReport = openApiMapper.map(items);
            schemaItemsReports.add(itemsReport);
            items = items.getItems();
        }
        return schemaItemsReports;
    }

    private MethodInfo buildMethodInfo(Map.Entry<String, PathItem> entry, OpenAPI openAPI) {
        var operationModel = getOperation(entry.getValue())
                .orElseThrow(() -> new OperationNotSpecifiedException(
                        String.format("Operation not specified for endpoint [%s]", entry.getKey())));
        var operation = operationModel.getOperation();
        var requestParameters = openApiMapper.map(operation.getParameters());
        var apiResponses = buildApiResponsesReport(operation, openAPI);
        log.info("[{}] api responses has been built for method [{}]", apiResponses.size(), entry.getKey());
        var requestBodyModel = buildRequestBodyReport(operation, openAPI);
        var securityRequirementModel = buildSecurityRequirementReports(operation);
        return MethodInfo.builder()
                .requestType(operationModel.getRequestMethod().name())
                .endpoint(entry.getKey())
                .summary(operation.getSummary())
                .description(operation.getDescription())
                .requestBody(requestBodyModel)
                .requestParameters(requestParameters)
                .apiResponses(apiResponses)
                .security(securityRequirementModel)
                .build();
    }

    private List<SecurityRequirementReport> buildSecurityRequirementReports(Operation operation) {
        if (CollectionUtils.isEmpty(operation.getSecurity())) {
            return Collections.emptyList();
        }
        List<SecurityRequirementReport> securityRequirementReports = newArrayList();
        operation.getSecurity().forEach(map -> map.forEach((name, scopes) -> {
            var securityRequirementModel = SecurityRequirementReport.builder()
                    .name(name)
                    .scopes(new ArrayList<>(scopes))
                    .build();
            securityRequirementReports.add(securityRequirementModel);
        }));
        return securityRequirementReports;
    }

    private RequestBodyReport buildRequestBodyReport(Operation operation, OpenAPI openAPI) {
        return Optional.ofNullable(operation.getRequestBody())
                .map(requestBody -> {
                    RequestBodyReport requestBodyReport = openApiMapper.map(requestBody);
                    if (!CollectionUtils.isEmpty(requestBody.getContent())) {
                        var mediaType = requestBody.getContent().entrySet().iterator().next();
                        var schema = mediaType.getValue().getSchema();
                        var schemaReport = buildSchemaReport(schema);
                        requestBodyReport.setContentType(mediaType.getKey());
                        requestBodyReport.setSchema(schemaReport);
                        var schemaReports = buildFieldReports(schema, Collections.emptyMap());
                        requestBodyReport.setSchemaProperties(schemaReports);
                        requestBodyReport.setExample(getExampleAsJsonString(mediaType.getValue(), openAPI));
                    }
                    return requestBodyReport;
                }).orElse(null);
    }

    private List<ApiResponseReport> buildApiResponsesReport(Operation operation, OpenAPI openAPI) {
        if (CollectionUtils.isEmpty(operation.getResponses())) {
            return Collections.emptyList();
        }
        return operation.getResponses().entrySet()
                .stream()
                .map(entry -> buildApiResponseReport(entry.getKey(), entry.getValue(), openAPI))
                .collect(Collectors.toList());
    }

    private ApiResponseReport buildApiResponseReport(String responseCode, ApiResponse apiResponse, OpenAPI openAPI) {
        ApiResponseReport apiResponseReport = new ApiResponseReport();
        apiResponseReport.setResponseCode(responseCode);
        apiResponseReport.setDescription(apiResponse.getDescription());
        if (!CollectionUtils.isEmpty(apiResponse.getContent())) {
            var mediaType = apiResponse.getContent().entrySet().iterator().next();
            var schema = mediaType.getValue().getSchema();
            apiResponseReport.setContentType(mediaType.getKey());
            apiResponseReport.setExample(getExampleAsJsonString(mediaType.getValue(), openAPI));
            var schemaReport = buildSchemaReport(schema);
            apiResponseReport.setSchema(schemaReport);
        }
        return apiResponseReport;
    }

    private String getBodyRef(Schema schema) {
        return Optional.ofNullable(schema)
                .map(Schema::getRef)
                .map(ref -> StringUtils.substringAfterLast(ref, SLASH_SEPARATOR))
                .orElse(null);
    }

    private String getExampleAsJsonString(MediaType mediaType, OpenAPI openAPI) {
        Object exampleValue = getExampleValue(mediaType, openAPI);
        return convertExampleToJsonString(exampleValue);
    }

    private Object getExampleValue(MediaType mediaType, OpenAPI openAPI) {
        if (!CollectionUtils.isEmpty(mediaType.getExamples())) {
            var example = mediaType.getExamples().values().iterator().next();
            if (StringUtils.isNotEmpty(example.getRef())) {
                String exampleKey = StringUtils.substringAfterLast(example.getRef(), SLASH_SEPARATOR);
                return getExampleValueByKey(exampleKey, openAPI);
            } else if (example.getValue() != null) {
                return example.getValue();
            }
        }
        return mediaType.getExample();
    }

    private Object getExampleValueByKey(String key, OpenAPI openAPI) {
        if (Optional.ofNullable(openAPI.getComponents()).isPresent() &&
                !CollectionUtils.isEmpty(openAPI.getComponents().getExamples())) {
            var example = openAPI.getComponents().getExamples().get(key);
            if (example == null) {
                log.warn("Can't find example with key [{}]", key);
            } else {
                return example.getValue();
            }
        }
        return null;
    }

    private String convertExampleToJsonString(Object exampleValue) {
        if (exampleValue == null) {
            return null;
        }
        try {
            return exampleObjectMapper.writeValueAsString(exampleValue);
        } catch (JsonProcessingException ex) {
            log.error("Can't serialize example to json: {}", ex.getMessage());
            return String.valueOf(exampleValue);
        }
    }

    private List<SecuritySchemaReport> buildSecuritySchemesReports(OpenAPI openAPI) {
        if (Optional.ofNullable(openAPI.getComponents()).map(Components::getSecuritySchemes).isEmpty()) {
            return Collections.emptyList();
        }
        var securitySchemaReports = openAPI.getComponents()
                .getSecuritySchemes()
                .entrySet()
                .stream()
                .map(entry -> {
                    var securitySchemaReport = buildSecuritySchemeReport(entry.getValue());
                    securitySchemaReport.setName(entry.getKey());
                    return securitySchemaReport;
                })
                .collect(Collectors.toList());
        log.info("[{}] security schema report has been build", securitySchemaReports.size());
        return securitySchemaReports;
    }

    private SecuritySchemaReport buildSecuritySchemeReport(SecurityScheme securityScheme) {
        var securitySchemaModel = openApiMapper.map(securityScheme);
        securitySchemaModel.setOauth2Flows(newArrayList());
        var flows = securityScheme.getFlows();
        if (Optional.ofNullable(flows).isPresent()) {
            addOauth2Flow(flows.getPassword(), securitySchemaModel, PASSWORD_GRANT);
            addOauth2Flow(flows.getImplicit(), securitySchemaModel, IMPLICIT_GRANT);
            addOauth2Flow(flows.getAuthorizationCode(), securitySchemaModel, AUTHORIZATION_CODE_GRANT);
            addOauth2Flow(flows.getClientCredentials(), securitySchemaModel, CLIENT_CREDENTIALS_GRANT);
        }
        return securitySchemaModel;
    }

    private void addOauth2Flow(Oauth2Flow oauth2Flow, SecuritySchemaReport securitySchemaReport, String grantType) {
        Optional.ofNullable(oauth2Flow)
                .ifPresent(flow -> {
                    var flowModel = openApiMapper.map(flow);
                    flowModel.setGrantType(grantType);
                    securitySchemaReport.getOauth2Flows().add(flowModel);
                });
    }

    private List<String> buildOneOfRefs(Schema schema) {
        if (CollectionUtils.isEmpty(schema.getOneOf())) {
            return Collections.emptyList();
        }
        return schema.getOneOf().stream()
                .map(this::getBodyRef)
                .collect(Collectors.toList());
    }
}
