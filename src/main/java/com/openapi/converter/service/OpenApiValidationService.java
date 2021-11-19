package com.openapi.converter.service;

import com.openapi.converter.dto.openapi.Info;
import com.openapi.converter.dto.openapi.OpenAPI;
import com.openapi.converter.dto.openapi.Operation;
import com.openapi.converter.dto.openapi.OperationWrapper;
import com.openapi.converter.dto.openapi.Parameter;
import com.openapi.converter.dto.openapi.PathItem;
import com.openapi.converter.dto.openapi.Schema;
import com.openapi.converter.exception.ReportException;
import com.openapi.converter.model.validation.Rule;
import com.openapi.converter.model.validation.Severity;
import com.openapi.converter.model.validation.ValidationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Open API validation service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OpenApiValidationService {

    private static final String INTEGER_TYPE = "integer";
    private static final String STRING_TYPE = "string";
    private static final String ARRAY_TYPE = "array";
    private static final String BINARY_FORMAT = "binary";

    /**
     * Validates specified open api.
     *
     * @param openAPI - open api model
     * @return validation results
     */
    public List<ValidationResult> validate(OpenAPI openAPI) {
        String title = Optional.ofNullable(openAPI.getInfo())
                .map(Info::getTitle)
                .orElse(null);
        log.info("Starting to validate open api [{}]", title);
        List<ValidationResult> validationResults = newArrayList();
        validationResults.addAll(validateApiInfo(openAPI));
        validationResults.addAll(validatePaths(openAPI));
        validationResults.sort(Comparator.comparing(ValidationResult::getSeverity));
        log.info("Open api [{}] validation has been finished", title);
        printValidationResults(title, validationResults);
        return validationResults;
    }

    private List<ValidationResult> validateApiInfo(OpenAPI openAPI) {
        List<ValidationResult> validationResults = newArrayList();
        if (Optional.ofNullable(openAPI.getInfo()).isEmpty() || StringUtils.isEmpty(openAPI.getInfo().getTitle())) {
            validationResults.add(
                    ValidationResult.builder()
                            .rule(Rule.API_TITLE_REQUIRED)
                            .severity(Severity.CRITICAL)
                            .message(Rule.API_TITLE_REQUIRED.getMessage())
                            .build()
            );
        }
        if (Optional.ofNullable(openAPI.getInfo()).isEmpty() || StringUtils.isEmpty(openAPI.getInfo().getVersion())) {
            validationResults.add(
                    ValidationResult.builder()
                            .rule(Rule.API_VERSION_REQUIRED)
                            .severity(Severity.CRITICAL)
                            .message(Rule.API_VERSION_REQUIRED.getMessage())
                            .build()
            );
        }
        if (Optional.ofNullable(openAPI.getInfo()).isEmpty() ||
                StringUtils.isEmpty(openAPI.getInfo().getDescription())) {
            validationResults.add(
                    ValidationResult.builder()
                            .rule(Rule.API_DESCRIPTION_REQUIRED)
                            .severity(Severity.MAJOR)
                            .message(Rule.API_DESCRIPTION_REQUIRED.getMessage())
                            .build()
            );
        }
        if (Optional.ofNullable(openAPI.getInfo()).map(Info::getContact).isEmpty() ||
                StringUtils.isEmpty(openAPI.getInfo().getContact().getName())) {
            validationResults.add(
                    ValidationResult.builder()
                            .rule(Rule.API_CONTACT_NAME_REQUIRED)
                            .severity(Severity.MAJOR)
                            .message(Rule.API_CONTACT_NAME_REQUIRED.getMessage())
                            .build()
            );
        }
        if (Optional.ofNullable(openAPI.getInfo()).map(Info::getContact).isEmpty() ||
                StringUtils.isEmpty(openAPI.getInfo().getContact().getEmail())) {
            validationResults.add(
                    ValidationResult.builder()
                            .rule(Rule.API_CONTACT_EMAIL_REQUIRED)
                            .severity(Severity.MAJOR)
                            .message(Rule.API_CONTACT_EMAIL_REQUIRED.getMessage())
                            .build()
            );
        }
        return validationResults;
    }

    private List<ValidationResult> validateRequestBody(OpenAPI openAPI, String path, Operation operation) {
        List<ValidationResult> validationResults = newArrayList();
        var requestBody = operation.getRequestBody();
        if (requestBody != null && !CollectionUtils.isEmpty(requestBody.getContent())) {
            var mediaType = requestBody.getContent().entrySet().iterator().next();
            var schema = mediaType.getValue().getSchema();
            if (StringUtils.isNotEmpty(schema.getRef())) {
                Schema foundSchema = getSchemaByRef(openAPI, schema.getRef());
                if (foundSchema != null && !CollectionUtils.isEmpty(foundSchema.getProperties())) {
                    foundSchema.getProperties().forEach((fieldName, schemaVal) -> validationResults.addAll(
                            validateSchemaFull(path, fieldName, schemaVal)));
                }
            }
            if (!CollectionUtils.isEmpty(schema.getProperties())) {
                schema.getProperties().forEach((fieldName, schemaVal) -> validationResults.addAll(
                        validateSchemaFull(path, fieldName, schemaVal)));
            }
            if (!MediaType.MULTIPART_FORM_DATA_VALUE.equals(mediaType.getKey()) &&
                    mediaType.getValue().getExample() == null) {
                validationResults.add(
                        ValidationResult.builder()
                                .rule(Rule.REQUEST_PARAMETER_EXAMPLE_REQUIRED)
                                .severity(Severity.INFO)
                                .path(path)
                                .schemaRef(schema.getRef())
                                .message(Rule.REQUEST_PARAMETER_DESCRIPTION_REQUIRED.getMessage())
                                .build()
                );
            }
        }
        return validationResults;
    }

    private List<ValidationResult> validatePaths(OpenAPI openAPI) {
        var paths = Optional.ofNullable(openAPI.getPaths()).orElse(Collections.emptyMap());
        List<ValidationResult> validationResults = newArrayList();
        paths.forEach((path, pathItem) -> {
            var operationModel = getOperation(pathItem)
                    .orElseThrow(() -> new ReportException(
                            String.format("Can't handle operation for endpoint [%s]", path)));
            var operation = operationModel.getOperation();
            validationResults.addAll(validateOperation(path, operation));
            validationResults.addAll(validateRequestBody(openAPI, path, operation));
        });
        return validationResults;
    }

    private List<ValidationResult> validateOperation(String path, Operation operation) {
        List<ValidationResult> validationResults = newArrayList();
        if (StringUtils.isEmpty(operation.getDescription())) {
            validationResults.add(
                    ValidationResult.builder()
                            .rule(Rule.API_OPERATION_DESCRIPTION_REQUIRED)
                            .severity(Severity.CRITICAL)
                            .path(path)
                            .message(Rule.API_OPERATION_DESCRIPTION_REQUIRED.getMessage())
                            .build()
            );
        }
        if (StringUtils.isEmpty(operation.getSummary())) {
            validationResults.add(
                    ValidationResult.builder()
                            .rule(Rule.API_OPERATION_SUMMARY_REQUIRED)
                            .severity(Severity.INFO)
                            .path(path)
                            .message(Rule.API_OPERATION_SUMMARY_REQUIRED.getMessage())
                            .build()
            );
        }
        var requestParameters = operation.getParameters();
        validationResults.addAll(validateRequestParameters(path, requestParameters));
        return validationResults;
    }

    private List<ValidationResult> validateRequestParameters(String path, List<Parameter> parameters) {
        List<ValidationResult> validationResults = newArrayList();
        if (!CollectionUtils.isEmpty(parameters)) {
            parameters.forEach(parameter -> {
                if (StringUtils.isEmpty(parameter.getDescription())) {
                    validationResults.add(
                            ValidationResult.builder()
                                    .rule(Rule.REQUEST_PARAMETER_DESCRIPTION_REQUIRED)
                                    .severity(Severity.CRITICAL)
                                    .path(path)
                                    .field(parameter.getName())
                                    .message(Rule.REQUEST_PARAMETER_DESCRIPTION_REQUIRED.getMessage())
                                    .build()
                    );
                }
                if (StringUtils.isEmpty(parameter.getExample())) {
                    validationResults.add(
                            ValidationResult.builder()
                                    .rule(Rule.REQUEST_PARAMETER_EXAMPLE_REQUIRED)
                                    .severity(Severity.INFO)
                                    .path(path)
                                    .field(parameter.getName())
                                    .message(Rule.REQUEST_PARAMETER_EXAMPLE_REQUIRED.getMessage())
                                    .build()
                    );
                }
                if (parameter.getSchema() != null) {
                    validationResults.addAll(validateSchemaCommonFields(path, parameter.getName(), parameter.getSchema()));
                }
            });
        }
        return validationResults;
    }

    private Schema getSchemaByRef(OpenAPI openAPI, String ref) {
        if (openAPI.getComponents() == null || CollectionUtils.isEmpty(openAPI.getComponents().getSchemas())) {
            return null;
        }
        return openAPI.getComponents().getSchemas().entrySet()
                .stream()
                .filter(entry -> ref.equals(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    private List<ValidationResult> validateSchemaFull(String path, String field, Schema schema) {
        List<ValidationResult> validationResults = validateSchemaCommonFields(path, field, schema);
        if (StringUtils.isEmpty(schema.getDescription())) {
            validationResults.add(
                    ValidationResult.builder()
                            .rule(Rule.REQUEST_PARAMETER_DESCRIPTION_REQUIRED)
                            .severity(Severity.CRITICAL)
                            .schemaRef(schema.getRef())
                            .path(path)
                            .field(field)
                            .message(Rule.REQUEST_PARAMETER_DESCRIPTION_REQUIRED.getMessage())
                            .build()
            );
        }
        return validationResults;
    }

    private List<ValidationResult> validateSchemaCommonFields(String path, String field, Schema schema) {
        List<ValidationResult> validationResults = newArrayList();
        if (INTEGER_TYPE.equals(schema.getType()) && schema.getMaximum() == null) {
            validationResults.add(
                    ValidationResult.builder()
                            .rule(Rule.REQUEST_PARAMETER_MAXIMUM_REQUIRED)
                            .severity(Severity.CRITICAL)
                            .schemaRef(schema.getRef())
                            .path(path)
                            .field(field)
                            .message(Rule.REQUEST_PARAMETER_MAXIMUM_REQUIRED.getMessage())
                            .build()
            );
        }
        if (STRING_TYPE.equals(schema.getType()) && !BINARY_FORMAT.equals(schema.getFormat()) &&
                schema.getMaxLength() == null) {
            validationResults.add(
                    ValidationResult.builder()
                            .rule(Rule.REQUEST_PARAMETER_MAX_LENGTH_REQUIRED)
                            .severity(Severity.CRITICAL)
                            .schemaRef(schema.getRef())
                            .path(path)
                            .field(field)
                            .message(Rule.REQUEST_PARAMETER_MAX_LENGTH_REQUIRED.getMessage())
                            .build()
            );
        }
        if (ARRAY_TYPE.equals(schema.getType()) && schema.getMaxItems() == null) {
            validationResults.add(
                    ValidationResult.builder()
                            .rule(Rule.REQUEST_PARAMETER_MAX_ITEMS_REQUIRED)
                            .severity(Severity.CRITICAL)
                            .schemaRef(schema.getRef())
                            .path(path)
                            .field(field)
                            .message(Rule.REQUEST_PARAMETER_MAX_ITEMS_REQUIRED.getMessage())
                            .build()
            );
        }
        return validationResults;
    }

    private Optional<OperationWrapper> getOperation(PathItem pathItem) {
        var operationWrapper = ObjectUtils.firstNonNull(
                getOperationOrNull(pathItem, PathItem::getGet, RequestMethod.GET),
                getOperationOrNull(pathItem, PathItem::getPost, RequestMethod.POST),
                getOperationOrNull(pathItem, PathItem::getPut, RequestMethod.PUT),
                getOperationOrNull(pathItem, PathItem::getDelete, RequestMethod.DELETE),
                getOperationOrNull(pathItem, PathItem::getPatch, RequestMethod.PATCH),
                getOperationOrNull(pathItem, PathItem::getOptions, RequestMethod.OPTIONS),
                getOperationOrNull(pathItem, PathItem::getHead, RequestMethod.HEAD),
                getOperationOrNull(pathItem, PathItem::getTrace, RequestMethod.TRACE)
        );
        return Optional.ofNullable(operationWrapper);
    }

    private OperationWrapper getOperationOrNull(PathItem pathItem,
                                                Function<PathItem, Operation> operationFunction,
                                                RequestMethod requestMethod) {
        return Optional.ofNullable(operationFunction.apply(pathItem))
                .map(operation -> OperationWrapper
                        .builder()
                        .operation(operation)
                        .requestMethod(requestMethod)
                        .build()
                ).orElse(null);
    }

    private long countBySeverity(List<ValidationResult> validationResults, Severity severity) {
        return validationResults.stream()
                .filter(validationResult -> severity.equals(validationResult.getSeverity()))
                .count();
    }

    private void printValidationResults(String title, List<ValidationResult> validationResults) {
        log.info("Open api [{}] validation results:", title);
        log.info("[{}] CRITICAL severities", countBySeverity(validationResults, Severity.CRITICAL));
        log.info("[{}] MAJOR severities", countBySeverity(validationResults, Severity.MAJOR));
        log.info("[{}] MINOR severities", countBySeverity(validationResults, Severity.MINOR));
        log.info("[{}] INFO severities", countBySeverity(validationResults, Severity.INFO));
        validationResults.forEach(validationResult -> log.info("Validation result: {}", validationResult));
    }
}
