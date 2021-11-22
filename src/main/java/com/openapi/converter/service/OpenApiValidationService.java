package com.openapi.converter.service;

import com.openapi.converter.dto.openapi.ApiResponse;
import com.openapi.converter.dto.openapi.Info;
import com.openapi.converter.dto.openapi.OpenAPI;
import com.openapi.converter.dto.openapi.Operation;
import com.openapi.converter.dto.openapi.Parameter;
import com.openapi.converter.dto.openapi.Schema;
import com.openapi.converter.exception.OperationNotSpecifiedException;
import com.openapi.converter.model.validation.Rule;
import com.openapi.converter.model.validation.Severity;
import com.openapi.converter.model.validation.ValidationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static com.openapi.converter.util.Utils.countBySeverity;
import static com.openapi.converter.util.Utils.getOperation;
import static com.openapi.converter.util.Utils.hasMaxItems;
import static com.openapi.converter.util.Utils.hasMaxLength;
import static com.openapi.converter.util.Utils.hasMaximum;

/**
 * Open API validation service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OpenApiValidationService {

    private final ValidationResultHelper validationResultHelper;

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
        validationResults.addAll(validateSchemas(openAPI));
        validationResults.sort(Comparator.comparing(ValidationResult::getSeverity));
        log.info("Open api [{}] validation has been finished", title);
        printValidationResults(title, validationResults);
        return validationResults;
    }

    private List<ValidationResult> validateApiInfo(OpenAPI openAPI) {
        List<ValidationResult> validationResults = newArrayList();
        if (Optional.ofNullable(openAPI.getInfo()).isEmpty() || StringUtils.isEmpty(openAPI.getInfo().getTitle())) {
            validationResults.add(
                    validationResultHelper.buildValidationResult(Rule.API_TITLE_REQUIRED)
            );
        }
        if (Optional.ofNullable(openAPI.getInfo()).isEmpty() || StringUtils.isEmpty(openAPI.getInfo().getVersion())) {
            validationResults.add(
                    validationResultHelper.buildValidationResult(Rule.API_VERSION_REQUIRED)
            );
        }
        if (Optional.ofNullable(openAPI.getInfo()).isEmpty() ||
                StringUtils.isEmpty(openAPI.getInfo().getDescription())) {
            validationResults.add(
                    validationResultHelper.buildValidationResult(Rule.API_DESCRIPTION_REQUIRED)
            );
        }
        if (Optional.ofNullable(openAPI.getInfo()).map(Info::getContact).isEmpty() ||
                StringUtils.isEmpty(openAPI.getInfo().getContact().getName())) {
            validationResults.add(
                    validationResultHelper.buildValidationResult(Rule.API_CONTACT_NAME_REQUIRED)
            );
        }
        if (Optional.ofNullable(openAPI.getInfo()).map(Info::getContact).isEmpty() ||
                StringUtils.isEmpty(openAPI.getInfo().getContact().getEmail())) {
            validationResults.add(
                    validationResultHelper.buildValidationResult(Rule.API_CONTACT_EMAIL_REQUIRED)
            );
        }
        return validationResults;
    }

    private List<ValidationResult> validateRequestBody(String path, Operation operation) {
        List<ValidationResult> validationResults = newArrayList();
        var requestBody = operation.getRequestBody();
        if (requestBody != null && !CollectionUtils.isEmpty(requestBody.getContent())) {
            var mediaType = requestBody.getContent().entrySet().iterator().next();
            var schema = mediaType.getValue().getSchema();
            if (!CollectionUtils.isEmpty(schema.getProperties())) {
                schema.getProperties().forEach((fieldName, schemaVal) ->
                        validationResults.addAll(validateSchema(path, fieldName, schemaVal))
                );
            }
            if (mediaType.getValue().getExample() == null) {
                validationResults.add(
                        validationResultHelper.buildValidationResult(Rule.REQUEST_BODY_EXAMPLE_REQUIRED, path,
                                schema.getRef(), null)
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
                    .orElseThrow(() -> new OperationNotSpecifiedException(
                            String.format("Operation not spcified for endpoint [%s]", path)));
            var operation = operationModel.getOperation();
            validationResults.addAll(validateOperation(path, operation));
            validationResults.addAll(validateRequestBody(path, operation));
            validationResults.addAll(validateResponses(path, operation));
        });
        return validationResults;
    }

    private List<ValidationResult> validateOperation(String path, Operation operation) {
        List<ValidationResult> validationResults = newArrayList();
        if (StringUtils.isEmpty(operation.getDescription())) {
            validationResults.add(
                    validationResultHelper.buildValidationResult(Rule.API_OPERATION_DESCRIPTION_REQUIRED, path)
            );
        }
        if (StringUtils.isEmpty(operation.getSummary())) {
            validationResults.add(
                    validationResultHelper.buildValidationResult(Rule.API_OPERATION_SUMMARY_REQUIRED, path)
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
                            validationResultHelper.buildValidationResult(Rule.REQUEST_PARAMETER_DESCRIPTION_REQUIRED,
                                    path, parameter.getName())
                    );
                }
                if (StringUtils.isEmpty(parameter.getExample())) {
                    validationResults.add(
                            validationResultHelper.buildValidationResult(Rule.REQUEST_PARAMETER_EXAMPLE_REQUIRED,
                                    path, parameter.getName())
                    );
                }
                if (parameter.getSchema() != null) {
                    validationResults.addAll(
                            validateRequestParameterSchema(path, parameter.getName(), parameter.getSchema()));
                }
            });
        }
        return validationResults;
    }

    private List<ValidationResult> validateSchemas(OpenAPI openAPI) {
        if (openAPI.getComponents() == null || CollectionUtils.isEmpty(openAPI.getComponents().getSchemas())) {
            return Collections.emptyList();
        }
        List<ValidationResult> validationResults = newArrayList();
        openAPI.getComponents().getSchemas().forEach((ref, schema) -> {
            if (StringUtils.isEmpty(schema.getDescription())) {
                validationResults.add(
                        validationResultHelper.buildValidationResult(Rule.SCHEMA_DESCRIPTION_REQUIRED, null,
                                schema.getRef(), null)
                );
            }
            schema.getProperties().forEach((fieldName, schemaVal) ->
                    validationResults.addAll(validateSchema(ref, fieldName, schemaVal))
            );
        });
        return validationResults;
    }

    private List<ValidationResult> validateSchema(String path, String field, Schema schema) {
        List<ValidationResult> validationResults = newArrayList();
        if (StringUtils.isEmpty(schema.getDescription())) {
            validationResults.add(
                    validationResultHelper.buildValidationResult(Rule.SCHEMA_PROPERTY_DESCRIPTION_REQUIRED, path,
                            schema.getRef(), field)
            );
        }
        if (StringUtils.isEmpty(schema.getExample())) {
            validationResults.add(
                    validationResultHelper.buildValidationResult(Rule.SCHEMA_PROPERTY_EXAMPLE_REQUIRED, path,
                            schema.getRef(), field)
            );
        }
        if (!hasMaximum(schema)) {
            validationResults.add(
                    validationResultHelper.buildValidationResult(Rule.SCHEMA_PROPERTY_MAXIMUM_REQUIRED, path,
                            schema.getRef(), field)
            );
        }
        if (!hasMaximum(schema)) {
            validationResults.add(
                    validationResultHelper.buildValidationResult(Rule.SCHEMA_PROPERTY_MINIMUM_REQUIRED, path,
                            schema.getRef(), field)
            );
        }
        if (!hasMaxLength(schema)) {
            validationResults.add(
                    validationResultHelper.buildValidationResult(Rule.SCHEMA_PROPERTY_MAX_LENGTH_REQUIRED, path,
                            schema.getRef(), field)
            );
        }
        if (!hasMaxItems(schema)) {
            validationResults.add(
                    validationResultHelper.buildValidationResult(Rule.SCHEMA_PROPERTY_MAX_ITEMS_REQUIRED, path,
                            schema.getRef(), field)
            );
        }
        return validationResults;
    }

    private List<ValidationResult> validateRequestParameterSchema(String path, String parameter, Schema schema) {
        List<ValidationResult> validationResults = newArrayList();
        if (!hasMaximum(schema)) {
            validationResults.add(
                    validationResultHelper.buildValidationResult(Rule.REQUEST_PARAMETER_MAXIMUM_REQUIRED, path,
                            parameter)
            );
        }
        if (!hasMaximum(schema)) {
            validationResults.add(
                    validationResultHelper.buildValidationResult(Rule.REQUEST_PARAMETER_MINIMUM_REQUIRED, path,
                            parameter)
            );
        }
        if (!hasMaxLength(schema)) {
            validationResults.add(
                    validationResultHelper.buildValidationResult(Rule.REQUEST_PARAMETER_MAX_LENGTH_REQUIRED, path,
                            parameter)
            );
        }
        if (!hasMaxItems(schema)) {
            validationResults.add(
                    validationResultHelper.buildValidationResult(Rule.REQUEST_PARAMETER_MAX_ITEMS_REQUIRED, path,
                            parameter)
            );
        }
        return validationResults;
    }

    private List<ValidationResult> validateResponses(String path, Operation operation) {
        if (CollectionUtils.isEmpty(operation.getResponses())) {
            return Collections.emptyList();
        }
        List<ValidationResult> validationResults = newArrayList();
        operation.getResponses().forEach((code, response) ->
                validationResults.addAll(validateResponse(path, code, response))
        );
        return validationResults;
    }

    private List<ValidationResult> validateResponse(String path, String responseCode, ApiResponse apiResponse) {
        if (CollectionUtils.isEmpty(apiResponse.getContent())) {
            return Collections.emptyList();
        }
        List<ValidationResult> validationResults = newArrayList();
        var mediaType = apiResponse.getContent().entrySet().iterator().next();
        if (StringUtils.isEmpty(apiResponse.getDescription())) {
            validationResults.add(
                    validationResultHelper.buildResponseValidationResult(Rule.API_RESPONSE_DESCRIPTION_REQUIRED, path,
                            responseCode)
            );
        }
        if (mediaType.getValue().getExample() == null) {
            validationResults.add(
                    validationResultHelper.buildResponseValidationResult(Rule.API_RESPONSE_EXAMPLE_REQUIRED, path,
                            responseCode)
            );
        }
        return validationResults;
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
