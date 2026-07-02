package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.status;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.status.Meldung;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.status.Status;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.status.StatusRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.status.Validierungsstatus;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.StatusModelMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.TimePrecisionComparators;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.time.LocalDateTime;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
public class StatusControllerIntegrationTest {

  @Autowired StatusRepository statusRepository;

  @Autowired StatusModelMapper statusModelMapper;

  @Autowired StatusDTOMapper statusDTOMapper;

  @Autowired ObjectMapper objectMapper;

  @Autowired MockMvc mockMvc;

  @BeforeEach
  void setup() {
    SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_STATUS);
    statusRepository.deleteAll();
  }

  @Nested
  class GetStatus {

    @Test
    void should_returnData_when_dataIsPresentInRepository() throws Exception {
      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";

      SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_STATUS);
      val schnellMeldung = createMeldung();
      val niederschrift = createMeldung();
      val entityToFind =
          new Status(new BezirkUndWahlID(wahlID, wahlbezirkID), schnellMeldung, niederschrift);
      statusRepository.save(entityToFind);

      val response =
          mockMvc
              .perform(createGetRequest(wahlID, wahlbezirkID, wahlbezirkID))
              .andExpect(status().isOk())
              .andReturn()
              .getResponse();
      val responseBodyAsDTO =
          objectMapper.readValue(response.getContentAsString(), StatusDTO.class);

      val expectedResult = statusDTOMapper.toDTO(statusModelMapper.toModel(entityToFind));

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
              ExceptionConstants.GET_STATUS_PARAMETER_UNVOLLSTAENDIG.code(),
              "WLS-ERGEBNISMELDUNG",
              ExceptionConstants.GET_STATUS_PARAMETER_UNVOLLSTAENDIG.message());
      Assertions.assertThat(receivedWlsException).isEqualTo(expectedWlsExceptionDTO);
    }

    @Test
    void should_returnForbidden_when_userHasWrongBezirkId() throws Exception {
      val wahlbezirkID = "wahlbezirkID";
      mockMvc
          .perform(createGetRequest("wahlID", wahlbezirkID, wahlbezirkID + "sth"))
          .andExpect(status().isForbidden());
    }

    private MockHttpServletRequestBuilder createGetRequest(
        final String wahlID, final String wahlbezirkID, final String claimWahlbezirkID) {
      return MockMvcRequestBuilders.get("/businessActions/status/" + wahlID + "/" + wahlbezirkID)
          .with(
              jwt()
                  .authorities(
                      new SimpleGrantedAuthority(Authorities.SERVICE_GET_STATUS),
                      new SimpleGrantedAuthority(Authorities.REPOSITORY_READ_STATUS))
                  .jwt(jwt -> jwt.claim("wahlbezirkID", claimWahlbezirkID)));
    }
  }

  @Nested
  class PostStatus {

    @Test
    void should_persistData_when_noDataIsPresentInRepository() throws Exception {
      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";
      val requestBody =
          new StatusDTO(
              new BezirkUndWahlID(wahlID, wahlbezirkID), createMeldungDTO(), createMeldungDTO());

      WireMock.stubFor(
          WireMock.post(UrlPattern.ANY)
              .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())));

      mockMvc
          .perform(createPostRequest(wahlID, wahlbezirkID, wahlbezirkID, requestBody))
          .andExpect(status().isOk())
          .andReturn()
          .getResponse();

      SecurityUtils.runWith(Authorities.REPOSITORY_READ_STATUS);
      val entityFromRepo = statusRepository.findById(requestBody.bezirkUndWahlID()).get();
      val expectedEntity = statusModelMapper.toEntity(statusDTOMapper.toModel(requestBody));
      Assertions.assertThat(entityFromRepo)
          .usingRecursiveComparison()
          .withComparatorForType(
              TimePrecisionComparators.LOCAL_DATE_TIME_PRECISION_MILLISECONDS, LocalDateTime.class)
          .isEqualTo(expectedEntity);
    }

    @Test
    void should_replaceOldData_when_dataIsPresentInRepository() throws Exception {
      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";
      val requestBody =
          new StatusDTO(
              new BezirkUndWahlID(wahlID, wahlbezirkID), createMeldungDTO(), createMeldungDTO());

      SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_STATUS);
      val schnellmeldung = createMeldung();
      schnellmeldung.setGedruckt(!requestBody.schnellmeldung().gedruckt());
      val niederschrift = createMeldung();
      niederschrift.setGedruckt(!requestBody.niederschrift().gedruckt());
      val entityToReplace =
          new Status(requestBody.bezirkUndWahlID(), schnellmeldung, niederschrift);
      Assertions.assertThat(entityToReplace).usingRecursiveComparison().isNotEqualTo(requestBody);
      statusRepository.save(entityToReplace);

      WireMock.stubFor(
          WireMock.post(UrlPattern.ANY)
              .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())));

      mockMvc
          .perform(createPostRequest(wahlID, wahlbezirkID, wahlbezirkID, requestBody))
          .andExpect(status().isOk())
          .andReturn()
          .getResponse();

      SecurityUtils.runWith(Authorities.REPOSITORY_READ_STATUS);
      val entityFromRepo = statusRepository.findById(requestBody.bezirkUndWahlID()).get();
      val expectedEntity = statusModelMapper.toEntity(statusDTOMapper.toModel(requestBody));
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
      val requestBody =
          new StatusDTO(
              new BezirkUndWahlID(wahlID, wahlbezirkID), createMeldungDTO(), createMeldungDTO());

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
              ExceptionConstants.POST_STATUS_PARAMETER_UNVOLLSTAENDIG.code(),
              "WLS-ERGEBNISMELDUNG",
              ExceptionConstants.POST_STATUS_PARAMETER_UNVOLLSTAENDIG.message());
      Assertions.assertThat(receivedWlsException).isEqualTo(expectedWlsExceptionDTO);
    }

    @Test
    void should_notifyAllSenders_when_newDataIsSet() throws Exception {
      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";
      val requestBody =
          new StatusDTO(
              new BezirkUndWahlID(wahlID, wahlbezirkID), createMeldungDTO(), createMeldungDTO());

      WireMock.stubFor(
          WireMock.post("/businessActions/niederschriftDruckuhrzeit")
              .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())));
      WireMock.stubFor(
          WireMock.post("/businessActions/niederschriftSendungsuhrzeit")
              .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())));
      WireMock.stubFor(
          WireMock.post("/businessActions/schnellmeldungDruckuhrzeit")
              .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())));
      WireMock.stubFor(
          WireMock.post("/businessActions/schnellmeldungSendungsuhrzeit")
              .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())));

      mockMvc
          .perform(createPostRequest(wahlID, wahlbezirkID, wahlbezirkID, requestBody))
          .andExpect(status().isOk());
    }

    @Test
    void should_returnForbidden_when_userHasWrongBezirkId() throws Exception {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";
      val requestBody =
          new StatusDTO(
              new BezirkUndWahlID(wahlID, wahlbezirkID), createMeldungDTO(), createMeldungDTO());
      mockMvc
          .perform(createPostRequest(wahlID, wahlbezirkID, wahlbezirkID + "sth", requestBody))
          .andExpect(status().isForbidden());
    }

    private MockHttpServletRequestBuilder createPostRequest(
        final String wahlID,
        final String wahlbezirkID,
        final String claimWahlbezirkID,
        final StatusDTO requestBody)
        throws Exception {
      return MockMvcRequestBuilders.post("/businessActions/status/" + wahlID + "/" + wahlbezirkID)
          .with(csrf())
          .with(
              jwt()
                  .authorities(
                      new SimpleGrantedAuthority(Authorities.SERVICE_SET_STATUS),
                      new SimpleGrantedAuthority(Authorities.REPOSITORY_READ_STATUS),
                      new SimpleGrantedAuthority(Authorities.REPOSITORY_WRITE_STATUS))
                  .jwt(jwt -> jwt.claim("wahlbezirkID", claimWahlbezirkID)))
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(requestBody));
    }

    private MeldungDTO createMeldungDTO() {
      return new MeldungDTO(ValidierungsstatusDTO.VALIDE, true, true, LocalDateTime.now());
    }
  }

  private Meldung createMeldung() {
    val meldung = new Meldung();

    meldung.setSendeuhrzeit(LocalDateTime.now());
    meldung.setUebermittelt(true);
    meldung.setValidierungsstatus(Validierungsstatus.VALIDE);
    meldung.setGedruckt(true);

    return meldung;
  }
}
