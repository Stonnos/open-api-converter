package com.openapi.converter.model.report;

import lombok.Data;

/**
 * Api response report.
 *
 * @author Roman Batygin
 */
@Data
public class ApiResponseReport {

    /**
     * Response code
     */
    private String responseCode;
    /**
     * Description
     */
    private String description;
    /**
     * Content type
     */
    private String contentType;
    /**
     * Example value
     */
    private String example;
    /**
     * Example external reference
     */
    private String exampleExternalRef;
    /**
     * Example external reference key
     */
    private String exampleExternalRefKey;
    /**
     * Schema report
     */
    private SchemaReport schema;
}
