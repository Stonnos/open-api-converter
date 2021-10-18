package com.openapi.converter.controller;

import com.openapi.converter.dto.ResponseDto;
import com.openapi.converter.exception.ValidationErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static com.openapi.converter.util.ResponseHelper.buildErrorResponse;

/**
 * Exception handler for controllers.
 *
 * @author Roman Batygin
 */
@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class ErrorHandler {

    /**
     * Handles validation error exception.
     *
     * @param ex - validation error exception
     * @return response entity
     */
    @ExceptionHandler(ValidationErrorException.class)
    public ResponseEntity<ResponseDto<Void>> handleValidationError(ValidationErrorException ex) {
        log.error("Got validation error [{}]: {}", ex.getErrorCode(), ex.getMessage());
        var responseDto = buildErrorResponse(ex.getErrorCode());
        return ResponseEntity.badRequest().body(responseDto);
    }
}
