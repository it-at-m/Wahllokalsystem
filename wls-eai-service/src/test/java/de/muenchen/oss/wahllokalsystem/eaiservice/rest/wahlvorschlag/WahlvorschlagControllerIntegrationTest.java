package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag;

import static de.muenchen.oss.wahllokalsystem.eaiservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.eaiservice.Authorities;
import de.muenchen.oss.wahllokalsystem.eaiservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.Kandidat;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.Referendumoption;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.Referendumvorlage;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.Referendumvorlagen;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.ReferendumvorlagenRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.Wahlvorschlaege;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.WahlvorschlaegeListe;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.WahlvorschlaegeListeRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.Wahlvorschlag;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.WahlvorschlagRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.dto.ReferendumvorlagenDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.dto.WahlvorschlaegeDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.dto.WahlvorschlaegeListeDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.service.wahlvorschlag.WahlvorschlagMapper;
import de.muenchen.oss.wahllokalsystem.eaiservice.service.wahlvorschlag.WahlvorschlagService;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.Set;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = MicroServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE })
public class WahlvorschlagControllerIntegrationTest {

    @Value("${service.info.oid}")
    String serviceInfoOid;

    @Autowired
    MockMvc api;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WahlvorschlagRepository wahlvorschlagRepository;

    @Autowired
    WahlvorschlaegeListeRepository wahlvorschlaegeListeRepository;

    @Autowired
    private ReferendumvorlagenRepository referendumvorlagenRepository;

    @Autowired
    WahlvorschlagMapper wahlvorschlagMapper;

    @Autowired
    WahlvorschlagService wahlvorschlagService;

    @Autowired
    ExceptionFactory exceptionFactory;

    @Autowired
    EntityManager entityManager;

    @AfterEach
    void tearDown() {
        wahlvorschlagRepository.deleteAll();
    }

    @Nested
    class LoadWahlvorschlaege {

        @Test
        @WithMockUser(authorities = Authorities.SERVICE_LOAD_WAHLVORSCHLAEGE)
        void noDataFound() throws Exception {
            val request = MockMvcRequestBuilders.get("/vorschlaege/wahl/wahlID/wahlbezirkID");

            val response = api.perform(request).andExpect(status().isNotFound()).andReturn();

            Assertions.assertThat(response.getResponse().getContentAsString()).isEmpty();
        }

        @Test
        @WithMockUser(authorities = Authorities.SERVICE_LOAD_WAHLVORSCHLAEGE)
        @Transactional
        void dataFound() throws Exception {

            val wahlvorschlag1 = new Wahlvorschlag(1, "wahlvorschlag1", true, Set.of(
                    new Kandidat("name1", 1, false, 1, false),
                    new Kandidat("name2", 2, true, 2, true)));
            val wahlvorschlag2 = new Wahlvorschlag(1, "wahlvorschlag2", true, Set.of(
                    new Kandidat("name3", 1, false, 1, false),
                    new Kandidat("name4", 2, true, 2, true)));

            val wahlvorschlaege1 = new Wahlvorschlaege("wahlbezirkID", "wahlID", "stimmzettelgebietID", Set.of(wahlvorschlag1, wahlvorschlag2));

            val wahlvorschlaegeToLoad = wahlvorschlagRepository.save(wahlvorschlaege1);

            val request = MockMvcRequestBuilders.get("/vorschlaege/wahl/" + wahlvorschlaegeToLoad.getWahlID() + "/" + wahlvorschlaegeToLoad.getWahlbezirkID());

            val response = api.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyAsDTO = objectMapper.readValue(response.getResponse().getContentAsString(), WahlvorschlaegeDTO.class);

            val expectedResponseDTO = wahlvorschlagMapper.toDTO(wahlvorschlaegeToLoad);

            Assertions.assertThat(responseBodyAsDTO).isEqualTo(expectedResponseDTO);
        }

        @Test
        @WithMockUser(authorities = Authorities.SERVICE_LOAD_WAHLVORSCHLAEGE)
        void wlsExceptionOnMissingWahlbezirkID() throws Exception {

            val request = MockMvcRequestBuilders.get("/vorschlaege/wahl/wahlID/ ");

            val response = api.perform(request).andExpect(status().isBadRequest()).andReturn();
            val wlsExceptionDTO = objectMapper.readValue(response.getResponse().getContentAsString(), WlsExceptionDTO.class);

            val expectedWlsException = new WlsExceptionDTO(WlsExceptionCategory.F, ExceptionConstants.LOADWAHLVORSCHLAEGE_BEZIRKID_FEHLT.code(),
                    serviceInfoOid,
                    ExceptionConstants.LOADWAHLVORSCHLAEGE_BEZIRKID_FEHLT.message());

            Assertions.assertThat(wlsExceptionDTO).isEqualTo(expectedWlsException);
        }

