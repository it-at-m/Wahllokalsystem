package de.muenchen.oss.wahllokalsystem.eaiservice.configuration;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import io.swagger.v3.core.util.AnnotationsUtils;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

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
                        new Info().title("EAI Service")
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

    @Bean
    public GlobalOpenApiCustomizer errorCustomizer() {
        return api -> api.getPaths().values().forEach(path -> path.readOperations()
                .forEach(operation -> addErrorToApi(operation, api.getComponents())));
    }

    // add response based on the behavior of the global exception handler
    private void addErrorToApi(Operation operation, Components components) {
        if (operation.getResponses() != null) {
            addRequestBodyValidationErrorToAPI(operation, components);
        }
        addNotFoundErrorToAPI(operation, components);
        addInternalErrorToAPI(operation, components);
        addUnhandledCommunicationErrorToAPI(operation, components);
    }

    private void addRequestBodyValidationErrorToAPI(Operation operation, Components components) {
        operation.getResponses().addApiResponse("400", new ApiResponse()
                .description("request body validation failed")
                .content(new Content().addMediaType(APPLICATION_JSON_VALUE, createWlsExceptionDTOMediaType(components))));
    }

    private void addNotFoundErrorToAPI(Operation operation, Components components) {
        operation.getResponses().addApiResponse("404", new ApiResponse()
                .description("no resource found")
                .content(new Content().addMediaType(APPLICATION_JSON_VALUE, createWlsExceptionDTOMediaType(components))));
    }

    private void addInternalErrorToAPI(Operation operation, Components components) {
        operation.getResponses().addApiResponse("500", new ApiResponse()
                .description("unhandled internal error")
                .content(new Content().addMediaType(APPLICATION_JSON_VALUE, createWlsExceptionDTOMediaType(components))));
    }

    private void addUnhandledCommunicationErrorToAPI(Operation operation, Components components) {
        operation.getResponses().addApiResponse("500", new ApiResponse()
                .description("unhandled error during communication with other system")
                .content(new Content().addMediaType(APPLICATION_JSON_VALUE, createWlsExceptionDTOMediaType(components))));
    }

    private MediaType createWlsExceptionDTOMediaType(final Components components) {
        return new MediaType().schema(AnnotationsUtils.resolveSchemaFromType(WlsExceptionDTO.class, components, null));
    }
}
