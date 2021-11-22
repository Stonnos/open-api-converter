package com.openapi.converter.util;

import com.openapi.converter.dto.openapi.Operation;
import com.openapi.converter.dto.openapi.OperationWrapper;
import com.openapi.converter.dto.openapi.PathItem;
import com.openapi.converter.dto.openapi.Schema;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ObjectUtils;
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
        return NUMBER_TYPES.contains(schema.getType()) && schema.getMaximum() != null;
    }

    /**
     * Checks that schema minimum value is specified.
     *
     * @param schema - schema
     * @return {@code true} if schema minimum value is specified, {@code false} otherwise
     */
    public static boolean hasMinimum(Schema schema) {
        return NUMBER_TYPES.contains(schema.getType()) && schema.getMinimum() != null;
    }

    /**
     * Checks that schema max length value is specified.
     *
     * @param schema - schema
     * @return {@code true} if schema max length value is specified, {@code false} otherwise
     */
    public static boolean hasMaxLength(Schema schema) {
        return STRING_TYPE.equals(schema.getType()) && !BINARY_FORMAT.equals(schema.getFormat()) &&
                schema.getMaxLength() != null;
    }

    /**
     * Checks that schema max items value is specified.
     *
     * @param schema - schema
     * @return {@code true} if schema max items value is specified, {@code false} otherwise
     */
    public static boolean hasMaxItems(Schema schema) {
        return ARRAY_TYPE.equals(schema.getType()) && schema.getMaxItems() != null;
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
