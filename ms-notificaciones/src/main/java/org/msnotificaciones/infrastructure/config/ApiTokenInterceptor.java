package org.msnotificaciones.infrastructure.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ApiTokenInterceptor implements HandlerInterceptor {

    private final String apiToken;

    public ApiTokenInterceptor(@Value("${notificaciones.api-token}") String apiToken) {
        this.apiToken = apiToken;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorization = request.getHeader("Authorization");
        String expectedToken = "Bearer " + apiToken;

        if (expectedToken.equals(authorization)) {
            return true;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Token invalido o ausente");
        return false;
    }
}
