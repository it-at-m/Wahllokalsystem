package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.eaiservice.Authorities;
import de.muenchen.oss.wahllokalsystem.eaiservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.eaiservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Stimmzettelgebiet;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.StimmzettelgebietRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Stimmzettelgebietsart;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Wahl;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.WahlRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Wahlart;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Wahlbezirk;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.WahlbezirkRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Wahltag;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.WahltageRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.BasisdatenDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahlDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahlberechtigteDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahlbezirkArtDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahlbezirkDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahltagDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.service.wahldaten.WahldatenMapper;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = MicroServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles({ TestConstants.SPRING_TEST_PROFILE })
public class WahldatenControllerIntegrationTest {

    private static final String EMPTY_ARRAY_JSON = "[]";

    @Value("${service.info.oid}")
    String serviceID;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    WahltageRepository wahltageRepository;

    @Autowired
    WahlRepository wahlRepository;

    @Autowired
    StimmzettelgebietRepository stimmzettelgebietRepository;

    @Autowired
    WahlbezirkRepository wahlbezirkRepository;

    @Autowired
    WahldatenMapper wahldatenMapper;

    @Autowired
    ObjectMapper objectMapper;

    @AfterEach
    public void tearDown() throws Exception {
        wahlbezirkRepository.deleteAll();
        stimmzettelgebietRepository.deleteAll();
        wahlRepository.deleteAll();
        wahltageRepository.deleteAll();
    }

    @Nested
    class LoadWahlberechtigte {

        @Test
        @WithMockUser(authorities = Authorities.SERVICE_LOAD_WAHLBERECHTIGTE)
        void dataFound() throws Exception {
            val wahltag = wahltageRepository.save(new Wahltag(LocalDate.now(), "beschreibung wahltag", "nummer"));
            val wahl = wahlRepository.save(new Wahl("wahl", Wahlart.BTW, wahltag, "1"));
            val stimmzettelgebiet = stimmzettelgebietRepository.save(new Stimmzettelgebiet("sgz1", "sgz1", Stimmzettelgebietsart.SK, wahl));
            val wahlbezirkToFind = wahlbezirkRepository.save(new Wahlbezirk(WahlbezirkArtDTO.UWB, "nummer", stimmzettelgebiet, 10, 11, 12));

            wahlbezirkRepository.save(new Wahlbezirk(WahlbezirkArtDTO.UWB, "nummer", stimmzettelgebiet, 10, 11, 12));

            val request = get(WahldatenController.WAHLDATEN_REQUEST_MAPPING + "/wahlbezirke/" + wahlbezirkToFind.getId() + "/wahlberechtigte");

            val response = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyAsDTO = objectMapper.readValue(response.getResponse().getContentAsString(), WahlberechtigteDTO[].class);

            val expectedResponseBody = new WahlberechtigteDTO[] { wahldatenMapper.toWahlberechtigteDTO(wahlbezirkToFind) };

            Assertions.assertThat(responseBodyAsDTO).isEqualTo(expectedResponseBody);
        }

        @Test
        @WithMockUser(authorities = Authorities.SERVICE_LOAD_WAHLBERECHTIGTE)
        void noDataFound() throws Exception {
            val request = get(WahldatenController.WAHLDATEN_REQUEST_MAPPING + "/wahlbezirke/" + UUID.randomUUID() + "/wahlberechtigte");

            val response = mockMvc.perform(request).andExpect(status().isOk()).andReturn();

            Assertions.assertThat(response.getResponse().getContentAsString()).isEqualTo(EMPTY_ARRAY_JSON);
        }

