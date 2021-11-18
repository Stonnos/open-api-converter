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
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Collections;
import java.util.List;
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
        validationResults.addAll(validatePaths(openAPI));
        log.info("Open api [{}] validation has been finished", title);
        printValidationResults(title, validationResults);
        return validationResults;
    }

    private long countBySeverity(List<ValidationResult> validationResults, Severity severity) {
        return validationResults.stream()
                .filter(validationResult -> severity.equals(validationResult.getSeverity()))
                .count();
    }

    private void printValidationResults(String title, List<ValidationResult> validationResults) {
        if (!CollectionUtils.isEmpty(validationResults)) {
            log.info("Open api [{}] validation results:", title);
            log.info("[{}] CRITICAL severities", countBySeverity(validationResults, Severity.CRITICAL));
            log.info("[{}] MAJOR severities", countBySeverity(validationResults, Severity.MAJOR));
            log.info("[{}] MINOR severities", countBySeverity(validationResults, Severity.MINOR));
            log.info("[{}] INFO severities", countBySeverity(validationResults, Severity.INFO));
            validationResults.forEach(validationResult -> log.info("Validation result: {}", validationResult));
        }
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
                    validationResults.addAll(validateParameterSchema(path, parameter.getName(), parameter.getSchema()));
                }
            });
        }
        return validationResults;
    }

    private List<ValidationResult> validateParameterSchema(String path, String parameterName, Schema schema) {
        List<ValidationResult> validationResults = newArrayList();
        if (INTEGER_TYPE.equals(schema.getType()) && schema.getMaximum() == null) {
            validationResults.add(
                    ValidationResult.builder()
                            .rule(Rule.REQUEST_PARAMETER_MAXIMUM_REQUIRED)
                            .severity(Severity.CRITICAL)
                            .path(path)
                            .field(parameterName)
                            .message(Rule.REQUEST_PARAMETER_MAXIMUM_REQUIRED.getMessage())
                            .build()
            );
        }
        if (STRING_TYPE.equals(schema.getType()) && schema.getMaxLength() == null) {
            validationResults.add(
                    ValidationResult.builder()
                            .rule(Rule.REQUEST_PARAMETER_MAX_LENGTH_REQUIRED)
                            .severity(Severity.CRITICAL)
                            .path(path)
                            .field(parameterName)
                            .message(Rule.REQUEST_PARAMETER_MAX_LENGTH_REQUIRED.getMessage())
                            .build()
            );
        }
        if (ARRAY_TYPE.equals(schema.getType()) && schema.getMaxItems() == null) {
            validationResults.add(
                    ValidationResult.builder()
                            .rule(Rule.REQUEST_PARAMETER_MAX_ITEMS_REQUIRED)
                            .severity(Severity.CRITICAL)
                            .path(path)
                            .field(parameterName)
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
}
