package com.openapi.converter.model.validation;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidationResult {

    private Rule rule;

    private Severity severity;

    private String path;

    private String schemaRef;

    private String parameter;

    private String responseCode;

    private String message;
}
