package com.openapi.converter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotEmpty;

/**
 * Open api report request dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Open api report request dto model")
public class OpenApiReportRequestDto {

    /**
     * Service base url
     */
    @NotEmpty
    @URL
    @Schema(description = "Service base url")
    private String url;

    /**
     * Report file name.
     */
    @NotEmpty
    @Schema(description = "Report file name")
    private String reportFileName;
}
