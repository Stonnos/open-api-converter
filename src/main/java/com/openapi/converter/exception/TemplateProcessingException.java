package com.openapi.converter.exception;

/**
 * Template processing exception.
 *
 * @author Roman Batygin
 */
public class TemplateProcessingException extends RuntimeException {

    /**
     * Creates template processing exception.
     *
     * @param message - message
     */
    public TemplateProcessingException(String message) {
        super(message);
    }
}
