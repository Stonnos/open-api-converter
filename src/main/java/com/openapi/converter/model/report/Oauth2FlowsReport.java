package com.openapi.converter.model.report;

import lombok.Data;

import java.util.List;

/**
 * Oauth2 flows report.
 * @author Roman Batygin
 */
@Data
public class Oauth2FlowsReport {
    /**
     * Grant type
     */
    private String grantType;
    /**
     * Authorization url
     */
    private String authorizationUrl;
    /**
     * Token url
     */
    private String tokenUrl;
    /**
     * Refresh url
     */
    private String refreshUrl;
    /**
     * Scopes list
     */
    private List<String> scopes;
}