        @Test
        @WithMockUser(authorities = Authorities.SERVICE_LOAD_WAHLBERECHTIGTE)
        void wlsExceptionOnValidationFailed() throws Exception {
            val request = get(WahldatenController.WAHLDATEN_REQUEST_MAPPING + "/wahlbezirke/     /wahlberechtigte");

            val response = mockMvc.perform(request).andExpect(status().isBadRequest()).andReturn();
            val responseBodyAsWlsException = objectMapper.readValue(response.getResponse().getContentAsString(StandardCharsets.UTF_8), WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F,
                    ExceptionConstants.LOADWAHLBERECHTIGTE_SUCHKRITERIEN_UNVOLLSTAENDIG.code(), serviceID,
                    ExceptionConstants.LOADWAHLBERECHTIGTE_SUCHKRITERIEN_UNVOLLSTAENDIG.message());

            Assertions.assertThat(responseBodyAsWlsException).isEqualTo(expectedWlsExceptionDTO);
        }

    }

    @Nested
    class LoadWahltageSinceIncluding {

        @Test
        @WithMockUser(authorities = Authorities.SERVICE_LOAD_WAHLTAGE)
        void dataFound() throws Exception {
            val wahltageSinceAsString = "2024-07-03";

            val wahltag1ToFind = wahltageRepository.save(new Wahltag(LocalDate.of(2024, 12, 24), "wahltag1", "1"));
            val wahltag2ToFind = wahltageRepository.save(new Wahltag(LocalDate.of(2024, 10, 3), "wahltag2", "2"));
            val wahltag3ToFind = wahltageRepository.save(new Wahltag(LocalDate.parse(wahltageSinceAsString), "wahltag3", "3"));
            wahltageRepository.save(new Wahltag(LocalDate.of(2024, 1, 1), "wahltagNotFound1", "0"));
            wahltageRepository.save(new Wahltag(LocalDate.of(2024, 7, 2), "wahltagNotFound2", "0"));

            val request = get(WahldatenController.WAHLDATEN_REQUEST_MAPPING + "/wahltage?includingSince=" + wahltageSinceAsString);

            val response = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyAsDTO = objectMapper.readValue(response.getResponse().getContentAsString(), WahltagDTO[].class);

            val expectedResponseBody = new WahltagDTO[] {
                    wahldatenMapper.toDTO(wahltag1ToFind),
                    wahldatenMapper.toDTO(wahltag2ToFind),
                    wahldatenMapper.toDTO(wahltag3ToFind)
            };
            Assertions.assertThat(responseBodyAsDTO).containsOnly(expectedResponseBody);
        }

        @Test
        @WithMockUser(authorities = Authorities.SERVICE_LOAD_WAHLTAGE)
        void noDataFound() throws Exception {
            val request = get(WahldatenController.WAHLDATEN_REQUEST_MAPPING + "/wahltage?includingSince=1900-01-01");

            val response = mockMvc.perform(request).andExpect(status().isOk()).andReturn();

            Assertions.assertThat(response.getResponse().getContentAsString()).isEqualTo(EMPTY_ARRAY_JSON);
        }

        @Test
        @WithMockUser(authorities = Authorities.SERVICE_LOAD_WAHLTAGE)
        void wlsExceptionOnDateQueryParameterIsMissing() throws Exception {
            val request = get(WahldatenController.WAHLDATEN_REQUEST_MAPPING + "/wahltage?includingSince=  ");

            val response = mockMvc.perform(request).andExpect(status().isBadRequest()).andReturn();
            val responseBodyAsWlsException = objectMapper.readValue(response.getResponse().getContentAsString(StandardCharsets.UTF_8), WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F,
                    ExceptionConstants.LOADWAHLTAGE_TAG_FEHLT.code(), serviceID,
                    ExceptionConstants.LOADWAHLTAGE_TAG_FEHLT.message());

            Assertions.assertThat(responseBodyAsWlsException).isEqualTo(expectedWlsExceptionDTO);
        }
    }

    @Nested
    class LoadWahlbezirke {

