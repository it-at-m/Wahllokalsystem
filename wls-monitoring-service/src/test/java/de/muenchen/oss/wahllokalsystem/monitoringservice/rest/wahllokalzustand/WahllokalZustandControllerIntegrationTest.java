package de.muenchen.oss.wahllokalsystem.monitoringservice.rest.wahllokalzustand;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static de.muenchen.oss.wahllokalsystem.monitoringservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.muenchen.oss.wahllokalsystem.monitoringservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.monitoringservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.monitoringservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionKonstanten;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(
    classes = MicroServiceApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureWireMock
@ActiveProfiles(profiles = {SPRING_TEST_PROFILE})
class WahllokalZustandControllerIntegrationTest {

  @Value("${service.info.oid}")
  String serviceID;

  @Autowired ObjectMapper objectMapper;

  @Autowired MockMvc mockMvc;

  @BeforeEach
  void setup() {
    WireMock.resetAllRequests();
  }

  @Nested
  class PostLastSeen {

    @Test
    void should_notThrowAnyException_when_requestParamValid() {
      val wahlbezirkID = "wahlbezirkID";
      val teamID = "A";
      val request =
          MockMvcRequestBuilders.post(
                  "/businessActions/lastSeen?wahlbezirkID=" + wahlbezirkID + "&teamID=" + teamID)
              .with(csrf())
              .with(
                  jwt()
                      .authorities(new SimpleGrantedAuthority(Authorities.SERVICE_POST_LASTSEEN))
                      .jwt(jwt -> jwt.claim("wahlbezirkID", wahlbezirkID).claim("teamID", teamID)));
      Assertions.assertThatNoException().isThrownBy(() -> mockMvc.perform(request));
    }

    @Test
    void should_throwWlsException_when_requestParamsAreNotSet() throws Exception {
      val request =
          MockMvcRequestBuilders.post("/businessActions/lastSeen")
              .with(csrf())
              .with(
                  jwt()
                      .authorities(new SimpleGrantedAuthority(Authorities.SERVICE_POST_LAST_LOGOUT))
                      .jwt(
                          jwt ->
                              jwt.claim("wahlbezirkID", "wahlbezirkID").claim("teamID", "teamID")));
      val response =
          mockMvc.perform(request).andExpect(status().isInternalServerError()).andReturn();
      val responseBodyAsWlsExceptionDTO =
          objectMapper.readValue(
              response.getResponse().getContentAsString(), WlsExceptionDTO.class);

      val expectedWlsExceptionDTO =
          new WlsExceptionDTO(
              WlsExceptionCategory.T,
              ExceptionKonstanten.CODE_ALLGEMEIN_UNBEKANNT,
              "WLS-MONITORING",
              "");

      Assertions.assertThat(responseBodyAsWlsExceptionDTO)
          .usingRecursiveComparison()
          .ignoringFields("message")
          .isEqualTo(expectedWlsExceptionDTO);
    }

    @Test
    void should_throwWlsException_when_requestParamTeamIDIsInvalid() throws Exception {
      val wahlbezirkID = "1234";
      val teamID = "  ";
      val request =
          MockMvcRequestBuilders.post(
                  "/businessActions/lastSeen?wahlbezirkID=" + wahlbezirkID + "&teamID=" + teamID)
              .with(csrf())
              .with(
                  jwt()
                      .authorities(new SimpleGrantedAuthority(Authorities.SERVICE_POST_LASTSEEN))
                      .jwt(jwt -> jwt.claim("wahlbezirkID", wahlbezirkID).claim("teamID", teamID)));
      val response = mockMvc.perform(request).andExpect(status().isBadRequest()).andReturn();
      val responseBodyAsWlsExceptionDTO =
          objectMapper.readValue(
              response.getResponse().getContentAsString(), WlsExceptionDTO.class);

      val expectedWlsExceptionDTO =
          new WlsExceptionDTO(
              WlsExceptionCategory.F,
              ExceptionConstants.POST_LASTSEEN_SUCHKRITERIEN_UNVOLLSTAENDIG.code(),
              serviceID,
              ExceptionConstants.POST_LASTSEEN_SUCHKRITERIEN_UNVOLLSTAENDIG.message());

      Assertions.assertThat(responseBodyAsWlsExceptionDTO)
          .usingRecursiveComparison()
          .ignoringFields("message")
          .isEqualTo(expectedWlsExceptionDTO);
    }

    @Test
    void should_throwWlsException_when_requestParamWahlbezirkIDIsInvalid() throws Exception {
      val wahlbezirkID = "  ";
      val teamID = "1234";
      val request =
          MockMvcRequestBuilders.post(
                  "/businessActions/lastSeen?wahlbezirkID=" + wahlbezirkID + "&teamID=" + teamID)
              .with(csrf())
              .with(
                  jwt()
                      .authorities(new SimpleGrantedAuthority(Authorities.SERVICE_POST_LASTSEEN))
                      .jwt(jwt -> jwt.claim("wahlbezirkID", wahlbezirkID).claim("teamID", teamID)));
      val response = mockMvc.perform(request).andExpect(status().isBadRequest()).andReturn();
      val responseBodyAsWlsExceptionDTO =
          objectMapper.readValue(
              response.getResponse().getContentAsString(), WlsExceptionDTO.class);

      val expectedWlsExceptionDTO =
          new WlsExceptionDTO(
              WlsExceptionCategory.F,
              ExceptionConstants.POST_LASTSEEN_SUCHKRITERIEN_UNVOLLSTAENDIG.code(),
              serviceID,
              ExceptionConstants.POST_LASTSEEN_SUCHKRITERIEN_UNVOLLSTAENDIG.message());

      Assertions.assertThat(responseBodyAsWlsExceptionDTO)
          .usingRecursiveComparison()
          .ignoringFields("message")
          .isEqualTo(expectedWlsExceptionDTO);
    }

    @Test
    void should_throwFachlicheWlsException_when_wahlbezirkIdDoesNotMatchUserWahlbezirkID()
        throws Exception {
      String wahlbezirkID = "wahlbezirkID";
      String teamID = "teamID";
      val request =
          MockMvcRequestBuilders.post(
                  "/businessActions/lastSeen?wahlbezirkID=" + wahlbezirkID + "&teamID=" + teamID)
              .with(csrf())
              .with(
                  jwt()
                      .authorities(new SimpleGrantedAuthority(Authorities.SERVICE_POST_LASTSEEN))
                      .jwt(
                          jwt ->
                              jwt.claim("wahlbezirkID", wahlbezirkID + "sth")
                                  .claim("teamID", teamID)));

      mockMvc.perform(request).andExpect(status().isForbidden());
    }

    @Test
    void should_throwFachlicheWlsException_when_teamIdDoesNotMatchUserTeamID() throws Exception {
      String wahlbezirkID = "wahlbezirkID";
      String teamID = "teamID";
      val request =
          MockMvcRequestBuilders.post(
                  "/businessActions/lastSeen?wahlbezirkID=" + wahlbezirkID + "&teamID=" + teamID)
              .with(csrf())
              .with(
                  jwt()
                      .authorities(new SimpleGrantedAuthority(Authorities.SERVICE_POST_LASTSEEN))
                      .jwt(
                          jwt ->
                              jwt.claim("wahlbezirkID", wahlbezirkID)
                                  .claim("teamID", teamID + "sth")));

      mockMvc.perform(request).andExpect(status().isForbidden());
    }
  }

  @Nested
  class PostLetzteAbmeldung {

    @Test
    void should_notThrowAnyException_when_requestParamsValid() {
      String wahlbezirkID = "wahlbezirkID";
      String teamID = "teamID";
      val request =
          MockMvcRequestBuilders.post(
                  "/businessActions/letzteAbmeldung?wahlbezirkID="
                      + wahlbezirkID
                      + "&teamID="
                      + teamID)
              .with(csrf())
              .with(
                  jwt()
                      .authorities(new SimpleGrantedAuthority(Authorities.SERVICE_POST_LAST_LOGOUT))
                      .jwt(jwt -> jwt.claim("wahlbezirkID", wahlbezirkID).claim("teamID", teamID)));
      Assertions.assertThatNoException().isThrownBy(() -> mockMvc.perform(request));
    }

    @Test
    void should_throwWlsException_when_requestParamsAreNotSet() throws Exception {
      val request =
          MockMvcRequestBuilders.post("/businessActions/letzteAbmeldung")
              .with(csrf())
              .with(
                  jwt()
                      .authorities(new SimpleGrantedAuthority(Authorities.SERVICE_POST_LAST_LOGOUT))
                      .jwt(
                          jwt ->
                              jwt.claim("wahlbezirkID", "wahlbezirkID").claim("teamID", "teamID")));
      val response =
          mockMvc.perform(request).andExpect(status().isInternalServerError()).andReturn();
      val responseBodyAsWlsExceptionDTO =
          objectMapper.readValue(
              response.getResponse().getContentAsString(), WlsExceptionDTO.class);

      val expectedWlsExceptionDTO =
          new WlsExceptionDTO(
              WlsExceptionCategory.T,
              ExceptionKonstanten.CODE_ALLGEMEIN_UNBEKANNT,
              "WLS-MONITORING",
              "");

      Assertions.assertThat(responseBodyAsWlsExceptionDTO)
          .usingRecursiveComparison()
          .ignoringFields("message")
          .isEqualTo(expectedWlsExceptionDTO);
    }

    @Test
    void should_throwWlsException_when_requestParamTeamIdIsInvaid() throws Exception {
      val wahlbezirkID = "1234";
      val teamID = "  ";
      val request =
          MockMvcRequestBuilders.post(
                  "/businessActions/letzteAbmeldung?wahlbezirkID="
                      + wahlbezirkID
                      + "&teamID="
                      + teamID)
              .with(csrf())
              .with(
                  jwt()
                      .authorities(new SimpleGrantedAuthority(Authorities.SERVICE_POST_LAST_LOGOUT))
                      .jwt(jwt -> jwt.claim("wahlbezirkID", wahlbezirkID).claim("teamID", teamID)));
      val response = mockMvc.perform(request).andExpect(status().isBadRequest()).andReturn();
      val responseBodyAsWlsExceptionDTO =
          objectMapper.readValue(
              response.getResponse().getContentAsString(), WlsExceptionDTO.class);

      val expectedWlsExceptionDTO =
          new WlsExceptionDTO(
              WlsExceptionCategory.F,
              ExceptionConstants.POST_LETZTEABMELDUNG_SUCHKRITERIEN_UNVOLLSTAENDIG.code(),
              "WLS-MONITORING",
              "");

      Assertions.assertThat(responseBodyAsWlsExceptionDTO)
          .usingRecursiveComparison()
          .ignoringFields("message")
          .isEqualTo(expectedWlsExceptionDTO);
    }

    @Test
    void should_throwWlsException_when_requestParamWahlbezirkIDIsInvaid() throws Exception {
      val wahlbezirkID = "  ";
      val teamID = "1234";
      val request =
          MockMvcRequestBuilders.post(
                  "/businessActions/letzteAbmeldung?wahlbezirkID="
                      + wahlbezirkID
                      + "&teamID="
                      + teamID)
              .with(csrf())
              .with(
                  jwt()
                      .authorities(new SimpleGrantedAuthority(Authorities.SERVICE_POST_LAST_LOGOUT))
                      .jwt(jwt -> jwt.claim("wahlbezirkID", wahlbezirkID).claim("teamID", teamID)));
      val response = mockMvc.perform(request).andExpect(status().isBadRequest()).andReturn();
      val responseBodyAsWlsExceptionDTO =
          objectMapper.readValue(
              response.getResponse().getContentAsString(), WlsExceptionDTO.class);

      val expectedWlsExceptionDTO =
          new WlsExceptionDTO(
              WlsExceptionCategory.F,
              ExceptionConstants.POST_LETZTEABMELDUNG_SUCHKRITERIEN_UNVOLLSTAENDIG.code(),
              "WLS-MONITORING",
              "");

      Assertions.assertThat(responseBodyAsWlsExceptionDTO)
          .usingRecursiveComparison()
          .ignoringFields("message")
          .isEqualTo(expectedWlsExceptionDTO);
    }

    @Test
    void should_throwFachlicheWlsException_when_wahlbezirkIdDoesNotMatchUserWahlbezirkID()
        throws Exception {
      String wahlbezirkID = "wahlbezirkID";
      String teamID = "teamID";
      val request =
          MockMvcRequestBuilders.post(
                  "/businessActions/letzteAbmeldung?wahlbezirkID="
                      + wahlbezirkID
                      + "&teamID="
                      + teamID)
              .with(csrf())
              .with(
                  jwt()
                      .authorities(new SimpleGrantedAuthority(Authorities.SERVICE_POST_LAST_LOGOUT))
                      .jwt(
                          jwt ->
                              jwt.claim("wahlbezirkID", wahlbezirkID + "sth")
                                  .claim("teamID", teamID)));

      mockMvc.perform(request).andExpect(status().isForbidden());
    }

    @Test
    void should_throwFachlicheWlsException_when_teamIdDoesNotMatchUserTeamID() throws Exception {
      String wahlbezirkID = "wahlbezirkID";
      String teamID = "teamID";
      val request =
          MockMvcRequestBuilders.post(
                  "/businessActions/letzteAbmeldung?wahlbezirkID="
                      + wahlbezirkID
                      + "&teamID="
                      + teamID)
              .with(csrf())
              .with(
                  jwt()
                      .authorities(new SimpleGrantedAuthority(Authorities.SERVICE_POST_LAST_LOGOUT))
                      .jwt(
                          jwt ->
                              jwt.claim("wahlbezirkID", wahlbezirkID)
                                  .claim("teamID", teamID + "sth")));

      mockMvc.perform(request).andExpect(status().isForbidden());
    }
  }

  @Nested
  class PostSchnellmeldungsSendungsuhrzeit {

    @Test
    void should_notThrowAnyException_when_requestParamValid() throws Exception {
      val wahlbezirkID = "wahlbezirkID";
      val sendungsDatenDTO_valid = createSendungsdatenDTO("wahlID", wahlbezirkID);
      val request_valid_param =
          MockMvcRequestBuilders.post("/businessActions/schnellmeldungSendungsuhrzeit")
              .with(csrf())
              .with(
                  jwt()
                      .authorities(
                          new SimpleGrantedAuthority(
                              Authorities.SERVICE_POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT))
                      .jwt(jwt -> jwt.claim("wahlbezirkID", wahlbezirkID)))
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(sendungsDatenDTO_valid));
      Assertions.assertThatNoException().isThrownBy(() -> mockMvc.perform(request_valid_param));
    }

    @Test
    void should_throwWlsException_when_requestParamIsInvalid() throws Exception {
      val request_sendungsDatenNull =
          MockMvcRequestBuilders.post("/businessActions/schnellmeldungSendungsuhrzeit")
              .with(csrf())
              .with(
                  jwt()
                      .authorities(
                          new SimpleGrantedAuthority(
                              Authorities.SERVICE_POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT))
                      .jwt(jwt -> jwt.claim("wahlbezirkID", "wahlbezirkID")))
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(null));
      val response_sendungsDatenNull =
          mockMvc.perform(request_sendungsDatenNull).andExpect(status().isBadRequest()).andReturn();
      val responseBodyAsWlsExceptionDTO_sendungsDatenNull =
          objectMapper.readValue(
              response_sendungsDatenNull.getResponse().getContentAsString(), WlsExceptionDTO.class);

      val expectedWlsExceptionDTO_sendungsDatenNull =
          new WlsExceptionDTO(
              WlsExceptionCategory.F,
              ExceptionKonstanten.CODE_HTTP_MESSAGE_NOT_READABLE,
              serviceID,
              "");

      Assertions.assertThat(responseBodyAsWlsExceptionDTO_sendungsDatenNull)
          .usingRecursiveComparison()
          .ignoringFields("message")
          .isEqualTo(expectedWlsExceptionDTO_sendungsDatenNull);
    }

    @Test
    void should_throwFachlicheWlsException_when_wahlbezirkIdDoesNotMatchUserWahlbezirkID()
        throws Exception {
      String wahlbezirkID = "wahlbezirkID";
      val sendungsDatenDTO = createSendungsdatenDTO("wahlID", wahlbezirkID);
      val request =
          MockMvcRequestBuilders.post("/businessActions/schnellmeldungSendungsuhrzeit")
              .with(csrf())
              .with(
                  jwt()
                      .authorities(
                          new SimpleGrantedAuthority(
                              Authorities.SERVICE_POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT))
                      .jwt(jwt -> jwt.claim("wahlbezirkID", wahlbezirkID + "sth")))
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(sendungsDatenDTO));

      mockMvc.perform(request).andExpect(status().isForbidden());
    }
  }

  @Nested
  class PostSchnellmeldungDruckuhrzeit {

    @Test
    void should_notThrowAnyException_when_requestParamValid() throws Exception {
      val wahlbezirkID = "wahlbezirkID";
      val druckDatenDTO_valid = createSendungsdatenDTO("wahlID", wahlbezirkID);
      val request_valid_param =
          MockMvcRequestBuilders.post("/businessActions/schnellmeldungDruckuhrzeit")
              .with(csrf())
              .with(
                  jwt()
                      .authorities(
                          new SimpleGrantedAuthority(
                              Authorities.SERVICE_POST_SCHNELLMELDUNG_DRUCKUHRZEIT))
                      .jwt(jwt -> jwt.claim("wahlbezirkID", wahlbezirkID)))
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(druckDatenDTO_valid));
      Assertions.assertThatNoException().isThrownBy(() -> mockMvc.perform(request_valid_param));
    }

    @Test
    void should_throwWlsException_when_requestParamIsInvalid() throws Exception {
      val request_druckDatenNull =
          MockMvcRequestBuilders.post("/businessActions/schnellmeldungDruckuhrzeit")
              .with(csrf())
              .with(
                  jwt()
                      .authorities(
                          new SimpleGrantedAuthority(
                              Authorities.SERVICE_POST_SCHNELLMELDUNG_DRUCKUHRZEIT))
                      .jwt(jwt -> jwt.claim("wahlbezirkID", "wahlbezirkID")))
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(null));

      val response_druckDatenNull =
          mockMvc.perform(request_druckDatenNull).andExpect(status().isBadRequest()).andReturn();
      val responseBodyAsWlsExceptionDTO_druckDatenNull =
          objectMapper.readValue(
              response_druckDatenNull.getResponse().getContentAsString(), WlsExceptionDTO.class);

      val expectedWlsExceptionDTO_druckDatenNull =
          new WlsExceptionDTO(
              WlsExceptionCategory.F,
              ExceptionKonstanten.CODE_HTTP_MESSAGE_NOT_READABLE,
              serviceID,
              "");

      Assertions.assertThat(responseBodyAsWlsExceptionDTO_druckDatenNull)
          .usingRecursiveComparison()
          .ignoringFields("message")
          .isEqualTo(expectedWlsExceptionDTO_druckDatenNull);
    }

    @Test
    void should_throwFachlicheWlsException_when_wahlbezirkIdDoesNotMatchUserWahlbezirkID()
        throws Exception {
      String wahlbezirkID = "wahlbezirkID";
      val druckDatenDTO = createSendungsdatenDTO("wahlID", wahlbezirkID);
      val request =
          MockMvcRequestBuilders.post("/businessActions/schnellmeldungDruckuhrzeit")
              .with(csrf())
              .with(
                  jwt()
                      .authorities(
                          new SimpleGrantedAuthority(
                              Authorities.SERVICE_POST_SCHNELLMELDUNG_DRUCKUHRZEIT))
                      .jwt(jwt -> jwt.claim("wahlbezirkID", wahlbezirkID + "sth")))
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(druckDatenDTO));

      mockMvc.perform(request).andExpect(status().isForbidden());
    }
  }

  @Nested
  class PostNiederschriftSendungsuhrzeit {

    @Test
    void should_notThrowAnyException_when_requestParamValid() throws Exception {
      val wahlbezirkID = "wahlbezirkID";
      val sendungsDatenDTO_valid = createSendungsdatenDTO("wahlID", wahlbezirkID);
      val request_valid_param =
          MockMvcRequestBuilders.post("/businessActions/niederschriftSendungsuhrzeit")
              .with(csrf())
              .with(
                  jwt()
                      .authorities(
                          new SimpleGrantedAuthority(
                              Authorities.SERVICE_POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT))
                      .jwt(jwt -> jwt.claim("wahlbezirkID", wahlbezirkID)))
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(sendungsDatenDTO_valid));
      Assertions.assertThatNoException().isThrownBy(() -> mockMvc.perform(request_valid_param));
    }

    @Test
    void should_throwWlsException_when_requestParamsAreInvalid() throws Exception {
      val wahlbezirkID = "wahlbezirkID";
      val sendungsDatenDTO_wahlID_blank = createSendungsdatenDTO("  ", wahlbezirkID);

      val request_wahlID_blank =
          MockMvcRequestBuilders.post("/businessActions/niederschriftSendungsuhrzeit")
              .with(csrf())
              .with(
                  jwt()
                      .authorities(
                          new SimpleGrantedAuthority(
                              Authorities.SERVICE_POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT))
                      .jwt(jwt -> jwt.claim("wahlbezirkID", wahlbezirkID)))
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(sendungsDatenDTO_wahlID_blank));

      val response_wahlID_blank =
          mockMvc.perform(request_wahlID_blank).andExpect(status().isBadRequest()).andReturn();
      val responseBodyAsWlsExceptionDTO_wahlID_blank =
          objectMapper.readValue(
              response_wahlID_blank.getResponse().getContentAsString(), WlsExceptionDTO.class);

      val expectedWlsExceptionDTO =
          new WlsExceptionDTO(
              WlsExceptionCategory.F,
              ExceptionConstants.POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG
                  .code(),
              serviceID,
              ExceptionConstants.POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG
                  .message());

      Assertions.assertThat(responseBodyAsWlsExceptionDTO_wahlID_blank)
          .usingRecursiveComparison()
          .ignoringFields("message")
          .isEqualTo(expectedWlsExceptionDTO);
    }

    @Test
    void should_throwFachlicheWlsException_when_wahlbezirkIdDoesNotMatchUserWahlbezirkID()
        throws Exception {
      String wahlbezirkID = "wahlbezirkID";
      val sendungsDatenDTO = createSendungsdatenDTO("wahlID", wahlbezirkID);
      val request =
          MockMvcRequestBuilders.post("/businessActions/niederschriftSendungsuhrzeit")
              .with(csrf())
              .with(
                  jwt()
                      .authorities(
                          new SimpleGrantedAuthority(
                              Authorities.SERVICE_POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT))
                      .jwt(jwt -> jwt.claim("wahlbezirkID", wahlbezirkID + "sth")))
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(sendungsDatenDTO));

      mockMvc.perform(request).andExpect(status().isForbidden());
    }
  }

  @Nested
  class PostNiederschriftDruckuhrzeit {

    @Test
    void should_notThrowAnyException_when_requestParamValid() throws Exception {
      val wahlbezirkID = "wahlbezirkID";
      val druckDatenDTO_valid = createSendungsdatenDTO("wahlID", wahlbezirkID);
      val request_valid_param =
          MockMvcRequestBuilders.post("/businessActions/niederschriftDruckuhrzeit")
              .with(csrf())
              .with(
                  jwt()
                      .authorities(
                          new SimpleGrantedAuthority(
                              Authorities.SERVICE_POST_NIEDERSCHRIFT_DRUCKUHRZEIT))
                      .jwt(jwt -> jwt.claim("wahlbezirkID", wahlbezirkID)))
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(druckDatenDTO_valid));
      Assertions.assertThatNoException().isThrownBy(() -> mockMvc.perform(request_valid_param));
    }

    @Test
    void should_throwWlsException_when_requestParamsAreInvalid() throws Exception {
      val druckDatenDTO_wahlbezirkID_empty = createDruckdatenDTO("wahlID", "");
      val request_wahlbezirkID_empty =
          MockMvcRequestBuilders.post("/businessActions/niederschriftDruckuhrzeit")
              .with(csrf())
              .with(
                  jwt()
                      .authorities(
                          new SimpleGrantedAuthority(
                              Authorities.SERVICE_POST_NIEDERSCHRIFT_DRUCKUHRZEIT))
                      .jwt(jwt -> jwt.claim("wahlbezirkID", "")))
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(druckDatenDTO_wahlbezirkID_empty));

      val response_wahlbezirkID_empty =
          mockMvc
              .perform(request_wahlbezirkID_empty)
              .andExpect(status().isBadRequest())
              .andReturn();
      val responseBodyAsWlsExceptionDTO_wahlbezirkID_empty =
          objectMapper.readValue(
              response_wahlbezirkID_empty.getResponse().getContentAsString(),
              WlsExceptionDTO.class);

      val expectedWlsExceptionDTO =
          new WlsExceptionDTO(
              WlsExceptionCategory.F,
              ExceptionConstants.POST_NIEDERSCHRIFT_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG
                  .code(),
              serviceID,
              ExceptionConstants.POST_NIEDERSCHRIFT_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG
                  .message());

      Assertions.assertThat(responseBodyAsWlsExceptionDTO_wahlbezirkID_empty)
          .usingRecursiveComparison()
          .ignoringFields("message")
          .isEqualTo(expectedWlsExceptionDTO);
    }

    @Test
    void should_notThrowAnyException_when_requestParamValidAndLocalDateTimeGotTimezone()
        throws Exception {
      val request_valid_param =
          MockMvcRequestBuilders.post("/businessActions/niederschriftDruckuhrzeit")
              .with(csrf())
              .with(
                  jwt()
                      .authorities(
                          new SimpleGrantedAuthority(
                              Authorities.SERVICE_POST_NIEDERSCHRIFT_DRUCKUHRZEIT))
                      .jwt(jwt -> jwt.claim("wahlbezirkID", "wahlbezirkID")))
              .contentType(MediaType.APPLICATION_JSON)
              .content(
                  """
                                    {
                                      "druckuhrzeit": "2024-10-21T23:59:12.123",
                                      "bezirkUndWahlID": {
                                        "wahlID": "wahlID",
                                        "wahlbezirkID": "wahlbezirkID"
                                      }
                                    }""");
      Assertions.assertThatNoException().isThrownBy(() -> mockMvc.perform(request_valid_param));
    }

    @Test
    void should_convertTimeToJsonFormat_when_restCallWithTimestamp() throws Exception {
      val wahlbezirkID = "wahlbezirkID";
      val validDruckDatenDTO =
          DruckdatenDTO.builder()
              .druckuhrzeit(LocalDateTime.parse("2024-10-21T23:59:12.123"))
              .bezirkUndWahlID(new BezirkUndWahlID("wahlID", wahlbezirkID))
              .build();
      val request_wahlbezirkID_empty =
          MockMvcRequestBuilders.post("/businessActions/niederschriftDruckuhrzeit")
              .with(csrf())
              .with(
                  jwt()
                      .authorities(
                          new SimpleGrantedAuthority(
                              Authorities.SERVICE_POST_NIEDERSCHRIFT_DRUCKUHRZEIT))
                      .jwt(jwt -> jwt.claim("wahlbezirkID", wahlbezirkID)))
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(validDruckDatenDTO));

      mockMvc.perform(request_wahlbezirkID_empty);

      WireMock.verify(
          WireMock.postRequestedFor(WireMock.urlEqualTo("/wahllokalzustand"))
              .withHeader("Content-Type", equalTo("application/json"))
              .withRequestBody(matchingJsonPath("$.wahlbezirkID", equalTo("wahlbezirkID")))
              .withRequestBody(matchingJsonPath("$.druckzustaende[0].wahlID", equalTo("wahlID")))
              .withRequestBody(
                  matchingJsonPath(
                      "$.druckzustaende[0].niederschriftDruckUhrzeit",
                      equalTo("2024-10-21T23:59:12.123"))));
    }

    @Test
    void should_throwFachlicheWlsException_when_wahlbezirkIdDoesNotMatchUserWahlbezirkID()
        throws Exception {
      String wahlbezirkID = "wahlbezirkID";
      val druckDatenDTO =
          DruckdatenDTO.builder()
              .druckuhrzeit(LocalDateTime.parse("2024-10-21T23:59:12.123"))
              .bezirkUndWahlID(new BezirkUndWahlID("wahlID", wahlbezirkID))
              .build();
      val request =
          MockMvcRequestBuilders.post("/businessActions/niederschriftDruckuhrzeit")
              .with(csrf())
              .with(
                  jwt()
                      .authorities(
                          new SimpleGrantedAuthority(
                              Authorities.SERVICE_POST_NIEDERSCHRIFT_DRUCKUHRZEIT))
                      .jwt(jwt -> jwt.claim("wahlbezirkID", wahlbezirkID + "sth")))
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(druckDatenDTO));

      mockMvc.perform(request).andExpect(status().isForbidden());
    }
  }

  private SendungsdatenDTO createSendungsdatenDTO(final String wahlID, final String wahlbezirkID) {
    return SendungsdatenDTO.builder()
        .bezirkUndWahlID(new BezirkUndWahlID(wahlID, wahlbezirkID))
        .build();
  }

  private DruckdatenDTO createDruckdatenDTO(final String wahlID, final String wahlbezirkID) {
    return DruckdatenDTO.builder()
        .bezirkUndWahlID(new BezirkUndWahlID(wahlID, wahlbezirkID))
        .build();
  }
}
