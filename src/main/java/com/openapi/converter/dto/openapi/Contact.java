package com.openapi.converter.dto.openapi;

import lombok.Data;

/**
 * Contact info model.
 *
 * @author Roman Batygin
 */
@Data
public class Contact {
    /**
     * Contact full name
     */
    private String name;
    /**
     * Site url
     */
    private String url;
    /**
     * Email
     */
    private String email;
}
