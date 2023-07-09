package com.openapi.converter.controller;

import com.openapi.converter.report.OpenApiValidationResultsCsvReportGenerator;
import com.openapi.converter.service.OpenApiReader;
import com.openapi.converter.service.OpenApiValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static com.openapi.converter.util.ResponseHelper.setContentDisposition;

/**
 * Rest controller for open api validation reports generation.
 *
 * @author Roman Batygin
 */
@Slf4j
@Validated
@Tag(name = "API for Open API validation reports generation")
@RestController
@RequestMapping("/v1/open-api/report/validation")
@RequiredArgsConstructor
public class OpenApiValidationController {

    private static final String VALIDATION_RESULTS_REPORT_NAME = "%s-validation-results.zip";

    private final OpenApiReader openApiReader;
    private final OpenApiValidationService openApiValidationService;
    private final OpenApiValidationResultsCsvReportGenerator openApiValidationResultsCsvReportGenerator;

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
        setContentDisposition(httpServletResponse, reportName);
        log.info("Starting to generate validation results report [{}] archive", reportName);
        openApiValidationResultsCsvReportGenerator.generateReport(validationResults, outputStream);
        outputStream.flush();
        log.info("Open api validation results report [{}] archive has been generated", reportName);
    }
}
