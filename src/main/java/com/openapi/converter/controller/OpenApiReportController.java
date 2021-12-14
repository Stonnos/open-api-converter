package com.openapi.converter.controller;

import com.openapi.converter.dto.OpenApiReportRequestDto;
import com.openapi.converter.service.OpenApiReader;
import com.openapi.converter.service.OpenApiReportGenerator;
import com.openapi.converter.service.OpenApiReportProcessor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.openapi.converter.controller.docs.ApiExamples.OPEN_API_REPORTS_REQUEST_EXAMPLE;
import static com.openapi.converter.dto.Constraints.OPEN_API_REQUESTS_MAX_SIZE;
import static com.openapi.converter.util.ResponseHelper.setContentDisposition;

/**
 * Rest controller for open api reports generation.
 *
 * @author Roman Batygin
 */
@Slf4j
@Validated
@Tag(name = "API for Open API reports generation")
@RestController
@RequestMapping("/v1/open-api/report")
@RequiredArgsConstructor
public class OpenApiReportController {

    private static final String ASCII_DOC_REPORT_NAME = "%s.adoc";
    private static final String OPEN_API_REPORTS_ZIP = "open-api-reports.zip";

    private final OpenApiReader openApiReader;
    private final OpenApiReportProcessor openApiReportProcessor;
    private final OpenApiReportGenerator openApiReportGenerator;

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
        var reportString = openApiReportProcessor.processAsciiDocReport(openApi);
        var fileBaseName = FilenameUtils.getBaseName(openApiJson.getOriginalFilename());
        String reportName = String.format(ASCII_DOC_REPORT_NAME, fileBaseName);
        @Cleanup var outputStream = httpServletResponse.getOutputStream();
        setContentDisposition(httpServletResponse, reportName);
        IOUtils.write(reportString, outputStream, StandardCharsets.UTF_8);
        outputStream.flush();
        log.info("Open api report file [{}] has been generated", reportName);
    }

    /**
     * Generates Open API adoc reports zip archive.
     *
     * @param openApiReportRequests - open api report requests
     * @throws Exception in case of error
     */
    @Operation(description = "Generates Open API adoc reports zip archive",
            summary = "Generates Open API adoc reports zip archive",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(value = OPEN_API_REPORTS_REQUEST_EXAMPLE)
                    })
            })
    )
    @PostMapping(value = "/adoc/zip")
    public void generateAsciiDocReportsZipArchive(
            @RequestBody @NotEmpty @Size(max = OPEN_API_REQUESTS_MAX_SIZE)
            @Valid List<@NotNull OpenApiReportRequestDto> openApiReportRequests,
            HttpServletResponse httpServletResponse) throws Exception {
        log.info("Request to generate adoc reports zip archive");
        var openApis = openApiReader.readOpenApis(openApiReportRequests);
        @Cleanup var outputStream = httpServletResponse.getOutputStream();
        setContentDisposition(httpServletResponse, OPEN_API_REPORTS_ZIP);
        openApiReportGenerator.generate(openApiReportRequests, openApis, outputStream);
        outputStream.flush();
    }
}
