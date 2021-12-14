package com.openapi.converter.exception;

import com.openapi.converter.dto.ErrorCode;

/**
 * Invalid format exception.
 *
 * @author Roman Batygin
 */
public class InvalidFormatException extends OpenApiErrorException {

    /**
     * Creates exception object.
     *
     * @param message - error message
     */
    public InvalidFormatException(String message) {
        super(ErrorCode.INVALID_FORMAT, message);
    }
}
