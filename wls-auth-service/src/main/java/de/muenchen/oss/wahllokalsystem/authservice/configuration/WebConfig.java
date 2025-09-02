package de.muenchen.oss.wahllokalsystem.authservice.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Erlaubt CORS für alle Endpoints
        registry.addMapping("/**")
                .allowedOriginPatterns("https://*") // Erlaubte Origin
                //                .allowedOrigins("https://localhost:8083") // Erlaubte Origin
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("*")
                .allowCredentials(true); // Wenn Cookies oder Authentifizierung benötigt werden
    }
}