        @Test
        @WithMockUser(authorities = Authorities.SERVICE_LOAD_WAHLBEZIRKE)
        void dataFound() throws Exception {
            val wahltagDate = "2024-07-03";
            val nummerToFind = "1";

            val wahltag1 = wahltageRepository.save(new Wahltag(LocalDate.parse(wahltagDate), "wahltag1", "1"));
            wahltageRepository.save(new Wahltag(LocalDate.parse(wahltagDate), "wahltag2", "2"));

            val wahl = wahlRepository.save(new Wahl("wahl", Wahlart.BTW, wahltag1, "nummer"));
            val stimmzettelgebiet = stimmzettelgebietRepository.save(new Stimmzettelgebiet("nummer", "name", Stimmzettelgebietsart.SK, wahl));

            val wahlbezirk1 = wahlbezirkRepository.save(new Wahlbezirk(WahlbezirkArtDTO.UWB, "wbz1", stimmzettelgebiet, 0, 0, 0));
            val wahlbezirk2 = wahlbezirkRepository.save(new Wahlbezirk(WahlbezirkArtDTO.BWB, "wbz2", stimmzettelgebiet, 0, 0, 0));

            val request = get(WahldatenController.WAHLDATEN_REQUEST_MAPPING + "/wahlbezirk?forDate=" + wahltagDate + "&withNummer=" + nummerToFind);

            val response = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyAsDTO = objectMapper.readValue(response.getResponse().getContentAsString(), WahlbezirkDTO[].class);

            val expectedResponseBody = new WahlbezirkDTO[] {
                    wahldatenMapper.toDTO(wahlbezirk1),
                    wahldatenMapper.toDTO(wahlbezirk2)
            };
            Assertions.assertThat(responseBodyAsDTO).containsOnly(expectedResponseBody);
        }

        @Test
        @WithMockUser(authorities = Authorities.SERVICE_LOAD_WAHLBEZIRKE)
        void noDataFound() throws Exception {
            val request = get(WahldatenController.WAHLDATEN_REQUEST_MAPPING + "/wahlbezirk?forDate=2024-07-03&withNummer=1");

            val response = mockMvc.perform(request).andExpect(status().isOk()).andReturn();

            Assertions.assertThat(response.getResponse().getContentAsString()).isEqualTo(EMPTY_ARRAY_JSON);
        }

        @Test
        @WithMockUser(authorities = Authorities.SERVICE_LOAD_WAHLBEZIRKE)
        void wlsExceptionOnNummerQueryParameterIsMissing() throws Exception {
            val request = get(WahldatenController.WAHLDATEN_REQUEST_MAPPING + "/wahlbezirk?forDate=2024-07-03");

            val response = mockMvc.perform(request).andExpect(status().isBadRequest()).andReturn();
            val responseBodyAsWlsException = objectMapper.readValue(response.getResponse().getContentAsString(StandardCharsets.UTF_8), WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F,
                    ExceptionConstants.LOADWAHLEBZIRKE_NUMMER_FEHLT.code(), serviceID,
                    ExceptionConstants.LOADWAHLEBZIRKE_NUMMER_FEHLT.message());

            Assertions.assertThat(responseBodyAsWlsException).isEqualTo(expectedWlsExceptionDTO);
        }

        @Test
        @WithMockUser(authorities = Authorities.SERVICE_LOAD_WAHLBEZIRKE)
        void wlsExceptionOnDateQueryParameterIsMissing() throws Exception {
            val request = get(WahldatenController.WAHLDATEN_REQUEST_MAPPING + "/wahlbezirk?nummer=2");

            val response = mockMvc.perform(request).andExpect(status().isBadRequest()).andReturn();
            val responseBodyAsWlsException = objectMapper.readValue(response.getResponse().getContentAsString(StandardCharsets.UTF_8), WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F,
                    ExceptionConstants.LOADWAHLEBZIRKE_WAHLTAG_FEHLT.code(), serviceID,
                    ExceptionConstants.LOADWAHLEBZIRKE_WAHLTAG_FEHLT.message());

            Assertions.assertThat(responseBodyAsWlsException).isEqualTo(expectedWlsExceptionDTO);
        }
    }

