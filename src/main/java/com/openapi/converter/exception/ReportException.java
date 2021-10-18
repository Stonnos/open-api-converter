package com.openapi.converter.exception;

/**
 * Report exception.
 *
 * @author Roman Batygin
 */
public class ReportException extends RuntimeException {

    /**
     * Creates exception object.
     *
     * @param message - error message
     */
    public ReportException(String message) {
        super(message);
    }
}
