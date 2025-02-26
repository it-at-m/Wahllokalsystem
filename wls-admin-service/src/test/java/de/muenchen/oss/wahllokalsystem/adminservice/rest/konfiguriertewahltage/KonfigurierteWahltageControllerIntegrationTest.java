package de.muenchen.oss.wahllokalsystem.adminservice.rest.konfiguriertewahltage;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static de.muenchen.oss.wahllokalsystem.adminservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.muenchen.oss.wahllokalsystem.adminservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.adminservice.eai.basisdaten.client.WahlenControllerApi;
import de.muenchen.oss.wahllokalsystem.adminservice.eai.infomanagement.model.KonfigurierterWahltagDTO;
import de.muenchen.oss.wahllokalsystem.adminservice.rest.konfigurierterwahltag.WahltagStatusDTO;
import de.muenchen.oss.wahllokalsystem.adminservice.utils.Authorities;
import java.time.LocalDate;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(classes = MicroServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureWireMock
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE })
class KonfigurierteWahltageControllerIntegrationTest {

    @Autowired
    MockMvc api;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    WahlenControllerApi wahlenControllerApi;

    @BeforeEach
    void setup() {

    }

    @Nested
    class GetKonfigurierteWahltage {

        @Test
        @WithMockUser(authorities = { Authorities.ADMIN_READ_KONFIGURIERTEWAHLTAGE })
        void should_returnData_when_apiReturnsData() throws Exception {
            val request = get("/businessActions/konfigurierteWahltage").with(csrf());
            val expectedResponse = new de.muenchen.oss.wahllokalsystem.adminservice.rest.konfigurierterwahltag.KonfigurierterWahltagDTO(LocalDate.now(),
                    "wahltagID", WahltagStatusDTO.AKTIV, "0");
            val apiResponse = new KonfigurierterWahltagDTO();
            apiResponse.setWahltag(LocalDate.now());
            apiResponse.setWahltagID("wahltagID");
            apiResponse.setWahltagStatus(KonfigurierterWahltagDTO.WahltagStatusEnum.AKTIV);
            apiResponse.setNummer("0");

            stubFor(WireMock.get("/businessActions/konfigurierteWahltage").willReturn(createWireMockResponse(List.of(apiResponse), HttpStatus.OK)));

            val result = api.perform(request).andExpect(status().isOk()).andReturn();

            Assertions.assertThat(result.getResponse().getContentAsString()).isEqualTo(objectMapper.writeValueAsString(List.of(expectedResponse)));
        }
    }

    @Nested
    class PostKonfigurierteWahltage {

        @Test
        @WithMockUser(authorities = { Authorities.ADMIN_POST_KONFIGURIERTERWAHLTAG })
        void should_giveDataToApi_when_dataIsValid() throws Exception {
            val requestBody = new de.muenchen.oss.wahllokalsystem.adminservice.rest.konfigurierterwahltag.KonfigurierterWahltagDTO(
                    LocalDate.now(), "wahltagID", WahltagStatusDTO.INAKTIV, "0");

            val request = MockMvcRequestBuilders.post("/businessActions/konfigurierterWahltag").with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody));

            stubFor(WireMock.post("/businessActions/konfigurierterWahltag").willReturn(createWireMockResponse(HttpStatus.OK)));

            api.perform(request).andExpect(status().isOk());
        }

        @Test
        @WithMockUser(authorities = { Authorities.ADMIN_POST_KONFIGURIERTERWAHLTAG })
        void should_giveDataToApiAndResetWahlen_when_statusIsAktiv() throws Exception {
            val requestBody = new de.muenchen.oss.wahllokalsystem.adminservice.rest.konfigurierterwahltag.KonfigurierterWahltagDTO(
                    LocalDate.now(), "wahltagID", WahltagStatusDTO.AKTIV, "0");

            val request = MockMvcRequestBuilders.post("/businessActions/konfigurierterWahltag").with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody));

            stubFor(WireMock.post("/businessActions/konfigurierterWahltag").willReturn(createWireMockResponse(HttpStatus.OK)));

            api.perform(request).andExpect(status().isOk());
            Mockito.verify(wahlenControllerApi).resetWahlen();
        }
    }

    private ResponseDefinitionBuilder createWireMockResponse(final HttpStatus responseStatus) {
        return aResponse()
                .withStatus(responseStatus.value());
    }

    private ResponseDefinitionBuilder createWireMockResponse(final Object responseBody, final HttpStatus responseStatus) throws Exception {
        return aResponse()
                .withBody(objectMapper.writeValueAsString(responseBody))
                .withHeader("Content-Type", "application/json")
                .withStatus(responseStatus.value());
    }
}
