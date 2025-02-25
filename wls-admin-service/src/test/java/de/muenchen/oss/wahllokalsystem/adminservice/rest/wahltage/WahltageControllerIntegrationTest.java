package de.muenchen.oss.wahllokalsystem.adminservice.rest.wahltage;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static de.muenchen.oss.wahllokalsystem.adminservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.muenchen.oss.wahllokalsystem.adminservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.adminservice.eai.basisdaten.model.WahltagDTO;
import de.muenchen.oss.wahllokalsystem.adminservice.utils.Authorities;
import java.time.LocalDate;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(classes = MicroServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureWireMock
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE })
class WahltageControllerIntegrationTest {

    @Autowired
    MockMvc api;

    @Autowired
    ObjectMapper objectMapper;

    @AfterEach
    void teardown() {
        reset();
    }

    @Nested
    class GetWahltage {

        @Test
        @WithMockUser(
                authorities = { Authorities.ADMIN_GETWAHLTAGE }
        )
        void should_returnData_when_wahltageClientReturnsData() throws Exception {
            val request = MockMvcRequestBuilders.get("/businessActions/wahltage");
            val nowDate = LocalDate.now();
            val mockedWahltage = List.of(
                    new WahltagDTO().wahltagID("wahltagID1").wahltag(nowDate).beschreibung("beschreibung").nummer("1"),
                    new WahltagDTO().wahltagID("wahltagID2").wahltag(nowDate).beschreibung("beschreibung").nummer("2"));

            stubFor(WireMock.get("/businessActions/wahltage").willReturn(createWireMockResponse(mockedWahltage, HttpStatus.OK)));

            val response = api.perform(request).andExpect(status().isOk()).andReturn();
            val responseBody = objectMapper.readValue(response.getResponse().getContentAsString(),
                    de.muenchen.oss.wahllokalsystem.adminservice.rest.wahltage.WahltagDTO[].class);

            val expectedResponseBody = new de.muenchen.oss.wahllokalsystem.adminservice.rest.wahltage.WahltagDTO[] {
                    new de.muenchen.oss.wahllokalsystem.adminservice.rest.wahltage.WahltagDTO(
                            mockedWahltage.get(0).getWahltagID(),
                            mockedWahltage.get(0).getWahltag(),
                            mockedWahltage.get(0).getBeschreibung(),
                            mockedWahltage.get(0).getNummer()),
                    new de.muenchen.oss.wahllokalsystem.adminservice.rest.wahltage.WahltagDTO(
                            mockedWahltage.get(1).getWahltagID(),
                            mockedWahltage.get(1).getWahltag(),
                            mockedWahltage.get(1).getBeschreibung(),
                            mockedWahltage.get(1).getNummer())
            };

            Assertions.assertThat(responseBody).usingRecursiveComparison().isEqualTo(expectedResponseBody);
        }

        @Test
        @WithMockUser(
                authorities = { Authorities.ADMIN_GETWAHLTAGE }
        )
        void should_returnNoContent_when_wahltageClientReturnsEmptyList() throws Exception {
            val request = MockMvcRequestBuilders.get("/businessActions/wahltage");
            val mockedWahltage = List.of();

            stubFor(WireMock.get("/businessActions/wahltage").willReturn(createWireMockResponse(mockedWahltage, HttpStatus.OK)));

            api.perform(request).andExpect(status().isNoContent()).andReturn();
        }
    }

    private ResponseDefinitionBuilder createWireMockResponse(final Object responseBody, final HttpStatus responseStatus) throws Exception {
        return aResponse()
                .withBody(objectMapper.writeValueAsString(responseBody))
                .withHeader("Content-Type", "application/json")
                .withStatus(responseStatus.value());
    }
}
