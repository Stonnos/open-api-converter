package com.openapi.converter.dto.openapi;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Schema model.
 *
 * @author Roman Batygin
 */
@Data
public class Schema {
    /**
     * Name
     */
    private String name;
    /**
     * Title
     */
    private String title;
    /**
     * Multiple of
     */
    private BigDecimal multipleOf;
    /**
     * Max. value
     */
    private BigDecimal maximum;
    /**
     * Exclude max. value?
     */
    private Boolean exclusiveMaximum;
    /**
     * Min. value
     */
    private BigDecimal minimum;
    /**
     * Exclude min. value?
     */
    private Boolean exclusiveMinimum;
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
     * Collection max items
     */
    private Integer maxItems;
    /**
     * Collection min items
     */
    private Integer minItems;
    /**
     * Unique items?
     */
    private Boolean uniqueItems;
    /**
     * MAx. properties
     */
    private Integer maxProperties;
    /**
     * Min. properties
     */
    private Integer minProperties;
    /**
     * Required fields list
     */
    private List<String> required;
    /**
     * Data type
     */
    private String type;
    /**
     * Not schema
     */
    private Schema not;
    /**
     * Properties map
     */
    private Map<String, Schema> properties;
    /**
     * Additional properties map
     */
    private Map<String, String> additionalProperties;
    /**
     * Schema description
     */
    private String description;
    /**
     * Data format
     */
    private String format;
    /**
     * Dto model reference
     */
    @JsonProperty("$ref")
    private String ref;
    /**
     * Is nullable?
     */
    private Boolean nullable;
    /**
     * Is read only?
     */
    private Boolean readOnly;
    /**
     * Is write only?
     */
    private Boolean writeOnly;
    /**
     * Example value
     */
    private String example;
    /**
     * Is deprecated?
     */
    private Boolean deprecated;
    /**
     * Enum values list
     */
    @JsonProperty("enum")
    private List<String> enums;
    /**
     * Items schema for array
     */
    private Schema items;
    /**
     * Sub types schemas list
     */
    private List<Schema> oneOf;
    /**
     * Parent type schemas list
     */
    private List<Schema> allOf;
}
