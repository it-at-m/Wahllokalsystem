package de.muenchen.oss.wahllokalsystem.briefwahlservice.configuration;

import static de.muenchen.oss.wahllokalsystem.briefwahlservice.TestConstants.SPRING_TEST_PROFILE;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.MicroServiceApplication;
import lombok.Data;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(classes = MicroServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureObservability
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE })
class SwaggerConfigurationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${info.application.version}")
    String version;

    @Test
    void versionIsSetInDoc() throws Exception {
        val request = MockMvcRequestBuilders.get("/v3/api-docs/public-apis").contentType(MediaType.APPLICATION_JSON);

        val response = mockMvc.perform(request).andReturn();

        val openApiDoc = objectMapper.readValue(response.getResponse().getContentAsString(), OpenApiDoc.class);

        Assertions.assertThat(openApiDoc.getInfo().getVersion()).isNotNull();
        Assertions.assertThat(openApiDoc.getInfo().getVersion()).isEqualTo(version);
    }

    @Data
    private static class OpenApiDoc {

        private Info info;

        @Data
        private static class Info {
            private String version;
        }
    }

}
