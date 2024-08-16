package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.kopfdaten;

import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.KonfigurierterWahltagClientMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.kopfdaten.Kopfdaten;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.kopfdaten.KopfdatenRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.kopfdaten.Stimmzettelgebietsart;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.BasisdatenDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.infomanagement.model.KonfigurierterWahltagDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.KopfdatenModelMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.MockDataFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDate;
import java.util.List;
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
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = MicroServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureWireMock
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
public class KopfdatenControllerIntegrationTest {

    @Value("${service.info.oid}")
    String serviceID;

    @Autowired
    MockMvc api;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    KopfdatenDTOMapper dtoMapper;

    @Autowired
    KopfdatenModelMapper modelMapper;

    @Autowired
    KopfdatenRepository kopfdatenRepository;

    @Autowired
    KonfigurierterWahltagClientMapper konfigurierterWahltagClientMapper;

    @AfterEach
    void tearDown() {
        kopfdatenRepository.deleteAll();
    }

    @BeforeEach
    void setup() {
        WireMock.resetAllRequests();
        kopfdatenRepository.deleteAll();
    }

    @Nested
    class GetKopfdaten {

        @Test
        void loadedFromExternal() throws Exception {
            // mock infomanagement konfigurierterWahltag
            KonfigurierterWahltagDTO infomanagementKonfigurierterWahltag = MockDataFactory.createClientKonfigurierterWahltagDTO(LocalDate.now().plusMonths(1), KonfigurierterWahltagDTO.WahltagStatusEnum.AKTIV);
            WireMock.stubFor(WireMock.get("/businessActions/konfigurierterWahltag")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(infomanagementKonfigurierterWahltag))));

            val wahlID = "wahlID1";
            val wahlbezirkID = "wahlbezirkID1_1";

