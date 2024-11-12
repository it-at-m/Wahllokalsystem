package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.rest.wahlvorstand;

import static de.muenchen.oss.wahllokalsystem.wahlvorstandservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.wahlvorstandservice.TestConstants.SPRING_TEST_PROFILE;
import static de.muenchen.oss.wahllokalsystem.wls.common.security.Profiles.NO_BEZIRKS_ID_CHECK;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.domain.wahlvorstand.WahlvorstandRepository;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.eai.infomanagement.model.KonfigurierterWahltagDTO;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.Farbe;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.WahlModel;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.Wahlart;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.WahlvorstandModelMapper;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.utils.TestDataFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.time.LocalDate;
import java.util.ArrayList;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = MicroServiceApplication.class)
@AutoConfigureMockMvc
@AutoConfigureWireMock
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE, NO_BEZIRKS_ID_CHECK })
public class WahlvorstandControllerIntegrationTest {

    @Autowired
    MockMvc api;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WahlvorstandRepository wahlvorstandRepository;

    @Autowired
    WahlvorstandDTOMapper wahlvorstandDTOMapper;

    @Autowired
    WahlvorstandModelMapper wahlvorstandModelMapper;

    @AfterEach
    void tearDown() {
        SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_WAHLVORSTAND);
        wahlvorstandRepository.deleteAll();
    }

    @Nested
    class GetWahlvorstand {

        @Test
        @WithMockUser(authorities = { Authorities.SERVICE_GET_WAHLVORSTAND, Authorities.SERVICE_UPDATE_WAHLVORSTAND, Authorities.REPOSITORY_READ_WAHLVORSTAND })
        @Transactional
        void should_returnFallbackWahlvorstand_when_noDataFound() throws Exception {
            val infomanagementKonfigurierterWahltag = TestDataFactory.CreateFromClient.konfigurierterWahltagDTO(LocalDate.now().plusMonths(1),
                    KonfigurierterWahltagDTO.WahltagStatusEnum.AKTIV);
            WireMock.stubFor(WireMock.get("/businessActions/konfigurierterWahltag")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(infomanagementKonfigurierterWahltag))));

            var searchingForWahltag = infomanagementKonfigurierterWahltag.getWahltag();
            val eaiWahlen = createWahlModels();
            WireMock.stubFor(WireMock.get("/wahldaten/wahlen?forDate=" + searchingForWahltag + "&withNummer=nummerWahltag")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json")
                            .withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsString(eaiWahlvorstand))));

            val request = MockMvcRequestBuilders.get("/businessActions/wahlvorstand/wahlbezirkID");
            val response = api.perform(request).andExpect(status().isNoContent()).andReturn();
            Assertions.assertThat(response.getResponse().getContentAsString()).isEmpty();
        }

        @Test
        void should_returnWahlvorstand_when_dataFound() throws Exception {
            val mockedWahlvorstand = TestDataFactory.CreateWahlvorstandEntity.withData();
            wahlvorstandRepository.save(mockedWahlvorstand);
            val mockedWahlvorstandModel = TestDataFactory.CreateWahlvorstandModel.fromEntity(mockedWahlvorstand);

            val request = MockMvcRequestBuilders.get("/businessActions/wahlvorstand/wahlbezirkID");
            val response = api.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyAsDTO = objectMapper.readValue(response.getResponse().getContentAsString(), WahlvorstandDTO.class);

            val expectedResponseDTO = wahlvorstandDTOMapper.toDTO(mockedWahlvorstandModel);
            Assertions.assertThat(responseBodyAsDTO).isEqualTo(expectedResponseDTO);
        }

        private List<WahlModel> createWahlModels() {
            WahlModel wahl1 = new WahlModel("wahlid1", "wahl1", 1L,
                    1L, LocalDate.now().plusMonths(1),
                    Wahlart.BAW, new Farbe(1, 1, 1), "0");
            WahlModel wahl2 = new WahlModel("wahlid2", "wahl2", 2L,
                    2L, LocalDate.now().plusMonths(2),
                    Wahlart.LTW, new Farbe(2, 2, 2), "1");
            WahlModel wahl3 = new WahlModel("wahlid3", "wahl3", 3L,
                    3L, LocalDate.now().plusMonths(3),
                    Wahlart.LTW, new Farbe(3, 3, 3), "2");
            List<WahlModel> lw = new ArrayList<>();
            lw.add(wahl1);
            lw.add(wahl2);
            lw.add(wahl3);
            return lw;
        }
    }

    @Nested
    class PostWahlvorstand {

        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_POST_WAHLVORSTAND, Authorities.REPOSITORY_DELETE_WAHLVORSTAND, Authorities.REPOSITORY_WRITE_WAHLVORSTAND }
        )
        void should_saveWahlvorstand_when_newDataSuccessfullySaved() throws Exception {
            val wahlbezirkID = "wahlbezirkID";
            val mockedWahlvorstandDTO = TestDataFactory.CreateWahlvorstandDto.withWahlbezirkID(wahlbezirkID);

            WireMock.stubFor(WireMock.put("/wahlvorstaende/anwesenheit")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json")
                            .withStatus(HttpStatus.OK.value())));

            val request = MockMvcRequestBuilders.post("/businessActions/wahlvorstand/" + wahlbezirkID)
                    .with(csrf()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(mockedWahlvorstandDTO));
            api.perform(request).andExpect(status().isOk()).andReturn();

            val wahlvorstandFromRepo = wahlvorstandRepository.findById(wahlbezirkID).get();
            val expectedWahlvorstand = wahlvorstandModelMapper.toEntity(wahlvorstandDTOMapper.toModel(mockedWahlvorstandDTO));
            Assertions.assertThat(wahlvorstandFromRepo).usingRecursiveComparison().isEqualTo(expectedWahlvorstand);
        }

        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_POST_WAHLVORSTAND, Authorities.REPOSITORY_DELETE_WAHLVORSTAND, Authorities.REPOSITORY_WRITE_WAHLVORSTAND }
        )
        void should_overrideOldWahlvorstand_when_newDataSuccessfullySaved() throws Exception {
            val wahlbezirkID = "wahlbezirkID";

            val mockedWahlvorstandToOverride = TestDataFactory.CreateWahlvorstandEntity.withData();
            wahlvorstandRepository.save(mockedWahlvorstandToOverride);
            val wahlvorstandBeforeOverridden = wahlvorstandRepository.findById(wahlbezirkID).get();

            val mockedWahlvorstandDTO = TestDataFactory.CreateWahlvorstandDto.withWahlbezirkID(wahlbezirkID);
            WireMock.stubFor(WireMock.put("/wahlvorstaende/anwesenheit")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json")
                            .withStatus(HttpStatus.OK.value())));

            val request = MockMvcRequestBuilders.post("/businessActions/wahlvorstand/" + wahlbezirkID)
                    .with(csrf()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(mockedWahlvorstandDTO));
            api.perform(request).andExpect(status().isOk()).andReturn();

            val wahlvorstandFromRepo = wahlvorstandRepository.findById(wahlbezirkID).get();
            val expectedWahlvorstand = wahlvorstandModelMapper.toEntity(wahlvorstandDTOMapper.toModel(mockedWahlvorstandDTO));

            Assertions.assertThat(wahlvorstandBeforeOverridden).usingRecursiveComparison().isEqualTo(mockedWahlvorstandToOverride);
            Assertions.assertThat(wahlvorstandFromRepo).usingRecursiveComparison().isEqualTo(expectedWahlvorstand);
            Assertions.assertThat(wahlvorstandBeforeOverridden.getWahlvorstandsmitglieder()).isNotEqualTo(wahlvorstandFromRepo.getWahlvorstandsmitglieder());
        }
    }
}
