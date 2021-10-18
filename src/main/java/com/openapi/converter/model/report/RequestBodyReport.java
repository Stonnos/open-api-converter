package com.openapi.converter.model.report;

import lombok.Data;

import java.util.List;

/**
 * Request body report.
 *
 * @author Roman Batygin
 */
@Data
public class RequestBodyReport {
    /**
     * Content type
     */
    private String contentType;
    /**
     * Is required?
     */
    private String required;
    /**
     * Dto reference
     */
    private String bodyRef;
    /**
     * References for sub classes
     */
    private List<String> oneOfRefs;
    /**
     * Example value
     */
    private String example;
    /**
     * Field properties (used for multipart form data requests)
     */
    private List<FieldReport> schemaProperties;
}
