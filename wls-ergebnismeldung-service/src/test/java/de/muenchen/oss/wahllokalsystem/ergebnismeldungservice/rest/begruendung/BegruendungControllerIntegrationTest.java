package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.begruendung;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.begruendung.Begruendung;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.begruendung.BegruendungRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.begruendung.BezirkUndWahlIDStapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.begruendung.Stapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = MicroServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, "dummy.nobezirkid.check" })
public class BegruendungControllerIntegrationTest {

    @Autowired
    MockMvc api;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    BegruendungRepository begruendungRepository;

    @Nested
    class GetBegruendung {

        @AfterEach
        void tearDown() {
            SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_BEGRUENDUNG);
            begruendungRepository.deleteAll();
        }

        @Test
        @WithMockUser(authorities = { Authorities.SERVICE_GET_BEGRUENDUNG, Authorities.REPOSITORY_READ_BEGRUENDUNG })
        void should_returnNoContent_when_noDataIsPresent() throws Exception {
            val request = get("/businessActions/begruendung/wahlbezirkID/21/LTW_BZW_A");

            val response = api.perform(request).andExpect(status().isNoContent()).andReturn();

            Assertions.assertThat(response.getResponse().getContentAsString()).isEmpty();
        }

        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_GET_BEGRUENDUNG, Authorities.REPOSITORY_READ_BEGRUENDUNG,
                        Authorities.REPOSITORY_WRITE_BEGRUENDUNG }
        )
        void should_returnData_when_dataIsPresentInRepository() throws Exception {
            val wahlbezirkID1 = "wahlbezirkID1";
            val wahlbezirkID2 = "wahlbezirkID2";

            val wahlID1 = "wahlID1";
            val wahlID2 = "wahlID2";

            val stapelart1 = Stapelart.LTW_BZW_A;
            val stapelart2 = Stapelart.LTW_BZW_B;

            val begruendung1 = new Begruendung();
            begruendung1.setBezirkUndWahlIDStapelart(new BezirkUndWahlIDStapelart(wahlbezirkID1, wahlID1, stapelart1));
            begruendung1.setGrund1("grund1");
            begruendung1.setGrund2("grund2");
            begruendung1.setUnstimmigkeiten(true);
            begruendung1.setNachzaehlung(true);
            begruendungRepository.save(begruendung1);

            val begruendung2 = new Begruendung();
            begruendung2.setBezirkUndWahlIDStapelart(new BezirkUndWahlIDStapelart(wahlbezirkID1, wahlID2, stapelart2));
            begruendung2.setGrund1("grund1");
            begruendungRepository.save(begruendung2);

            val begruendung3 = new Begruendung();
            begruendung3.setBezirkUndWahlIDStapelart(new BezirkUndWahlIDStapelart(wahlbezirkID2, wahlID1, stapelart2));
            begruendung3.setGrund1("grund1");
            begruendungRepository.save(begruendung3);

            val expectedIDOfResponse = new BezirkUndWahlIDStapelart(wahlbezirkID1, wahlID1, stapelart1);
            val expectedResponse = new BegruendungDTO(expectedIDOfResponse, "grund1", "grund2", true, true);

            val request = get("/businessActions/begruendung/" + wahlbezirkID1 + "/" + wahlID1 + "/" + stapelart1);
            val response = api.perform(request).andExpect(status().isOk()).andReturn();

            val responseBody = objectMapper.readValue(response.getResponse().getContentAsString(), BegruendungDTO.class);
            Assertions.assertThat(responseBody).usingRecursiveComparison().isEqualTo(expectedResponse);
        }
    }

    @Nested
    class PostBegruendung {

        @AfterEach
        void tearDown() {
            SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_BEGRUENDUNG);
            begruendungRepository.deleteAll();
        }

        @Test
        @WithMockUser(authorities = { Authorities.SERVICE_SET_BEGRUENDUNG, Authorities.REPOSITORY_WRITE_BEGRUENDUNG })
        void should_returnBadRequestWlsException_when_validationFailed() throws Exception {
            val requestBody = BegruendungDTO.builder().build();
            val request = post("/businessActions/begruendung/wahlbezirkID/0/LTW_BZW_A").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody));

            val expecetedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F, "103", "WLS-ERGEBNISMELDUNG", null);

            val result = api.perform(request).andExpect(status().isBadRequest()).andReturn();
            val resultBodyAsWlsExceptionDTO = objectMapper.readValue(result.getResponse().getContentAsString(), WlsExceptionDTO.class);

            Assertions.assertThat(resultBodyAsWlsExceptionDTO).usingRecursiveComparison().ignoringFields("message").isEqualTo(expecetedWlsExceptionDTO);
            Assertions.assertThat(resultBodyAsWlsExceptionDTO.message()).isNotNull();
        }

        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_SET_BEGRUENDUNG, Authorities.REPOSITORY_READ_BEGRUENDUNG,
                        Authorities.REPOSITORY_WRITE_BEGRUENDUNG }
        )
        void should_persistData_when_noDataIsPresentInRepository() throws Exception {
            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "wahlID";
            val stapelart = Stapelart.LTW_BZW_A;
            val bezirkUndWahlIDStapelart = new BezirkUndWahlIDStapelart(wahlbezirkID, wahlID, stapelart);
            val grund1 = "grund1";
            val grund2 = "grund2";

            val requestBody = new BegruendungDTO(bezirkUndWahlIDStapelart, grund1, grund2, true, true);
            val request = post("/businessActions/begruendung/" + wahlbezirkID + "/" + wahlID + "/" + stapelart).with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody));

            val expectedRepoResponse = new Begruendung();
            expectedRepoResponse.setBezirkUndWahlIDStapelart(new BezirkUndWahlIDStapelart(wahlbezirkID, wahlID, stapelart));
            expectedRepoResponse.setGrund1(grund1);
            expectedRepoResponse.setGrund2(grund2);
            expectedRepoResponse.setUnstimmigkeiten(true);
            expectedRepoResponse.setNachzaehlung(true);

            api.perform(request).andExpect(status().isOk());

            SecurityUtils.runWith(Authorities.REPOSITORY_READ_BEGRUENDUNG);
            val repoResponse = begruendungRepository.findById(new BezirkUndWahlIDStapelart(wahlbezirkID, wahlID, stapelart))
                    .orElseThrow();

            Assertions.assertThat(repoResponse).usingRecursiveComparison().isEqualTo(expectedRepoResponse);
        }
    }
}
