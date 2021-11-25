package com.openapi.converter.model.validation;

/**
 * Validation rules.
 *
 * @author Roman Batygin
 */
public enum Rule {

    /**
     * Api title required
     */
    API_TITLE_REQUIRED,

    /**
     * Api description required
     */
    API_DESCRIPTION_REQUIRED,

    /**
     * Api version required
     */
    API_VERSION_REQUIRED,

    /**
     * Api contact name required
     */
    API_CONTACT_NAME_REQUIRED,

    /**
     * Api contact email required
     */
    API_CONTACT_EMAIL_REQUIRED,

    /**
     * Api operation description required
     */
    API_OPERATION_DESCRIPTION_REQUIRED,

    /**
     * Api operation summary required
     */
    API_OPERATION_SUMMARY_REQUIRED,

    /**
     * Request body example required
     */
    REQUEST_BODY_EXAMPLE_REQUIRED,

    /**
     * Request parameter description required
     */
    REQUEST_PARAMETER_DESCRIPTION_REQUIRED,

    /**
     * Request parameter example required
     */
    REQUEST_PARAMETER_EXAMPLE_REQUIRED,

    /**
     * Request parameter max length required
     */
    REQUEST_PARAMETER_MAX_LENGTH_REQUIRED,

    /**
     * Request parameter minimum required
     */
    REQUEST_PARAMETER_MINIMUM_REQUIRED,

    /**
     * Request parameter maximum required
     */
    REQUEST_PARAMETER_MAXIMUM_REQUIRED,

    /**
     * Request parameter max items required
     */
    REQUEST_PARAMETER_MAX_ITEMS_REQUIRED,

    /**
     * Schema description required
     */
    SCHEMA_DESCRIPTION_REQUIRED,

    /**
     * Schema property max length required
     */
    SCHEMA_PROPERTY_MAX_LENGTH_REQUIRED,

    /**
     * Schema property minimum required
     */
    SCHEMA_PROPERTY_MINIMUM_REQUIRED,

    /**
     * Schema property maximum required
     */
    SCHEMA_PROPERTY_MAXIMUM_REQUIRED,

    /**
     * Schema property description required
     */
    SCHEMA_PROPERTY_DESCRIPTION_REQUIRED,

    /**
     * Schema property max items required
     */
    SCHEMA_PROPERTY_MAX_ITEMS_REQUIRED,

    /**
     * Schema property example required
     */
    SCHEMA_PROPERTY_EXAMPLE_REQUIRED,

    /**
     * Api response description required
     */
    API_RESPONSE_DESCRIPTION_REQUIRED,

    /**
     * Api response example required
     */
    API_RESPONSE_EXAMPLE_REQUIRED
}
