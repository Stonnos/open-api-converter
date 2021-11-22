package com.openapi.converter.service;

import com.openapi.converter.model.validation.Rule;
import com.openapi.converter.model.validation.ValidationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Validation result helper class.
 *
 * @author Roman Batygin
 */
@Service
@RequiredArgsConstructor
public class ValidationResultHelper {

    private final ValidationRuleService validationRuleService;

    /**
     * Builds validation result.
     *
     * @param rule - rule code
     * @return validation result
     */
    public ValidationResult buildValidationResult(Rule rule) {
        return builder(rule).build();
    }

    /**
     * Builds validation result.
     *
     * @param rule      - rule code
     * @param path      - endpoint
     * @return validation result
     */
    public ValidationResult buildValidationResult(Rule rule, String path) {
        return builder(rule)
                .path(path)
                .build();
    }

    /**
     * Builds validation result.
     *
     * @param rule      - rule code
     * @param path      - endpoint
     * @param parameter - request parameter
     * @return validation result
     */
    public ValidationResult buildValidationResult(Rule rule, String path, String parameter) {
        return builder(rule)
                .path(path)
                .parameter(parameter)
                .build();
    }

    /**
     * Builds validation result.
     *
     * @param rule      - rule code
     * @param path      - endpoint
     * @param schemaRef - schema reference
     * @param parameter - schema property
     * @return validation result
     */
    public ValidationResult buildValidationResult(Rule rule, String path, String schemaRef, String parameter) {
        return builder(rule)
                .path(path)
                .schemaRef(schemaRef)
                .parameter(parameter)
                .build();
    }

    /**
     * Builds validation result.
     *
     * @param rule         - rule code
     * @param path         - endpoint
     * @param responseCode - response code
     * @return validation result
     */
    public ValidationResult buildResponseValidationResult(Rule rule, String path, String responseCode) {
        return builder(rule)
                .path(path)
                .responseCode(responseCode)
                .build();
    }

    private ValidationResult.ValidationResultBuilder builder(Rule rule) {
        var validationRuleConfig = validationRuleService.getValidationRuleConfig(rule);
        return ValidationResult.builder()
                .rule(rule)
                .severity(validationRuleConfig.getSeverity())
                .message(validationRuleConfig.getMessage());
    }
}
