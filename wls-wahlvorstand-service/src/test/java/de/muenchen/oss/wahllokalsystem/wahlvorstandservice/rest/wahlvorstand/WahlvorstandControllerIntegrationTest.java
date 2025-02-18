package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.rest.wahlvorstand;

import static de.muenchen.oss.wahllokalsystem.wahlvorstandservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.wahlvorstandservice.TestConstants.SPRING_TEST_PROFILE;
import static de.muenchen.oss.wahllokalsystem.wls.common.security.Profiles.NO_BEZIRKS_ID_CHECK;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.clients.aoueai.WahlvorstandClientMapper;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.domain.wahlvorstand.WahlvorstandRepository;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.eai.infomanagement.model.KonfigurierterWahltagDTO;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.WahlvorstandModelMapper;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.utils.TestDataFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.time.LocalDate;
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
    WahlvorstandClientMapper wahlvorstandClientMapper;

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
            val eaiWahlen = TestDataFactory.CreateFromClient.wahlModelList();
            WireMock.stubFor(WireMock.get("/wahldaten/wahlen?forDate=" + searchingForWahltag + "&withNummer=nummerWahltag")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json")
                            .withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(eaiWahlen))));

            val request = MockMvcRequestBuilders.get("/businessActions/wahlvorstand/wahlbezirkID");
            val response = api.perform(request).andExpect(status().isOk()).andReturn();
            Assertions.assertThat(response.getResponse().getContentAsString()).contains("FALLBACK");
        }

        @Test
        void should_returnWahlvorstand_when_dataFound() throws Exception {
            val wahlvorstand = TestDataFactory.CreateWahlvorstandEntity.withData();
            wahlvorstandRepository.save(wahlvorstand);
            val mockedWahlvorstandModel = TestDataFactory.CreateWahlvorstandModel.fromEntity(wahlvorstand);

            val request = MockMvcRequestBuilders.get("/businessActions/wahlvorstand/wahlbezirkID");
            val response = api.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyAsDTO = objectMapper.readValue(response.getResponse().getContentAsString(), WahlvorstandDTO.class);

            val expectedResponseDTO = wahlvorstandDTOMapper.toDTO(mockedWahlvorstandModel);
            Assertions.assertThat(responseBodyAsDTO).isEqualTo(expectedResponseDTO);
        }

        @Test
        void should_updateWahlvorstand_when_forceUpdateParamIsTrue() throws Exception {
            val wahlbezirkID = "wahlbezirkID";
            val infomanagementKonfigurierterWahltag = TestDataFactory.CreateFromClient.konfigurierterWahltagDTO(LocalDate.now().plusMonths(1),
                    KonfigurierterWahltagDTO.WahltagStatusEnum.AKTIV);
            WireMock.stubFor(WireMock.get("/businessActions/konfigurierterWahltag")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(infomanagementKonfigurierterWahltag))));
            var searchingForWahltag = infomanagementKonfigurierterWahltag.getWahltag();
            val eaiWahlen = TestDataFactory.CreateFromClient.wahlModelList();
            WireMock.stubFor(WireMock.get("/wahldaten/wahlen?forDate=" + searchingForWahltag + "&withNummer=nummerWahltag")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json")
                            .withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(eaiWahlen))));
            val eaiWahlvorstandDto = TestDataFactory.CreateFromClient.wahlvorstandDto(wahlbezirkID);
            WireMock.stubFor(WireMock.get("/wahlvorstaende?wahlbezirkID=" + wahlbezirkID)
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(eaiWahlvorstandDto))));

            val request = MockMvcRequestBuilders
                    .get("/businessActions/wahlvorstand/" + wahlbezirkID)
                    .header("forceUpdate", true);
            val response = api.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyAsDTO = objectMapper.readValue(response.getResponse().getContentAsString(), WahlvorstandDTO.class);

            val expectedResponseDTO = wahlvorstandDTOMapper.toDTO(wahlvorstandClientMapper.toModel(eaiWahlvorstandDto));
            Assertions.assertThat(responseBodyAsDTO).isEqualTo(expectedResponseDTO);
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
            val mockedWahlvorstandDTO = TestDataFactory.CreateWahlvorstandDto.withData();

            WireMock.stubFor(WireMock.put("/wahlvorstaende/anwesenheit")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json")
                            .withStatus(HttpStatus.OK.value())));

            val request = MockMvcRequestBuilders.post("/businessActions/wahlvorstand/" + wahlbezirkID)
                    .with(csrf()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(mockedWahlvorstandDTO));
            api.perform(request).andExpect(status().isOk()).andReturn();

            val wahlvorstandFromRepo = wahlvorstandRepository.findById(wahlbezirkID).get();
            val expectedWahlvorstand = wahlvorstandModelMapper.toEntity(wahlvorstandDTOMapper.toModel(wahlbezirkID, mockedWahlvorstandDTO));
            Assertions.assertThat(wahlvorstandFromRepo).usingRecursiveComparison().isEqualTo(expectedWahlvorstand);
        }

        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_POST_WAHLVORSTAND, Authorities.REPOSITORY_DELETE_WAHLVORSTAND, Authorities.REPOSITORY_WRITE_WAHLVORSTAND }
        )
        void should_overrideOldWahlvorstand_when_newDataSuccessfullySaved() throws Exception {
            val wahlbezirkID = "wahlbezirkID";

            val wahlvorstandToOverride = TestDataFactory.CreateWahlvorstandEntity.withData();
            wahlvorstandRepository.save(wahlvorstandToOverride);
            val wahlvorstandBeforeOverridden = wahlvorstandRepository.findById(wahlbezirkID).get();

            val mockedWahlvorstandDTO = TestDataFactory.CreateWahlvorstandDto.withData();
            WireMock.stubFor(WireMock.put("/wahlvorstaende/anwesenheit")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json")
                            .withStatus(HttpStatus.OK.value())));

            val request = MockMvcRequestBuilders.post("/businessActions/wahlvorstand/" + wahlbezirkID)
                    .with(csrf()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(mockedWahlvorstandDTO));
            api.perform(request).andExpect(status().isOk()).andReturn();

            val wahlvorstandFromRepo = wahlvorstandRepository.findById(wahlbezirkID).get();
            val expectedWahlvorstand = wahlvorstandModelMapper.toEntity(wahlvorstandDTOMapper.toModel(wahlbezirkID, mockedWahlvorstandDTO));

            Assertions.assertThat(wahlvorstandFromRepo).usingRecursiveComparison().isEqualTo(expectedWahlvorstand);
            Assertions.assertThat(wahlvorstandBeforeOverridden.getWahlvorstandsmitglieder()).isNotEqualTo(wahlvorstandFromRepo.getWahlvorstandsmitglieder());
        }
    }
}
