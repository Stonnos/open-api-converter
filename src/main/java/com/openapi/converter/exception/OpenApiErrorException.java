package com.openapi.converter.exception;

import com.openapi.converter.dto.ErrorCode;
import lombok.Getter;

/**
 * Exception throws in case of open api errors.
 *
 * @author Roman Batygin
 */
public class OpenApiErrorException extends RuntimeException {

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
    public OpenApiErrorException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
