package com.openapi.converter.exception;

import com.openapi.converter.dto.ErrorCode;

/**
 * Invalid file format exception.
 *
 * @author Roman Batygin
 */
public class InvalidFileFormatException extends OpenApiErrorException {

    /**
     * Creates exception object.
     *
     * @param message - error message
     */
    public InvalidFileFormatException(String message) {
        super(ErrorCode.INVALID_FILE_FORMAT, message);
    }
}