    @Nested
    class LoadWahlen {

        @Test
        @WithMockUser(authorities = Authorities.SERVICE_LOAD_WAHLEN)
        void dataFound() throws Exception {
            val wahltagDate = "2024-07-03";
            val nummerToFind = "1";

            val wahltag1 = wahltageRepository.save(new Wahltag(LocalDate.parse(wahltagDate), "wahltag1", "1"));
            val wahltag2 = wahltageRepository.save(new Wahltag(LocalDate.parse(wahltagDate), "wahltag2", "2"));

            val wahl1ToFind = wahlRepository.save(new Wahl("wahl1", Wahlart.BTW, wahltag1, "nummer"));
            val wahl2ToFind = wahlRepository.save(new Wahl("wahl2", Wahlart.BTW, wahltag1, "nummer"));
            wahlRepository.save(new Wahl("wahl", Wahlart.BTW, wahltag2, "nummer"));

            val request = get(WahldatenController.WAHLDATEN_REQUEST_MAPPING + "/wahlen?forDate=" + wahltagDate + "&withNummer=" + nummerToFind);

            val response = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyAsDTO = objectMapper.readValue(response.getResponse().getContentAsString(), WahlDTO[].class);

            val expectedResponseBody = new WahlDTO[] {
                    wahldatenMapper.toDTO(wahl1ToFind),
                    wahldatenMapper.toDTO(wahl2ToFind)
            };
            Assertions.assertThat(responseBodyAsDTO).containsOnly(expectedResponseBody);
        }

        @Test
        @WithMockUser(authorities = Authorities.SERVICE_LOAD_WAHLEN)
        void noDataFound() throws Exception {
            val request = get(WahldatenController.WAHLDATEN_REQUEST_MAPPING + "/wahlen?forDate=2024-07-03&withNummer=1");

            val response = mockMvc.perform(request).andExpect(status().isOk()).andReturn();

            Assertions.assertThat(response.getResponse().getContentAsString()).isEqualTo(EMPTY_ARRAY_JSON);
        }

        @Test
        @WithMockUser(authorities = Authorities.SERVICE_LOAD_WAHLEN)
        void wlsExceptionOnNummerQueryParameterIsMissing() throws Exception {
            val request = get(WahldatenController.WAHLDATEN_REQUEST_MAPPING + "/wahlen?forDate=2024-07-03");

            val response = mockMvc.perform(request).andExpect(status().isBadRequest()).andReturn();
            val responseBodyAsWlsException = objectMapper.readValue(response.getResponse().getContentAsString(StandardCharsets.UTF_8), WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F,
                    ExceptionConstants.LOADWAHLEN_NUMMER_FEHLT.code(), serviceID,
                    ExceptionConstants.LOADWAHLEN_NUMMER_FEHLT.message());

            Assertions.assertThat(responseBodyAsWlsException).isEqualTo(expectedWlsExceptionDTO);
        }

        @Test
        @WithMockUser(authorities = Authorities.SERVICE_LOAD_WAHLEN)
        void wlsExceptionOnDateQueryParameterIsMissing() throws Exception {
            val request = get(WahldatenController.WAHLDATEN_REQUEST_MAPPING + "/wahlen?withNummer=1");

            val response = mockMvc.perform(request).andExpect(status().isBadRequest()).andReturn();
            val responseBodyAsWlsException = objectMapper.readValue(response.getResponse().getContentAsString(StandardCharsets.UTF_8), WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F,
                    ExceptionConstants.LOADWAHLEN_WAHLTAG_FEHLT.code(), serviceID,
                    ExceptionConstants.LOADWAHLEN_WAHLTAG_FEHLT.message());

            Assertions.assertThat(responseBodyAsWlsException).isEqualTo(expectedWlsExceptionDTO);
        }

    }

    @Nested
    class LoadBasisdaten {

