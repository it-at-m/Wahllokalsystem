package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.awerte;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.client.awerte.AWerteClientMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.awerte.AWerteRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.WahlberechtigteDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte.AWerteModelMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(classes = MicroServiceApplication.class)
@AutoConfigureMockMvc
@AutoConfigureWireMock
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
public class AWerteControllerIntegrationTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AWerteDTOMapper aWerteDTOMapper;

    @Autowired
    AWerteModelMapper aWerteModelMapper;

    @Autowired
    AWerteClientMapper aWerteClientMapper;

    @Autowired
    MockMvc api;

    @Autowired
    AWerteRepository awerteRepository;

    @AfterEach
    void tearDown() {
        SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_AWERTE);
        awerteRepository.deleteAll();
    }

    @BeforeEach
    void setup() {
        WireMock.resetAllRequests();
    }

    @Nested
    class GetAWerte {

        @Test
        void should_returnAWerteListFromEAI_when_dataFound() throws Exception {
            val wahlbezirkID = "wahlbezirkID1";
            val eaiWahlberechtigte = createClientListOfAWahlberechtigteDTO(wahlbezirkID);
            WireMock.stubFor(WireMock.get("/wahldaten/wahlbezirke/" + wahlbezirkID + "/wahlberechtigte")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json")
                            .withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(eaiWahlberechtigte))));

            val request = MockMvcRequestBuilders.get("/businessActions/awerte/" + wahlbezirkID);
            val response = api.perform(request).andExpect(status().isOk()).andReturn();

            val responseBodyAsDTO = objectMapper.readValue(response.getResponse().getContentAsString(),
                    de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.awerte.AWerteDTO[].class);
            val expectedResponseBody = aWerteDTOMapper
                    .fromListOfAWerteModelToListOfAWerteDTO(aWerteClientMapper.fromRemoteClientListOfWahlberechtigteDtoToListOfAWerteModel(eaiWahlberechtigte));

            Assertions.assertThat(responseBodyAsDTO).containsExactlyInAnyOrderElementsOf(expectedResponseBody);
        }

        @Test
        void should_saveAWerteListFromEAIToRepo_when_dataFound() throws Exception {
            val wahlbezirkID = "wahlbezirkID1";
            val eaiWahlberechtigte = createClientListOfAWahlberechtigteDTO(wahlbezirkID);
            WireMock.stubFor(WireMock.get("/wahldaten/wahlbezirke/" + wahlbezirkID + "/wahlberechtigte")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json")
                            .withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(eaiWahlberechtigte))));

            val request = MockMvcRequestBuilders.get("/businessActions/awerte/" + wahlbezirkID);
            api.perform(request).andExpect(status().isOk()).andReturn();

            val aWerteFromRepo = awerteRepository.findByBezirkUndWahlID_WahlbezirkID(wahlbezirkID);
            val expectedEntities = aWerteModelMapper.fromListOfAWerteModeltoListOfAWerteEntity(
                    aWerteClientMapper.fromRemoteClientListOfWahlberechtigteDtoToListOfAWerteModel(eaiWahlberechtigte));

            Assertions.assertThat(aWerteFromRepo).usingRecursiveComparison().isEqualTo(expectedEntities);
        }
    }

    private List<WahlberechtigteDTO> createClientListOfAWahlberechtigteDTO(String wahlbezirkID) {
        val wahlberechtigte1 = new WahlberechtigteDTO();
        wahlberechtigte1.setWahlID("wahlID1");
        wahlberechtigte1.setWahlbezirkID(wahlbezirkID);
        wahlberechtigte1.setA1(2L);
        wahlberechtigte1.setA2(3L);
        val wahlberechtigte2 = new WahlberechtigteDTO();
        wahlberechtigte2.setWahlID("wahlID2");
        wahlberechtigte2.setWahlbezirkID(wahlbezirkID);
        wahlberechtigte2.setA1(4L);
        wahlberechtigte2.setA2(5L);
        val wahlberechtigte3 = new WahlberechtigteDTO();
        wahlberechtigte3.setWahlID("wahlID3");
        wahlberechtigte3.setWahlbezirkID(wahlbezirkID);
        wahlberechtigte3.setA1(6L);
        wahlberechtigte3.setA2(7L);
        return List.of(wahlberechtigte1, wahlberechtigte2, wahlberechtigte3);
    }
}
