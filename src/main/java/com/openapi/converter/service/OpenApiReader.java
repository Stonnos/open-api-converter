package com.openapi.converter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openapi.converter.dto.openapi.OpenAPI;
import com.openapi.converter.exception.InvalidFileExtensionException;
import com.openapi.converter.exception.InvalidFileFormatException;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
            throw new InvalidFileFormatException(
                    String.format("Invalid file [%s] format", multipartFile.getOriginalFilename()));
        }
    }
}
