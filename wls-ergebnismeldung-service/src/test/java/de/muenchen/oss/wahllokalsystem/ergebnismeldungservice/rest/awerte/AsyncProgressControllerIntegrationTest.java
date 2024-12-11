package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.awerte;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte.AsyncProgress;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(classes = MicroServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
public class AsyncProgressControllerIntegrationTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc api;

    @Autowired
    AsyncProgress asyncProgress;

    @Autowired
    AsyncProgressDTOMapper asyncProgressDTOMapper;

    @Nested
    class GetAsyncProgress {

        @Test
        void should_returnHttpStatusOKWithAsyncProgressDTO_when_serviceReturnsData() throws Exception {
            val request = MockMvcRequestBuilders.get("/businessActions/asyncProgress");

            val response = api.perform(request)
                    .andExpect(status().isOk())
                    .andReturn().getResponse();
            val asyncProgressDTO = objectMapper.readValue(response.getContentAsString(), AsyncProgressDTO.class);

            val expectedAsyncProgress = asyncProgressDTOMapper.toDTO(asyncProgress);

            Assertions.assertThat(asyncProgressDTO).usingRecursiveComparison().isEqualTo(expectedAsyncProgress);
        }
    }
}
