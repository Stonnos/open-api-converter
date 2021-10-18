package com.openapi.converter.exception;

import com.openapi.converter.dto.ErrorCode;
import lombok.Getter;

/**
 * Exception throws in case of validation errors.
 *
 * @author Roman Batygin
 */
public class ValidationErrorException extends RuntimeException {

    /**
     * Error code.
     */
    @Getter
    private final ErrorCode errorCode;

    /**
     * Creates exception object.
     *
     * @param errorCode - error code
     * @param message   - error message
     */
    public ValidationErrorException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
