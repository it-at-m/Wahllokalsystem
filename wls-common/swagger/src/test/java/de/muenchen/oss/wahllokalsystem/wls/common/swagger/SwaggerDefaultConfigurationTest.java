package de.muenchen.oss.wahllokalsystem.wls.common.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SwaggerDefaultConfigurationTest {
    private GlobalOpenApiCustomizer customizer;
    private Components components;

    @BeforeEach
    void setUp() {
        SwaggerDefaultConfiguration configuration = new SwaggerDefaultConfiguration();
        customizer = configuration.errorCustomizer();
        components = new Components();
    }

    @Test
    public void should_AddErrorResponsesToGetOperation_when_CustomizerIsCalled() {
        Operation operation = new Operation();
        operation.setResponses(new ApiResponses());
        PathItem pathItem = new PathItem();
        pathItem.setGet(operation);
        assertErrorResponsesAddedForOperation(operation, pathItem);
    }

    @Test
    public void should_AddErrorResponsesToPostOperation_when_CustomizerIsCalled() {
        Operation operation = new Operation();
        operation.setResponses(new ApiResponses());
        PathItem pathItem = new PathItem();
        pathItem.setPost(operation);
        assertErrorResponsesAddedForOperation(operation, pathItem);
    }

    @Test
    public void should_AddErrorResponsesToDeleteOperation_when_CustomizerIsCalled() {
        Operation operation = new Operation();
        operation.setResponses(new ApiResponses());
        PathItem pathItem = new PathItem();
        pathItem.setDelete(operation);
        assertErrorResponsesAddedForOperation(operation, pathItem);
    }

    private void assertErrorResponsesAddedForOperation(Operation operation, PathItem pathItem) {
        Paths paths = new Paths();
        paths.put("/test", pathItem);

        OpenAPI api = mock(OpenAPI.class);
        when(api.getPaths()).thenReturn(paths);
        when(api.getComponents()).thenReturn(components);

        customizer.customise(api);

        assertApiResponse(operation.getResponses(), "400");
        assertApiResponse(operation.getResponses(), "404");
        assertApiResponse(operation.getResponses(), "500");
    }

    private void assertApiResponse(ApiResponses responses, String code) {
        ApiResponse apiResponse = responses.get(code);
        assertNotNull(apiResponse, "Response for code " + code + " should not be null");
    }
}
