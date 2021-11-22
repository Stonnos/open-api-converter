package com.openapi.converter.model.validation;

/**
 * Validation rules.
 *
 * @author Roman Batygin
 */
public enum Rule {

    API_TITLE_REQUIRED,

    API_DESCRIPTION_REQUIRED,

    API_VERSION_REQUIRED,

    API_CONTACT_NAME_REQUIRED,

    API_CONTACT_EMAIL_REQUIRED,

    API_OPERATION_DESCRIPTION_REQUIRED,

    API_OPERATION_SUMMARY_REQUIRED,

    REQUEST_PARAMETER_DESCRIPTION_REQUIRED,

    REQUEST_PARAMETER_EXAMPLE_REQUIRED,

    REQUEST_PARAMETER_MAX_LENGTH_REQUIRED,

    REQUEST_PARAMETER_MINIMUM_REQUIRED,

    REQUEST_PARAMETER_MAXIMUM_REQUIRED,

    REQUEST_PARAMETER_MAX_ITEMS_REQUIRED,

    SCHEMA_DESCRIPTION_REQUIRED,

    SCHEMA_PROPERTY_MAX_LENGTH_REQUIRED,

    SCHEMA_PROPERTY_MINIMUM_REQUIRED,

    SCHEMA_PROPERTY_MAXIMUM_REQUIRED,

    SCHEMA_PROPERTY_DESCRIPTION_REQUIRED,

    SCHEMA_PROPERTY_MAX_ITEMS_REQUIRED,

    SCHEMA_PROPERTY_EXAMPLE_REQUIRED,

    API_RESPONSE_DESCRIPTION_REQUIRED,

    API_RESPONSE_EXAMPLE_REQUIRED
}
