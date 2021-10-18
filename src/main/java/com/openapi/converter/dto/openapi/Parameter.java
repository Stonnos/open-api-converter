package com.openapi.converter.dto.openapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

/**
 * Request parameter model.
 * @author Roman Batygin
 */
@Data
public class Parameter {
    /**
     * Parameter name
     */
    private String name;
    /**
     * Parameter location
     */
    private String in;
    /**
     * Description
     */
    private String description;
    /**
     * Is required?
     */
    private Boolean required;
    /**
     * Is deprecated?
     */
    private Boolean deprecated;
    /**
     * Allow empty value?
     */
    private Boolean allowEmptyValue;
    /**
     * Dto model reference
     */
    @JsonProperty("$ref")
    private String ref;
    /**
     * Style
     */
    private String style;
    /**
     * Explode
     */
    private Boolean explode;
    /**
     * Allow reserved
     */
    private Boolean allowReserved;
    /**
     * Schema model
     */
    private Schema schema;
    /**
     * Examples map
     */
    private Map<String, Example> examples;
    /**
     * Example value
     */
    private String example;
    /**
     * Contents map
     */
    private Map<String, MediaType> content;
}
