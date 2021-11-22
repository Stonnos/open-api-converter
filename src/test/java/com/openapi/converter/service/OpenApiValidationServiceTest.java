package com.openapi.converter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openapi.converter.config.AppProperties;
import com.openapi.converter.config.OpenApiConverterConfiguration;
import com.openapi.converter.dto.openapi.OpenAPI;
import lombok.Cleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.io.IOException;

/**
 * Unit tests for open api conversion.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import({OpenApiValidationService.class, OpenApiConverterConfiguration.class, ValidationResultHelper.class,
        ValidationRuleService.class})
class OpenApiValidationServiceTest {

    @Value("classpath:api-docs/eca-oauth-api-docs.json")
    private Resource resource;

    @Inject
    private ObjectMapper openApiObjectMapper;
    @Inject
    private OpenApiValidationService openApiValidationService;

    private OpenAPI openApi;

    @BeforeEach
    void init() throws IOException {
        @Cleanup var inputStream = resource.getInputStream();
        openApi = openApiObjectMapper.readValue(inputStream, OpenAPI.class);
    }

    @Test
    void testValidateOpenApi() {
        var validationResults = openApiValidationService.validate(openApi);
    }
}
