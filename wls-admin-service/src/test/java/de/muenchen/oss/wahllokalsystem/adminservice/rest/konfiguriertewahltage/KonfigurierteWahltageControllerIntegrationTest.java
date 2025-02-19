package de.muenchen.oss.wahllokalsystem.adminservice.rest.konfiguriertewahltage;

import static de.muenchen.oss.wahllokalsystem.adminservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.adminservice.MicroServiceApplication;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

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

    @BeforeEach
    void setup() {

    }

    @Nested
    class GetKonfigurierteWahltage {

        @Test
        @WithMockUser(authorities = { Authorities.ADMIN_READ_KONFIGURIERTEWAHLTAGE })
        void should_returnKonfigurierteWahltage_when_clientIsCalled() throws Exception {
            val request = get("/businessActions/konfigurierteWahltage").with(csrf());
            val expectedResponse = new KonfigurierterWahltagDTO();
            expectedResponse.setWahltag(LocalDate.now());
            expectedResponse.setWahltagID("wahltagID");
            expectedResponse.setWahltagStatus(KonfigurierterWahltagDTO.WahltagStatusEnum.AKTIV);
            expectedResponse.setNummer("0");

            Mockito.when(konfigurierterWahltagControllerApi.getKonfigurierteWahltage()).thenReturn(List.of(expectedResponse));

            val result = api.perform(request).andExpect(status().isOk()).andReturn();

            Assertions.assertThat(result.getResponse().getContentAsString()).isEqualTo(objectMapper.writeValueAsString(List.of(expectedResponse)));
        }
    }
}
