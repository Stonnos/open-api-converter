package com.openapi.converter.config;

import freemarker.cache.TemplateLoader;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

/**
 * Implements template loading from file.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FileTemplateLoader implements TemplateLoader {

    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    @Override
    public Object findTemplateSource(String code) throws IOException {
        log.debug("Starting to load template [{}]", code);
        @Cleanup var inputStream = resolver.getResource(code).getInputStream();
        @Cleanup var reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        String template = FileCopyUtils.copyToString(reader);
        log.debug("Template [{}] has been loaded", code);
        return template;
    }

    @Override
    public long getLastModified(Object template) {
        return -1L;
    }

    @Override
    public Reader getReader(Object template, String encoding) {
        return new StringReader(String.valueOf(template));
    }

    @Override
    public void closeTemplateSource(Object template) {
        //Empty implementation
    }
}
