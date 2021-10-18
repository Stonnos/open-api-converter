package com.openapi.converter.mapping;

import com.openapi.converter.dto.openapi.Oauth2Flow;
import com.openapi.converter.dto.openapi.OpenAPI;
import com.openapi.converter.dto.openapi.Parameter;
import com.openapi.converter.dto.openapi.RequestBody;
import com.openapi.converter.dto.openapi.Schema;
import com.openapi.converter.dto.openapi.SecurityScheme;
import com.openapi.converter.model.report.Oauth2FlowsReport;
import com.openapi.converter.model.report.OpenApiReport;
import com.openapi.converter.model.report.RequestBodyReport;
import com.openapi.converter.model.report.RequestParameterReport;
import com.openapi.converter.model.report.SchemaReport;
import com.openapi.converter.model.report.SecuritySchemaReport;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Open API mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface OpenApiMapper {

    String SLASH_SEPARATOR = "/";
    String VERTICAL_LINE = "|";
    String VERTICAL_LINE_REPLACEMENT = "\\|";

    /**
     * Maps open api to report model.
     *
     * @param openAPI - open api model
     * @return open api report
     */
    @Mapping(source = "info.title", target = "title")
    @Mapping(source = "info.description", target = "description")
    @Mapping(source = "info.contact.name", target = "author")
    @Mapping(source = "info.contact.email", target = "email")
    @Mapping(target = "methods", ignore = true)
    @Mapping(target = "components", ignore = true)
    @Mapping(target = "securitySchemes", ignore = true)
    OpenApiReport map(OpenAPI openAPI);

    /**
     * Maps request body to report model.
     *
     * @param requestBody - request body
     * @return request body report
     */
    @Mapping(source = "required", target = "required", defaultValue = "false")
    RequestBodyReport map(RequestBody requestBody);

    /**
     * Maps request parameter to report model.
     *
     * @param parameter - request parameter
     * @return request parameter report
     */
    RequestParameterReport map(Parameter parameter);

    /**
     * Maps schema to report model.
     *
     * @param schema - schema
     * @return schema report
     */
    @Mapping(source = "pattern", target = "pattern", qualifiedByName = "mapPattern")
    @Mapping(source = "enums", target = "enumValues")
    @Mapping(source = "ref", target = "objectTypeRef", qualifiedByName = "mapRef")
    @Mapping(source = "items.ref", target = "itemsObjectRef", qualifiedByName = "mapRef")
    SchemaReport map(Schema schema);

    /**
     * Maps security scheme to report model.
     *
     * @param securityScheme - security scheme
     * @return security scheme report
     */
    @Mapping(target = "oauth2Flows", ignore = true)
    SecuritySchemaReport map(SecurityScheme securityScheme);

    /**
     * Maps oauth2 flow to report model.
     *
     * @param oauth2Flow - oauth2 flow
     * @return oauth2 flow report
     */
    @Mapping(target = "scopes", ignore = true)
    Oauth2FlowsReport map(Oauth2Flow oauth2Flow);

    /**
     * Maps request parameters to report model
     *
     * @param parameters - request parameters
     * @return request parameters report
     */
    List<RequestParameterReport> map(List<Parameter> parameters);

    /**
     * Maps scopes.
     *
     * @param oauth2Flow        - oauth2 flow
     * @param oauth2FlowsReport - oauth2 flow report
     */
    @AfterMapping
    default void mapScopes(Oauth2Flow oauth2Flow, @MappingTarget Oauth2FlowsReport oauth2FlowsReport) {
        if (!CollectionUtils.isEmpty(oauth2Flow.getScopes())) {
            oauth2FlowsReport.setScopes(new ArrayList<>(oauth2Flow.getScopes().keySet()));
        }
    }

    /**
     * Map pattern value (replaced special characters).
     *
     * @param pattern - pattern
     * @return result pattern
     */
    @Named("mapPattern")
    default String mapPattern(String pattern) {
        return Optional.ofNullable(pattern)
                .map(val -> val.replace(VERTICAL_LINE, VERTICAL_LINE_REPLACEMENT))
                .orElse(null);
    }

    /**
     * Maps reference.
     *
     * @param value - reference value
     * @return result reference value
     */
    @Named("mapRef")
    default String mapRef(String value) {
        return Optional.ofNullable(value)
                .map(ref -> StringUtils.substringAfterLast(ref, SLASH_SEPARATOR))
                .orElse(null);
    }
}
