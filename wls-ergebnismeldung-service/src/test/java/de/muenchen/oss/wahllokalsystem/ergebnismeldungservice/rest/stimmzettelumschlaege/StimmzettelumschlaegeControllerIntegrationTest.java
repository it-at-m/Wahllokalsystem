package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelumschlaege;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelumschlaege.Stimmzettelumschlaege;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelumschlaege.StimmzettelumschlaegeRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelumschlaege.StimmzettelumschlaegeModelMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.TimePrecisionComparators;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(classes = MicroServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = {SPRING_TEST_PROFILE})
public class StimmzettelumschlaegeControllerIntegrationTest {

  @Autowired StimmzettelumschlaegeRepository stimmzettelumschlaegeRepository;

  @Autowired StimmzettelumschlaegeModelMapper stimmzettelumschlaegeModelMapper;

  @Autowired StimmzettelumschlaegeDTOMapper stimmzettelumschlaegeDTOMapper;

  @Autowired ObjectMapper objectMapper;

  @Autowired MockMvc mockMvc;

  @AfterEach
  void teardown() {
    SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_STIMMZETTELUMSCHLAEGE);
    stimmzettelumschlaegeRepository.deleteAll();
  }

  @Nested
  class GetStimmzettelumschlaege {

    @Test
    void should_returnData_when_dataIsPresentInRepository() throws Exception {
      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";
      val urneneroeffnungsUhrzeit = LocalDateTime.now();
      val anzahlWaehler = 47;
      val anzahlWaehler2 = 11L;

      SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_STIMMZETTELUMSCHLAEGE);
      val entityToFind =
          new Stimmzettelumschlaege(
              new BezirkUndWahlID(wahlID, wahlbezirkID),
              urneneroeffnungsUhrzeit,
              anzahlWaehler,
              anzahlWaehler2);
      stimmzettelumschlaegeRepository.save(entityToFind);

      val response =
          mockMvc
              .perform(createGetRequest(wahlID, wahlbezirkID, wahlbezirkID))
              .andExpect(status().isOk())
              .andReturn()
              .getResponse();
      val responseBodyAsDTO =
          objectMapper.readValue(response.getContentAsString(), StimmzettelumschlaegeDTO.class);

      val expectedResult =
          stimmzettelumschlaegeDTOMapper.toDTO(
              stimmzettelumschlaegeModelMapper.toModel(entityToFind));

      Assertions.assertThat(responseBodyAsDTO)
          .usingRecursiveComparison()
          .withComparatorForType(
              TimePrecisionComparators.LOCAL_DATE_TIME_PRECISION_MILLISECONDS, LocalDateTime.class)
          .isEqualTo(expectedResult);
    }

    @Test
    void should_returnNoContent_when_dataIsNotPresentInRepository() throws Exception {
      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";

      val response =
          mockMvc
              .perform(createGetRequest(wahlID, wahlbezirkID, wahlbezirkID))
              .andExpect(status().isNoContent())
              .andReturn()
              .getResponse();

      Assertions.assertThat(response.getContentAsString()).isEmpty();
    }

    @Test
    void should_returnBadRequestWlsException_when_validationFailed() throws Exception {
      val wahlID = "    ";
      val wahlbezirkID = "wahlbezirkID";

      val response =
          mockMvc
              .perform(createGetRequest(wahlID, wahlbezirkID, wahlbezirkID))
              .andExpect(status().isBadRequest())
              .andReturn()
              .getResponse();
      val receivedWlsException =
          objectMapper.readValue(response.getContentAsString(), WlsExceptionDTO.class);

      val expectedWlsExceptionDTO =
          new WlsExceptionDTO(
              WlsExceptionCategory.F,
              ExceptionConstants.GET_STIMMZETTELUMSCHLAEGE_PARAMETER_UNVOLLSTAENDIG.code(),
              "WLS-ERGEBNISMELDUNG",
              ExceptionConstants.GET_STIMMZETTELUMSCHLAEGE_PARAMETER_UNVOLLSTAENDIG.message());
      Assertions.assertThat(receivedWlsException).isEqualTo(expectedWlsExceptionDTO);
    }

    @Test
    void should_returnForbidden_when_userHasWrongBezirkId() throws Exception {
      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";

      mockMvc
          .perform(createGetRequest(wahlID, wahlbezirkID, wahlbezirkID + "sth"))
          .andExpect(status().isForbidden());
    }

    private MockHttpServletRequestBuilder createGetRequest(
        final String wahlID, final String wahlbezirkID, final String claimWahlbezirkID) {
      return MockMvcRequestBuilders.get(
              "/businessActions/stimmzettelumschlaege/" + wahlID + "/" + wahlbezirkID)
          .with(
              jwt()
                  .authorities(
                      new SimpleGrantedAuthority(Authorities.SERVICE_GET_STIMMZETTELUMSCHLAEGE),
                      new SimpleGrantedAuthority(Authorities.REPOSITORY_READ_STIMMZETTELUMSCHLAEGE))
                  .jwt(jwt -> jwt.claim("wahlbezirkID", claimWahlbezirkID)));
    }
  }

  @Nested
  class PostStimmzettelumschlaege {

    @Test
    void should_persistData_when_noDataIsPresentInRepository() throws Exception {
      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";
      val urneneroeffnungsUhrzeit = LocalDateTime.now();
      val anzahlWaehler = 47;
      val anzahlWaehler2 = 11L;
      val requestBody =
          new StimmzettelumschlaegeDTO(
              new BezirkUndWahlID(wahlID, wahlbezirkID),
              urneneroeffnungsUhrzeit,
              anzahlWaehler,
              anzahlWaehler2);

      mockMvc
          .perform(createPostRequest(wahlID, wahlbezirkID, wahlbezirkID, requestBody))
          .andExpect(status().isOk())
          .andReturn()
          .getResponse();

      SecurityUtils.runWith(Authorities.REPOSITORY_READ_STIMMZETTELUMSCHLAEGE);
      val entityFromRepo =
          stimmzettelumschlaegeRepository.findById(requestBody.bezirkUndWahlID()).get();

      val expectedEntity =
          stimmzettelumschlaegeModelMapper.toEntity(
              stimmzettelumschlaegeDTOMapper.toModel(requestBody));
      Assertions.assertThat(entityFromRepo)
          .usingRecursiveComparison()
          .withComparatorForType(
              TimePrecisionComparators.LOCAL_DATE_TIME_PRECISION_MILLISECONDS, LocalDateTime.class)
          .isEqualTo(expectedEntity);
    }

    @Test
    void should_returnBadRequestWlsException_when_validationFailed() throws Exception {
      val wahlID = "    ";
      val wahlbezirkID = "wahlbezirkID";
      val urneneroeffnungsUhrzeit = LocalDateTime.now();
      val anzahlWaehler = 47;
      val anzahlWaehler2 = 11L;
      val requestBody =
          new StimmzettelumschlaegeDTO(
              new BezirkUndWahlID(wahlID, wahlbezirkID),
              urneneroeffnungsUhrzeit,
              anzahlWaehler,
              anzahlWaehler2);

      val response =
          mockMvc
              .perform(createPostRequest(wahlID, wahlbezirkID, wahlbezirkID, requestBody))
              .andExpect(status().isBadRequest())
              .andReturn()
              .getResponse();
      val receivedWlsException =
          objectMapper.readValue(response.getContentAsString(), WlsExceptionDTO.class);

      val expectedWlsExceptionDTO =
          new WlsExceptionDTO(
              WlsExceptionCategory.F,
              ExceptionConstants.POST_STIMMZETTELUMSCHLAEGE_PARAMETER_UNVOLLSTAENDIG.code(),
              "WLS-ERGEBNISMELDUNG",
              ExceptionConstants.POST_STIMMZETTELUMSCHLAEGE_PARAMETER_UNVOLLSTAENDIG.message());
      Assertions.assertThat(receivedWlsException).isEqualTo(expectedWlsExceptionDTO);
    }

    @Test
    void should_returnForbidden_when_userHasWrongBezirkId() throws Exception {
      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";
      val urneneroeffnungsUhrzeit = LocalDateTime.now();
      val anzahlWaehler = 47;
      val anzahlWaehler2 = 11L;
      val requestBody =
          new StimmzettelumschlaegeDTO(
              new BezirkUndWahlID(wahlID, wahlbezirkID),
              urneneroeffnungsUhrzeit,
              anzahlWaehler,
              anzahlWaehler2);

      mockMvc
          .perform(createPostRequest(wahlID, wahlbezirkID, wahlbezirkID + "sth", requestBody))
          .andExpect(status().isForbidden());
    }

    @Nested
    class PostStimmzettelumschlaegeWithExistingSetupData {

      private Stimmzettelumschlaege entityFromRepo;

      @BeforeEach()
      void setup() {
        val entityToReplace =
            new Stimmzettelumschlaege(
                new BezirkUndWahlID("wahlID", "wahlbezirkID"), LocalDateTime.now(), 47, 11L);
        SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_STIMMZETTELUMSCHLAEGE);
        entityFromRepo = stimmzettelumschlaegeRepository.save(entityToReplace);
      }

      @Test
      void should_replaceOldData_when_dataIsPresentInRepository() throws Exception {
        val wahlID = "wahlID";
        val wahlbezirkID = "wahlbezirkID";
        val urneneroeffnungsUhrzeit = LocalDateTime.now();
        val anzahlWaehlerToReplace = 8;
        val anzahlWaehler2ToReplace = 15L;
        val requestBody =
            new StimmzettelumschlaegeDTO(
                new BezirkUndWahlID(wahlID, wahlbezirkID),
                urneneroeffnungsUhrzeit,
                anzahlWaehlerToReplace,
                anzahlWaehler2ToReplace);

        Assertions.assertThat(entityFromRepo).usingRecursiveComparison().isNotEqualTo(requestBody);

        mockMvc
            .perform(createPostRequest(wahlID, wahlbezirkID, wahlbezirkID, requestBody))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

        SecurityUtils.runWith(Authorities.REPOSITORY_READ_STIMMZETTELUMSCHLAEGE);
        val replacedEntityFromRepo =
            stimmzettelumschlaegeRepository.findById(requestBody.bezirkUndWahlID()).get();

        val expectedEntity =
            stimmzettelumschlaegeModelMapper.toEntity(
                stimmzettelumschlaegeDTOMapper.toModel(requestBody));
        Assertions.assertThat(replacedEntityFromRepo)
            .usingRecursiveComparison()
            .withComparatorForType(
                TimePrecisionComparators.LOCAL_DATE_TIME_PRECISION_MILLISECONDS,
                LocalDateTime.class)
            .isEqualTo(expectedEntity);
      }
    }

    private MockHttpServletRequestBuilder createPostRequest(
        final String wahlID,
        final String wahlbezirkID,
        final String claimWahlbezirkID,
        final StimmzettelumschlaegeDTO requestBody)
        throws Exception {
      return MockMvcRequestBuilders.post(
              "/businessActions/stimmzettelumschlaege/" + wahlID + "/" + wahlbezirkID)
          .with(csrf())
          .with(
              jwt()
                  .authorities(
                      new SimpleGrantedAuthority(Authorities.SERVICE_SET_STIMMZETTELUMSCHLAEGE),
                      new SimpleGrantedAuthority(
                          Authorities.REPOSITORY_WRITE_STIMMZETTELUMSCHLAEGE))
                  .jwt(
                      jwt ->
                          jwt.claim("wahlbezirkID", claimWahlbezirkID)
                              .claim("wahlbezirksArt", "BWB")))
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(requestBody));
    }
  }
}
