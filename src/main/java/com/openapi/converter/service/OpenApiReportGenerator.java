package com.openapi.converter.service;

import com.openapi.converter.dto.OpenApiReportRequestDto;
import com.openapi.converter.dto.openapi.OpenAPI;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Open api generator service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OpenApiReportGenerator {

    private static final String ASCII_DOC_REPORT_NAME = "%s.adoc";

    private final OpenApiReportProcessor openApiReportProcessor;

    /**
     * Generates ascii doc reports zip archive.
     *
     * @param openApiReportRequests - open api report requests
     * @param openApis              - open api dto list
     * @param outputStream          - output stream
     * @throws IOException in case of I/O errors
     */
    public void generate(List<OpenApiReportRequestDto> openApiReportRequests,
                         List<OpenAPI> openApis,
                         OutputStream outputStream) throws IOException {
        log.info("Starting to generate open api reports zip archive");
        @Cleanup var zipOutputStream = new ZipOutputStream(outputStream);
        @Cleanup var writer = new OutputStreamWriter(zipOutputStream, StandardCharsets.UTF_8);
        for (int i = 0; i < openApiReportRequests.size(); i++) {
            log.info("Starting to generate report with index [{}]", i);
            var openApiResource = openApiReportRequests.get(i);
            String fileName = String.format(ASCII_DOC_REPORT_NAME, openApiResource.getReportFileName());
            zipOutputStream.putNextEntry(new ZipEntry(fileName));
            var openApi = openApis.get(i);
            String reportString = openApiReportProcessor.processAsciiDocReport(openApi);
            log.info("Report with index [{}] has been generated", i);
            IOUtils.write(reportString, writer);
            writer.flush();
            zipOutputStream.flush();
            zipOutputStream.closeEntry();
            log.info("Report with index [{}] has been put into archive", i);
        }
    }
}
