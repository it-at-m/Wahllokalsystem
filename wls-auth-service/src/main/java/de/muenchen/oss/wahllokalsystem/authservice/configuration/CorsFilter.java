package de.muenchen.oss.wahllokalsystem.authservice.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.val;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConfigurationProperties(prefix = "service.config.cors")
public class CorsFilter extends OncePerRequestFilter {

    private List<String> allowedOrigins = new ArrayList<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        setHeaderAccessControlAllowOrigin(request, response);
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        //        response.setHeader("Access-Control-Allow-Origin", "https://localhost:8083/");
        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE, PATCH");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers",
                "Origin, X-Requested-With, Content-Type, Accept, Authorization, Access-Control-Request-Method, Access-Control-Request-Headers");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        filterChain.doFilter(request, response);
    }

    private void setHeaderAccessControlAllowOrigin(HttpServletRequest request, final HttpServletResponse response) {
        val requestOrigin = request.getHeader("Origin");
        if (allowedOrigins.contains(requestOrigin)) {
            response.setHeader("Access-Control-Allow-Origin", requestOrigin);
        }

    }
}
