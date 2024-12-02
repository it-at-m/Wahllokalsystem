package de.muenchen.oss.wahllokalsystem.briefwahlservice.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.MapSchema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.Map;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    public static final String SCHEMA_BEANSTANDETEWAHLBRIEFE_PROPERTY = "beanstandeteWahlbriefeProperty";

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
                                        .bearerFormat("JWT"))
                                .addSchemas(SCHEMA_BEANSTANDETEWAHLBRIEFE_PROPERTY,
                                        new MapSchema().title("beanstandeteWahlbriefe as Map<String, Zurueckweisungsgrund_[]>")
                                                .example(Map.of("BZW_2018",
                                                        new String[] { "NICHT_WAHLBERECHTIGT", "UMSCHLAG_FEHLT", "LOSE_STIMMZETTEL", "ZUGELASSEN",
                                                                "GEGENSTAND_IM_UMSCHLAG", "KEIN_ORIGINAL_SCHEIN" },
                                                        "LTW_2018",
                                                        new String[] { "WAHLBRIEF_UND_UMSCHLAG_OFFEN", "UNTERSCHRIFT_FEHLT", "UMSCHLAG_NICHT_AMTLICH",
                                                                "UMSCHLAG_GEFAEHRDET_WAHLGEHEIMNIS", "SCHEINE_UNGLEICH_UMSCHLAEGE", "SCHEIN_UNGUELTIG" }))
                                                .type("Map[String, Zurueckweisungsgrund_[]]")));
    }
}
