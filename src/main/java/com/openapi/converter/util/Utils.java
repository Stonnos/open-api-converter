package com.openapi.converter.util;

import com.openapi.converter.dto.openapi.Schema;
import lombok.experimental.UtilityClass;

import java.util.List;

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
}
