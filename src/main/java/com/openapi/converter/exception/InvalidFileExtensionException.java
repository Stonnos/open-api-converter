package com.openapi.converter.exception;

import com.openapi.converter.dto.ErrorCode;

/**
 * Invalid file extension exception.
 *
 * @author Roman Batygin
 */
public class InvalidFileExtensionException extends OpenApiErrorException {

    /**
     * Creates exception object.
     *
     * @param message - error message
     */
    public InvalidFileExtensionException(String message) {
        super(ErrorCode.INVALID_FILE_EXTENSION, message);
    }
}
