package com.openapi.converter.controller;

import com.openapi.converter.report.OpenApiValidationResultsCsvReportGenerator;
import com.openapi.converter.service.OpenApiReader;
import com.openapi.converter.service.OpenApiReportGenerator;
import com.openapi.converter.service.OpenApiValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

/**
 * Rest controller for open api reports generation.
 *
 * @author Roman Batygin
 */
@Slf4j
@Tag(name = "API for Open API reports generation")
@RestController
@RequestMapping("/v1/open-api/report")
@RequiredArgsConstructor
public class OpenApiController {

    private static final String ATTACHMENT_FORMAT = "attachment; filename=%s";
    private static final String ASCII_DOC_REPORT_NAME = "%s.adoc";
    private static final String VALIDATION_RESULTS_REPORT_NAME = "%s-validation-results.zip";

    private final OpenApiReader openApiReader;
    private final OpenApiReportGenerator openApiReportGenerator;
    private final OpenApiValidationService openApiValidationService;
    private final OpenApiValidationResultsCsvReportGenerator openApiValidationResultsCsvReportGenerator;

    /**
     * Generates Open API report in adoc format.
     *
     * @param openApiJson - open api json file
     * @throws Exception in case of error
     */
    @Operation(description = "Generates Open API report in adoc format",
            summary = "Generates Open API report in adoc format")
    @PostMapping(value = "/adoc", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void generateAsciiDocReport(
            @Parameter(description = "Open api json file (swagger.json)") @RequestParam MultipartFile openApiJson,
            HttpServletResponse httpServletResponse) throws Exception {
        log.info("Starting to generate adoc report for file [{}]", openApiJson.getOriginalFilename());
        var openApi = openApiReader.read(openApiJson);
        var reportString = openApiReportGenerator.generateAsciiDocReport(openApi);
        var fileBaseName = FilenameUtils.getBaseName(openApiJson.getOriginalFilename());
        String reportName = String.format(ASCII_DOC_REPORT_NAME, fileBaseName);
        @Cleanup var outputStream = httpServletResponse.getOutputStream();
        setResponseHeaders(httpServletResponse, reportName);
        IOUtils.write(reportString, outputStream, StandardCharsets.UTF_8);
        outputStream.flush();
        log.info("Open api report file [{}] has been generated", reportName);
    }

    /**
     * Generates Open API validation results report archive.
     *
     * @param openApiJson - open api json file
     * @throws Exception in case of error
     */
    @Operation(description = "Generates Open API validation results report archive",
            summary = "Generates Open API validation results report archive")
    @PostMapping(value = "/validation", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void generateValidationResultsReport(
            @Parameter(description = "Open api json file (swagger.json)") @RequestParam MultipartFile openApiJson,
            HttpServletResponse httpServletResponse) throws Exception {
        log.info("Starting to generate open api validation results report for file [{}]",
                openApiJson.getOriginalFilename());
        var openApi = openApiReader.read(openApiJson);
        var validationResults = openApiValidationService.validate(openApi);
        var fileBaseName = FilenameUtils.getBaseName(openApiJson.getOriginalFilename());
        String reportName = String.format(VALIDATION_RESULTS_REPORT_NAME, fileBaseName);
        @Cleanup var outputStream = httpServletResponse.getOutputStream();
        setResponseHeaders(httpServletResponse, reportName);
        log.info("Starting to generate validation results report [{}] archive", reportName);
        openApiValidationResultsCsvReportGenerator.generateReport(validationResults, outputStream);
        outputStream.flush();
        log.info("Open api validation results report [{}] archive has been generated", reportName);
    }

    private void setResponseHeaders(HttpServletResponse httpServletResponse, String reportName) {
        httpServletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        httpServletResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format(ATTACHMENT_FORMAT, reportName));
    }
}
