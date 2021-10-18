package com.openapi.converter.model.report;

import lombok.Data;

/**
 * Request parameter report.
 *
 * @author Roman Batygin
 */
@Data
public class RequestParameterReport {

    /**
     * Parameter name
     */
    private String name;
    /**
     * Description
     */
    private String description;
    /**
     * Is required?
     */
    private boolean required;
    /**
     * Parameter location
     */
    private String in;
    /**
     * Example value
     */
    private String example;
    /**
     * Parameter schema
     */
    private SchemaReport schema;
}
