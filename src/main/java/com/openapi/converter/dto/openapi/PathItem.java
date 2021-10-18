package com.openapi.converter.dto.openapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 *
 * Path item model.
 * @author Roman Batygin
 */
@Data
public class PathItem {
    /**
     * Summary
     */
    private String summary;
    /**
     * Description
     */
    private String description;
    /**
     * GET operation
     */
    private Operation get;
    /**
     * PUT operation
     */
    private Operation put;
    /**
     * POST operation
     */
    private Operation post;
    /**
     * DELETE operation
     */
    private Operation delete;
    /**
     * OPTIONS operation
     */
    private Operation options;
    /**
     * HEAD operation
     */
    private Operation head;
    /**
     * PATCH operation
     */
    private Operation patch;
    /**
     * TRACE operation
     */
    private Operation trace;
    /**
     * Parameters list
     */
    private List<Parameter> parameters;
    /**
     * Dto model reference
     */
    @JsonProperty("$ref")
    private String ref;
}
