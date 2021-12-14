package com.openapi.converter.exception;

import com.openapi.converter.dto.ErrorCode;

/**
 * Operation not specified exception.
 *
 * @author Roman Batygin
 */
public class OperationNotSpecifiedException extends OpenApiErrorException {

    /**
     * Creates exception object.
     *
     * @param message - error message
     */
    public OperationNotSpecifiedException(String message) {
        super(ErrorCode.OPERATION_NOT_SPECIFIED, message);
    }
}
