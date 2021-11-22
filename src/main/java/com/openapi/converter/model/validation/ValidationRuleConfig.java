package com.openapi.converter.model.validation;

import lombok.Data;

/**
 * Validation rule config model.
 *
 * @author Roman Batygin
 */
@Data
public class ValidationRuleConfig {

    /**
     * Severity level
     */
    private Severity severity;

    /**
     * Rule message
     */
    private String message;
}
