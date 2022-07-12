package com.openapi.converter.util;

import com.openapi.converter.dto.openapi.Example;
import com.openapi.converter.dto.openapi.MediaType;
import com.openapi.converter.dto.openapi.Operation;
import com.openapi.converter.dto.openapi.OperationWrapper;
import com.openapi.converter.dto.openapi.PathItem;
import com.openapi.converter.dto.openapi.Schema;
import com.openapi.converter.model.validation.Severity;
import com.openapi.converter.model.validation.ValidationResult;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Utils {

    private static final String STRING_TYPE = "string";
    private static final String ARRAY_TYPE = "array";
    private static final String BINARY_FORMAT = "binary";

    private static final List<String> NUMBER_TYPES = List.of("integer", "number");

    /**
     * Checks that schema maximum value is specified.
     *
     * @param schema - schema
     * @return {@code true} if schema maximum value is specified, {@code false} otherwise
     */
    public static boolean hasMaximum(Schema schema) {
        return StringUtils.isEmpty(schema.getType()) || !NUMBER_TYPES.contains(schema.getType()) ||
                schema.getMaximum() != null;
    }

    /**
     * Checks that schema minimum value is specified.
     *
     * @param schema - schema
     * @return {@code true} if schema minimum value is specified, {@code false} otherwise
     */
    public static boolean hasMinimum(Schema schema) {
        return StringUtils.isEmpty(schema.getType()) || !NUMBER_TYPES.contains(schema.getType()) ||
                schema.getMinimum() != null;
    }

    /**
     * Checks that schema max length value is specified.
     *
     * @param schema - schema
     * @return {@code true} if schema max length value is specified, {@code false} otherwise
     */
    public static boolean hasMaxLength(Schema schema) {
        return StringUtils.isEmpty(schema.getType()) || !STRING_TYPE.equals(schema.getType()) ||
                BINARY_FORMAT.equals(schema.getFormat()) || schema.getMaxLength() != null;
    }

    /**
     * Checks that schema max items value is specified.
     *
     * @param schema - schema
     * @return {@code true} if schema max items value is specified, {@code false} otherwise
     */
    public static boolean hasMaxItems(Schema schema) {
        return StringUtils.isEmpty(schema.getType()) || !ARRAY_TYPE.equals(schema.getType()) ||
                schema.getMaxItems() != null;
    }

    /**
     * Checks that media type has example.
     *
     * @param mediaType - media type
     * @return {@code true} if media type has example, {@code false} otherwise
     */
    public static boolean hasExample(MediaType mediaType) {
        return mediaType.getExample() != null ||
                (!CollectionUtils.isEmpty(mediaType.getExamples()) &&
                        mediaType.getExamples().values().stream().anyMatch(Utils::hasExample));
    }

    /**
     * Checks that example is not empty.
     *
     * @param example - example object
     * @return {@code true} if example is not empty, {@code false} otherwise
     */
    public static boolean hasExample(Example example) {
        return example.getValue() != null || StringUtils.isNotEmpty(example.getRef()) ||
                StringUtils.isNotEmpty(example.getExternalValue());
    }

    /**
     * Gets operation for specified path item.
     *
     * @param pathItem - path item
     * @return operation wrapper
     */
    public static Optional<OperationWrapper> getOperation(PathItem pathItem) {
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

    /**
     * Gets validation errors count with specified severity level.
     *
     * @param validationResults - validation results
     * @param severity          - severity level
     * @return validation errors count
     */
    public static long countBySeverity(List<ValidationResult> validationResults, Severity severity) {
        return validationResults.stream()
                .filter(validationResult -> severity.equals(validationResult.getSeverity()))
                .count();
    }

    private static OperationWrapper getOperationOrNull(PathItem pathItem,
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
