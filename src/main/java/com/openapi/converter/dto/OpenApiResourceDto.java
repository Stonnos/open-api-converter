package com.openapi.converter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * Open api resource dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Open api resource dto model")
public class OpenApiResourceDto {

    /**
     * Service base url
     */
    @NotEmpty
    @Schema(description = "Service base url")
    private String url;

    /**
     * Use ssl?
     */
    @Schema(description = "Use ssl?")
    private boolean useSsl;
}
