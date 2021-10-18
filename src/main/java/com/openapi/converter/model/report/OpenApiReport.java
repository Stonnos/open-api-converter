package com.openapi.converter.model.report;

import lombok.Data;

import java.util.List;

/**
 * Open API report.
 * @author Roman Batygin
 */
@Data
public class OpenApiReport {
    /**
     * Report title
     */
    private String title;
    /**
     * Description
     */
    private String description;
    /**
     * Author name
     */
    private String author;
    /**
     * Email
     */
    private String email;
    /**
     * Api methods list
     */
    private List<MethodInfo> methods;
    /**
     * Api components list
     */
    private List<ComponentReport> components;
    /**
     * Api security schemas
     */
    private List<SecuritySchemaReport> securitySchemes;
}
