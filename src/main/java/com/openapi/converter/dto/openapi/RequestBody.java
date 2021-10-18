package com.openapi.converter.dto.openapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

/**
 * Request body model.
 *
 * @author Roman Batygin
 */
@Data
public class RequestBody {

    /**
     * Description
     */
    private String description;
    /**
     * Contents map
     */
    private Map<String, MediaType> content;
    /**
     * Is required?
     */
    private Boolean required;
    /**
     * Dto model reference
     */
    @JsonProperty("$ref")
    private String ref;
}
