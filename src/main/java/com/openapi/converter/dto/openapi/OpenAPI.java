package com.openapi.converter.dto.openapi;

import lombok.Data;

import java.util.Map;

/**
 * Open api model.
 *
 * @author Roman Batygin
 */
@Data
public class OpenAPI {

    /**
     * Version
     */
    private String openapi = "3.0.1";
    /**
     * Api info
     */
    private Info info;
    /**
     * Endpoints map
     */
    private Map<String, PathItem> paths;
    /**
     * Components map
     */
    private Components components;
}
