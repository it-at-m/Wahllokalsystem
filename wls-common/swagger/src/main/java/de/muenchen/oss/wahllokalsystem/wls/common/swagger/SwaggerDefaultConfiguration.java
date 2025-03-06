package de.muenchen.oss.wahllokalsystem.wls.common.swagger;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import io.swagger.v3.core.util.AnnotationsUtils;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerDefaultConfiguration {
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
        addNotFoundErrorToAPI(operation);
        addInternalErrorToAPI(operation, components);
    }

    private void addRequestBodyValidationErrorToAPI(Operation operation, Components components) {
        operation.getResponses().addApiResponse("400", new ApiResponse()
                .description("request body validation failed")
                .content(new Content().addMediaType(APPLICATION_JSON_VALUE, createWlsExceptionDTOMediaType(components))));
    }

    private void addNotFoundErrorToAPI(Operation operation) {
        operation.getResponses().addApiResponse("404", new ApiResponse()
                .description("resource not found"));
    }

    private void addInternalErrorToAPI(Operation operation, Components components) {
        operation.getResponses().addApiResponse("500", new ApiResponse()
                .description("unhandled internal error e.g. communication with other system or some types of unhandled exception")
                .content(new Content().addMediaType(APPLICATION_JSON_VALUE, createWlsExceptionDTOMediaType(components))));
    }

    private MediaType createWlsExceptionDTOMediaType(final Components components) {
        return new MediaType().schema(AnnotationsUtils.resolveSchemaFromType(WlsExceptionDTO.class, components, null));
    }
}
