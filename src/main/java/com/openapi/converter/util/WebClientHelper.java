package com.openapi.converter.util;

import com.openapi.converter.dto.OpenApiReportRequestDto;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

/**
 * Web client helper utility class.
 *
 * @author Roman Batygin
 */
@Slf4j
@UtilityClass
public class WebClientHelper {

    private static final String HTTPS_PREFIX = "https://";

    /**
     * Creates and initialize web client.
     *
     * @param openApiReportRequestDto - open api resource dto
     * @return web client
     */
    public static WebClient createWebClient(OpenApiReportRequestDto openApiReportRequestDto) {
        var webClientBuilder = WebClient.builder().baseUrl(openApiReportRequestDto.getUrl());
        if (openApiReportRequestDto.getUrl().startsWith(HTTPS_PREFIX)) {
            initializeSslContext(webClientBuilder);
        }
        return webClientBuilder.build();
    }

    private static void initializeSslContext(WebClient.Builder webClientBuilder) {
        try {
            var sslContext = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
            var httpClient = HttpClient
                    .create()
                    .secure(ssl -> ssl.sslContext(sslContext));
            webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient));
        } catch (SSLException ex) {
            log.error("Error while ssl context creation: {}", ex.getMessage());
            throw new IllegalStateException(ex);
        }
    }
}
