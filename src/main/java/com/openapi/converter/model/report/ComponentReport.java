package com.openapi.converter.model.report;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Component report.
 *
 * @author Roman Batygin
 */
@Data
@Builder
public class ComponentReport {

    /**
     * Dto name
     */
    private String name;
    /**
     * Description
     */
    private String description;
    /**
     * Fields list
     */
    private List<FieldReport> fields;
}
