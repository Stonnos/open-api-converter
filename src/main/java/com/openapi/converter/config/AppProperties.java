package com.openapi.converter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

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
