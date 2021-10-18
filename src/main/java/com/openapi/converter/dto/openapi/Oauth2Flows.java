package com.openapi.converter.dto.openapi;

import lombok.Data;

/**
 * Oauth2 flows model.
 *
 * @author Roman Batygin
 */
@Data
public class Oauth2Flows {
    /**
     * Implicit grant model
     */
    private Oauth2Flow implicit;
    /**
     * Password grant model
     */
    private Oauth2Flow password;
    /**
     * Client credentials grant model
     */
    private Oauth2Flow clientCredentials;
    /**
     * Authorization code grant model
     */
    private Oauth2Flow authorizationCode;
}
