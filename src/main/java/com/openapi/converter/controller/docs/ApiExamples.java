package com.openapi.converter.controller.docs;

import lombok.experimental.UtilityClass;

/**
 * Api examples.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ApiExamples {

    /**
     * Open api reports request example
     */
    public static final String OPEN_API_REPORTS_REQUEST_EXAMPLE = "[{\"url\": \"http://localhost:8080/eca-server\", " +
            "\"reportFileName\": \"eca-server-api-docs\"}, {\"url\": \"http://localhost:8080/eca-oauth\", " +
            "\"reportFileName\": \"eca-oauth-api-docs\"}, {\"url\": \"http://localhost:8080/eca-ds\", " +
            "\"reportFileName\": \"eca-ds-api-docs\"}, {\"url\": \"http://localhost:8080/eca-ers\", " +
            "\"reportFileName\": \"eca-ers-api-docs\"}, {\"url\": \"http://localhost:8080/eca-mail\", " +
            "\"reportFileName\": \"eca-mail-api-docs\"}, {\"url\": \"http://localhost:8080/eca-audit-log\", " +
            "\"reportFileName\": \"eca-audit-log-api-docs\"}, {\"url\": \"http://localhost:8080/external-api\", " +
            "\"reportFileName\": \"external-api-docs\"}]";
}
