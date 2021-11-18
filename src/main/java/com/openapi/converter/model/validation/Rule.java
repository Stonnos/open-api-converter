package com.openapi.converter.model.validation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Rule {

    API_OPERATION_DESCRIPTION_REQUIRED("Api operation 'description' field must be not empty"),

    API_OPERATION_SUMMARY_REQUIRED("Api operation 'summary' field must be not empty"),

    REQUEST_PARAMETER_DESCRIPTION_REQUIRED("Request parameter description must be not empty"),

    REQUEST_PARAMETER_EXAMPLE_REQUIRED("Request parameter example must be not empty"),

    REQUEST_PARAMETER_MAX_LENGTH_REQUIRED("Request parameter max length must be specified"),

    REQUEST_PARAMETER_MAXIMUM_REQUIRED("Request parameter maximum value must be specified"),

    REQUEST_PARAMETER_MAX_ITEMS_REQUIRED("Request parameter max items value must be specified");

    private final String message;
}
