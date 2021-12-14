package com.openapi.converter.util;

import com.openapi.converter.dto.ErrorCode;
import com.openapi.converter.dto.ResponseDto;
import com.openapi.converter.dto.ResponseStatus;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;

/**
 * Response helper class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ResponseHelper {

    private static final String ATTACHMENT_FORMAT = "attachment; filename=%s";

    /**
     * Builds error response.
     *
     * @param errorCode    - error code
     * @param errorMessage - error message
     * @return error response
     */
    public static ResponseDto<Void> buildErrorResponse(ErrorCode errorCode, String errorMessage) {
        return ResponseDto.<Void>builder()
                .status(ResponseStatus.ERROR)
                .errorCode(errorCode.name())
                .errorMessage(errorMessage)
                .build();
    }

    /**
     * Builds error response.
     *
     * @param errorCode - error code
     * @param payload   - response payload
     * @return error response
     */
    public static <T> ResponseDto<T> buildErrorResponse(ErrorCode errorCode, T payload) {
        return ResponseDto.<T>builder()
                .status(ResponseStatus.ERROR)
                .errorCode(errorCode.name())
                .payload(payload)
                .build();
    }

    /**
     * Sets content disposition header for http servlet response.
     *
     * @param httpServletResponse - http servlet response.
     * @param fileName            - file name
     */
    public static void setContentDisposition(HttpServletResponse httpServletResponse, String fileName) {
        httpServletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        httpServletResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format(ATTACHMENT_FORMAT, fileName));
    }
}
