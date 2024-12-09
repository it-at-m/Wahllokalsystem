package de.muenchen.oss.wahllokalsystem.briefwahlservice.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    public static final String BEANSTANDETE_WAHLBRIEFE_EXAMPLE = "{\n" +
            "    \"LTW_2018\": [\n" +
            "      \"WAHLBRIEF_UND_UMSCHLAG_OFFEN\",\n" +
            "      \"UNTERSCHRIFT_FEHLT\",\n" +
            "      \"UMSCHLAG_NICHT_AMTLICH\",\n" +
            "      \"UMSCHLAG_GEFAEHRDET_WAHLGEHEIMNIS\",\n" +
            "      \"SCHEINE_UNGLEICH_UMSCHLAEGE\",\n" +
            "      \"SCHEIN_UNGUELTIG\"\n" +
            "    ],\n" +
            "    \"BZW_2018\": [\n" +
            "      \"NICHT_WAHLBERECHTIGT\",\n" +
            "      \"UMSCHLAG_FEHLT\",\n" +
            "      \"LOSE_STIMMZETTEL\",\n" +
            "      \"ZUGELASSEN\",\n" +
            "      \"GEGENSTAND_IM_UMSCHLAG\",\n" +
            "      \"KEIN_ORIGINAL_SCHEIN\"\n" +
            "    ]\n" +
            "}";

    @Value("${info.application.version:unknown}")
    String version;

    @Bean
    GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public-apis")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(
                        new Info().title("Briefwahl Service")
                                .version(version)
                                .contact(new Contact().name("Your Name").email("Your E-Mail-Address")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(
                        new Components()
                                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
