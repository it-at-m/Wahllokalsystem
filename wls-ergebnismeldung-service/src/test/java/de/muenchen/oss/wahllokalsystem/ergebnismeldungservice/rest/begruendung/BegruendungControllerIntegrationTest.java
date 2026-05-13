package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.begruendung;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.begruendung.Begruendung;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.begruendung.BegruendungRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.BezirkUndWahlIDStapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.Stapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.common.BezirkUndWahlIDStapelartDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.common.StapelartDTO;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(
    classes = MicroServiceApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = {SPRING_TEST_PROFILE})
public class BegruendungControllerIntegrationTest {

  @Autowired MockMvc api;

  @Autowired ObjectMapper objectMapper;

  @Autowired BegruendungRepository begruendungRepository;

  @Autowired MockMvc mockMvc;

  @AfterEach
  void teardown() {
    SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_BEGRUENDUNG);
    begruendungRepository.deleteAll();
  }

  @Nested
  class GetBegruendung {

    @Test
    void should_returnBadRequestWlsException_when_validationFailed() throws Exception {
      val wahlbezirkID1 = "wahlbezirkID1";
      val wahlID1 = "wahlID1";
      val stapelart1 = Stapelart.LTW_BZW_A;
      val stapelartDTO = StapelartDTO.LTW_BZW_A;

      val requestBody =
          new BegruendungDTO(
              new BezirkUndWahlIDStapelartDTO(wahlbezirkID1, wahlID1, stapelartDTO),
              null,
              null,
              true,
              true);

      val request =
          MockMvcRequestBuilders.post(
                  "/businessActions/begruendung/"
                      + wahlbezirkID1
                      + "/"
                      + wahlID1
                      + "/"
                      + stapelart1)
              .with(csrf())
              .with(
                  jwt()
                      .authorities(new SimpleGrantedAuthority(Authorities.SERVICE_SET_BEGRUENDUNG))
                      .jwt(jwt -> jwt.claim("wahlbezirkID", wahlbezirkID1)))
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(requestBody));

      val response =
          mockMvc.perform(request).andExpect(status().isBadRequest()).andReturn().getResponse();
      val receivedWlsException =
          objectMapper.readValue(response.getContentAsString(), WlsExceptionDTO.class);

      val expectedWlsExceptionDTO =
          new WlsExceptionDTO(
              WlsExceptionCategory.F,
              ExceptionConstants.POST_BEGRUENDUNG_PARAMETER_UNVOLLSTAENDIG.code(),
              "WLS-ERGEBNISMELDUNG",
              ExceptionConstants.POST_BEGRUENDUNG_PARAMETER_UNVOLLSTAENDIG.message());
      Assertions.assertThat(receivedWlsException).isEqualTo(expectedWlsExceptionDTO);
    }

    @Test
    void should_returnData_when_dataIsPresentInRepository() throws Exception {
      val wahlbezirkID1 = "wahlbezirkID1";
      val wahlbezirkID2 = "wahlbezirkID2";

      val wahlID1 = "wahlID1";
      val wahlID2 = "wahlID2";

      val stapelart1 = Stapelart.LTW_BZW_A;
      val stapelart2 = Stapelart.LTW_BZW_B;
      val stapelartDTO = StapelartDTO.LTW_BZW_A;

      val begruendung1 = new Begruendung();
      begruendung1.setBezirkUndWahlIDStapelart(
          new BezirkUndWahlIDStapelart(wahlbezirkID1, wahlID1, stapelart1));
      begruendung1.setGrund1("grund1");
      begruendung1.setGrund2("grund2");
      begruendung1.setUnstimmigkeiten(true);
      begruendung1.setNachzaehlung(true);
      SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_BEGRUENDUNG);
      begruendungRepository.save(begruendung1);

      val begruendung2 = new Begruendung();
      begruendung2.setBezirkUndWahlIDStapelart(
          new BezirkUndWahlIDStapelart(wahlbezirkID1, wahlID2, stapelart2));
      begruendung2.setGrund1("grund1");
      begruendungRepository.save(begruendung2);

      val begruendung3 = new Begruendung();
      begruendung3.setBezirkUndWahlIDStapelart(
          new BezirkUndWahlIDStapelart(wahlbezirkID2, wahlID1, stapelart2));
      begruendung3.setGrund1("grund1");
      begruendungRepository.save(begruendung3);

      val expectedIDOfResponse =
          new BezirkUndWahlIDStapelartDTO(wahlbezirkID1, wahlID1, stapelartDTO);
      val expectedResponse =
          new BegruendungDTO(expectedIDOfResponse, "grund1", "grund2", true, true);

      val response =
          api.perform(createGetRequest(wahlID1, wahlbezirkID1, wahlbezirkID1, stapelart1))
              .andExpect(status().isOk())
              .andReturn();

      val responseBody =
          objectMapper.readValue(response.getResponse().getContentAsString(), BegruendungDTO.class);
      Assertions.assertThat(responseBody).usingRecursiveComparison().isEqualTo(expectedResponse);
    }

    @Test
    void should_returnForbidden_when_userHasWrongBezirkId() throws Exception {
      String wahlbezirkID = "wahlbezirkID";
      api.perform(
              createGetRequest("wahlID", wahlbezirkID, wahlbezirkID + "sth", Stapelart.LTW_BZW_A))
          .andExpect(status().isForbidden());
    }

    private MockHttpServletRequestBuilder createGetRequest(
        final String wahlID,
        final String wahlbezirkID,
        final String claimWahlbezirkID,
        final Stapelart stapelart) {
      return MockMvcRequestBuilders.get(
              "/businessActions/begruendung/" + wahlbezirkID + "/" + wahlID + "/" + stapelart)
          .with(
              jwt()
                  .authorities(
                      new SimpleGrantedAuthority(Authorities.SERVICE_GET_BEGRUENDUNG),
                      new SimpleGrantedAuthority(Authorities.REPOSITORY_READ_BEGRUENDUNG))
                  .jwt(jwt -> jwt.claim("wahlbezirkID", claimWahlbezirkID)));
    }
  }

  @Nested
  class PostBegruendung {

    @Test
    void should_returnBadRequestWlsException_when_validationFailed() throws Exception {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "0";
      val stapelart = Stapelart.LTW_BZW_A;
      val requestBody = BegruendungDTO.builder().build();

      val expectedWlsExceptionDTO =
          new WlsExceptionDTO(
              WlsExceptionCategory.F,
              ExceptionConstants.POST_BEGRUENDUNG_PARAMETER_UNVOLLSTAENDIG.code(),
              "WLS-ERGEBNISMELDUNG",
              ExceptionConstants.POST_BEGRUENDUNG_PARAMETER_UNVOLLSTAENDIG.message());

      val result =
          api.perform(createPostRequest(wahlID, wahlbezirkID, wahlbezirkID, stapelart, requestBody))
              .andExpect(status().isBadRequest())
              .andReturn();
      val resultBodyAsWlsExceptionDTO =
          objectMapper.readValue(result.getResponse().getContentAsString(), WlsExceptionDTO.class);

      Assertions.assertThat(resultBodyAsWlsExceptionDTO)
          .usingRecursiveComparison()
          .ignoringFields("message")
          .isEqualTo(expectedWlsExceptionDTO);
      Assertions.assertThat(resultBodyAsWlsExceptionDTO.message()).isNotNull();
    }

    @Test
    void should_persistData_when_noDataIsPresentInRepository() throws Exception {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";
      val stapelart = Stapelart.LTW_BZW_A;
      val stapelartDTO = StapelartDTO.LTW_BZW_A;
      val bezirkUndWahlIDStapelartDTO =
          new BezirkUndWahlIDStapelartDTO(wahlbezirkID, wahlID, stapelartDTO);
      val grund1 = "grund1";
      val grund2 = "grund2";

      val requestBody = new BegruendungDTO(bezirkUndWahlIDStapelartDTO, grund1, grund2, true, true);

      val expectedRepoResponse = new Begruendung();
      expectedRepoResponse.setBezirkUndWahlIDStapelart(
          new BezirkUndWahlIDStapelart(wahlbezirkID, wahlID, stapelart));
      expectedRepoResponse.setGrund1(grund1);
      expectedRepoResponse.setGrund2(grund2);
      expectedRepoResponse.setUnstimmigkeiten(true);
      expectedRepoResponse.setNachzaehlung(true);

      api.perform(createPostRequest(wahlID, wahlbezirkID, wahlbezirkID, stapelart, requestBody))
          .andExpect(status().isOk());

      SecurityUtils.runWith(Authorities.REPOSITORY_READ_BEGRUENDUNG);
      val repoResponse =
          begruendungRepository
              .findById(new BezirkUndWahlIDStapelart(wahlbezirkID, wahlID, stapelart))
              .orElseThrow();

      Assertions.assertThat(repoResponse)
          .usingRecursiveComparison()
          .isEqualTo(expectedRepoResponse);
    }

    @Test
    void should_returnForbidden_when_userHasWrongBezirkId() throws Exception {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";
      val stapelart = Stapelart.LTW_BZW_A;
      val stapelartDTO = StapelartDTO.LTW_BZW_A;
      val bezirkUndWahlIDStapelartDTO =
          new BezirkUndWahlIDStapelartDTO(wahlbezirkID, wahlID, stapelartDTO);
      val grund1 = "grund1";
      val grund2 = "grund2";

      val requestBody = new BegruendungDTO(bezirkUndWahlIDStapelartDTO, grund1, grund2, true, true);
      api.perform(
              createPostRequest(wahlID, wahlbezirkID, wahlbezirkID + "sth", stapelart, requestBody))
          .andExpect(status().isForbidden());
    }

    private MockHttpServletRequestBuilder createPostRequest(
        final String wahlID,
        final String wahlbezirkID,
        final String claimWahlbezirkID,
        final Stapelart stapelart,
        final BegruendungDTO requestBody)
        throws Exception {
      return MockMvcRequestBuilders.post(
              "/businessActions/begruendung/" + wahlbezirkID + "/" + wahlID + "/" + stapelart)
          .with(csrf())
          .with(
              jwt()
                  .authorities(
                      new SimpleGrantedAuthority(Authorities.SERVICE_SET_BEGRUENDUNG),
                      new SimpleGrantedAuthority(Authorities.REPOSITORY_WRITE_BEGRUENDUNG))
                  .jwt(jwt -> jwt.claim("wahlbezirkID", claimWahlbezirkID)))
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(requestBody));
    }
  }
}
