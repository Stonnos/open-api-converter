package com.openapi.converter.exception;

import com.openapi.converter.dto.ErrorCode;

/**
 * External integration error exception.
 *
 * @author Roman Batygin
 */
public class ExternalIntegrationErrorException extends OpenApiErrorException {

    /**
     * Creates exception object.
     *
     * @param message - error message
     */
    public ExternalIntegrationErrorException(String message) {
        super(ErrorCode.EXTERNAL_INTEGRATION_ERROR, message);
    }
}
