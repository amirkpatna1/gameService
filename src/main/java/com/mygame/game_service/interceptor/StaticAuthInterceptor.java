package com.mygame.game_service.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.mygame.game_service.constant.GameConstants.X_SECRET_KEY;

@Component
public class StaticAuthInterceptor implements HandlerInterceptor {

    @Value("${auth.secret-key}")
    private String secretKey;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!secretKey.equals(request.getHeader(X_SECRET_KEY))) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "request secret key is missing or invalid");
            return false;
        }
        return true;
    }
}
