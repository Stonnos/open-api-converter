package com.openapi.converter.dto;

import lombok.experimental.UtilityClass;

/**
 * Constraints utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Constraints {

    public static final int REPORT_FILE_NAME_MAX_LENGTH = 50;

    public static final int STRING_MIN_LENGTH_1 = 1;

    public static final int STRING_MAX_LENGTH_255 = 255;

    public static final int OPEN_API_REQUESTS_MAX_SIZE = 50;
}
