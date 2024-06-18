package com.openapi.converter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openapi.converter.dto.OpenApiReportRequestDto;
import com.openapi.converter.dto.openapi.OpenAPI;
import com.openapi.converter.exception.ExternalIntegrationErrorException;
import com.openapi.converter.exception.InvalidFileExtensionException;
import com.openapi.converter.exception.InvalidFileFormatException;
import com.openapi.converter.exception.InvalidFormatException;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.openapi.converter.util.WebClientHelper.createWebClient;

/**
 * Open api reader.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OpenApiReader {

    private static final String JSON_EXTENSION = "json";
    private static final String API_DOCS_URL = "/v3/api-docs";

    private final ObjectMapper openApiObjectMapper;

    /**
     * Reads open api file.
     *
     * @param multipartFile - json file
     * @return open api dto model
     */
    public OpenAPI read(MultipartFile multipartFile) {
        log.info("Starting to read file [{}]", multipartFile.getOriginalFilename());
        String fileExtension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        if (StringUtils.isEmpty(fileExtension) || !fileExtension.equals(JSON_EXTENSION)) {
            throw new InvalidFileExtensionException(String.format("Invalid file [%s] extension",
                    multipartFile.getOriginalFilename()));
        }
        try {
            @Cleanup var inputStream = multipartFile.getInputStream();
            var openApi = openApiObjectMapper.readValue(inputStream, OpenAPI.class);
            log.info("File [{}] has been read", multipartFile.getOriginalFilename());
            return openApi;
        } catch (IOException ex) {
            log.error("Error while read swagger json [{}]: {}", multipartFile.getOriginalFilename(), ex.getMessage());
            throw new InvalidFileFormatException(
                    String.format("Invalid file [%s] format", multipartFile.getOriginalFilename()));
        }
    }

    /**
     * Reads open api model from external resource.
     *
     * @param openApiReportRequestDto - open api resource dto
     * @return open api model
     */
    public OpenAPI readOpenApi(OpenApiReportRequestDto openApiReportRequestDto) {
        log.info("Starting to read open api from [{}]", openApiReportRequestDto.getUrl());
        var webClient = createWebClient(openApiReportRequestDto);
        try {
            String openApiJson = webClient.get()
                    .uri(API_DOCS_URL)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            Assert.notNull(openApiJson,
                    String.format("Expected not null response for url [%s]", openApiReportRequestDto.getUrl()));
            log.info("Open api docs has been fetched from [{}]", openApiReportRequestDto.getUrl());
            var openApi = openApiObjectMapper.readValue(openApiJson, OpenAPI.class);
            log.info("Open api model has been read for [{}]", openApiReportRequestDto.getUrl());
            return openApi;
        } catch (WebClientResponseException ex) {
            log.error("Got http error code [{}] from [{}] with response [{}]: {}", ex.getRawStatusCode(),
                    openApiReportRequestDto.getUrl(), ex.getResponseBodyAsString(), ex.getMessage());
            throw new ExternalIntegrationErrorException(String.format("Got [%d] code from [%s]",
                    ex.getRawStatusCode(), openApiReportRequestDto.getUrl()));
        } catch (IOException ex) {
            throw new InvalidFormatException(
                    String.format("Can't deserialize json for open api from resource [%s]",
                            openApiReportRequestDto.getUrl()));
        }
    }

    /**
     * Reads open api models from external resources.
     *
     * @param openApiResources - open api resource dto
     * @return open api models list
     */
    public List<OpenAPI> readOpenApis(List<OpenApiReportRequestDto> openApiResources) {
        log.info("Starting to read [{}] open api docs", openApiResources.size());
        var openApis = openApiResources.stream()
                .map(this::readOpenApi)
                .collect(Collectors.toList());
        log.info("[{}] open apis has been read", openApis.size());
        return openApis;
    }
}
