package com.mygame.game_service.config;

import com.mygame.game_service.interceptor.StaticAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class RegisterInterceptor implements WebMvcConfigurer {
    @Autowired
    private HandlerInterceptor staticAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(staticAuthInterceptor)
                .addPathPatterns("/game/v1/**");
    }
}
