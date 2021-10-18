package com.openapi.converter.dto.openapi;

import lombok.Data;

import java.util.Map;

/**
 * Oauth2 flow model.
 *
 * @author Roman Batygin
 */
@Data
public class Oauth2Flow {
    /**
     * Authorization code url
     */
    private String authorizationUrl;
    /**
     * Token url
     */
    private String tokenUrl;
    /**
     * Refresh token url
     */
    private String refreshUrl;
    /**
     * Scopes map
     */
    private Map<String, String> scopes;
}
