package com.example.contacts.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    private static final String TOKEN = "J7ybt6jv6pdJ4gyQP9gNonsY";

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("Authorization", "Bearer " + TOKEN);
        };
    }
}
