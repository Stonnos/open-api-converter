package com.openapi.converter.model.report;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Schema report.
 *
 * @author Roman Batygin
 */
@Data
public class SchemaReport {
    /**
     * Description
     */
    private String description;
    /**
     * Data type
     */
    private String type;
    /**
     * Data format
     */
    private String format;
    /**
     * Dto reference
     */
    private String objectTypeRef;
    /**
     * Max. value
     */
    private BigDecimal maximum;
    /**
     * Exclude maximum?
     */
    private boolean exclusiveMaximum;
    /**
     * Min. value
     */
    private BigDecimal minimum;
    /**
     * Exclude minimum?
     */
    private boolean exclusiveMinimum;
    /**
     * Max. length
     */
    private Integer maxLength;
    /**
     * Min. length
     */
    private Integer minLength;
    /**
     * Pattern value
     */
    private String pattern;
    /**
     * Max. items
     */
    private Integer maxItems;
    /**
     * Min. items
     */
    private Integer minItems;
    /**
     * Enum values
     */
    private List<String> enumValues;
    /**
     * Subclasses references
     */
    private List<String> oneOfRefs;
    /**
     * Items reports
     */
    private List<SchemaReport> itemsReport;
}
