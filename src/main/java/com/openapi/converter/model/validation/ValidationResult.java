package com.openapi.converter.model.validation;

import lombok.Builder;
import lombok.Data;

/**
 * Validation result model.
 * @author Roman Batygin
 */
@Data
@Builder
public class ValidationResult {

    /**
     * Rule code
     */
    private Rule rule;

    /**
     * Severity level
     */
    private Severity severity;

    /**
     * Endpoint path
     */
    private String path;

    /**
     * Schema reference
     */
    private String schemaRef;

    /**
     * Request parameter or schema property
     */
    private String parameterOrProperty;

    /**
     * Response code
     */
    private String responseCode;

    /**
     * Message
     */
    private String message;
}
