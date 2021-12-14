package com.openapi.converter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openapi.converter.dto.openapi.OpenAPI;
import lombok.Cleanup;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for open api conversion.
 *
 * @author Roman Batygin
 */
@SpringBootTest
class OpenApiConverterTest {

    private static final String TARGET_FILE_FORMAT = "%s/target/%s";
    private static final String ADOC_FORMAT = "%s.adoc";

    @Value("classpath*:api-docs/**/*.json")
    private Resource[] resources;

    @Inject
    private ObjectMapper openApiObjectMapper;
    @Inject
    private OpenApiReportProcessor openApiReportProcessor;

    @Test
    void testApiDocConversion() throws IOException {
        for (Resource resource : resources) {
            @Cleanup var inputStream = resource.getInputStream();
            var openApi = openApiObjectMapper.readValue(inputStream, OpenAPI.class);
            String reportString = openApiReportProcessor.processAsciiDocReport(openApi);
            assertThat(reportString).isNotNull();
            String targetFileName = String.format(ADOC_FORMAT, FilenameUtils.getBaseName(resource.getFilename()));
            String targetFilePath = String.format(TARGET_FILE_FORMAT, System.getProperty("user.dir"), targetFileName);
            FileUtils.write(new File(targetFilePath), reportString, StandardCharsets.UTF_8);
        }
    }
}
