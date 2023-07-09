package com.openapi.converter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import static com.openapi.converter.dto.Constraints.REPORT_FILE_NAME_MAX_LENGTH;
import static com.openapi.converter.dto.Constraints.STRING_MAX_LENGTH_255;
import static com.openapi.converter.dto.Constraints.STRING_MIN_LENGTH_1;

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
    @Size(min = STRING_MIN_LENGTH_1, max = STRING_MAX_LENGTH_255)
    @Schema(description = "Service base url")
    private String url;

    /**
     * Report file name.
     */
    @NotEmpty
    @Size(min = STRING_MIN_LENGTH_1, max = REPORT_FILE_NAME_MAX_LENGTH)
    @Schema(description = "Report file name")
    private String reportFileName;
}
