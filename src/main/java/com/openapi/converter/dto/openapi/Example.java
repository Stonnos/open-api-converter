package com.openapi.converter.dto.openapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Example model.
 *
 * @author Roman Batygin
 */
@Data
public class Example {

    /**
     * Summary
     */
    private String summary;
    /**
     * Description
     */
    private String description;
    /**
     * Example value
     */
    private String value;
    /**
     * External value
     */
    private String externalValue;
    /**
     * Dto model reference
     */
    @JsonProperty("$ref")
    private String ref;
}