        @Test
        @WithMockUser(authorities = Authorities.SERVICE_LOAD_WAHLVORSCHLAEGE)
        void wlsExceptionOnMissingWahlID() throws Exception {

            val request = MockMvcRequestBuilders.get("/vorschlaege/wahl/ /wahlbezirkID");

            val response = api.perform(request).andExpect(status().isBadRequest()).andReturn();
            val wlsExceptionDTO = objectMapper.readValue(response.getResponse().getContentAsString(), WlsExceptionDTO.class);

            val expectedWlsException = new WlsExceptionDTO(WlsExceptionCategory.F, ExceptionConstants.LOADWAHLVORSCHLAEGE_WAHLID_FEHLT.code(),
                    serviceInfoOid,
                    ExceptionConstants.LOADWAHLVORSCHLAEGE_WAHLID_FEHLT.message());

            Assertions.assertThat(wlsExceptionDTO).isEqualTo(expectedWlsException);
        }
    }

    @Nested
    class LoadWahlvorschlaegeListe {
        @Test
        @WithMockUser(authorities = Authorities.SERVICE_LOAD_WAHLVORSCHLAEGELISTE)
        void noDataFound() throws Exception {
            val wahlID = "wahlID";
            val forDate = "2024-10-10";

            val request = MockMvcRequestBuilders.get("/vorschlaege/wahl/" + wahlID + "/liste?forDate=" + forDate);

            val response = api.perform(request).andExpect(status().isNotFound()).andReturn();

            Assertions.assertThat(response.getResponse().getContentAsString()).isEmpty();
        }

        @Test
        @WithMockUser(authorities = Authorities.SERVICE_LOAD_WAHLVORSCHLAEGELISTE)
        @Transactional
        void dataFound() throws Exception {
            val wahlID = "wahlID";
            val forDate = LocalDate.of(2024, 10, 10);

            val wahlvorschlag1 = new Wahlvorschlag(1, "wahlvorschlag1", true, Set.of(
                    new Kandidat("name1", 1, false, 1, false),
                    new Kandidat("name2", 2, true, 2, true)));
            val wahlvorschlag2 = new Wahlvorschlag(1, "wahlvorschlag2", true, Set.of(
                    new Kandidat("name3", 1, false, 1, false),
                    new Kandidat("name4", 2, true, 2, true)));
            val wahlvorschlaege1 = new Wahlvorschlaege("wahlbezirkID1", "wahlID1", "stimmzettelgebietID1", Set.of(wahlvorschlag1, wahlvorschlag2));

            val wahlvorschlag3 = new Wahlvorschlag(1, "wahlvorschlag1", true, Set.of(
                    new Kandidat("name5", 1, false, 1, false),
                    new Kandidat("name6", 2, true, 2, true)));
            val wahlvorschlag4 = new Wahlvorschlag(1, "wahlvorschlag2", true, Set.of(
                    new Kandidat("name7", 1, false, 1, false),
                    new Kandidat("name8", 2, true, 2, true)));
            val wahlvorschlaege2 = new Wahlvorschlaege("wahlbezirkID2", "wahlID1", "stimmzettelgebietID2", Set.of(wahlvorschlag3, wahlvorschlag4));

            val wahlvorschlaegeListe = new WahlvorschlaegeListe(forDate, wahlID, Set.of(wahlvorschlaege1, wahlvorschlaege2));

            val wahlvorschlaegeListeToLoad = wahlvorschlaegeListeRepository.save(wahlvorschlaegeListe);

            val request = MockMvcRequestBuilders.get("/vorschlaege/wahl/" + wahlID + "/liste?forDate=" + forDate);

            val response = api.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyAsDTO = objectMapper.readValue(response.getResponse().getContentAsString(), WahlvorschlaegeListeDTO.class);

            val expectedResponseDTO = wahlvorschlagMapper.toDTO(wahlvorschlaegeListeToLoad);

            Assertions.assertThat(responseBodyAsDTO).isEqualTo(expectedResponseDTO);
        }

        @Test
        @WithMockUser(authorities = Authorities.SERVICE_LOAD_WAHLVORSCHLAEGELISTE)
        void wlsExceptionOnMissingWahlID() throws Exception {
            val wahlID = " ";
            val forDate = "2024-10-10";

            val request = MockMvcRequestBuilders.get("/vorschlaege/wahl/" + wahlID + "/liste?forDate=" + forDate);

            val response = api.perform(request).andExpect(status().isBadRequest()).andReturn();
            val wlsExceptionDTO = objectMapper.readValue(response.getResponse().getContentAsString(), WlsExceptionDTO.class);

            val expectedWlsException = new WlsExceptionDTO(WlsExceptionCategory.F, ExceptionConstants.LOADWAHLVORSCHLAEGELISTE_WAHLID_FEHLT.code(),
                    serviceInfoOid,
                    ExceptionConstants.LOADWAHLVORSCHLAEGELISTE_WAHLID_FEHLT.message());

            Assertions.assertThat(wlsExceptionDTO).isEqualTo(expectedWlsException);

        }
    }

