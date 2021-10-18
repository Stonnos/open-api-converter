package com.openapi.converter.model.report;

import lombok.Data;

import java.util.List;

/**
 * Security schema report.
 *
 * @author Roman Batygin
 */
@Data
public class SecuritySchemaReport {
    /**
     * Schema type
     */
    private String type;
    /**
     * Description
     */
    private String description;
    /**
     * Schema name
     */
    private String name;
    /**
     * Reference
     */
    private String ref;
    /**
     * Client credentials location
     */
    private String in;
    /**
     * Scheme
     */
    private String scheme;
    /**
     * Bearer format
     */
    private String bearerFormat;
    /**
     * Oauth2 flows
     */
    private List<Oauth2FlowsReport> oauth2Flows;
}