        @Test
        @WithMockUser(authorities = Authorities.SERVICE_LOAD_BASISDATEN)
        void dataFound() throws Exception {
            val wahltagDateToFind = "2024-07-03";
            val nummerToFind = "1";

            val wahltag = wahltageRepository.save(new Wahltag(LocalDate.parse(wahltagDateToFind), "beschreibung wahltag", nummerToFind));
            val wahl = wahlRepository.save(new Wahl("wahl", Wahlart.BTW, wahltag, "1"));
            val stimmzettelgebiet = stimmzettelgebietRepository.save(new Stimmzettelgebiet("sgz1", "sgz1", Stimmzettelgebietsart.SK, wahl));
            val wahlbezirkToFind = wahlbezirkRepository.save(new Wahlbezirk(WahlbezirkArtDTO.UWB, "nummer", stimmzettelgebiet, 10, 11, 12));

            val request = get(WahldatenController.WAHLDATEN_REQUEST_MAPPING + "/basisdaten?forDate=" + wahltagDateToFind + "&withNummer=" + nummerToFind);

            val response = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyAsDTO = objectMapper.readValue(response.getResponse().getContentAsString(), BasisdatenDTO.class);

            val expectedResponseBody = new BasisdatenDTO(Set.of(wahldatenMapper.toBasisstrukturdatenDTO(wahlbezirkToFind)),
                    Set.of(wahldatenMapper.toDTO(wahl)),
                    Set.of(wahldatenMapper.toDTO(wahlbezirkToFind)),
                    Set.of(wahldatenMapper.toDTO(stimmzettelgebiet)));
            Assertions.assertThat(responseBodyAsDTO).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(expectedResponseBody);
        }

        @Test
        @WithMockUser(authorities = Authorities.SERVICE_LOAD_BASISDATEN)
        void noDataFound() throws Exception {
            val request = get(WahldatenController.WAHLDATEN_REQUEST_MAPPING + "/basisdaten?forDate=2024-07-03&withNummer=1");

            val response = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
            val responseAsDTO = objectMapper.readValue(response.getResponse().getContentAsString(), BasisdatenDTO.class);

            val expectedResponseDTO = new BasisdatenDTO(null, null, null, null);

            Assertions.assertThat(responseAsDTO).isEqualTo(expectedResponseDTO);
        }

        @Test
        @WithMockUser(authorities = Authorities.SERVICE_LOAD_BASISDATEN)
        void wlsExceptionOnNummerQueryParameterIsMissing() throws Exception {
            val request = get(WahldatenController.WAHLDATEN_REQUEST_MAPPING + "/basisdaten?forDate=2024-07-03");

            val response = mockMvc.perform(request).andExpect(status().isBadRequest()).andReturn();
            val responseBodyAsWlsException = objectMapper.readValue(response.getResponse().getContentAsString(StandardCharsets.UTF_8), WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F,
                    ExceptionConstants.LOADBASISDATEN_NUMMER_FEHLT.code(), serviceID,
                    ExceptionConstants.LOADBASISDATEN_NUMMER_FEHLT.message());

            Assertions.assertThat(responseBodyAsWlsException).isEqualTo(expectedWlsExceptionDTO);
        }

        @Test
        @WithMockUser(authorities = Authorities.SERVICE_LOAD_BASISDATEN)
        void wlsExceptionOnDateQueryParameterIsMissing() throws Exception {
            val request = get(WahldatenController.WAHLDATEN_REQUEST_MAPPING + "/basisdaten?withNummer=1");

            val response = mockMvc.perform(request).andExpect(status().isBadRequest()).andReturn();
            val responseBodyAsWlsException = objectMapper.readValue(response.getResponse().getContentAsString(StandardCharsets.UTF_8), WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F,
                    ExceptionConstants.LOADBASISDATEN_TAG_FEHLT.code(), serviceID,
                    ExceptionConstants.LOADBASISDATEN_TAG_FEHLT.message());

            Assertions.assertThat(responseBodyAsWlsException).isEqualTo(expectedWlsExceptionDTO);
        }

    }
}
