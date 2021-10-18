package com.openapi.converter.util;

import com.openapi.converter.dto.ErrorCode;
import com.openapi.converter.dto.ResponseDto;
import com.openapi.converter.dto.ResponseStatus;
import lombok.experimental.UtilityClass;

/**
 * Response helper class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ResponseHelper {

    /**
     * Builds error response.
     *
     * @param errorCode - error code
     * @return error response
     */
    public static ResponseDto<Void> buildErrorResponse(ErrorCode errorCode) {
        return ResponseDto.<Void>builder()
                .status(ResponseStatus.ERROR)
                .errorCode(errorCode.name())
                .build();
    }
}
