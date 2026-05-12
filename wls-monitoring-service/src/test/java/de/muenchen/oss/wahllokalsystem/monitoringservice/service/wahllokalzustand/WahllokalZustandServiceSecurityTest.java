package de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand;

import static org.springframework.security.core.context.SecurityContextHolder.clearContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.muenchen.oss.wahllokalsystem.monitoringservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.monitoringservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.monitoringservice.eai.aou.model.DruckzustandDTO;
import de.muenchen.oss.wahllokalsystem.monitoringservice.eai.aou.model.WahllokalZustandDTO;
import de.muenchen.oss.wahllokalsystem.monitoringservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.security.BezirkIDPermissionEvaluator;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles({TestConstants.SPRING_TEST_PROFILE})
@AutoConfigureWireMock
@DirtiesContext
public class WahllokalZustandServiceSecurityTest {

  @Autowired WahllokalZustandService wahllokalZustandService;

  @Autowired ObjectMapper objectMapper;

  @MockitoBean BezirkIDPermissionEvaluator bezirkIDPermissionEvaluator;

  @BeforeEach
  void setup() {
    clearContext();
  }

  @Nested
  class PostLastSeen {

    @Test
    void should_grantAccessAndThrowNoException_when_authoritiesAreValid() throws Exception {
      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.any(), Mockito.any()))
          .thenReturn(true);
      SecurityUtils.runWith(Authorities.SERVICE_POST_LASTSEEN);
      String wahlbezirkID = "wahlbezirkID01";

      val wahllokalZustandDTO = new WahllokalZustandDTO();
      wahllokalZustandDTO.setWahlbezirkID(wahlbezirkID);
      wahllokalZustandDTO.setZuletztGesehen(LocalDateTime.now());

      WireMock.stubFor(
          WireMock.post("/wahllokalzustand")
              .willReturn(
                  WireMock.aResponse()
                      .withHeader("Content-Type", "application/json")
                      .withStatus(HttpStatus.OK.value())
                      .withBody(objectMapper.writeValueAsBytes(wahllokalZustandDTO))));

