package com.openapi.converter.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openapi.converter.config.AppProperties;
import com.openapi.converter.exception.ValidationRuleNotFoundException;
import com.openapi.converter.model.validation.Rule;
import com.openapi.converter.model.validation.ValidationRuleConfig;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;

/**
 * Validation rule service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ValidationRuleService {

    private final AppProperties appProperties;
    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Map<Rule, ValidationRuleConfig> ruleConfigMap;

    /**
     * Loads validation rules from resource.
     *
     * @throws IOException in case of I/O errors
     */
    @PostConstruct
    public void loadValidationRules() throws IOException {
        log.info("Starting to load validation rules from [{}]", appProperties.getValidationRulesLocation());
        var resource = resolver.getResource(appProperties.getValidationRulesLocation());
        @Cleanup var inputStream = resource.getInputStream();
        ruleConfigMap = objectMapper.readValue(inputStream, new TypeReference<>() {
        });
        log.info("[{}] validation rules has been loaded from [{}]", ruleConfigMap.size(),
                appProperties.getValidationRulesLocation());
    }

    /**
     * Gets validation rule config.
     *
     * @param rule - rule
     * @return rule config
     */
    public ValidationRuleConfig getValidationRuleConfig(Rule rule) {
        log.debug("Gets rule [{}] config", rule);
        var ruleConfig = ruleConfigMap.get(rule);
        if (ruleConfig == null) {
            throw new ValidationRuleNotFoundException(rule);
        }
        log.debug("Rule [{}] config has been fetched {}", rule, ruleConfig);
        return ruleConfig;
    }
}
