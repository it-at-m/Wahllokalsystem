package de.muenchen.oss.wahllokalsystem.adminservice.rest.konfiguriertewahltage;

import static de.muenchen.oss.wahllokalsystem.adminservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.adminservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.adminservice.eai.basisdaten.client.WahlenControllerApi;
import de.muenchen.oss.wahllokalsystem.adminservice.eai.infomanagement.client.KonfigurierterWahltagControllerApi;
import de.muenchen.oss.wahllokalsystem.adminservice.eai.infomanagement.model.KonfigurierterWahltagDTO;
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
    KonfigurierterWahltagControllerApi konfigurierterWahltagControllerApi;

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
                    "wahltagID", true, "0");
            val apiResponse = new KonfigurierterWahltagDTO();
            apiResponse.setWahltag(LocalDate.now());
            apiResponse.setWahltagID("wahltagID");
            apiResponse.setWahltagStatus(KonfigurierterWahltagDTO.WahltagStatusEnum.AKTIV);
            apiResponse.setNummer("0");

            Mockito.when(konfigurierterWahltagControllerApi.getKonfigurierteWahltage()).thenReturn(List.of(apiResponse));

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
                    LocalDate.now(), "wahltagID", false, "0");

            val request = MockMvcRequestBuilders.post("/businessActions/konfigurierterWahltag").with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody));

            api.perform(request).andExpect(status().isOk());
        }

        @Test
        @WithMockUser(authorities = { Authorities.ADMIN_POST_KONFIGURIERTERWAHLTAG })
        void should_giveDataToApiAndResetWahlen_when_statusIsAktiv() throws Exception {
            val requestBody = new de.muenchen.oss.wahllokalsystem.adminservice.rest.konfigurierterwahltag.KonfigurierterWahltagDTO(
                    LocalDate.now(), "wahltagID", true, "0");

            val request = MockMvcRequestBuilders.post("/businessActions/konfigurierterWahltag").with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody));

            api.perform(request).andExpect(status().isOk());
            Mockito.verify(wahlenControllerApi).resetWahlen();
        }
    }
}
