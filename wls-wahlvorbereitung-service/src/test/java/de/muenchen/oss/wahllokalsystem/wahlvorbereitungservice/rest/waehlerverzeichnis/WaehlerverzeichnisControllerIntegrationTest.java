package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.waehlerverzeichnis;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.Waehlerverzeichnis;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.WaehlerverzeichnisRepository;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.waehlerverzeichnis.WaehlerverzeichnisModelMapper;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.waehlerverzeichnis.WaehlerverzeichnisValidator;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SpringBootTest(classes = MicroServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles({TestConstants.SPRING_TEST_PROFILE})
public class WaehlerverzeichnisControllerIntegrationTest {

  @Autowired MockMvc mockMvc;

  @Autowired ObjectMapper objectMapper;

  @Autowired WaehlerverzeichnisRepository waehlerverzeichnisRepository;

  @Autowired WaehlerverzeichnisDTOMapper waehlerverzeichnisDTOMapper;

  @Autowired WaehlerverzeichnisModelMapper waehlerverzeichnisModelMapper;

  @Autowired ExceptionFactory exceptionFactory;

  @MockitoSpyBean WaehlerverzeichnisValidator waehlerverzeichnisValidator;

  @AfterEach
  void teardown() {
    SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_WAEHLERVERZEICHNIS);
    waehlerverzeichnisRepository.deleteAll();
  }

  @Nested
  class PostWaehlerverzeichnis {

    @Test
    void should_setNewData_when_callingPost() throws Exception {
      val wahlbezirkID = "wahlbezirkID";
      val waehlerverzeichnisNummer = 89L;
      val requestBody = new WaehlerverzeichnisWriteDTO(true, false, true, false);

      val request =
          buildPostRequest(wahlbezirkID, wahlbezirkID, waehlerverzeichnisNummer, requestBody);

      mockMvc.perform(request).andExpect(status().isCreated());

      SecurityUtils.runWith(Authorities.REPOSITORY_READ_WAEHLERVERZEICHNIS);
      val savedWaehlerverzeichnis =
          waehlerverzeichnisRepository
              .findById(
                  new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer))
              .get();
      val expectedWaehlerverzeichnis =
          new Waehlerverzeichnis(
              new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer),
              requestBody.verzeichnisLagVor(),
              requestBody.berichtigungVorBeginnDerAbstimmung(),
              requestBody.nachtraeglicheBerichtigung(),
              requestBody.mitteilungUeberUngueltigeWahlscheineErhalten());
      Assertions.assertThat(savedWaehlerverzeichnis).isEqualTo(expectedWaehlerverzeichnis);
    }

    @Test
    void should_replaceData_when_dataIsPresent() throws Exception {
      val wahlbezirkID = "wahlbezirkID";
      val waehlerverzeichnisNummer = 89L;
      val requestBody = new WaehlerverzeichnisWriteDTO(true, false, true, false);
      SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_WAEHLERVERZEICHNIS);
      waehlerverzeichnisRepository.save(
          new Waehlerverzeichnis(
              new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer),
              false,
              false,
              false,
              false));

      val request =
          buildPostRequest(wahlbezirkID, wahlbezirkID, waehlerverzeichnisNummer, requestBody);

      mockMvc.perform(request).andExpect(status().isCreated());

      SecurityUtils.runWith(Authorities.REPOSITORY_READ_WAEHLERVERZEICHNIS);
      val savedWaehlerverzeichnis =
          waehlerverzeichnisRepository
              .findById(
                  new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer))
              .get();
      val expectedWaehlerverzeichnis =
          new Waehlerverzeichnis(
              new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer),
              requestBody.verzeichnisLagVor(),
              requestBody.berichtigungVorBeginnDerAbstimmung(),
              requestBody.nachtraeglicheBerichtigung(),
              requestBody.mitteilungUeberUngueltigeWahlscheineErhalten());
      Assertions.assertThat(savedWaehlerverzeichnis).isEqualTo(expectedWaehlerverzeichnis);
      Assertions.assertThat(waehlerverzeichnisRepository.count()).isEqualTo(1);
    }

    @Test
    void should_returnFachlicheWlsException_when_requestIsInvalidWhenParameterNotComplete()
        throws Exception {
      val wahlbezirkID = "wahlbezirkID";
      val waehlerverzeichnisNummer = 89L;
      val requestBody = new WaehlerverzeichnisWriteDTO(true, false, true, false);

      val request =
          buildPostRequest(wahlbezirkID, wahlbezirkID, waehlerverzeichnisNummer, requestBody);

      val mockedValidationException =
          exceptionFactory.createFachlicheWlsException(ExceptionConstants.PARAMS_UNVOLLSTAENDIG);
      Mockito.doThrow(mockedValidationException)
          .when(waehlerverzeichnisValidator)
          .validModelToSetOrThrow(any());

      val result = mockMvc.perform(request).andExpect(status().isBadRequest()).andReturn();
      val wlsExceptionFromBody =
          objectMapper.readValue(
              result.getResponse().getContentAsString(StandardCharsets.UTF_8),
              WlsExceptionDTO.class);

      Assertions.assertThat(wlsExceptionFromBody)
          .isEqualTo(
              new WlsExceptionDTO(
                  WlsExceptionCategory.F,
                  mockedValidationException.getCode(),
                  mockedValidationException.getServiceName(),
                  mockedValidationException.getMessage()));
    }

    @Test
    void should_returnForbidden_when_userHasWrongBezirkId() throws Exception {
      val wahlbezirkID = "wahlbezirkID";
      val waehlerverzeichnisNummer = 89L;
      val requestBody = new WaehlerverzeichnisWriteDTO(true, false, true, false);

      val request =
          buildPostRequest(
              wahlbezirkID, wahlbezirkID + "sth", waehlerverzeichnisNummer, requestBody);

      mockMvc.perform(request).andExpect(status().isForbidden());
    }

    private MockHttpServletRequestBuilder buildPostRequest(
        final String wahlbezirkID,
        final String claimWahlbezirkID,
        final long waehlerverzeichnisNummer,
        final WaehlerverzeichnisWriteDTO requestBody)
        throws JsonProcessingException {
      return post(buildURL(wahlbezirkID, waehlerverzeichnisNummer))
          .with(
              jwt()
                  .authorities(
                      new SimpleGrantedAuthority(Authorities.SERVICE_POST_WAEHLERVERZEICHNIS),
                      new SimpleGrantedAuthority(Authorities.REPOSITORY_WRITE_WAEHLERVERZEICHNIS))
                  .jwt(jwt -> jwt.claim("wahlbezirkID", claimWahlbezirkID)))
          .with(csrf())
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(requestBody));
    }
  }

  @Nested
  class GetWaehlerverzeichnis {

    @Test
    void should_returnData_when_dataIsPresentInRepo() throws Exception {
      val wahlbezirkIDToFind = "wahlbezirkIDToFind";
      val waehlerverzeichnisNummerToFind = 23L;

      val waehlerverzeichnis1 =
          new Waehlerverzeichnis(
              new BezirkIDUndWaehlerverzeichnisNummer(
                  wahlbezirkIDToFind, waehlerverzeichnisNummerToFind + 1),
              true,
              true,
              true,
              true);
      val waehlerverzeichnis2 =
          new Waehlerverzeichnis(
              new BezirkIDUndWaehlerverzeichnisNummer(
                  wahlbezirkIDToFind + "!", waehlerverzeichnisNummerToFind),
              true,
              true,
              true,
              false);
      val waehlerverzeichnis3 =
          new Waehlerverzeichnis(
              new BezirkIDUndWaehlerverzeichnisNummer(
                  wahlbezirkIDToFind, waehlerverzeichnisNummerToFind),
              true,
              false,
              true,
              false);
      val waehlerverzeichnis4 =
          new Waehlerverzeichnis(
              new BezirkIDUndWaehlerverzeichnisNummer(
                  wahlbezirkIDToFind + "!", waehlerverzeichnisNummerToFind + 1),
              false,
              false,
              true,
              true);

      SecurityUtils.runWith(Authorities.REPOSITORY_READ_WAEHLERVERZEICHNIS);
      waehlerverzeichnisRepository.saveAll(
          List.of(
              waehlerverzeichnis1, waehlerverzeichnis2, waehlerverzeichnis3, waehlerverzeichnis4));

      val request =
          buildGetRequest(wahlbezirkIDToFind, wahlbezirkIDToFind, waehlerverzeichnisNummerToFind);

      val result = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
      val resultBodyAsDTO =
          objectMapper.readValue(
              result.getResponse().getContentAsString(), WaehlerverzeichnisDTO.class);

      val expectedResultBody =
          waehlerverzeichnisDTOMapper.toDto(
              waehlerverzeichnisModelMapper.toModel(waehlerverzeichnis3));
      Assertions.assertThat(resultBodyAsDTO).isEqualTo(expectedResultBody);
    }

    @Test
    void should_returnNoContent_when_noDataFound() throws Exception {
      val wahlbezirkIDToFind = "wahlbezirkIDToFind";
      val waehlerverzeichnisNummerToFind = 23L;

      val waehlerverzeichnis1 =
          new Waehlerverzeichnis(
              new BezirkIDUndWaehlerverzeichnisNummer(
                  wahlbezirkIDToFind, waehlerverzeichnisNummerToFind + 1),
              true,
              true,
              true,
              true);
      val waehlerverzeichnis2 =
          new Waehlerverzeichnis(
              new BezirkIDUndWaehlerverzeichnisNummer(
                  wahlbezirkIDToFind + "!", waehlerverzeichnisNummerToFind),
              true,
              true,
              true,
              false);
      val waehlerverzeichnis3 =
          new Waehlerverzeichnis(
              new BezirkIDUndWaehlerverzeichnisNummer(
                  wahlbezirkIDToFind + "!", waehlerverzeichnisNummerToFind + 1),
              false,
              false,
              true,
              true);

      SecurityUtils.runWith(Authorities.REPOSITORY_READ_WAEHLERVERZEICHNIS);
      waehlerverzeichnisRepository.saveAll(
          List.of(waehlerverzeichnis1, waehlerverzeichnis2, waehlerverzeichnis3));

      val request =
          buildGetRequest(wahlbezirkIDToFind, wahlbezirkIDToFind, waehlerverzeichnisNummerToFind);

      val result = mockMvc.perform(request).andExpect(status().isNoContent()).andReturn();

      Assertions.assertThat(result.getResponse().getContentAsString()).isEmpty();
    }

    @Test
    void should_returnForbidden_when_userHasWrongBezirkId() throws Exception {
      String wahlbezirkIDToFind = "123";
      val waehlerverzeichnisNummerToFind = 23L;
      val request =
          buildGetRequest(
              wahlbezirkIDToFind, wahlbezirkIDToFind + "sth", waehlerverzeichnisNummerToFind);

      mockMvc.perform(request).andExpect(status().isForbidden());
    }

    private MockHttpServletRequestBuilder buildGetRequest(
        final String wahlbezirkID,
        final String claimWahlbezirkID,
        final long waehlerverzeichnisNummer) {
      return get(buildURL(wahlbezirkID, waehlerverzeichnisNummer))
          .with(
              jwt()
                  .authorities(
                      new SimpleGrantedAuthority(Authorities.SERVICE_GET_WAEHLERVERZEICHNIS),
                      new SimpleGrantedAuthority(Authorities.REPOSITORY_READ_WAEHLERVERZEICHNIS))
                  .jwt(jwt -> jwt.claim("wahlbezirkID", claimWahlbezirkID)));
    }
  }

  private String buildURL(final String wahlbezirkID, final long waehlerverzeichnisnummer) {
    return "/businessActions/waehlerverzeichnis/" + wahlbezirkID + "/" + waehlerverzeichnisnummer;
  }
}
