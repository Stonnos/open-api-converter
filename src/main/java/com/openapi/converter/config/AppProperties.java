package com.openapi.converter.config;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Application properties.
 *
 * @author Roman Batygin
 */
@Validated
@Data
@ConfigurationProperties("app")
public class AppProperties {

    /**
     * Template location
     */
    @NotEmpty(message = "Template location must be specified!")
    private String templateLocation;

    /**
     * Validation rules location
     */
    @NotEmpty(message = "Validation rules location!")
    private String validationRulesLocation;
}
