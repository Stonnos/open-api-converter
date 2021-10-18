package com.openapi.converter.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * App configuration class.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableConfigurationProperties(AppProperties.class)
public class OpenApiConverterConfiguration {

    /**
     * Creates object mapper bean for examples serialization.
     *
     * @return {@link ObjectMapper} bean
     */
    @Bean
    public ObjectMapper exampleObjectMapper() {
        var objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        return objectMapper;
    }

    /**
     * Creates object mapper bean for open api deserialization.
     *
     * @return {@link ObjectMapper} bean
     */
    @Primary
    @Bean
    public ObjectMapper openApiObjectMapper() {
        var objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }
}
