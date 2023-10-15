package com.haguri.publicdata.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class PublicDataConfig {

    // 고속버스 BaseUrl
    @Value("${OpenAPI.baseUrl.expBus}")
    private String expBusBaseUrl;

    // 시외버스 BaseUrl
    @Value("${OpenAPI.baseUrl.suburbsBus}")
    private String suburbsBusBaseUrl;

    // 행정안전부 법정동코드 BaseUrl
    @Value("${OpenAPI.baseUrl.stanReginCd}")
    private String stanReginCdBaseUrl;

    // 국토교통부 법정동코드 ModelInfoUrl
    @Value("${OpenAPI.baseUrl.stanReginCd2.modelInfo}")
    private String stanReginCd2ModelInfoUrl;

    // 국토교통부 법정동코드 BaseUrl
    @Value("${OpenAPI.baseUrl.stanReginCd2}")
    private String stanReginCd2BaseUrl;

    @Bean
    public WebClient expBusWebClient() {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(expBusBaseUrl);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        return WebClient
                .builder()
                .uriBuilderFactory(factory)
                .baseUrl(expBusBaseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public WebClient suburbsBusWebClient() {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(suburbsBusBaseUrl);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        return WebClient
                .builder()
                .uriBuilderFactory(factory)
                .baseUrl(suburbsBusBaseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public WebClient stanReginCdWebClient() {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(stanReginCdBaseUrl);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        return WebClient
                .builder()
                .uriBuilderFactory(factory)
                .baseUrl(stanReginCdBaseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1)) // -1: unlimited
                .build();
    }

    @Bean
    public WebClient stanReginCd2ModelInfoWebClient() {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(stanReginCd2ModelInfoUrl);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        return WebClient
                .builder()
                .uriBuilderFactory(factory)
                .baseUrl(stanReginCd2ModelInfoUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1)) // -1: unlimited
                .build();
    }

    @Bean
    public WebClient stanReginCd2WebClient() {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(stanReginCd2BaseUrl);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        return WebClient
                .builder()
                .uriBuilderFactory(factory)
                .baseUrl(stanReginCd2BaseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1)) // -1: unlimited
                .build();
    }

}
