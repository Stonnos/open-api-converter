package com.openapi.converter.controller;

import com.google.common.collect.Iterables;
import com.openapi.converter.dto.ErrorCode;
import com.openapi.converter.dto.ResponseDto;
import com.openapi.converter.dto.ValidationErrorDto;
import com.openapi.converter.exception.OpenApiErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import javax.validation.metadata.ConstraintDescriptor;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    @ExceptionHandler(OpenApiErrorException.class)
    public ResponseEntity<ResponseDto<Void>> handleValidationError(OpenApiErrorException ex) {
        log.error("Got validation error [{}]: {}", ex.getErrorCode(), ex.getMessage());
        var responseDto = buildErrorResponse(ex.getErrorCode(), ex.getMessage());
        return ResponseEntity.badRequest().body(responseDto);
    }

    /**
     * Handles validation error.
     *
     * @param ex -  method argument not valid exception
     * @return response entity
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<List<ValidationErrorDto>>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex) {
        log.error("Got method argument not valid error: {}", ex.getMessage());
        var errors = ex.getBindingResult().getAllErrors()
                .stream()
                .map(FieldError.class::cast)
                .map(fieldError -> new ValidationErrorDto(fieldError.getField(), fieldError.getCode(),
                        fieldError.getDefaultMessage())).collect(Collectors.toList());
        var responseDto = buildErrorResponse(ErrorCode.VALIDATION_ERROR, errors);
        return ResponseEntity.badRequest().body(responseDto);
    }

    /**
     * Handles constraint violation error.
     *
     * @param ex - constraint violation exception
     * @return response entity
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseDto<List<ValidationErrorDto>>> handleConstraintViolation(
            ConstraintViolationException ex) {
        log.error("Got constraint violation error: {}", ex.getMessage());
        var validationErrors = ex.getConstraintViolations().stream()
                .map(constraintViolation -> {
                    var node = Iterables.getLast(constraintViolation.getPropertyPath());
                    var validationErrorDto = new ValidationErrorDto();
                    validationErrorDto.setFieldName(node.getName());
                    var code = Optional.ofNullable(constraintViolation.getConstraintDescriptor())
                            .map(ConstraintDescriptor::getAnnotation)
                            .map(Annotation::annotationType)
                            .map(Class::getSimpleName).orElse(null);
                    validationErrorDto.setCode(code);
                    validationErrorDto.setErrorMessage(constraintViolation.getMessage());
                    return validationErrorDto;
                }).collect(Collectors.toList());
        var responseDto = buildErrorResponse(ErrorCode.VALIDATION_ERROR, validationErrors);
        return ResponseEntity.badRequest().body(responseDto);
    }
}
