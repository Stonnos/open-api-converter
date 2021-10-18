package com.openapi.converter.model.report;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Method info report.
 *
 * @author Roman Batygin
 */
@Data
@Builder
public class MethodInfo {
    /**
     * Request type
     */
    private String requestType;
    /**
     * Endpoint
     */
    private String endpoint;
    /**
     * Summary
     */
    private String summary;
    /**
     * Description
     */
    private String description;
    /**
     * Request body report
     */
    private RequestBodyReport requestBody;
    /**
     * Request parameters report
     */
    private List<RequestParameterReport> requestParameters;
    /**
     * Api responses report
     */
    private List<ApiResponseReport> apiResponses;
    /**
     * Security requirements report
     */
    private List<SecurityRequirementReport> security;
}
