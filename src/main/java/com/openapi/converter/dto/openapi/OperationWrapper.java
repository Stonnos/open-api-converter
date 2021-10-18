package com.openapi.converter.dto.openapi;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Operation wrapper.
 *
 * @author Roman Batygin
 */
@Data
@Builder
public class OperationWrapper {

    /**
     * Request method
     */
    private RequestMethod requestMethod;

    /**
     * Operation object
     */
    private Operation operation;
}
