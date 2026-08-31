package com.example.textsocial.feed.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Bean RequestInterceptor internalKey(@Value("${app.internal-api-key}") String key) {
        return template -> template.header("X-Internal-Key", key);
    }
}

