package com.openapi.converter.dto.openapi;

import lombok.Data;

import java.util.Map;

/**
 * Media type model.
 *
 * @author Roman Batygin
 */
@Data
public class MediaType {

    /**
     * Schema model
     */
    private Schema schema;
    /**
     * Examples map
     */
    private Map<String, Example> examples;
    /**
     * Example value as map of properties
     */
    private Object example;
}
