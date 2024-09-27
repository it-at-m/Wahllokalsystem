package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlbezirke;

import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.WahlbezirkeClientMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlbezirk.Wahlbezirk;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlbezirk.WahlbezirkRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.common.WahltagRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.WahlRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlbezirke.WahlbezirkModelMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlModelMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.MockDataFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
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
public class WahlbezirkeControllerIntegrationTest {

    @Value("${service.info.oid}")
    String serviceID;

    @Autowired
    MockMvc api;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WahlbezirkDTOMapper wahlbezirkDTOMapper;

    @Autowired
    WahlbezirkModelMapper wahlbezirkModelMapper;

    @Autowired
    WahlbezirkeClientMapper wahlbezirkeClientMapper;

    @Autowired
    WahlModelMapper wahlModelMapper;

    @Autowired
    WahlbezirkRepository wahlbezirkRepository;

    @Autowired
    WahltagRepository wahltagRepository;

    @Autowired
    WahlRepository wahlRepository;

    @Autowired
    ExceptionFactory exceptionFactory;

    @AfterEach
    void tearDown() {
        SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_WAHLBEZIRK, Authorities.REPOSITORY_DELETE_WAHL, Authorities.REPOSITORY_DELETE_WAHLTAG);
        wahlbezirkRepository.deleteAll();
        wahlRepository.deleteAll();
        wahltagRepository.deleteAll();
    }

    @BeforeEach
    void setup() {
        WireMock.resetAllRequests();
    }

    @Nested
    class GetWahlbezirke {

        @Test
        void loadedFromExternal() throws Exception {
            val forWahltagDate = LocalDate.now().minusMonths(2);
            val wahltagID = "_identifikatorWahltag1";
            val wahltagNummer = "nummerWahltag1";
            val wahlen = MockDataFactory.createWahlEntityList();
            wahlRepository.saveAll(wahlen);

            val eaiWahlbezirke = MockDataFactory.createSetOfClientWahlbezirkDTO(forWahltagDate);
            WireMock.stubFor(WireMock.get("/wahldaten/wahlbezirk?forDate=" + forWahltagDate + "&withNummer=" + wahltagNummer)
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(eaiWahlbezirke))));

            val repoWahltage = MockDataFactory.createWahltagList("");
            wahltagRepository.saveAll(repoWahltage);

            val request = MockMvcRequestBuilders.get("/businessActions/wahlbezirke/" + wahltagID);

            val responseFromController = api.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyAsDTO = objectMapper.readValue(responseFromController.getResponse().getContentAsString(),
                    de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlbezirke.WahlbezirkDTO[].class);

            val expectedResponseBody = wahlbezirkDTOMapper
                    .fromListOfWahlbezirkModelToListOfWahlbezirkDTO(List.copyOf(wahlbezirkeClientMapper.fromRemoteSetOfDTOsToSetOfModels(eaiWahlbezirke)));

            Assertions.assertThat(responseBodyAsDTO).containsExactlyInAnyOrderElementsOf(expectedResponseBody);
        }

        @Test
        @Transactional
        void externalDataIsPersisted() throws Exception {
            val forWahltagDate = LocalDate.now().minusMonths(2);
            val wahltagID = "_identifikatorWahltag1";
            val wahltagNummer = "nummerWahltag1";

            val repoWahltage = MockDataFactory.createWahltagList("");
            wahltagRepository.saveAll(repoWahltage);

            val repoWahlen = MockDataFactory.createWahlEntityList();
            wahlRepository.saveAll(repoWahlen);

            val eaiWahlbezirke = MockDataFactory.createSetOfClientWahlbezirkDTO(forWahltagDate);
            WireMock.stubFor(WireMock.get("/wahldaten/wahlbezirk?forDate=" + forWahltagDate + "&withNummer=" + wahltagNummer)
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(eaiWahlbezirke))));

            val request = MockMvcRequestBuilders.get("/businessActions/wahlbezirke/" + wahltagID);

            api.perform(request).andExpect(status().isOk()).andReturn();
            val dataFromRepo = wahlbezirkRepository.findAll();
            val expectedListOfData = wahlbezirkModelMapper.fromListOfWahlbezirkModeltoListOfWahlbezirkEntities(
                    (wahlbezirkeClientMapper.fromRemoteSetOfDTOsToSetOfModels(eaiWahlbezirke)).stream().toList());
            Assertions.assertThat(dataFromRepo).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(expectedListOfData);
        }

        @Test
        void repoHasPriorityForRemoteOnLoad() throws Exception {
            val forWahltagDate = LocalDate.now().minusMonths(2);
            val wahltagID = "_identifikatorWahltag1";

            val repoWahltage = MockDataFactory.createWahltagList("");
            wahltagRepository.saveAll(repoWahltage);

            val entitiesToFindInRepository = wahlbezirkModelMapper
                    .fromListOfWahlbezirkModeltoListOfWahlbezirkEntities(wahlbezirkeClientMapper
                            .fromRemoteSetOfDTOsToSetOfModels(
                                    MockDataFactory.createSetOfClientWahlbezirkDTO(forWahltagDate))
                            .stream().toList());
            val savedEntitiesInRepository_1 = wahlbezirkRepository.saveAll(entitiesToFindInRepository);

            val request = MockMvcRequestBuilders.get("/businessActions/wahlbezirke/" + wahltagID);

            val responseFromController = api.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyAsListOfDTOs = objectMapper.readValue(responseFromController.getResponse().getContentAsString(),
                    de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlbezirke.WahlbezirkDTO[].class);

            WireMock.verify(0, WireMock.anyRequestedFor(WireMock.anyUrl()));

            val expectedResponseBody_1 = wahlbezirkDTOMapper.fromListOfWahlbezirkModelToListOfWahlbezirkDTO(
                    wahlbezirkModelMapper.fromListOfWahlbezirkEntityToListOfWahlbezirkModel((List<Wahlbezirk>) savedEntitiesInRepository_1));

            Assertions.assertThat(new ArrayList<>(Arrays.asList(responseBodyAsListOfDTOs)))
                    .usingRecursiveComparison().ignoringCollectionOrder()
                    .isEqualTo(expectedResponseBody_1);

            val expectedResponseBody_2 = wahlbezirkDTOMapper.fromListOfWahlbezirkModelToListOfWahlbezirkDTO(
                    wahlbezirkModelMapper.fromListOfWahlbezirkEntityToListOfWahlbezirkModel(wahlbezirkRepository.findAll()));

            Assertions.assertThat(responseBodyAsListOfDTOs).containsExactlyInAnyOrderElementsOf(expectedResponseBody_2);
        }

        @Test
        void technischeWlsExceptionWhenNoExternalDataFound() throws Exception {
            val forWahltagDate = LocalDate.now().minusMonths(2);
            val wahltagID = "_identifikatorWahltag1";
            val wahltagNummer = "nummerWahltag1";
            val repoWahltage = MockDataFactory.createWahltagList("");
            wahltagRepository.saveAll(repoWahltage);

            WireMock.stubFor(WireMock.get("/wahldaten/wahlbezirk?forDate=" + forWahltagDate + "&withNummer=" + wahltagNummer)
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.NOT_FOUND.value())));

            val request = MockMvcRequestBuilders.get("/businessActions/wahlbezirke/" + wahltagID);

            val response = api.perform(request).andExpect(status().isInternalServerError()).andReturn();
            val responseBodyAsWlsExceptionDTO = objectMapper.readValue(response.getResponse().getContentAsString(), WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.T,
                    ExceptionConstants.FAILED_COMMUNICATION_WITH_EAI.code(), serviceID,
                    ExceptionConstants.FAILED_COMMUNICATION_WITH_EAI.message());
            Assertions.assertThat(responseBodyAsWlsExceptionDTO).isEqualTo(expectedWlsExceptionDTO);
        }
    }
}
