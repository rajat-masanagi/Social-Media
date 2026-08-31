package com.example.textsocial.gateway;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

@Configuration
class RequestIdFilter {
    private static final Logger log = LoggerFactory.getLogger(RequestIdFilter.class);

    @Bean
    GlobalFilter correlationIdFilter() {
        return (exchange, chain) -> {
            String id = exchange.getRequest().getHeaders().getFirst("X-Request-Id");
            if (id == null || id.isBlank()) id = UUID.randomUUID().toString();
            final String requestId = id;
            var request = exchange.getRequest().mutate().header("X-Request-Id", requestId).build();
            var response = exchange.getResponse();
            response.getHeaders().set("X-Request-Id", requestId);
            long started = System.nanoTime();
            return chain.filter(exchange.mutate().request(request).build())
                    .doFinally(signal -> log.info("request id={} method={} path={} status={} durationMs={}",
                            requestId, request.getMethod(), request.getURI().getPath(), response.getStatusCode(),
                            (System.nanoTime() - started) / 1_000_000));
        };
    }
}
