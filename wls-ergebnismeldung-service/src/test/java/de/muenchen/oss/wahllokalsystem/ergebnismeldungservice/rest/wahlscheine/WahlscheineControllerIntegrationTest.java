package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.wahlscheine;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.wahlscheine.Wahlscheine;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.wahlscheine.WahlscheineRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.wahlscheine.WahlscheineModelMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.wiremock.spring.EnableWireMock;

@SpringBootTest(classes = MicroServiceApplication.class)
@AutoConfigureMockMvc
@EnableWireMock
@ActiveProfiles(profiles = {SPRING_TEST_PROFILE})
public class WahlscheineControllerIntegrationTest {

  @Autowired WahlscheineRepository wahlscheineRepository;

  @Autowired WahlscheineModelMapper wahlscheineModelMapper;

  @Autowired WahlscheineDTOMapper wahlscheineDTOMapper;

  @Autowired ObjectMapper objectMapper;

  @Autowired MockMvc mockMvc;

  @AfterEach
  void teardown() {
    SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_WAHLSCHEINE);
    wahlscheineRepository.deleteAll();
  }

  @Nested
  class GetWahlscheine {

    @Test
    void should_returnData_when_dataIsPresentInRepository() throws Exception {
      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";
      val stimmabgabevermerke = 33;

      SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_WAHLSCHEINE);
      val entityToFind =
          new Wahlscheine(new BezirkUndWahlID(wahlID, wahlbezirkID), stimmabgabevermerke);
      wahlscheineRepository.save(entityToFind);

      val response =
          mockMvc
              .perform(createGetRequest(wahlID, wahlbezirkID, wahlbezirkID))
              .andExpect(status().isOk())
              .andReturn()
              .getResponse();
      val responseBodyAsDTO =
          objectMapper.readValue(response.getContentAsString(), WahlscheineDTO.class);

      val expectedResult = wahlscheineDTOMapper.toDTO(wahlscheineModelMapper.toModel(entityToFind));

      Assertions.assertThat(responseBodyAsDTO).usingRecursiveComparison().isEqualTo(expectedResult);
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
              ExceptionConstants.GET_WAHLSCHEINE_PARAMETER_UNVOLLSTAENDIG.code(),
              "WLS-ERGEBNISMELDUNG",
              ExceptionConstants.GET_WAHLSCHEINE_PARAMETER_UNVOLLSTAENDIG.message());
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
              "/businessActions/wahlscheine/" + wahlID + "/" + wahlbezirkID)
          .with(
              jwt()
                  .authorities(
                      new SimpleGrantedAuthority(Authorities.SERVICE_GET_WAHLSCHEINE),
                      new SimpleGrantedAuthority(Authorities.REPOSITORY_READ_WAHLSCHEINE))
                  .jwt(jwt -> jwt.claim("wahlbezirkID", claimWahlbezirkID)));
    }
  }

  @Nested
  class PostWahlscheine {

    @Test
    void should_persistData_when_noDataIsPresentInRepository() throws Exception {
      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";
      val stimmabgabevermerke = 33L;
      val requestBody =
          new WahlscheineDTO(new BezirkUndWahlID(wahlID, wahlbezirkID), stimmabgabevermerke);

      WireMock.stubFor(
          WireMock.post(UrlPattern.ANY)
              .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())));

      mockMvc
          .perform(createPostRequest(wahlID, wahlbezirkID, wahlbezirkID, requestBody))
          .andExpect(status().isOk())
          .andReturn()
          .getResponse();

      SecurityUtils.runWith(Authorities.REPOSITORY_READ_WAHLSCHEINE);
      val entityFromRepo = wahlscheineRepository.findById(requestBody.bezirkUndWahlID()).get();
      val expectedEntity =
          wahlscheineModelMapper.toEntity(wahlscheineDTOMapper.toModel(requestBody));
      Assertions.assertThat(entityFromRepo).usingRecursiveComparison().isEqualTo(expectedEntity);
    }

    @Test
    void should_replaceOldData_when_dataIsPresentInRepository() throws Exception {
      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";
      val stimmabgabevermerke = 33L;
      val requestBody =
          new WahlscheineDTO(new BezirkUndWahlID(wahlID, wahlbezirkID), stimmabgabevermerke);

      SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_WAHLSCHEINE);
      val entityToReplace = new Wahlscheine(requestBody.bezirkUndWahlID(), 77L);
      Assertions.assertThat(entityToReplace).usingRecursiveComparison().isNotEqualTo(requestBody);
      wahlscheineRepository.save(entityToReplace);

      WireMock.stubFor(
          WireMock.post(UrlPattern.ANY)
              .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())));

      mockMvc
          .perform(createPostRequest(wahlID, wahlbezirkID, wahlbezirkID, requestBody))
          .andExpect(status().isOk())
          .andReturn()
          .getResponse();

      SecurityUtils.runWith(Authorities.REPOSITORY_READ_WAHLSCHEINE);
      val entityFromRepo = wahlscheineRepository.findById(requestBody.bezirkUndWahlID()).get();
      val expectedEntity =
          wahlscheineModelMapper.toEntity(wahlscheineDTOMapper.toModel(requestBody));
      Assertions.assertThat(entityFromRepo).usingRecursiveComparison().isEqualTo(expectedEntity);
    }

    @Test
    void should_returnBadRequestWlsException_when_validationFailed() throws Exception {
      val wahlID = "    ";
      val wahlbezirkID = "wahlbezirkID";
      val stimmabgabevermerke = 33L;
      val requestBody =
          new WahlscheineDTO(new BezirkUndWahlID(wahlID, wahlbezirkID), stimmabgabevermerke);

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
              ExceptionConstants.POST_WAHLSCHEINE_PARAMETER_UNVOLLSTAENDIG.code(),
              "WLS-ERGEBNISMELDUNG",
              ExceptionConstants.POST_WAHLSCHEINE_PARAMETER_UNVOLLSTAENDIG.message());
      Assertions.assertThat(receivedWlsException).isEqualTo(expectedWlsExceptionDTO);
    }

    @Test
    void should_returnForbidden_when_userHasWrongBezirkId() throws Exception {
      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";
      val stimmabgabevermerke = 33L;
      val requestBody =
          new WahlscheineDTO(new BezirkUndWahlID(wahlID, wahlbezirkID), stimmabgabevermerke);

      mockMvc
          .perform(createPostRequest(wahlID, wahlbezirkID, wahlbezirkID + "sth", requestBody))
          .andExpect(status().isForbidden());
    }

    private MockHttpServletRequestBuilder createPostRequest(
        final String wahlID,
        final String wahlbezirkID,
        final String claimWahlbezirkID,
        final WahlscheineDTO requestBody)
        throws Exception {
      return MockMvcRequestBuilders.post(
              "/businessActions/wahlscheine/" + wahlID + "/" + wahlbezirkID)
          .with(csrf())
          .with(
              jwt()
                  .authorities(
                      new SimpleGrantedAuthority(Authorities.SERVICE_SET_WAHLSCHEINE),
                      new SimpleGrantedAuthority(Authorities.REPOSITORY_WRITE_WAHLSCHEINE))
                  .jwt(
                      jwt ->
                          jwt.claim("wahlbezirkID", claimWahlbezirkID)
                              .claim("wahlbezirksArt", "BWB")))
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(requestBody));
    }
  }
}
