package com.openapi.converter.dto.openapi;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Api operation model.
 * @author Roman Batygin
 */
@Data
public class Operation {
    /**
     * Tags list
     */
    private List<String> tags;
    /**
     * Summary
     */
    private String summary;
    /**
     * Description
     */
    private String description;
    /**
     * Operation id
     */
    private String operationId;
    /**
     * Request parameters list
     */
    private List<Parameter> parameters;
    /**
     * Request body model
     */
    private RequestBody requestBody;
    /**
     * Api responses map
     */
    private Map<String, ApiResponse> responses;
    /**
     * Security schemes list
     */
    private List<Map<String, List<String>>> security;
    /**
     * Is endpoint deprecated?
     */
    private Boolean deprecated;
}
