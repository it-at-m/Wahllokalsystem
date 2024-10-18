package de.muenchen.oss.wahllokalsystem.monitoringservice.rest.waehleranzahl;

import static de.muenchen.oss.wahllokalsystem.monitoringservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.monitoringservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.muenchen.oss.wahllokalsystem.monitoringservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.monitoringservice.client.waehleranzahl.WaehleranzahlClientMapper;
import de.muenchen.oss.wahllokalsystem.monitoringservice.domain.waehleranzahl.WaehleranzahlRepository;
import de.muenchen.oss.wahllokalsystem.monitoringservice.service.waehleranzahl.WaehleranzahlModelMapper;
import de.muenchen.oss.wahllokalsystem.monitoringservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.monitoringservice.utils.WithMockUserAsJwt;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(classes = MicroServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureWireMock
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
public class WaehleranzahlControllerIntegrationTest {

    @Value("${service.info.oid}")
    String serviceID;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WaehleranzahlRepository wahleranzahlRepository;

    @Autowired
    WaehleranzahlDTOMapper dtoMapper;

    @Autowired
    WaehleranzahlModelMapper waehleranzahlModelMapper;

    @Autowired
    WaehleranzahlClientMapper waehleranzahlClientMapper;

    @Autowired
    MockMvc api;

    @Autowired
    WaehleranzahlRepository waehleranzahlRepository;

    @AfterEach
    void tearDown() {
        SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_WAEHLERANZAHL);
        waehleranzahlRepository.deleteAll();
    }

    @BeforeEach
    void setup() {
        WireMock.resetAllRequests();
    }

    @Nested
    class GetWahlbeteiligung {

        @Test
        @WithMockUserAsJwt(authorities = { Authorities.SERVICE_GET_WAEHLERANZAHL, Authorities.REPOSITORY_READ_WAEHLERANZAHL })
        void emptyResponse() throws Exception {
            String wahlID = "wahlID01";
            String wahlbezirkID = "wahlbezirkID01";
            BezirkUndWahlID bezirkUndWahlID = new BezirkUndWahlID(wahlID, wahlbezirkID);
            val request = MockMvcRequestBuilders.get("/businessActions/wahlbeteiligung/" + wahlID + "/" + wahlbezirkID);

            val response = api.perform(request).andExpect(status().isNoContent()).andReturn();

            Assertions.assertThat(response.getResponse().getContentAsString()).isEmpty();
        }
    }
}
