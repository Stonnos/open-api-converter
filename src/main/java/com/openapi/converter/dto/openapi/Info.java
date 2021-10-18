package com.openapi.converter.dto.openapi;

import lombok.Data;

/**
 * Api info model.
 *
 * @author Roman Batygin
 */
@Data
public class Info {
    /**
     * Api title
     */
    private String title;
    /**
     * Api description
     */
    private String description;
    /**
     * Terms of service
     */
    private String termsOfService;
    /**
     * Contacts
     */
    private Contact contact;
    /**
     * Api version
     */
    private String version;
}
