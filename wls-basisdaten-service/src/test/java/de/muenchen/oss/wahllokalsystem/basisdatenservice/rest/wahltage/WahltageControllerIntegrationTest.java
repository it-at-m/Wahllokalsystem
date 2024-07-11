package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahltage;

import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.WahltageClientMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.WahltageClientMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.KandidatRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahltag;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahltagRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahlvorschlagRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahltagDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltag.WahltagModelMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
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
import org.springframework.boot.test.mock.mockito.SpyBean;
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
public class WahltageControllerIntegrationTest {

    @Value("${service.info.oid}")
    String serviceID;

    @Autowired
    MockMvc api;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WahltageDTOMapper dtoMapper;

    @Autowired
    WahltagModelMapper modelMapper;

    @Autowired
    WahltageClientMapper wahltageClientMapper;

    @Autowired
    WahltagRepository wahltagRepository;

    @Autowired
    WahlvorschlagRepository wahlvorschlagRepository;

    @Autowired
    KandidatRepository kandidatRepository;

    @AfterEach
    void tearDown() {
        SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_DELETE_WAHLTAGE);
        wahltagRepository.deleteAll();
    }

    @BeforeEach
    void setup() {
        WireMock.resetAllRequests();
    }

    @Nested
    class GetWahltage {

        @Test
        void loadedFromExternal() throws Exception {

            String requestDate = LocalDate.now().minusMonths(3).toString();

            val eaiWahltage = createClientWahltageDTO(LocalDate.now().minusMonths(3));
            WireMock.stubFor(WireMock.get("/wahldaten/wahltage?includingAfter=" + requestDate)
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(eaiWahltage))));

            val request = MockMvcRequestBuilders.get("/businessActions/wahltage");

            val responseFromController = api.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyAsDTO = objectMapper.readValue(responseFromController.getResponse().getContentAsString(), de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahltage.WahltagDTO[].class);

            val expectedResponseBody = dtoMapper.fromListOfWahltagModelToListOfWahltagDTO(wahltageClientMapper.fromRemoteClientWahltageDTOtoListOfWahltagModel(eaiWahltage));

            Assertions.assertThat(responseBodyAsDTO).containsExactlyInAnyOrderElementsOf(expectedResponseBody);
        }

        @Test
        @Transactional
        void externalDataIsPersisted() throws Exception {

            String requestDate = LocalDate.now().minusMonths(3).toString();

            val eaiWahltage = createClientWahltageDTO(LocalDate.now().minusMonths(3));
            WireMock.stubFor(WireMock.get("/wahldaten/wahltage?includingAfter=" + requestDate)
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(eaiWahltage))));

            val request = MockMvcRequestBuilders.get("/businessActions/wahltage");

            api.perform(request).andExpect(status().isOk());

            val dataFromRepo = wahltagRepository.findAllByOrderByWahltagAsc();

            val expectedListOfData = modelMapper.fromWahltagModelToWahltagEntityList(
                    wahltageClientMapper.fromRemoteClientWahltageDTOtoListOfWahltagModel(eaiWahltage)
            );

            Assertions.assertThat(dataFromRepo)
                    .usingRecursiveComparison().ignoringCollectionOrder()
                    .ignoringFields("id")
                    .isEqualTo(expectedListOfData);
        }

        @Test
        void loadFromRepository() throws Exception {

            val entitiesToFind = modelMapper
                    .fromWahltagModelToWahltagEntityList(wahltageClientMapper
                            .fromRemoteClientWahltageDTOtoListOfWahltagModel(createClientWahltageDTO(LocalDate.now().minusMonths(3))));
            //val savedEntities = wahltagRepository.saveAll(entitiesToFind);


            val request = MockMvcRequestBuilders.get("/businessActions/wahltage");

            //val responseFromController = api.perform(request).andExpect(status().isOk()).andReturn();



            // beim lesen wird im service auch gespeichert und wegen uuid wird doppelt gespeichert.

            val response = api.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyAsListOfDTOs = objectMapper.readValue(response.getResponse().getContentAsString(), de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahltage.WahltagDTO[].class);

            val expectedResponseBody = dtoMapper.fromListOfWahltagModelToListOfWahltagDTO(
                    modelMapper.fromWahltagEntityToWahltagModelList(entitiesToFind)
            );

            Assertions.assertThat(responseBodyAsListOfDTOs).containsExactlyInAnyOrderElementsOf(expectedResponseBody);
            //WireMock.verify(0, WireMock.anyRequestedFor(WireMock.anyUrl()));
        }

        @Test
        void technischeWlsExceptionWhenNoExternalDataFound() throws Exception {

            String requestDate = LocalDate.now().minusMonths(3).toString();

            WireMock.stubFor(WireMock.get("/wahldaten/wahltage?includingAfter=" + requestDate)
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.NOT_FOUND.value())));

            val request = MockMvcRequestBuilders.get("/businessActions/wahltage");

            val response = api.perform(request).andExpect(status().isInternalServerError()).andReturn();
            val responseBodyAsWlsExceptionDTO = objectMapper.readValue(response.getResponse().getContentAsString(), WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.T,
                    ExceptionConstants.FAILED_COMMUNICATION_WITH_EAI.code(), serviceID,
                    ExceptionConstants.FAILED_COMMUNICATION_WITH_EAI.message());
            Assertions.assertThat(responseBodyAsWlsExceptionDTO).isEqualTo(expectedWlsExceptionDTO);
        }
    }

    private de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahltageDTO createClientWahltageDTO(LocalDate sinceTag) {

        val clientWahltageDTO = new de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahltageDTO();

        val wahltag1 = new de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahltagDTO();
        wahltag1.setIdentifikator("identifikatorWahltag1");
        wahltag1.setBeschreibung("beschreibungWahltag1");
        wahltag1.setNummer("nummerWahltag1");
        wahltag1.setTag(LocalDate.now().minusMonths(2));

        val wahltag2 = new de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahltagDTO();
        wahltag2.setIdentifikator("identifikatorWahltag2");
        wahltag2.setBeschreibung("beschreibungWahltag2");
        wahltag2.setNummer("nummerWahltag2");
        wahltag2.setTag(LocalDate.now().minusMonths(1));

        val wahltag3 = new WahltagDTO();
        wahltag3.setIdentifikator("identifikatorWahltag3");
        wahltag3.setBeschreibung("beschreibungWahltag3");
        wahltag3.setNummer("nummerWahltag3");
        wahltag3.setTag(LocalDate.now().plusMonths(1));



        val wahltage = Set.of(wahltag1, wahltag2, wahltag3);
        clientWahltageDTO.setWahltage(wahltage);

        return clientWahltageDTO;
    }

}
