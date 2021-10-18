package com.openapi.converter.model.report;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Security requirement report.
 *
 * @author Roman Batygin
 */
@Data
@Builder
public class SecurityRequirementReport {
    /**
     * Name
     */
    private String name;
    /**
     * Scopes
     */
    private List<String> scopes;
}
