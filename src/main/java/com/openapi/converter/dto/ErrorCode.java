package com.openapi.converter.dto;

/**
 * Error code enum.
 *
 * @author Roman Batygin
 */
public enum ErrorCode {

    /**
     * Invalid file extension
     */
    INVALID_FILE_EXTENSION,

    /**
     * Invalid file format
     */
    INVALID_FILE_FORMAT,

    /**
     * Operation not specified
     */
    OPERATION_NOT_SPECIFIED,

    /**
     * Invalid format
     */
    INVALID_FORMAT,

    /**
     * External integration error
     */
    EXTERNAL_INTEGRATION_ERROR,

    /**
     * Validation error
     */
    VALIDATION_ERROR
}
