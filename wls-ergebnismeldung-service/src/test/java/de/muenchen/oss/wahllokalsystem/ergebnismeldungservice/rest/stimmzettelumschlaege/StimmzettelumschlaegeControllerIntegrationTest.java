package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelumschlaege;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_TEST_PROFILE;
import static de.muenchen.oss.wahllokalsystem.wls.common.security.Profiles.NO_BEZIRKS_ID_CHECK;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelumschlaege.Stimmzettelumschlaege;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelumschlaege.StimmzettelumschlaegeRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelumschlaege.StimmzettelumschlaegeModelMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.LocalDateTimeComparators;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.WithMockUserAsJwt;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.time.LocalDateTime;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(classes = MicroServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles(
        profiles = { SPRING_TEST_PROFILE, NO_BEZIRKS_ID_CHECK }
)
public class StimmzettelumschlaegeControllerIntegrationTest {

    @Autowired
    StimmzettelumschlaegeRepository stimmzettelumschlaegeRepository;

    @Autowired
    StimmzettelumschlaegeModelMapper stimmzettelumschlaegeModelMapper;

    @Autowired
    StimmzettelumschlaegeDTOMapper stimmzettelumschlaegeDTOMapper;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @AfterEach
    void tearDown() {
        SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_STIMMZETTELUMSCHLAEGE);
        stimmzettelumschlaegeRepository.deleteAll();
    }

    @Nested
    class GetStimmzettelumschlaege {

        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_GET_STIMMZETTELUMSCHLAEGE,
                        Authorities.REPOSITORY_WRITE_STIMMZETTELUMSCHLAEGE,
                        Authorities.REPOSITORY_READ_STIMMZETTELUMSCHLAEGE }
        )
        void should_returnData_when_dataIsPresentInRepository() throws Exception {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val urneneroeffnungsUhrzeit = LocalDateTime.now();
            val anzahlWaehler = 47;
            val anzahlWaehler2 = 11L;
            val request = MockMvcRequestBuilders.get(buildStimmzettelumschlaegeURI(wahlID, wahlbezirkID));

            val entityToFind = new Stimmzettelumschlaege(new BezirkUndWahlID(wahlID, wahlbezirkID), urneneroeffnungsUhrzeit, anzahlWaehler, anzahlWaehler2);
            stimmzettelumschlaegeRepository.save(entityToFind);

            val response = mockMvc.perform(request).andExpect(status().isOk()).andReturn().getResponse();
            val responseBodyAsDTO = objectMapper.readValue(response.getContentAsString(), StimmzettelumschlaegeDTO.class);

            val expectedResult = stimmzettelumschlaegeDTOMapper.toDTO(stimmzettelumschlaegeModelMapper.toModel(entityToFind));

            Assertions.assertThat(responseBodyAsDTO)
                    .usingRecursiveComparison()
                    .withComparatorForType(LocalDateTimeComparators.PRECISION_MILLISECONDS, LocalDateTime.class)
                    .isEqualTo(expectedResult);
        }

        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_GET_STIMMZETTELUMSCHLAEGE,
                        Authorities.REPOSITORY_READ_STIMMZETTELUMSCHLAEGE }
        )
        void should_returnNoContent_when_dataIsNotPresentInRepository() throws Exception {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val request = MockMvcRequestBuilders.get(buildStimmzettelumschlaegeURI(wahlID, wahlbezirkID));

            val response = mockMvc.perform(request).andExpect(status().isNoContent()).andReturn().getResponse();

            Assertions.assertThat(response.getContentAsString()).isEmpty();
        }

        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_GET_STIMMZETTELUMSCHLAEGE }
        )
        void should_returnBadRequestWlsException_when_validationFailed() throws Exception {
            val wahlID = "    ";
            val wahlbezirkID = "wahlbezirkID";
            val request = MockMvcRequestBuilders.get(buildStimmzettelumschlaegeURI(wahlID, wahlbezirkID));

            val response = mockMvc.perform(request).andExpect(status().isBadRequest()).andReturn().getResponse();
            val receivedWlsException = objectMapper.readValue(response.getContentAsString(), WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F,
                    ExceptionConstants.GET_STIMMZETTELUMSCHLAEGE_PARAMETER_UNVOLLSTAENDIG.code(),
                    "WLS-ERGEBNISMELDUNG", ExceptionConstants.GET_STIMMZETTELUMSCHLAEGE_PARAMETER_UNVOLLSTAENDIG.message());
            Assertions.assertThat(receivedWlsException).isEqualTo(expectedWlsExceptionDTO);
        }
    }

    @Nested
    class PostStimmzettelumschlaege {

        @Test
        @WithMockUserAsJwt(
                authorities = { Authorities.SERVICE_SET_STIMMZETTELUMSCHLAEGE, Authorities.REPOSITORY_WRITE_STIMMZETTELUMSCHLAEGE },
                claimProperties = { "wahlbezirksArt=BWB" }
        )
        void should_persistData_when_noDataIsPresentInRepository() throws Exception {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val urneneroeffnungsUhrzeit = LocalDateTime.now();
            val anzahlWaehler = 47;
            val anzahlWaehler2 = 11L;
            val requestBody = new StimmzettelumschlaegeDTO(new BezirkUndWahlID(wahlID, wahlbezirkID), urneneroeffnungsUhrzeit, anzahlWaehler, anzahlWaehler2);

            val request = MockMvcRequestBuilders.post(buildStimmzettelumschlaegeURI(wahlID, wahlbezirkID)).with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody));

            mockMvc.perform(request).andExpect(status().isOk()).andReturn().getResponse();

            SecurityUtils.runWith(Authorities.REPOSITORY_READ_STIMMZETTELUMSCHLAEGE);
            val entityFromRepo = stimmzettelumschlaegeRepository.findById(requestBody.bezirkUndWahlID()).get();

            val expectedEntity = stimmzettelumschlaegeModelMapper.toEntity(stimmzettelumschlaegeDTOMapper.toModel(requestBody));
            Assertions.assertThat(entityFromRepo)
                    .usingRecursiveComparison()
                    .withComparatorForType(LocalDateTimeComparators.PRECISION_MILLISECONDS, LocalDateTime.class)
                    .isEqualTo(expectedEntity);
        }

        @Test
        @WithMockUserAsJwt(
                authorities = { Authorities.SERVICE_SET_STIMMZETTELUMSCHLAEGE }
        )
        void should_returnBadRequestWlsException_when_validationFailed() throws Exception {
            val wahlID = "    ";
            val wahlbezirkID = "wahlbezirkID";
            val urneneroeffnungsUhrzeit = LocalDateTime.now();
            val anzahlWaehler = 47;
            val anzahlWaehler2 = 11L;
            val requestBody = new StimmzettelumschlaegeDTO(new BezirkUndWahlID(wahlID, wahlbezirkID), urneneroeffnungsUhrzeit, anzahlWaehler, anzahlWaehler2);

            val request = MockMvcRequestBuilders.post(buildStimmzettelumschlaegeURI(wahlID, wahlbezirkID)).with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody));

            val response = mockMvc.perform(request).andExpect(status().isBadRequest()).andReturn().getResponse();
            val receivedWlsException = objectMapper.readValue(response.getContentAsString(), WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F,
                    ExceptionConstants.POST_STIMMZETTELUMSCHLAEGE_PARAMETER_UNVOLLSTAENDIG.code(),
                    "WLS-ERGEBNISMELDUNG", ExceptionConstants.POST_STIMMZETTELUMSCHLAEGE_PARAMETER_UNVOLLSTAENDIG.message());
            Assertions.assertThat(receivedWlsException).isEqualTo(expectedWlsExceptionDTO);
        }
    }

    @Nested
    class PostStimmzettelumschlaegeWithExistingSetupData {

        private Stimmzettelumschlaege entityFromRepo;

        @BeforeEach()
        void setup() {
            val entityToReplace = new Stimmzettelumschlaege(
                    new BezirkUndWahlID("wahlID", "wahlbezirkID"),
                    LocalDateTime.now(), 47, 11L);
            entityFromRepo = stimmzettelumschlaegeRepository.save(entityToReplace);
        }

        @Test
        @WithMockUserAsJwt(
                authorities = { Authorities.SERVICE_SET_STIMMZETTELUMSCHLAEGE, Authorities.REPOSITORY_WRITE_STIMMZETTELUMSCHLAEGE },
                claimProperties = { "wahlbezirksArt=BWB" }
        )
        void should_replaceOldData_when_dataIsPresentInRepository() throws Exception {
            val urneneroeffnungsUhrzeit = LocalDateTime.now();
            val anzahlWaehlerToReplace = 8;
            val anzahlWaehler2ToReplace = 15L;
            val requestBody = new StimmzettelumschlaegeDTO(new BezirkUndWahlID("wahlID", "wahlbezirkID"),
                    urneneroeffnungsUhrzeit, anzahlWaehlerToReplace, anzahlWaehler2ToReplace);

            val request = MockMvcRequestBuilders.post(buildStimmzettelumschlaegeURI("wahlID", "wahlbezirkID")).with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody));

            Assertions.assertThat(entityFromRepo).usingRecursiveComparison().isNotEqualTo(requestBody);

            mockMvc.perform(request).andExpect(status().isOk()).andReturn().getResponse();

            SecurityUtils.runWith(Authorities.REPOSITORY_READ_STIMMZETTELUMSCHLAEGE);
            val replacedEntityFromRepo = stimmzettelumschlaegeRepository.findById(requestBody.bezirkUndWahlID()).get();

            val expectedEntity = stimmzettelumschlaegeModelMapper.toEntity(stimmzettelumschlaegeDTOMapper.toModel(requestBody));
            Assertions.assertThat(replacedEntityFromRepo)
                    .usingRecursiveComparison()
                    .withComparatorForType(LocalDateTimeComparators.PRECISION_MILLISECONDS, LocalDateTime.class)
                    .isEqualTo(expectedEntity);
        }

    }

    private String buildStimmzettelumschlaegeURI(final String wahlID, final String wahlbezirkID) {
        return "/businessActions/stimmzettelumschlaege/" + wahlID + "/" + wahlbezirkID;
    }
}