            // @param wahlNummer=0 should be found in the service through the above mocked repo data
            // mock EAI basisdaten
            BasisdatenDTO eaiBasisdaten = MockDataFactory.createClientBasisdatenDTO(LocalDate.now().plusMonths(1));
            WireMock.stubFor(WireMock.get("/wahldaten/basisdaten?forDate=" + LocalDate.now().plusMonths(1) + "&withNummer=nummerWahltag")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(eaiBasisdaten))));

            val request = MockMvcRequestBuilders.get("/businessActions/kopfdaten/" + wahlID + "/" + wahlbezirkID);

            val responseFromController = api.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyAsDTO = objectMapper.readValue(responseFromController.getResponse().getContentAsString(),
                    de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.kopfdaten.KopfdatenDTO.class);

            val expectedResponse = MockDataFactory.createControllerKopfdatenDTO("wahlID1", "wahlbezirkID1_1",
                    StimmzettelgebietsartDTO.SG, "120", "Munich", "Bundestagswahl", "1201");
            Assertions.assertThat(responseBodyAsDTO).isNotNull();
            Assertions.assertThat(responseBodyAsDTO).usingRecursiveComparison().isEqualTo(expectedResponse);
        }

        @Test
        @Transactional
        void externalDataIsPersisted() throws Exception {
            // mock infomanagement konfigurierterWahltag
            KonfigurierterWahltagDTO infomanagementKonfigurierterWahltag = MockDataFactory.createClientKonfigurierterWahltagDTO(LocalDate.now().plusMonths(1), KonfigurierterWahltagDTO.WahltagStatusEnum.AKTIV);
            WireMock.stubFor(WireMock.get("/businessActions/konfigurierterWahltag")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(infomanagementKonfigurierterWahltag))));
            // mock eai getBasisdaten
            BasisdatenDTO eaiBasisdaten = MockDataFactory.createClientBasisdatenDTO(LocalDate.now().plusMonths(1));
            WireMock.stubFor(WireMock
                    .get("/wahldaten/basisdaten?forDate=" + LocalDate.now().plusMonths(1) + "&withNummer=" + infomanagementKonfigurierterWahltag.getNummer())
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(eaiBasisdaten))));

            val wahlID1 = "wahlID1";
            val wahlID2 = "wahlID2";
            val wahlbezirkID1 = "wahlbezirkID1_1";
            val wahlbezirkID2 = "wahlbezirkID2_1";
            val request1 = MockMvcRequestBuilders.get("/businessActions/kopfdaten/" + wahlID1 + "/" + wahlbezirkID1);
            val request2 = MockMvcRequestBuilders.get("/businessActions/kopfdaten/" + wahlID2 + "/" + wahlbezirkID2);
            val request3 = MockMvcRequestBuilders.get("/businessActions/kopfdaten/" + wahlID1 + "/" + wahlbezirkID2);

            val expectedKopfdaten1 = MockDataFactory.createKopfdatenEntityFor("wahlID1", "wahlbezirkID1_1",
                    Stimmzettelgebietsart.SG, "Munich", "120",
                    "Bundestagswahl", "1201");
            val expectedKopfdaten2 = MockDataFactory.createKopfdatenEntityFor("wahlID2", "wahlbezirkID2_1",
                    Stimmzettelgebietsart.SB, "Munich Center", "920",
                    "Europawahl", "1252");
            val expectedKopfdaten3 = MockDataFactory.createKopfdatenEntityFor("wahlID1", "wahlbezirkID2_1",
                    Stimmzettelgebietsart.SG, "Munich", "120",
                    "Bundestagswahl", "1202");

            api.perform(request1).andExpect(status().isOk());
            List<Kopfdaten> dataFromRepo = (List<Kopfdaten>) kopfdatenRepository.findAllById(List.of(new BezirkUndWahlID("wahlID1", "wahlbezirkID1_1")));
            val expectedListOfData1 = List.of(expectedKopfdaten1);
            Assertions.assertThat(dataFromRepo)
                    .usingRecursiveComparison().ignoringCollectionOrder()
                    .isEqualTo(expectedListOfData1);
            kopfdatenRepository.deleteAll();

            api.perform(request2).andExpect(status().isOk());
            dataFromRepo = (List<Kopfdaten>) kopfdatenRepository.findAllById(List.of(new BezirkUndWahlID("wahlID2", "wahlbezirkID2_1")));
            val expectedListOfData2 = List.of(expectedKopfdaten2);
            Assertions.assertThat(dataFromRepo)
                    .usingRecursiveComparison().ignoringCollectionOrder()
                    .isEqualTo(expectedListOfData2);
            kopfdatenRepository.deleteAll();

            api.perform(request3).andExpect(status().isOk());
            dataFromRepo = (List<Kopfdaten>) kopfdatenRepository.findAllById(List.of(new BezirkUndWahlID("wahlID1", "wahlbezirkID2_1")));
            val expectedListOfData3 = List.of(expectedKopfdaten3);

            Assertions.assertThat(dataFromRepo)
                    .usingRecursiveComparison().ignoringCollectionOrder()
                    .isEqualTo(expectedListOfData3);
        }

        @Test
        @Transactional
        void loadFromExternalOnlyIfNotFoundInRepo() throws Exception {
            val kopfdatenEntity1 = MockDataFactory.createKopfdatenEntityFor("wahlID1", "wahlbezirkID1_1",
                    Stimmzettelgebietsart.SG, "Munich-Repo1", "120",
                    "Bundestagswahl", "1201");
            val kopfdatenEntity2 = MockDataFactory.createKopfdatenEntityFor("wahlID2", "wahlbezirkID1_2",
                    Stimmzettelgebietsart.SG, "Munich-Repo2", "121",
                    "Bundestagswahl", "1202");
            kopfdatenRepository.saveAll(List.of(kopfdatenEntity1, kopfdatenEntity2));

            val request = MockMvcRequestBuilders.get("/businessActions/kopfdaten/wahlID1/wahlbezirkID1_1");

            val responseFromController_1 = api.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyAsDTO_1 = objectMapper.readValue(responseFromController_1.getResponse().getContentAsString(),
                    de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.kopfdaten.KopfdatenDTO.class);

            WireMock.verify(0, WireMock.anyRequestedFor(WireMock.anyUrl()));
            val expectedResponseBody_1 = dtoMapper.toDTO(
                    modelMapper.toModel(kopfdatenEntity1));
            Assertions.assertThat(responseBodyAsDTO_1).isEqualTo(expectedResponseBody_1);

            kopfdatenRepository.deleteAll();

            // mock infomanagement konfigurierterWahltag
            KonfigurierterWahltagDTO infomanagementKonfigurierterWahltag = MockDataFactory.createClientKonfigurierterWahltagDTO(LocalDate.now().plusMonths(1), KonfigurierterWahltagDTO.WahltagStatusEnum.AKTIV);
            WireMock.stubFor(WireMock.get("/businessActions/konfigurierterWahltag")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(infomanagementKonfigurierterWahltag))));
            // mock eai getBasisdaten
            BasisdatenDTO eaiBasisdaten = MockDataFactory.createClientBasisdatenDTO(LocalDate.now().plusMonths(1));
            WireMock.stubFor(WireMock
                    .get("/wahldaten/basisdaten?forDate=" + LocalDate.now().plusMonths(1) + "&withNummer=" + infomanagementKonfigurierterWahltag.getNummer())
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(eaiBasisdaten))));

            val responseFromController_2 = api.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyAsDTO_2 = objectMapper.readValue(responseFromController_2.getResponse().getContentAsString(),
                    de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.kopfdaten.KopfdatenDTO.class);
            //After calling 2 Clients infomanagement and eai
            WireMock.verify(2, WireMock.anyRequestedFor(WireMock.anyUrl()));

            val notExpectedResponseBody_2 = dtoMapper.toDTO(
                    modelMapper.toModel(kopfdatenEntity1));
            Assertions.assertThat(responseBodyAsDTO_2).isNotEqualTo(notExpectedResponseBody_2);
            val kopfdatenEntity3 = MockDataFactory.createKopfdatenEntityFor("wahlID1", "wahlbezirkID1_1",
                    Stimmzettelgebietsart.SG, "Munich", "120",
                    "Bundestagswahl", "1201");
            val expectedResponseBody_2 = dtoMapper.toDTO(
                    modelMapper.toModel(kopfdatenEntity3));
            Assertions.assertThat(responseBodyAsDTO_2).isEqualTo(expectedResponseBody_2);

        }

        @Test
        void technischeWlsExceptionWhenFailedCommunicationWithInfomanagementClient() throws Exception {
            KonfigurierterWahltagDTO infomanagementKonfigurierterWahltag = MockDataFactory.createClientKonfigurierterWahltagDTO(LocalDate.now().plusMonths(1), KonfigurierterWahltagDTO.WahltagStatusEnum.AKTIV);

            WireMock.stubFor(WireMock.get("/businessActions/konfigurierterWahltag")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.NOT_FOUND.value())));

            WireMock.stubFor(WireMock
                    .get("/wahldaten/basisdaten?forDate=" + LocalDate.now().plusMonths(1) + "&withNummer=" + infomanagementKonfigurierterWahltag.getNummer())
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.NOT_FOUND.value())));

            val request = MockMvcRequestBuilders.get("/businessActions/kopfdaten/wahlID1/wahlbezirkID1_1");

            val response = api.perform(request).andExpect(status().isInternalServerError()).andReturn();
            val responseBodyAsWlsExceptionDTO = objectMapper.readValue(response.getResponse().getContentAsString(), WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.T,
                    ExceptionConstants.FAILED_COMMUNICATION_WITH_SERVICE.code(), serviceID,
                    ExceptionConstants.FAILED_COMMUNICATION_WITH_SERVICE.message());
            Assertions.assertThat(responseBodyAsWlsExceptionDTO).isEqualTo(expectedWlsExceptionDTO);
        }

        @Test
        void technischeWlsExceptionWhenFailedComunicationWithEaiAndKopfdatenNotFound() throws Exception {
            KonfigurierterWahltagDTO infomanagementKonfigurierterWahltag = MockDataFactory.createClientKonfigurierterWahltagDTO(LocalDate.now().plusMonths(1), KonfigurierterWahltagDTO.WahltagStatusEnum.AKTIV);

            WireMock.stubFor(WireMock.get("/businessActions/konfigurierterWahltag")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(infomanagementKonfigurierterWahltag))));

            WireMock.stubFor(WireMock
                    .get("/wahldaten/basisdaten?forDate=" + LocalDate.now().plusMonths(1) + "&withNummer=" + infomanagementKonfigurierterWahltag.getNummer())
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.NOT_FOUND.value())));

            val request = MockMvcRequestBuilders.get("/businessActions/kopfdaten/wahlID1/wahlbezirkID1_1");

            val response = api.perform(request).andExpect(status().isInternalServerError()).andReturn();
            val responseBodyAsWlsExceptionDTO = objectMapper.readValue(response.getResponse().getContentAsString(), WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.T,
                    ExceptionConstants.FAILED_COMMUNICATION_WITH_EAI.code(), serviceID,
                    ExceptionConstants.FAILED_COMMUNICATION_WITH_EAI.message());
            Assertions.assertThat(responseBodyAsWlsExceptionDTO).isEqualTo(expectedWlsExceptionDTO);
        }

        @Test
        void fachlicheWlsExceptionWhenNoKonfigurierterWahltagNoContent() throws Exception {
            KonfigurierterWahltagDTO infomanagementKonfigurierterWahltag = MockDataFactory.createClientKonfigurierterWahltagDTO(LocalDate.now().plusMonths(1), KonfigurierterWahltagDTO.WahltagStatusEnum.AKTIV);

            WireMock.stubFor(WireMock.get("/businessActions/konfigurierterWahltag")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.NO_CONTENT.value())));

            WireMock.stubFor(WireMock
                    .get("/wahldaten/basisdaten?forDate=" + LocalDate.now().plusMonths(1) + "&withNummer=" + infomanagementKonfigurierterWahltag.getNummer())
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.NOT_FOUND.value())));

            val request = MockMvcRequestBuilders.get("/businessActions/kopfdaten/wahlID1/wahlbezirkID1_1");

            val response = api.perform(request).andExpect(status().isBadRequest()).andReturn();
            val responseBodyAsWlsExceptionDTO = objectMapper.readValue(response.getResponse().getContentAsString(), WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F,
                    ExceptionConstants.GETKOPFDATEN_NO_KONFIGURIERTERWAHLTAG.code(), serviceID,
                    ExceptionConstants.GETKOPFDATEN_NO_KONFIGURIERTERWAHLTAG.message());
            Assertions.assertThat(responseBodyAsWlsExceptionDTO).isEqualTo(expectedWlsExceptionDTO);
        }

        @Test
        void fachlichecheWlsExceptionWhenKopfdatenNoContent() throws Exception {
            KonfigurierterWahltagDTO infomanagementKonfigurierterWahltag = MockDataFactory.createClientKonfigurierterWahltagDTO(LocalDate.now().plusMonths(1), KonfigurierterWahltagDTO.WahltagStatusEnum.AKTIV);

            WireMock.stubFor(WireMock.get("/businessActions/konfigurierterWahltag")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(infomanagementKonfigurierterWahltag))));

            WireMock.stubFor(WireMock
                    .get("/wahldaten/basisdaten?forDate=" + LocalDate.now().plusMonths(1) + "&withNummer=" + infomanagementKonfigurierterWahltag.getNummer())
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.NO_CONTENT.value())));

            val request = MockMvcRequestBuilders.get("/businessActions/kopfdaten/wahlID1/wahlbezirkID1_1");

            val response = api.perform(request).andExpect(status().isBadRequest()).andReturn();
            val responseBodyAsWlsExceptionDTO = objectMapper.readValue(response.getResponse().getContentAsString(), WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F,
                    ExceptionConstants.GETKOPFDATEN_NO_BASISDATEN.code(), serviceID,
                    ExceptionConstants.GETKOPFDATEN_NO_BASISDATEN.message());
            Assertions.assertThat(responseBodyAsWlsExceptionDTO).isEqualTo(expectedWlsExceptionDTO);
        }
    }

}
