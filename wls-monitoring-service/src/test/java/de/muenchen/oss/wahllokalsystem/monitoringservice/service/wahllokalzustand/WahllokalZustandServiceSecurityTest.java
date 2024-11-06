package de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand;

import static org.springframework.security.core.context.SecurityContextHolder.clearContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.muenchen.oss.wahllokalsystem.monitoringservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.monitoringservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.monitoringservice.eai.aou.model.DruckzustandDTO;
import de.muenchen.oss.wahllokalsystem.monitoringservice.eai.aou.model.WahllokalZustandDTO;
import de.muenchen.oss.wahllokalsystem.monitoringservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.time.OffsetDateTime;
import java.util.Set;
import lombok.val;
import java.time.LocalDateTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

            val wahllokalZustandDTO = new WahllokalZustandDTO();
            wahllokalZustandDTO.setWahlbezirkID(wahlbezirkID);
            wahllokalZustandDTO.setZuletztGesehen(OffsetDateTime.now());

            WireMock.stubFor(WireMock.post("/wahllokalzustand")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(wahllokalZustandDTO))));

            Assertions.assertThatNoException().isThrownBy(() -> wahllokalZustandService.postLastSeen(wahlbezirkID));
        }

        @Test
        void should_failWithAccessDeniedException_when_serviceAuthorityIsMissing() {
            SecurityUtils.runWith(Authorities.SERVICE_POST_LAST_LOGOUT);
            String wahlbezirkID = "wahlbezirkID01";

            val wahllokalZustandDTO = new WahllokalZustandDTO();
            wahllokalZustandDTO.setWahlbezirkID(wahlbezirkID);
            wahllokalZustandDTO.setZuletztGesehen(OffsetDateTime.now());

            Assertions.assertThatThrownBy(() -> wahllokalZustandService.postLastSeen(wahlbezirkID)).isInstanceOf(AccessDeniedException.class);
        }
    }

    @Nested
    class PostLetzteAbmeldung {

        @Test
        void should_grantAccessAndThrowNoException_when_authoritiesAreValid() throws Exception {
            SecurityUtils.runWith(Authorities.SERVICE_POST_LAST_LOGOUT);
            String wahlbezirkID = "wahlbezirkID01";

            val wahllokalZustandDTO = new WahllokalZustandDTO();
            wahllokalZustandDTO.setWahlbezirkID(wahlbezirkID);
            wahllokalZustandDTO.setLetzteAbmeldung(OffsetDateTime.now());

            WireMock.stubFor(WireMock.post("/wahllokalzustand")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(wahllokalZustandDTO))));

            Assertions.assertThatNoException().isThrownBy(() -> wahllokalZustandService.postLetzteAbmeldung(wahlbezirkID));
        }

        @Test
        void should_failWithAccessDeniedException_when_serviceAuthorityIsMissing() {
            SecurityUtils.runWith(Authorities.SERVICE_POST_LASTSEEN);
            String wahlbezirkID = "wahlbezirkID01";

            val wahllokalZustandDTO = new WahllokalZustandDTO();
            wahllokalZustandDTO.setWahlbezirkID(wahlbezirkID);
            wahllokalZustandDTO.setZuletztGesehen(OffsetDateTime.now());

            Assertions.assertThatThrownBy(() -> wahllokalZustandService.postLetzteAbmeldung(wahlbezirkID)).isInstanceOf(AccessDeniedException.class);
        }
    }

    @Nested
    class PostSchnellmeldungSendungsuhrzeit {

        @Test
        void should_grantAccessAndThrowNoException_when_authoritiesAreValid() throws Exception {
            SecurityUtils.runWith(Authorities.SERVICE_POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT);

            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";

            val druckZustandDTO = new DruckzustandDTO();
            druckZustandDTO.setWahlID(wahlID);
            druckZustandDTO.setSchnellmeldungSendenUhrzeit(OffsetDateTime.now());
            val wahllokalZustandDTO = new WahllokalZustandDTO();
            wahllokalZustandDTO.setWahlbezirkID(wahlbezirkID);
            wahllokalZustandDTO.setDruckzustaende(Set.of(druckZustandDTO));

            WireMock.stubFor(WireMock.post("/wahllokalzustand")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(wahllokalZustandDTO))));

            Assertions.assertThatNoException().isThrownBy(
                    () -> wahllokalZustandService.postSchnellmeldungSendungsuhrzeit(new BezirkUndWahlID(wahlID, wahlbezirkID), LocalDateTime.now()));
        }

        @Test
        void should_failWithAccessDeniedException_when_serviceAuthorityIsMissing() {
            SecurityUtils.runWith(Authorities.SERVICE_POST_LASTSEEN);
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";

            val druckZustandDTO = new DruckzustandDTO();
            druckZustandDTO.setWahlID(wahlID);
            druckZustandDTO.setSchnellmeldungSendenUhrzeit(OffsetDateTime.now());
            val wahllokalZustandDTO = new WahllokalZustandDTO();
            wahllokalZustandDTO.setWahlbezirkID(wahlbezirkID);
            wahllokalZustandDTO.setDruckzustaende(Set.of(druckZustandDTO));

            Assertions
                    .assertThatThrownBy(
                            () -> wahllokalZustandService.postSchnellmeldungSendungsuhrzeit(new BezirkUndWahlID(wahlID, wahlbezirkID), LocalDateTime.now()))
                    .isInstanceOf(AccessDeniedException.class);
        }
    }

    @Nested
    class PostSchnellmeldungDruckuhrzeit {
    
        @Test
        void should_grantAccessAndThrowNoException_when_authoritiesAreValid() throws Exception {
            SecurityUtils.runWith(Authorities.SERVICE_POST_SCHNELLMELDUNG_DRUCKUHRZEIT);

            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";

            val druckZustandDTO = new DruckzustandDTO();
            druckZustandDTO.setWahlID(wahlID);
            druckZustandDTO.setSchnellmeldungDruckUhrzeit(OffsetDateTime.now());
            val wahllokalZustandDTO = new WahllokalZustandDTO();
            wahllokalZustandDTO.setWahlbezirkID(wahlbezirkID);
            wahllokalZustandDTO.setDruckzustaende(Set.of(druckZustandDTO));

            WireMock.stubFor(WireMock.post("/wahllokalzustand")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(wahllokalZustandDTO))));

            Assertions.assertThatNoException()
                    .isThrownBy(() -> wahllokalZustandService.postSchnellmeldungDruckuhrzeit(new BezirkUndWahlID(wahlID, wahlbezirkID), LocalDateTime.now()));
        }

        @Test
        void should_failWithAccessDeniedException_when_serviceAuthorityIsMissing() {
            SecurityUtils.runWith(Authorities.SERVICE_POST_LASTSEEN);
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";

            val druckZustandDTO = new DruckzustandDTO();
            druckZustandDTO.setWahlID(wahlID);
            druckZustandDTO.setSchnellmeldungDruckUhrzeit(OffsetDateTime.now());
            val wahllokalZustandDTO = new WahllokalZustandDTO();
            wahllokalZustandDTO.setWahlbezirkID(wahlbezirkID);
            wahllokalZustandDTO.setDruckzustaende(Set.of(druckZustandDTO));

            Assertions
                    .assertThatThrownBy(
                            () -> wahllokalZustandService.postSchnellmeldungDruckuhrzeit(new BezirkUndWahlID(wahlID, wahlbezirkID), LocalDateTime.now()))
                    .isInstanceOf(AccessDeniedException.class);
        }
    }

    @Nested
    class PostNiederschriftSendungsuhrzeit {

        @Test
        void should_grantAccessAndThrowNoException_when_authoritiesAreValid() throws Exception {
            SecurityUtils.runWith(Authorities.SERVICE_POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT);

            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";

            val druckZustandDTO = new DruckzustandDTO();
            druckZustandDTO.setWahlID(wahlID);
            druckZustandDTO.setNiederschriftSendenUhrzeit(OffsetDateTime.now());
            val wahllokalZustandDTO = new WahllokalZustandDTO();
            wahllokalZustandDTO.setWahlbezirkID(wahlbezirkID);
            wahllokalZustandDTO.setDruckzustaende(Set.of(druckZustandDTO));

            WireMock.stubFor(WireMock.post("/wahllokalzustand")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(wahllokalZustandDTO))));

            WireMock.stubFor(WireMock.post("/wahllokalzustand")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(wahllokalZustandDTO))));

            Assertions.assertThatNoException()
                    .isThrownBy(() -> wahllokalZustandService.postNiederschriftSendungsuhrzeit(new BezirkUndWahlID(wahlID, wahlbezirkID), LocalDateTime.now()));
        }

        @Test
        void should_failWithAccessDeniedException_when_serviceAuthorityIsMissing() {
            SecurityUtils.runWith(Authorities.SERVICE_POST_LASTSEEN);

            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";

            val druckZustandDTO = new DruckzustandDTO();
            druckZustandDTO.setWahlID(wahlID);
            druckZustandDTO.setNiederschriftSendenUhrzeit(OffsetDateTime.now());
            val wahllokalZustandDTO = new WahllokalZustandDTO();
            wahllokalZustandDTO.setWahlbezirkID(wahlbezirkID);
            wahllokalZustandDTO.setDruckzustaende(Set.of(druckZustandDTO));

            Assertions
                    .assertThatThrownBy(
                            () -> wahllokalZustandService.postNiederschriftSendungsuhrzeit(new BezirkUndWahlID(wahlID, wahlbezirkID), LocalDateTime.now()))
                    .isInstanceOf(AccessDeniedException.class);
        }
    }

    @Nested
    class PostNiederschriftDruckuhrzeit {

        @Test
        void should_grantAccessAndThrowNoException_when_authoritiesAreValid() throws Exception {
            SecurityUtils.runWith(Authorities.SERVICE_POST_NIEDERSCHRIFT_DRUCKUHRZEIT);

            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";

            val druckZustandDTO = new DruckzustandDTO();
            druckZustandDTO.setWahlID(wahlID);
            druckZustandDTO.setNiederschriftDruckUhrzeit(OffsetDateTime.now());
            val wahllokalZustandDTO = new WahllokalZustandDTO();
            wahllokalZustandDTO.setWahlbezirkID(wahlbezirkID);
            wahllokalZustandDTO.setDruckzustaende(Set.of(druckZustandDTO));

            WireMock.stubFor(WireMock.post("/wahllokalzustand")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(wahllokalZustandDTO))));

            Assertions.assertThatNoException()
                    .isThrownBy(() -> wahllokalZustandService.postNiederschriftDruckuhrzeit(new BezirkUndWahlID(wahlID, wahlbezirkID), LocalDateTime.now()));
        }

        @Test
        void should_failWithAccessDeniedException_when_serviceAuthorityIsMissing() {
            SecurityUtils.runWith(Authorities.SERVICE_POST_LASTSEEN);

            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";

            val druckZustandDTO = new DruckzustandDTO();
            druckZustandDTO.setWahlID(wahlID);
            druckZustandDTO.setNiederschriftDruckUhrzeit(OffsetDateTime.now());
            val wahllokalZustandDTO = new WahllokalZustandDTO();
            wahllokalZustandDTO.setWahlbezirkID(wahlbezirkID);
            wahllokalZustandDTO.setDruckzustaende(Set.of(druckZustandDTO));

            Assertions
                    .assertThatThrownBy(
                            () -> wahllokalZustandService.postSchnellmeldungDruckuhrzeit(new BezirkUndWahlID(wahlID, wahlbezirkID), LocalDateTime.now()))
                    .isInstanceOf(AccessDeniedException.class);
        }
    }

}
