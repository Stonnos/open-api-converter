package com.openapi.converter.model.report;

import lombok.Data;

import java.util.List;
import java.util.Map;

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
     * Api methods group by title
     */
    private Map<String, List<MethodInfo>> methods;
    /**
     * Api components list
     */
    private List<ComponentReport> components;
    /**
     * Api security schemas
     */
    private List<SecuritySchemaReport> securitySchemes;
}
