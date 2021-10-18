package com.openapi.converter.model.report;

import lombok.Builder;
import lombok.Data;

/**
 * Field report.
 *
 * @author Roman Batygin
 */
@Data
@Builder
public class FieldReport {
    /**
     * Field name
     */
    private String fieldName;
    /**
     * Description
     */
    private String description;
    /**
     * Is required?
     */
    private boolean required;
    /**
     * Schema report
     */
    private SchemaReport schema;
}
