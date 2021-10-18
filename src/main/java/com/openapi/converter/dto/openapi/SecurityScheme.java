package com.openapi.converter.dto.openapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Security scheme model.
 *
 * @author Roman Batygin
 */
@Data
public class SecurityScheme {

    /**
     * Schema type
     */
    private String type;
    /**
     * Description
     */
    private String description;
    /**
     * Schema name
     */
    private String name;
    /**
     * Reference
     */
    @JsonProperty("$ref")
    private String ref;
    /**
     * Client credentials location
     */
    private String in;
    /**
     * Scheme
     */
    private String scheme;
    /**
     * Bearer format
     */
    private String bearerFormat;
    /**
     * Oauth2 flows
     */
    private Oauth2Flows flows;
    /**
     * Open ID connect url
     */
    private String openIdConnectUrl;
}