      Assertions.assertThatNoException()
          .isThrownBy(() -> wahllokalZustandService.postLastSeen(wahlbezirkID));
    }

    @Test
    void should_failWithAccessDeniedException_when_serviceAuthorityIsMissing() {
      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.any(), Mockito.any()))
          .thenReturn(true);
      SecurityUtils.runWith(Authorities.SERVICE_POST_LAST_LOGOUT);
      String wahlbezirkID = "wahlbezirkID01";

      val wahllokalZustandDTO = new WahllokalZustandDTO();
      wahllokalZustandDTO.setWahlbezirkID(wahlbezirkID);
      wahllokalZustandDTO.setZuletztGesehen(LocalDateTime.now());

      Assertions.assertThatThrownBy(() -> wahllokalZustandService.postLastSeen(wahlbezirkID))
          .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void should_throwException_when_givenAllAuthoritiesButWahlbezirkIDDoesNotMatch() {
      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.any(), Mockito.any()))
          .thenReturn(false);
      SecurityUtils.runWith(Authorities.SERVICE_POST_LASTSEEN);

      val wahlbezirkID = "wahlbezirkID";

      Assertions.assertThatExceptionOfType(AccessDeniedException.class)
          .isThrownBy(() -> wahllokalZustandService.postLastSeen(wahlbezirkID))
          .withMessageStartingWith("Access Denied");
    }
  }

  @Nested
  class PostLetzteAbmeldung {

    @Test
    void should_grantAccessAndThrowNoException_when_authoritiesAreValid() throws Exception {
      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.any(), Mockito.any()))
          .thenReturn(true);
      SecurityUtils.runWith(Authorities.SERVICE_POST_LAST_LOGOUT);
      String wahlbezirkID = "wahlbezirkID01";

      val wahllokalZustandDTO = new WahllokalZustandDTO();
      wahllokalZustandDTO.setWahlbezirkID(wahlbezirkID);
      wahllokalZustandDTO.setLetzteAbmeldung(LocalDateTime.now());

      WireMock.stubFor(
          WireMock.post("/wahllokalzustand")
              .willReturn(
                  WireMock.aResponse()
                      .withHeader("Content-Type", "application/json")
                      .withStatus(HttpStatus.OK.value())
                      .withBody(objectMapper.writeValueAsBytes(wahllokalZustandDTO))));

      Assertions.assertThatNoException()
          .isThrownBy(() -> wahllokalZustandService.postLetzteAbmeldung(wahlbezirkID));
    }

    @Test
    void should_failWithAccessDeniedException_when_serviceAuthorityIsMissing() {
      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.any(), Mockito.any()))
          .thenReturn(true);
      SecurityUtils.runWith(Authorities.SERVICE_POST_LASTSEEN);
      String wahlbezirkID = "wahlbezirkID01";

      val wahllokalZustandDTO = new WahllokalZustandDTO();
      wahllokalZustandDTO.setWahlbezirkID(wahlbezirkID);
      wahllokalZustandDTO.setZuletztGesehen(LocalDateTime.now());

      Assertions.assertThatThrownBy(() -> wahllokalZustandService.postLetzteAbmeldung(wahlbezirkID))
          .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void should_throwException_when_givenAllAuthoritiesButWahlbezirkIDDoesNotMatch() {
      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.any(), Mockito.any()))
          .thenReturn(false);
      SecurityUtils.runWith(Authorities.SERVICE_POST_LAST_LOGOUT);

      val wahlbezirkID = "wahlbezirkID";

      Assertions.assertThatExceptionOfType(AccessDeniedException.class)
          .isThrownBy(() -> wahllokalZustandService.postLetzteAbmeldung(wahlbezirkID))
          .withMessageStartingWith("Access Denied");
    }
  }

  @Nested
  class PostSchnellmeldungSendungsuhrzeit {

    @Test
    void should_grantAccessAndThrowNoException_when_authoritiesAreValid() throws Exception {
      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.any(), Mockito.any()))
          .thenReturn(true);
      SecurityUtils.runWith(Authorities.SERVICE_POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT);

      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";

      val druckZustandDTO = new DruckzustandDTO();
      druckZustandDTO.setWahlID(wahlID);
      druckZustandDTO.setSchnellmeldungSendenUhrzeit(LocalDateTime.now());
      val wahllokalZustandDTO = new WahllokalZustandDTO();
      wahllokalZustandDTO.setWahlbezirkID(wahlbezirkID);
      wahllokalZustandDTO.setDruckzustaende(Set.of(druckZustandDTO));

      WireMock.stubFor(
          WireMock.post("/wahllokalzustand")
              .willReturn(
                  WireMock.aResponse()
                      .withHeader("Content-Type", "application/json")
                      .withStatus(HttpStatus.OK.value())
                      .withBody(objectMapper.writeValueAsBytes(wahllokalZustandDTO))));

      Assertions.assertThatNoException()
          .isThrownBy(
              () ->
                  wahllokalZustandService.postSchnellmeldungSendungsuhrzeit(
                      new BezirkUndWahlID(wahlID, wahlbezirkID), LocalDateTime.now()));
    }

    @Test
    void should_failWithAccessDeniedException_when_serviceAuthorityIsMissing() {
      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.any(), Mockito.any()))
          .thenReturn(true);
      SecurityUtils.runWith(Authorities.SERVICE_POST_LASTSEEN);
      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";

      val druckZustandDTO = new DruckzustandDTO();
      druckZustandDTO.setWahlID(wahlID);
      druckZustandDTO.setSchnellmeldungSendenUhrzeit(LocalDateTime.now());
      val wahllokalZustandDTO = new WahllokalZustandDTO();
      wahllokalZustandDTO.setWahlbezirkID(wahlbezirkID);
      wahllokalZustandDTO.setDruckzustaende(Set.of(druckZustandDTO));

      Assertions.assertThatThrownBy(
              () ->
                  wahllokalZustandService.postSchnellmeldungSendungsuhrzeit(
                      new BezirkUndWahlID(wahlID, wahlbezirkID), LocalDateTime.now()))
          .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void should_throwException_when_givenAllAuthoritiesButWahlbezirkIDDoesNotMatch() {
      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.any(), Mockito.any()))
          .thenReturn(false);
      SecurityUtils.runWith(Authorities.SERVICE_POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT);

      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";

      Assertions.assertThatExceptionOfType(AccessDeniedException.class)
          .isThrownBy(
              () ->
                  wahllokalZustandService.postSchnellmeldungSendungsuhrzeit(
                      new BezirkUndWahlID(wahlID, wahlbezirkID), LocalDateTime.now()))
          .withMessageStartingWith("Access Denied");
    }
  }

  @Nested
  class PostSchnellmeldungDruckuhrzeit {

    @Test
    void should_grantAccessAndThrowNoException_when_authoritiesAreValid() throws Exception {
      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.any(), Mockito.any()))
          .thenReturn(true);
      SecurityUtils.runWith(Authorities.SERVICE_POST_SCHNELLMELDUNG_DRUCKUHRZEIT);

      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";

      val druckZustandDTO = new DruckzustandDTO();
      druckZustandDTO.setWahlID(wahlID);
      druckZustandDTO.setSchnellmeldungDruckUhrzeit(LocalDateTime.now());
      val wahllokalZustandDTO = new WahllokalZustandDTO();
      wahllokalZustandDTO.setWahlbezirkID(wahlbezirkID);
      wahllokalZustandDTO.setDruckzustaende(Set.of(druckZustandDTO));

      WireMock.stubFor(
          WireMock.post("/wahllokalzustand")
              .willReturn(
                  WireMock.aResponse()
                      .withHeader("Content-Type", "application/json")
                      .withStatus(HttpStatus.OK.value())
                      .withBody(objectMapper.writeValueAsBytes(wahllokalZustandDTO))));

      Assertions.assertThatNoException()
          .isThrownBy(
              () ->
                  wahllokalZustandService.postSchnellmeldungDruckuhrzeit(
                      new BezirkUndWahlID(wahlID, wahlbezirkID), LocalDateTime.now()));
    }

    @Test
    void should_failWithAccessDeniedException_when_serviceAuthorityIsMissing() {
      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.any(), Mockito.any()))
          .thenReturn(true);
      SecurityUtils.runWith(Authorities.SERVICE_POST_LASTSEEN);
      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";

      val druckZustandDTO = new DruckzustandDTO();
      druckZustandDTO.setWahlID(wahlID);
      druckZustandDTO.setSchnellmeldungDruckUhrzeit(LocalDateTime.now());
      val wahllokalZustandDTO = new WahllokalZustandDTO();
      wahllokalZustandDTO.setWahlbezirkID(wahlbezirkID);
      wahllokalZustandDTO.setDruckzustaende(Set.of(druckZustandDTO));

      Assertions.assertThatThrownBy(
              () ->
                  wahllokalZustandService.postSchnellmeldungDruckuhrzeit(
                      new BezirkUndWahlID(wahlID, wahlbezirkID), LocalDateTime.now()))
          .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void should_throwException_when_givenAllAuthoritiesButWahlbezirkIDDoesNotMatch() {
      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.any(), Mockito.any()))
          .thenReturn(false);
      SecurityUtils.runWith(Authorities.SERVICE_POST_SCHNELLMELDUNG_DRUCKUHRZEIT);

      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";

      Assertions.assertThatExceptionOfType(AccessDeniedException.class)
          .isThrownBy(
              () ->
                  wahllokalZustandService.postSchnellmeldungDruckuhrzeit(
                      new BezirkUndWahlID(wahlID, wahlbezirkID), LocalDateTime.now()))
          .withMessageStartingWith("Access Denied");
    }
  }

  @Nested
  class PostNiederschriftSendungsuhrzeit {

    @Test
    void should_grantAccessAndThrowNoException_when_authoritiesAreValid() throws Exception {
      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.any(), Mockito.any()))
          .thenReturn(true);
      SecurityUtils.runWith(Authorities.SERVICE_POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT);

      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";

      val druckZustandDTO = new DruckzustandDTO();
      druckZustandDTO.setWahlID(wahlID);
      druckZustandDTO.setNiederschriftSendenUhrzeit(LocalDateTime.now());
      val wahllokalZustandDTO = new WahllokalZustandDTO();
      wahllokalZustandDTO.setWahlbezirkID(wahlbezirkID);
      wahllokalZustandDTO.setDruckzustaende(Set.of(druckZustandDTO));

      WireMock.stubFor(
          WireMock.post("/wahllokalzustand")
              .willReturn(
                  WireMock.aResponse()
                      .withHeader("Content-Type", "application/json")
                      .withStatus(HttpStatus.OK.value())
                      .withBody(objectMapper.writeValueAsBytes(wahllokalZustandDTO))));

      WireMock.stubFor(
          WireMock.post("/wahllokalzustand")
              .willReturn(
                  WireMock.aResponse()
                      .withHeader("Content-Type", "application/json")
                      .withStatus(HttpStatus.OK.value())
                      .withBody(objectMapper.writeValueAsBytes(wahllokalZustandDTO))));

      Assertions.assertThatNoException()
          .isThrownBy(
              () ->
                  wahllokalZustandService.postNiederschriftSendungsuhrzeit(
                      new BezirkUndWahlID(wahlID, wahlbezirkID), LocalDateTime.now()));
    }

    @Test
    void should_failWithAccessDeniedException_when_serviceAuthorityIsMissing() {
      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.any(), Mockito.any()))
          .thenReturn(true);
      SecurityUtils.runWith(Authorities.SERVICE_POST_LASTSEEN);

      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";

      val druckZustandDTO = new DruckzustandDTO();
      druckZustandDTO.setWahlID(wahlID);
      druckZustandDTO.setNiederschriftSendenUhrzeit(LocalDateTime.now());
      val wahllokalZustandDTO = new WahllokalZustandDTO();
      wahllokalZustandDTO.setWahlbezirkID(wahlbezirkID);
      wahllokalZustandDTO.setDruckzustaende(Set.of(druckZustandDTO));

      Assertions.assertThatThrownBy(
              () ->
                  wahllokalZustandService.postNiederschriftSendungsuhrzeit(
                      new BezirkUndWahlID(wahlID, wahlbezirkID), LocalDateTime.now()))
          .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void should_throwException_when_givenAllAuthoritiesButWahlbezirkIDDoesNotMatch() {
      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.any(), Mockito.any()))
          .thenReturn(false);
      SecurityUtils.runWith(Authorities.SERVICE_POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT);

      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";

      Assertions.assertThatExceptionOfType(AccessDeniedException.class)
          .isThrownBy(
              () ->
                  wahllokalZustandService.postNiederschriftSendungsuhrzeit(
                      new BezirkUndWahlID(wahlID, wahlbezirkID), LocalDateTime.now()))
          .withMessageStartingWith("Access Denied");
    }
  }

  @Nested
  class PostNiederschriftDruckuhrzeit {

    @Test
    void should_grantAccessAndThrowNoException_when_authoritiesAreValid() throws Exception {
      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.any(), Mockito.any()))
          .thenReturn(true);
      SecurityUtils.runWith(Authorities.SERVICE_POST_NIEDERSCHRIFT_DRUCKUHRZEIT);

      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";

      val druckZustandDTO = new DruckzustandDTO();
      druckZustandDTO.setWahlID(wahlID);
      druckZustandDTO.setNiederschriftDruckUhrzeit(LocalDateTime.now());
      val wahllokalZustandDTO = new WahllokalZustandDTO();
      wahllokalZustandDTO.setWahlbezirkID(wahlbezirkID);
      wahllokalZustandDTO.setDruckzustaende(Set.of(druckZustandDTO));

      WireMock.stubFor(
          WireMock.post("/wahllokalzustand")
              .willReturn(
                  WireMock.aResponse()
                      .withHeader("Content-Type", "application/json")
                      .withStatus(HttpStatus.OK.value())
                      .withBody(objectMapper.writeValueAsBytes(wahllokalZustandDTO))));

      Assertions.assertThatNoException()
          .isThrownBy(
              () ->
                  wahllokalZustandService.postNiederschriftDruckuhrzeit(
                      new BezirkUndWahlID(wahlID, wahlbezirkID), LocalDateTime.now()));
    }

    @Test
    void should_failWithAccessDeniedException_when_serviceAuthorityIsMissing() {
      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.any(), Mockito.any()))
          .thenReturn(true);
      SecurityUtils.runWith(Authorities.SERVICE_POST_LASTSEEN);

      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";

      val druckZustandDTO = new DruckzustandDTO();
      druckZustandDTO.setWahlID(wahlID);
      druckZustandDTO.setNiederschriftDruckUhrzeit(LocalDateTime.now());
      val wahllokalZustandDTO = new WahllokalZustandDTO();
      wahllokalZustandDTO.setWahlbezirkID(wahlbezirkID);
      wahllokalZustandDTO.setDruckzustaende(Set.of(druckZustandDTO));

      Assertions.assertThatThrownBy(
              () ->
                  wahllokalZustandService.postNiederschriftDruckuhrzeit(
                      new BezirkUndWahlID(wahlID, wahlbezirkID), LocalDateTime.now()))
          .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void should_throwException_when_givenAllAuthoritiesButWahlbezirkIDDoesNotMatch() {
      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.any(), Mockito.any()))
          .thenReturn(false);
      SecurityUtils.runWith(Authorities.SERVICE_POST_NIEDERSCHRIFT_DRUCKUHRZEIT);

      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";

      Assertions.assertThatExceptionOfType(AccessDeniedException.class)
          .isThrownBy(
              () ->
                  wahllokalZustandService.postNiederschriftDruckuhrzeit(
                      new BezirkUndWahlID(wahlID, wahlbezirkID), LocalDateTime.now()))
          .withMessageStartingWith("Access Denied");
    }
  }
}
