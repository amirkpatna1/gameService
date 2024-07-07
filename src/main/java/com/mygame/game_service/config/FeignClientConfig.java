package com.mygame.game_service.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.mygame.game_service.constant.GameConstants.X_SECRET_KEY;

@Configuration
public class FeignClientConfig {

    @Value("${leaderboard-service.secret-key}")
    private String secretKey;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                requestTemplate.header(X_SECRET_KEY, secretKey);
            }
        };
    }
}

