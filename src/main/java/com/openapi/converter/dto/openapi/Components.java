package com.openapi.converter.dto.openapi;


import lombok.Data;

import java.util.Map;

/**
 * Api data transport objects model.
 *
 * @author Roman Batygin
 */
@Data
public class Components {

    /**
     * Schemas map
     */
    private Map<String, Schema> schemas;
    /**
     * Api responses map
     */
    private Map<String, ApiResponse> responses;
    /**
     * Request parameters map
     */
    private Map<String, Parameter> parameters;
    /**
     * Examples map
     */
    private Map<String, Example> examples;
    /**
     * Request bodies map
     */
    private Map<String, RequestBody> requestBodies;
    /**
     * Security schemes map
     */
    private Map<String, SecurityScheme> securitySchemes;
}