    @Nested
    class LoadReferendumvorlagen {
        @Test
        @WithMockUser(authorities = Authorities.SERVICE_LOAD_REFERENDUMVORLAGEN)
        void noDataFound() throws Exception {
            val request = MockMvcRequestBuilders.get("/vorschlaege/referendum/wahlID/wahlbezirkID");

            val response = api.perform(request).andExpect(status().isNotFound()).andReturn();

            Assertions.assertThat(response.getResponse().getContentAsString()).isEmpty();
        }

        @Test
        @WithMockUser(authorities = Authorities.SERVICE_LOAD_REFERENDUMVORLAGEN)
        @Transactional
        void dataFound() throws Exception {
            val referendumoption1 = new Referendumoption("Optionsname1", 1L);
            val referendumoption2 = new Referendumoption("Optionsname2", 2L);
            val referendumvorlage1 = new Referendumvorlage("wahlvorschlagID1", 1, "referendum1", "Warum ist die Banane krumm?",
                    Set.of(referendumoption1, referendumoption2));

            val referendumoption3 = new Referendumoption("Optionsname3", 3L);
            val referendumoption4 = new Referendumoption("Optionsname4", 4L);
            val referendumvorlage2 = new Referendumvorlage("wahlvorschlagID2", 1, "referendum1", "Ist die Erde eine Scheibe?",
                    Set.of(referendumoption3, referendumoption4));

            val referendumvorlagen = new Referendumvorlagen("wahlbezirkID", "wahlID", "stimmzettelgebietID", Set.of(referendumvorlage1, referendumvorlage2));
            val referendumvorlagenToLoad = referendumvorlagenRepository.save(referendumvorlagen);

            val request = MockMvcRequestBuilders.get(
                    "/vorschlaege/referendum/" + referendumvorlagenToLoad.getWahlID() + "/" + referendumvorlagenToLoad.getWahlbezirkID());

            val response = api.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyAsDTO = objectMapper.readValue(response.getResponse().getContentAsString(), ReferendumvorlagenDTO.class);

            val expectedResponseDTO = wahlvorschlagMapper.toDTO(referendumvorlagenToLoad);

            Assertions.assertThat(responseBodyAsDTO).isEqualTo(expectedResponseDTO);
        }

        @Test
        @WithMockUser(authorities = Authorities.SERVICE_LOAD_REFERENDUMVORLAGEN)
        void wlsExceptionOnMissingWahlID() throws Exception {
            val wahlID = " ";
            val wahlbezirkID = "wahlbezirkID";

            val request = MockMvcRequestBuilders.get("/vorschlaege/referendum/" + wahlID + "/" + wahlbezirkID);

            val response = api.perform(request).andExpect(status().isBadRequest()).andReturn();
            val wlsExceptionDTO = objectMapper.readValue(response.getResponse().getContentAsString(), WlsExceptionDTO.class);

            val expectedWlsException = new WlsExceptionDTO(WlsExceptionCategory.F, ExceptionConstants.LOADREFERENDUMVORLAGEN_WAHLID_FEHLT.code(),
                    serviceInfoOid,
                    ExceptionConstants.LOADREFERENDUMVORLAGEN_WAHLID_FEHLT.message());

            Assertions.assertThat(wlsExceptionDTO).isEqualTo(expectedWlsException);

        }

        @Test
        @WithMockUser(authorities = Authorities.SERVICE_LOAD_REFERENDUMVORLAGEN)
        void wlsExceptionOnMissingWahlbezirk() throws Exception {
            val wahlID = "wahlID";
            val wahlbezirkID = " ";

            val request = MockMvcRequestBuilders.get("/vorschlaege/referendum/" + wahlID + "/" + wahlbezirkID);

            val response = api.perform(request).andExpect(status().isBadRequest()).andReturn();
            val wlsExceptionDTO = objectMapper.readValue(response.getResponse().getContentAsString(), WlsExceptionDTO.class);

            val expectedWlsException = new WlsExceptionDTO(WlsExceptionCategory.F, ExceptionConstants.LOADREFERENDUMVORLAGEN_WAHLBEZIRKID_FEHLT.code(),
                    serviceInfoOid,
                    ExceptionConstants.LOADREFERENDUMVORLAGEN_WAHLBEZIRKID_FEHLT.message());

            Assertions.assertThat(wlsExceptionDTO).isEqualTo(expectedWlsException);

        }
    }
}
