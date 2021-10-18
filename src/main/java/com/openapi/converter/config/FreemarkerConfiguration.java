package com.openapi.converter.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

import java.util.Properties;

import static freemarker.template.Configuration.LOCALIZED_LOOKUP_KEY;

/**
 * Freemarker configuration class.
 *
 * @author Roman Batygin.
 */
@Configuration
@Import(FileTemplateLoader.class)
@RequiredArgsConstructor
public class FreemarkerConfiguration {

    private final FileTemplateLoader fileTemplateLoader;

    /**
     * Creates freemarker configuration bean.
     *
     * @return freemarker configuration bean
     */
    @Primary
    @Bean
    public FreeMarkerConfigurationFactoryBean freeMarkerConfigurationFactoryBean() {
        var freeMarkerConfigurationFactoryBean = new FreeMarkerConfigurationFactoryBean();
        Properties properties = new Properties();
        properties.put(LOCALIZED_LOOKUP_KEY, Boolean.FALSE.toString());
        freeMarkerConfigurationFactoryBean.setFreemarkerSettings(properties);
        freeMarkerConfigurationFactoryBean.setPreTemplateLoaders(fileTemplateLoader);
        return freeMarkerConfigurationFactoryBean;
    }
}
