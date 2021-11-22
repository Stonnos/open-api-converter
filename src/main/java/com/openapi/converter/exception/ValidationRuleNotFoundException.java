package com.openapi.converter.exception;

import com.openapi.converter.model.validation.Rule;

/**
 * Validation rule not found exception.
 *
 * @author Roman Batygin
 */
public class ValidationRuleNotFoundException extends RuntimeException {

    /**
     * Creates exception object.
     *
     * @param rule - rule
     */
    public ValidationRuleNotFoundException(Rule rule) {
        super(String.format("Validation rule [%s] not found", rule));
    }
}
