package de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand;

import static org.springframework.security.core.context.SecurityContextHolder.clearContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.muenchen.oss.wahllokalsystem.monitoringservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.monitoringservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.monitoringservice.client.wahllokalzustand.WahllokalZustandClientMapper;
import de.muenchen.oss.wahllokalsystem.monitoringservice.rest.wahllokalzustand.DruckdatenDTOMapper;
import de.muenchen.oss.wahllokalsystem.monitoringservice.rest.wahllokalzustand.SendungsdatenDTOMapper;
import de.muenchen.oss.wahllokalsystem.monitoringservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.util.Set;
import lombok.val;
import java.time.LocalDateTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles({ TestConstants.SPRING_TEST_PROFILE })
@AutoConfigureWireMock
public class WahllokalZustandServiceSecurityTest {

    @Autowired
    WahllokalZustandService wahllokalZustandService;

    @Autowired
    WahllokalZustandClientMapper wahllokalZustandClientMapper;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        clearContext();
    }

    @Nested
    class PostLastSeen {

        @Test
        void should_grantAccessAndThrowNoException_when_authoritiesAreValid() throws Exception {
            SecurityUtils.runWith(Authorities.SERVICE_POST_LASTSEEN);
            String wahlbezirkID = "wahlbezirkID01";

           val wahllokalZustandToSave = new WahllokalZustandModel(wahlbezirkID, LocalDateTime.now(), null, null);
           val wahllokalZustandDTO = wahllokalZustandClientMapper.toDTO(wahllokalZustandToSave);

            WireMock.stubFor(WireMock.post("/wahllokalzustand")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(wahllokalZustandDTO))));

            Assertions.assertThatNoException().isThrownBy(() -> wahllokalZustandService.postLastSeen(wahlbezirkID));
        }

        @Test
        void should_failWithAccessDeniedException_when_serviceAuthorityIsMissing() throws Exception {
            SecurityUtils.runWith(Authorities.SERVICE_POST_LAST_LOGOUT);
            String wahlbezirkID = "wahlbezirkID01";
            val wahllokalZustandToSave = new WahllokalZustandModel(wahlbezirkID, LocalDateTime.now(), null, null);
            val wahllokalZustandDTO = wahllokalZustandClientMapper.toDTO(wahllokalZustandToSave);

            WireMock.stubFor(WireMock.post("/wahllokalzustand")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(wahllokalZustandDTO))));

            Assertions.assertThatThrownBy(() -> wahllokalZustandService.postLastSeen(wahlbezirkID)).isInstanceOf(AccessDeniedException.class);
        }
    }

    @Nested
    class PostLetzteAbmeldung {

        @Test
        void should_grantAccessAndThrowNoException_when_authoritiesAreValid() throws Exception {
            SecurityUtils.runWith(Authorities.SERVICE_POST_LAST_LOGOUT);
            String wahlbezirkID = "wahlbezirkID01";

            val wahllokalZustandToSave = new WahllokalZustandModel(wahlbezirkID, null, LocalDateTime.now(), null);
            val wahllokalZustandDTO = wahllokalZustandClientMapper.toDTO(wahllokalZustandToSave);

            WireMock.stubFor(WireMock.post("/wahllokalzustand")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(wahllokalZustandDTO))));

            Assertions.assertThatNoException().isThrownBy(() -> wahllokalZustandService.postLetzteAbmeldung(wahlbezirkID));
        }

        @Test
        void should_failWithAccessDeniedException_when_serviceAuthorityIsMissing() throws Exception {
            SecurityUtils.runWith(Authorities.SERVICE_POST_LASTSEEN);
            String wahlbezirkID = "wahlbezirkID01";
            val wahllokalZustandToSave = new WahllokalZustandModel(wahlbezirkID, null, LocalDateTime.now(), null);
            val wahllokalZustandDTO = wahllokalZustandClientMapper.toDTO(wahllokalZustandToSave);

            WireMock.stubFor(WireMock.post("/wahllokalzustand")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(wahllokalZustandDTO))));

            Assertions.assertThatThrownBy(() -> wahllokalZustandService.postLetzteAbmeldung(wahlbezirkID)).isInstanceOf(AccessDeniedException.class);
        }
    }

    @Nested
    class PostSchnellmeldungSendungsuhrzeit {

        @Test
        void should_grantAccessAndThrowNoException_when_authoritiesAreValid() throws Exception {
            SecurityUtils.runWith(Authorities.SERVICE_POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT);

            val sendungsdatenModel = SendungsdatenModel.builder().bezirkUndWahlID(new BezirkUndWahlID("wahlID", "wahlbezirkID")).sendungsuhrzeit(LocalDateTime.now()).build();

            val wahllokalZustandToSave = new WahllokalZustandModel(
                    sendungsdatenModel.bezirkUndWahlID().getWahlbezirkID(),
                    null,
                    null,
                    Set.of(
                            DruckzustandModel.builder()
                                    .wahlID(sendungsdatenModel.bezirkUndWahlID().getWahlID())
                                    .schnellmeldungSendenUhrzeit(sendungsdatenModel.sendungsuhrzeit())
                                    .build()
                    ));
            val wahllokalZustandDTO = wahllokalZustandClientMapper.toDTO(wahllokalZustandToSave);

            WireMock.stubFor(WireMock.post("/wahllokalzustand")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(wahllokalZustandDTO))));

            Assertions.assertThatNoException().isThrownBy(() -> wahllokalZustandService.postSchnellmeldungSendungsuhrzeit(sendungsdatenModel));
        }

        @Test
        void should_failWithAccessDeniedException_when_serviceAuthorityIsMissing() throws Exception {
            SecurityUtils.runWith(Authorities.SERVICE_POST_LASTSEEN);
            val sendungsdatenModel = SendungsdatenModel.builder().bezirkUndWahlID(new BezirkUndWahlID("wahlID", "wahlbezirkID")).sendungsuhrzeit(LocalDateTime.now()).build();

            val wahllokalZustandToSave = new WahllokalZustandModel(
                    sendungsdatenModel.bezirkUndWahlID().getWahlbezirkID(),
                    null,
                    null,
                    Set.of(
                            DruckzustandModel.builder()
                                    .wahlID(sendungsdatenModel.bezirkUndWahlID().getWahlID())
                                    .schnellmeldungSendenUhrzeit(sendungsdatenModel.sendungsuhrzeit())
                                    .build()
                    ));
            val wahllokalZustandDTO = wahllokalZustandClientMapper.toDTO(wahllokalZustandToSave);

            WireMock.stubFor(WireMock.post("/wahllokalzustand")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(wahllokalZustandDTO))));

            Assertions.assertThatThrownBy(() -> wahllokalZustandService.postSchnellmeldungSendungsuhrzeit(sendungsdatenModel)).isInstanceOf(AccessDeniedException.class);
        }
    }

    @Nested
    class PostSchnellmeldungDruckuhrzeit {

        @Test
        void should_grantAccessAndThrowNoException_when_authoritiesAreValid() throws Exception {
            SecurityUtils.runWith(Authorities.SERVICE_POST_SCHNELLMELDUNG_DRUCKUHRZEIT);

            val druckdatenModel = DruckdatenModel.builder().bezirkUndWahlID(new BezirkUndWahlID("wahlID", "wahlbezirkID")).druckuhrzeit(LocalDateTime.now()).build();

            val wahllokalZustandToSave = new WahllokalZustandModel(
                    druckdatenModel.bezirkUndWahlID().getWahlbezirkID(),
                    null,
                    null,
                    Set.of(
                            DruckzustandModel.builder()
                                    .wahlID(druckdatenModel.bezirkUndWahlID().getWahlID())
                                    .schnellmeldungDruckUhrzeit(druckdatenModel.druckuhrzeit())
                                    .build()
                    ));
            val wahllokalZustandDTO = wahllokalZustandClientMapper.toDTO(wahllokalZustandToSave);

            WireMock.stubFor(WireMock.post("/wahllokalzustand")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(wahllokalZustandDTO))));

            Assertions.assertThatNoException().isThrownBy(() -> wahllokalZustandService.postSchnellmeldungDruckuhrzeit(druckdatenModel));
        }

        @Test
        void should_failWithAccessDeniedException_when_serviceAuthorityIsMissing() throws Exception {
            SecurityUtils.runWith(Authorities.SERVICE_POST_LASTSEEN);
            val druckdatenModel = DruckdatenModel.builder().bezirkUndWahlID(new BezirkUndWahlID("wahlID", "wahlbezirkID")).druckuhrzeit(LocalDateTime.now()).build();

            val wahllokalZustandToSave = new WahllokalZustandModel(
                    druckdatenModel.bezirkUndWahlID().getWahlbezirkID(),
                    null,
                    null,
                    Set.of(
                            DruckzustandModel.builder()
                                    .wahlID(druckdatenModel.bezirkUndWahlID().getWahlID())
                                    .schnellmeldungDruckUhrzeit(druckdatenModel.druckuhrzeit())
                                    .build()
                    ));
            val wahllokalZustandDTO = wahllokalZustandClientMapper.toDTO(wahllokalZustandToSave);

            WireMock.stubFor(WireMock.post("/wahllokalzustand")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(wahllokalZustandDTO))));

            Assertions.assertThatThrownBy(() -> wahllokalZustandService.postSchnellmeldungDruckuhrzeit(druckdatenModel)).isInstanceOf(AccessDeniedException.class);
        }
    }

    @Nested
    class PostNiederschriftSendungsuhrzeit {

        @Test
        void should_grantAccessAndThrowNoException_when_authoritiesAreValid() throws Exception {
            SecurityUtils.runWith(Authorities.SERVICE_POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT);

            val sendungsdatenModel = SendungsdatenModel.builder().bezirkUndWahlID(new BezirkUndWahlID("wahlID", "wahlbezirkID")).sendungsuhrzeit(LocalDateTime.now()).build();

            val wahllokalZustandToSave = new WahllokalZustandModel(
                    sendungsdatenModel.bezirkUndWahlID().getWahlbezirkID(),
                    null,
                    null,
                    Set.of(
                            DruckzustandModel.builder()
                                    .wahlID(sendungsdatenModel.bezirkUndWahlID().getWahlID())
                                    .niederschriftSendenUhrzeit(sendungsdatenModel.sendungsuhrzeit())
                                    .build()
                    ));
            val wahllokalZustandDTO = wahllokalZustandClientMapper.toDTO(wahllokalZustandToSave);

            WireMock.stubFor(WireMock.post("/wahllokalzustand")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(wahllokalZustandDTO))));

            Assertions.assertThatNoException().isThrownBy(() -> wahllokalZustandService.postNiederschriftSendungsuhrzeit(sendungsdatenModel));
        }

        @Test
        void should_failWithAccessDeniedException_when_serviceAuthorityIsMissing() throws Exception {
            SecurityUtils.runWith(Authorities.SERVICE_POST_LASTSEEN);
            val sendungsdatenModel = SendungsdatenModel.builder().bezirkUndWahlID(new BezirkUndWahlID("wahlID", "wahlbezirkID")).sendungsuhrzeit(LocalDateTime.now()).build();

            val wahllokalZustandToSave = new WahllokalZustandModel(
                    sendungsdatenModel.bezirkUndWahlID().getWahlbezirkID(),
                    null,
                    null,
                    Set.of(
                            DruckzustandModel.builder()
                                    .wahlID(sendungsdatenModel.bezirkUndWahlID().getWahlID())
                                    .niederschriftSendenUhrzeit(sendungsdatenModel.sendungsuhrzeit())
                                    .build()
                    ));
            val wahllokalZustandDTO = wahllokalZustandClientMapper.toDTO(wahllokalZustandToSave);

            WireMock.stubFor(WireMock.post("/wahllokalzustand")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(wahllokalZustandDTO))));

            Assertions.assertThatThrownBy(() -> wahllokalZustandService.postNiederschriftSendungsuhrzeit(sendungsdatenModel)).isInstanceOf(AccessDeniedException.class);
        }
    }

    @Nested
    class PostNiederschriftDruckuhrzeit {

        @Test
        void should_grantAccessAndThrowNoException_when_authoritiesAreValid() throws Exception {
            SecurityUtils.runWith(Authorities.SERVICE_POST_NIEDERSCHRIFT_DRUCKUHRZEIT);

            val druckdatenModel = DruckdatenModel.builder().bezirkUndWahlID(new BezirkUndWahlID("wahlID", "wahlbezirkID")).druckuhrzeit(LocalDateTime.now()).build();

            val wahllokalZustandToSave = new WahllokalZustandModel(
                    druckdatenModel.bezirkUndWahlID().getWahlbezirkID(),
                    null,
                    null,
                    Set.of(
                            DruckzustandModel.builder()
                                    .wahlID(druckdatenModel.bezirkUndWahlID().getWahlID())
                                    .niederschriftDruckUhrzeit(druckdatenModel.druckuhrzeit())
                                    .build()
                    ));
            val wahllokalZustandDTO = wahllokalZustandClientMapper.toDTO(wahllokalZustandToSave);

            WireMock.stubFor(WireMock.post("/wahllokalzustand")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(wahllokalZustandDTO))));

            Assertions.assertThatNoException().isThrownBy(() -> wahllokalZustandService.postNiederschriftDruckuhrzeit(druckdatenModel));
        }

        @Test
        void should_failWithAccessDeniedException_when_serviceAuthorityIsMissing() throws Exception {
            SecurityUtils.runWith(Authorities.SERVICE_POST_LASTSEEN);
            val druckdatenModel = DruckdatenModel.builder().bezirkUndWahlID(new BezirkUndWahlID("wahlID", "wahlbezirkID")).druckuhrzeit(LocalDateTime.now()).build();

            val wahllokalZustandToSave = new WahllokalZustandModel(
                    druckdatenModel.bezirkUndWahlID().getWahlbezirkID(),
                    null,
                    null,
                    Set.of(
                            DruckzustandModel.builder()
                                    .wahlID(druckdatenModel.bezirkUndWahlID().getWahlID())
                                    .schnellmeldungSendenUhrzeit(druckdatenModel.druckuhrzeit())
                                    .build()
                    ));
            val wahllokalZustandDTO = wahllokalZustandClientMapper.toDTO(wahllokalZustandToSave);

            WireMock.stubFor(WireMock.post("/wahllokalzustand")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(wahllokalZustandDTO))));

            Assertions.assertThatThrownBy(() -> wahllokalZustandService.postSchnellmeldungDruckuhrzeit(druckdatenModel)).isInstanceOf(AccessDeniedException.class);
        }
    }



}
