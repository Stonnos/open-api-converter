package com.openapi.converter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Validation error dto.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Validation error model")
public class ValidationErrorDto {

    /**
     * Field name
     */
    @Schema(description = "Field name", example = "field")
    private String fieldName;

    /**
     * Error code
     */
    @Schema(description = "Error code", example = "NotNull")
    private String code;

    /**
     * Error message
     */
    @Schema(description = "Error message", example = "Must be not null")
    private String errorMessage;
}
