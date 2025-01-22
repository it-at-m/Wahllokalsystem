package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ergebnisse;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.BezirkUndWahlIDStapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.Stapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse.Ergebnis;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse.Ergebnisse;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse.ErgebnisseRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisseModelMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.util.ArrayList;
import java.util.Collections;
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

@SpringBootTest(classes = MicroServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureWireMock
@ActiveProfiles(
        profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE,
                de.muenchen.oss.wahllokalsystem.wls.common.security.Profiles.NO_BEZIRKS_ID_CHECK }
)
public class ErgebnisseControllerIntegrationTest {

    @Autowired
    MockMvc api;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ErgebnisseModelMapper ergebnisseModelMapper;

    @Autowired
    ErgebnisseDTOMapper ergebnisseDTOMapper;

    @Autowired
    ErgebnisseRepository ergebnisseRepository;

    @Autowired
    MockMvc mockMvc;

    @AfterEach
    void tearDown() {
        SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_ERGEBNISSE);
        ergebnisseRepository.deleteAll();
    }

    @Nested
    class GetErgebnisse {

        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_GET_ERGEBNISSE, Authorities.REPOSITORY_READ_ERGEBNISSE,
                        Authorities.REPOSITORY_WRITE_ERGEBNISSE }
        )
        void should_returnData_when_dataIsPresentInRepository() throws Exception {
            val wahlbezirkID1 = "wahlbezirkID1";
            val wahlbezirkID2 = "wahlbezirkID2";

            val wahlID1 = "wahlID1";
            val wahlID2 = "wahlID2";

            val stapelart1 = Stapelart.LTW_BZW_A;
            val stapelart2 = Stapelart.LTW_BZW_B;

            val stapelartDTO = StapelartDTO.LTW_BZW_A;

            val ergebnisDTO1 = new ErgebnisDTO(null, null, null, 1, null);
            val newErgebnisDTOList1 = new ArrayList<ErgebnisDTO>();
            newErgebnisDTOList1.add(ergebnisDTO1);

            val ergebnis1 = new Ergebnis(null, null, null, 1, null);
            val newErgebnisList1 = new ArrayList<Ergebnis>();
            newErgebnisList1.add(ergebnis1);

            val ergebnis2 = new Ergebnis("2", "2", 2L, 2, 2L);
            val newErgebnisList2 = new ArrayList<Ergebnis>();
            newErgebnisList2.add(ergebnis2);

            val ergebnis3 = new Ergebnis(null, null, null, 3, null);
            val newErgebnisList3 = new ArrayList<Ergebnis>();
            newErgebnisList3.add(ergebnis3);

            val ergebnisse1 = new Ergebnisse();
            ergebnisse1.setBezirkUndWahlIDStapelart(new BezirkUndWahlIDStapelart(wahlbezirkID1, wahlID1, stapelart1));
            ergebnisse1.setErgebnisse(newErgebnisList1);
            ergebnisseRepository.save(ergebnisse1);

            val ergebnisse2 = new Ergebnisse();
            ergebnisse2.setBezirkUndWahlIDStapelart(new BezirkUndWahlIDStapelart(wahlbezirkID1, wahlID2, stapelart2));
            ergebnisse2.setErgebnisse(newErgebnisList2);
            ergebnisseRepository.save(ergebnisse2);

            val ergebnisse3 = new Ergebnisse();
            ergebnisse3.setBezirkUndWahlIDStapelart(new BezirkUndWahlIDStapelart(wahlbezirkID2, wahlID1, stapelart2));
            ergebnisse3.setErgebnisse(newErgebnisList3);
            ergebnisseRepository.save(ergebnisse3);

            val expectedIDOfResponse = new BezirkUndWahlIDStapelartDTO(wahlbezirkID1, wahlID1, stapelartDTO);
            val expectedResponse = new ErgebnisseDTO(expectedIDOfResponse, newErgebnisDTOList1);

            val request = get("/businessActions/ergebnisse/" + wahlbezirkID1 + "/" + wahlID1 + "/" + stapelart1);
            val response = api.perform(request).andExpect(status().isOk()).andReturn();

            val responseBody = objectMapper.readValue(response.getResponse().getContentAsString(), ErgebnisseDTO.class);
            Assertions.assertThat(responseBody).usingRecursiveComparison().isEqualTo(expectedResponse);
        }

        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_SET_ERGEBNISSE }
        )
        void should_returnBadRequestWlsException_when_validationFailed() throws Exception {
            val wahlbezirkID1 = "wahlbezirkID1";
            val wahlID1 = "wahlID1";
            val stapelart1 = Stapelart.LTW_BZW_A;
            val stapelartDTO = StapelartDTO.LTW_BZW_A;

            val requestBody = new ErgebnisseDTO(new BezirkUndWahlIDStapelartDTO(wahlbezirkID1, wahlID1, stapelartDTO), Collections.emptyList());

            val request = MockMvcRequestBuilders.post("/businessActions/ergebnisse/" + wahlbezirkID1 + "/" + wahlID1 + "/" + stapelart1).with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody));

            val response = mockMvc.perform(request).andExpect(status().isBadRequest()).andReturn().getResponse();
            val receivedWlsException = objectMapper.readValue(response.getContentAsString(), WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F,
                    ExceptionConstants.POST_ERGEBNISSE_PARAMETER_UNVOLLSTAENDIG.code(),
                    "WLS-ERGEBNISMELDUNG", ExceptionConstants.POST_ERGEBNISSE_PARAMETER_UNVOLLSTAENDIG.message());
            Assertions.assertThat(receivedWlsException).isEqualTo(expectedWlsExceptionDTO);
        }
    }

    @Nested
    class GetAllErgebnisse {

        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_GET_ERGEBNISSE, Authorities.REPOSITORY_READ_ERGEBNISSE,
                        Authorities.REPOSITORY_WRITE_ERGEBNISSE }
        )
        void should_returnData_when_dataIsPresentInRepository() throws Exception {
            val wahlbezirkID1 = "wahlbezirkID1";
            val wahlbezirkID2 = "wahlbezirkID2";

            val wahlID1 = "wahlID1";
            val wahlID2 = "wahlID2";

            val stapelart1 = Stapelart.LTW_BZW_A;
            val stapelart2 = Stapelart.LTW_BZW_B;

            val stapelartDTO = StapelartDTO.LTW_BZW_A;

            val ergebnisDTO1 = new ErgebnisDTO(null, null, null, 1, null);
            val newErgebnisDTOList1 = new ArrayList<ErgebnisDTO>();
            newErgebnisDTOList1.add(ergebnisDTO1);

            val ergebnis1 = new Ergebnis(null, null, null, 1, null);
            val newErgebnisList1 = new ArrayList<Ergebnis>();
            newErgebnisList1.add(ergebnis1);

            val ergebnis2 = new Ergebnis("2", "2", 2L, 2, 2L);
            val newErgebnisList2 = new ArrayList<Ergebnis>();
            newErgebnisList2.add(ergebnis2);

            val ergebnis3 = new Ergebnis(null, null, null, 3, null);
            val newErgebnisList3 = new ArrayList<Ergebnis>();
            newErgebnisList3.add(ergebnis3);

            val ergebnisse1 = new Ergebnisse();
            ergebnisse1.setBezirkUndWahlIDStapelart(new BezirkUndWahlIDStapelart(wahlbezirkID1, wahlID1, stapelart1));
            ergebnisse1.setErgebnisse(newErgebnisList1);
            ergebnisseRepository.save(ergebnisse1);

            val ergebnisse2 = new Ergebnisse();
            ergebnisse2.setBezirkUndWahlIDStapelart(new BezirkUndWahlIDStapelart(wahlbezirkID1, wahlID2, stapelart2));
            ergebnisse2.setErgebnisse(newErgebnisList2);
            ergebnisseRepository.save(ergebnisse2);

            val ergebnisse3 = new Ergebnisse();
            ergebnisse3.setBezirkUndWahlIDStapelart(new BezirkUndWahlIDStapelart(wahlbezirkID2, wahlID1, stapelart2));
            ergebnisse3.setErgebnisse(newErgebnisList3);
            ergebnisseRepository.save(ergebnisse3);

            val expectedIDOfResponse = new BezirkUndWahlIDStapelartDTO(wahlbezirkID1, wahlID1, stapelartDTO);
            val expectedResponse = new ErgebnisseDTO(expectedIDOfResponse, newErgebnisDTOList1);

            val request = get("/businessActions/ergebnisse/" + wahlbezirkID1 + "/" + wahlID1);
            val response = api.perform(request).andExpect(status().isOk()).andReturn();

            val responseBody = objectMapper.readValue(response.getResponse().getContentAsString(), ErgebnisseDTO[].class);
            Assertions.assertThat(responseBody).usingRecursiveComparison().isEqualTo(expectedResponse);
        }

        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_SET_ERGEBNISSE }
        )
        void should_returnNoContent_when_ListIsEmpty() throws Exception {
            val wahlbezirkID1 = "wahlbezirkID1";
            val wahlID1 = "wahlID1";
            val stapelartDTO = StapelartDTO.LTW_BZW_A;

            val requestBody = new ErgebnisseDTO(new BezirkUndWahlIDStapelartDTO(wahlbezirkID1, wahlID1, stapelartDTO), Collections.emptyList());

            val request = MockMvcRequestBuilders.get("/businessActions/ergebnisse/" + wahlbezirkID1 + "/" + wahlID1).with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody));

            mockMvc.perform(request).andExpect(status().isNoContent());
        }
    }

    @Nested
    class PostErgebnisse {

        @Test
        @WithMockUser(authorities = { Authorities.SERVICE_SET_ERGEBNISSE, Authorities.REPOSITORY_WRITE_ERGEBNISSE })
        void should_returnBadRequestWlsException_when_validationFailed() throws Exception {
            val requestBody = ErgebnisseDTO.builder().build();
            val request = post("/businessActions/ergebnisse/wahlbezirkID/0/LTW_BZW_A").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody));

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F, ExceptionConstants.POST_ERGEBNISSE_PARAMETER_UNVOLLSTAENDIG.code(),
                    "WLS-ERGEBNISMELDUNG", ExceptionConstants.POST_STATUS_PARAMETER_UNVOLLSTAENDIG.message());

            val result = api.perform(request).andExpect(status().isBadRequest()).andReturn();
            val resultBodyAsWlsExceptionDTO = objectMapper.readValue(result.getResponse().getContentAsString(), WlsExceptionDTO.class);

            Assertions.assertThat(resultBodyAsWlsExceptionDTO).usingRecursiveComparison().ignoringFields("message").isEqualTo(expectedWlsExceptionDTO);
            Assertions.assertThat(resultBodyAsWlsExceptionDTO.message()).isNotNull();
        }

        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_SET_ERGEBNISSE, Authorities.REPOSITORY_READ_ERGEBNISSE,
                        Authorities.REPOSITORY_WRITE_ERGEBNISSE }
        )
        void should_persistData_when_noDataIsPresentInRepository() throws Exception {
            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "wahlID";
            val stapelart = Stapelart.LTW_BZW_A;
            val stapelartDTO = StapelartDTO.LTW_BZW_A;
            val bezirkUndWahlIDStapelartDTO = new BezirkUndWahlIDStapelartDTO(wahlbezirkID, wahlID, stapelartDTO);
            val ergebnis1 = new Ergebnis(null, null, null, 1, null);
            val newErgebnisList = new ArrayList<Ergebnis>();
            newErgebnisList.add(ergebnis1);

            val ergebnisDTO = new ErgebnisDTO(null, null, null, 1, null);
            val newErgebnisDTOList = new ArrayList<ErgebnisDTO>();
            newErgebnisDTOList.add(ergebnisDTO);

            val requestBody = new ErgebnisseDTO(bezirkUndWahlIDStapelartDTO, newErgebnisDTOList);
            val request = post("/businessActions/ergebnisse/" + wahlbezirkID + "/" + wahlID + "/" + stapelart).with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody));

            val expectedRepoResponse = new Ergebnisse();
            expectedRepoResponse.setBezirkUndWahlIDStapelart(new BezirkUndWahlIDStapelart(wahlbezirkID, wahlID, stapelart));
            expectedRepoResponse.setErgebnisse(newErgebnisList);

            api.perform(request).andExpect(status().isOk());

            SecurityUtils.runWith(Authorities.REPOSITORY_READ_ERGEBNISSE);
            val repoResponse = ergebnisseRepository.findById(new BezirkUndWahlIDStapelart(wahlbezirkID, wahlID, stapelart))
                    .orElseThrow();

            Assertions.assertThat(repoResponse).usingRecursiveComparison().isEqualTo(expectedRepoResponse);
        }

        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_SET_ERGEBNISSE, Authorities.REPOSITORY_READ_ERGEBNISSE,
                        Authorities.REPOSITORY_WRITE_ERGEBNISSE }
        )
        void should_replaceOldData_when_dataIsPresentInRepository() throws Exception {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val stapelart = Stapelart.LTW_BZW_A;
            val bezirkUndWahlIDStapelart = new BezirkUndWahlIDStapelart(wahlbezirkID, wahlID, stapelart);

            val ergebnisDTO1 = new ErgebnisDTO("wahlvorschlagID1", "kandidatID1", 1L, 1, 1L);
            val ergebnisDTO2 = new ErgebnisDTO("wahlvorschlagID2", "kandidatID2", 2L, 1, 2L);
            val newErgebnisDTOList = new ArrayList<ErgebnisDTO>();
            newErgebnisDTOList.add(ergebnisDTO1);
            newErgebnisDTOList.add(ergebnisDTO2);

            val requestBody = new ErgebnisseDTO(new BezirkUndWahlIDStapelartDTO(wahlbezirkID, wahlID, StapelartDTO.LTW_BZW_A), newErgebnisDTOList);

            val request = MockMvcRequestBuilders.post("/businessActions/ergebnisse/" + wahlID + "/" + wahlbezirkID + "/" + Stapelart.LTW_BZW_A).with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody));

            val ergebnis2 = new Ergebnis(null, null, null, 2, null);
            val newErgebnisList2 = new ArrayList<Ergebnis>();
            newErgebnisList2.add(ergebnis2);

            val entityToReplace = new Ergebnisse(bezirkUndWahlIDStapelart, newErgebnisList2);
            Assertions.assertThat(entityToReplace).usingRecursiveComparison().isNotEqualTo(requestBody);
            ergebnisseRepository.save(entityToReplace);

            WireMock.stubFor(WireMock.post(UrlPattern.ANY).willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())));

            mockMvc.perform(request).andExpect(status().isOk()).andReturn().getResponse();

            val entityFromRepo = ergebnisseRepository.findById(bezirkUndWahlIDStapelart).get();
            val expectedEntity = ergebnisseModelMapper.toEntity(ergebnisseDTOMapper.toModel(requestBody));
            Assertions.assertThat(entityFromRepo)
                    .usingRecursiveComparison()
                    .isEqualTo(expectedEntity);
        }
    }
}
