package com.openapi.converter.dto.openapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

/**
 * Api response model.
 *
 * @author Roman Batygin
 */
@Data
public class ApiResponse {

    /**
     * Response description
     */
    private String description;
    /**
     * Content types
     */
    private Map<String, MediaType> content;
    /**
     * Dto model reference
     */
    @JsonProperty("$ref")
    private String $ref;
}
