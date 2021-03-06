package com.openapi.converter.service;

import com.openapi.converter.config.AppProperties;
import com.openapi.converter.dto.openapi.OpenAPI;
import com.openapi.converter.service.template.TemplateProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

/**
 * Open api report processor.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OpenApiReportProcessor {

    private static final String OPEN_API_VARIABLE_NAME = "openApi";

    private final AppProperties appProperties;
    private final OpenApiReportService openApiReportService;
    private final TemplateProcessorService templateProcessorService;

    /**
     * Processes open api report in adoc format.
     *
     * @param openAPI - open api dto
     * @return report string
     */
    public String processAsciiDocReport(OpenAPI openAPI) {
        var openApiReport = openApiReportService.buildReport(openAPI);
        Map<String, Object> variables = Collections.singletonMap(OPEN_API_VARIABLE_NAME, openApiReport);
        log.info("Starting to generate open api report [{}]", openApiReport.getTitle());
        String report = templateProcessorService.process(appProperties.getTemplateLocation(), variables);
        log.info("Open api report has been generated [{}]", openApiReport.getTitle());
        return report;
    }
}
