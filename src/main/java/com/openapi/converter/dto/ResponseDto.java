package com.openapi.converter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * Report dto model.
 *
 * @param <T> - payload generic type
 * @author Roman Batygin
 */
@Data
@Builder
@Schema(description = "Response model")
public class ResponseDto<T> {

    /**
     * Response status
     */
    @Schema(description = "Response status")
    private ResponseStatus status;

    /**
     * Response payload
     */
    @Schema(description = "Response payload")
    private T payload;

    /**
     * Error code
     */
    @Schema(description = "Error code")
    private String errorCode;

    /**
     * Error message
     */
    @Schema(description = "Error message")
    private String errorMessage;
}
