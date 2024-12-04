package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.awerte.AWerteRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.WahlberechtigteDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.LoggerExtension;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles(TestConstants.SPRING_TEST_PROFILE)
@AutoConfigureWireMock
class AWerteServiceSecurityTest {

    @Autowired
    AWerteService aWerteService;

    @Autowired
    AWerteRepository aWerteRepository;

    @Autowired
    ObjectMapper objectMapper;

    @RegisterExtension
    public LoggerExtension loggerExtension = new LoggerExtension();

    @Nested
    class GetAWerte {

        @AfterEach
        void tearDown() {
            SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_AWERTE);
            aWerteRepository.deleteAll();
        }

        @Test
        void should_grantAccessAndThrowNoException_when_AllAuthoritiesUserGetAWerteIsValid() throws Exception {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_USER_GET_AWERTE);

            val wahlbezirkID = "wahlbezirkID";
            val eaiWahlberechtigte = createListOfWahlberechtigteDTO();

            WireMock.stubFor(WireMock.get("/wahldaten/wahlbezirke/" + wahlbezirkID + "/wahlberechtigte")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(eaiWahlberechtigte))));

            Assertions.assertThatNoException().isThrownBy(() -> aWerteService.getAWerte(wahlbezirkID));
            Assertions.assertThat(loggerExtension.getLoggedEventsStream().filter(event -> event.getLevel() == Level.ERROR).count()).isEqualTo(0);
        }

        @Test
        void should_grantAccessAndThrowNoException_when_allAuthoritiesAdminGetAwerteIsValid() throws Exception {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_ADMIN_GET_AWERTE);

            val wahlbezirkID = "wahlbezirkID";
            val eaiWahlberechtigte = createListOfWahlberechtigteDTO();

            WireMock.stubFor(WireMock.get("/wahldaten/wahlbezirke/" + wahlbezirkID + "/wahlberechtigte")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(eaiWahlberechtigte))));

            Assertions.assertThatNoException().isThrownBy(() -> aWerteService.getAWerte(wahlbezirkID));
            Assertions.assertThat(loggerExtension.getLoggedEventsStream().filter(event -> event.getLevel() == Level.ERROR).count()).isEqualTo(0);
        }

        @Test
        void should_logErrorAndThrowNoException_when_repositorySaveAWerteAuthorityIsMissing() throws Exception {
            SecurityUtils.runWith(Authorities.SERVICE_GET_AWERTE);

            val wahlbezirkID = "wahlbezirkID";
            val eaiWahlberechtigte = createListOfWahlberechtigteDTO();

            WireMock.stubFor(WireMock.get("/wahldaten/wahlbezirke/" + wahlbezirkID + "/wahlberechtigte")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(eaiWahlberechtigte))));

            aWerteService.getAWerte(wahlbezirkID);

            Assertions.assertThat(loggerExtension.getLoggedEventsStream().filter(event -> event.getLevel() == Level.ERROR).count()).isEqualTo(1);
        }

        @Test
        void should_failWithAccessDeniedException_when_anyAuthorityIsMissing() throws Exception {
            SecurityUtils.runWith();
            val wahlbezirkID = "wahlbezirkID";
            val eaiWahlberechtigte = createListOfWahlberechtigteDTO();

            WireMock.stubFor(WireMock.get("/wahldaten/wahlbezirke/" + wahlbezirkID + "/wahlberechtigte")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(eaiWahlberechtigte))));

            Assertions.assertThatThrownBy(() -> aWerteService.getAWerte(wahlbezirkID)).isInstanceOf(AccessDeniedException.class);
        }

        private List<WahlberechtigteDTO> createListOfWahlberechtigteDTO() {
            val wb1 = new WahlberechtigteDTO();
            wb1.setWahlbezirkID("wahlbezirkID1");
            wb1.setWahlID("wahlID");
            wb1.setA1(2L);
            wb1.setA2(3L);
            wb1.setA3(5L);
            val wb2 = new WahlberechtigteDTO();
            wb2.setWahlbezirkID("wahlbezirkID2");
            wb2.setWahlID("wahlID");
            wb2.setA1(3L);
            wb2.setA2(4L);
            wb2.setA3(7L);
            val wb3 = new WahlberechtigteDTO();
            wb3.setWahlbezirkID("wahlbezirkID3");
            wb3.setWahlID("wahlID");
            wb3.setA1(4L);
            wb3.setA2(5L);
            wb3.setA3(9L);
            return List.of(wb1, wb2, wb3);
        }
    }

    @Nested
    class InitialiseAWerte {

        @AfterEach
        void tearDown() {
            SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_AWERTE);
            aWerteRepository.deleteAll();
        }

        @Test
        void should_failWithAccessDeniedException_when_adminLoadwahltermindatenAuthorityIsMissing() {
            SecurityUtils.runWith();
            val wahlbezirkID = "wahlbezirkID";
            val wahlbezirkIDList = List.of(wahlbezirkID);

            Assertions.assertThatThrownBy(() -> aWerteService.initialiseAWerte(wahlbezirkIDList)).isInstanceOf(AccessDeniedException.class);
        }

        @Test
        void should_grantAccessAndThrowNoException_when_adminLoadwahltermindatenAuthorityIsValid() throws Exception {
            SecurityUtils.runWith(Authorities.ADMIN_LOADWAHLTERMINDATEN);

            val wahlbezirkID = "wahlbezirkID";
            val wahlbezirkIDList = List.of(wahlbezirkID);
            val eaiWahlberechtigte = getAWerteForWahlbezirkID(wahlbezirkID);

            WireMock.stubFor(WireMock.get("/wahldaten/wahlbezirke/" + wahlbezirkID + "/wahlberechtigte")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(eaiWahlberechtigte))));

            Assertions.assertThatNoException().isThrownBy(() -> aWerteService.initialiseAWerte(wahlbezirkIDList));
            Assertions.assertThat(loggerExtension.getLoggedEventsStream().filter(event -> event.getLevel() == Level.ERROR).count()).isEqualTo(0);
        }

        private List<WahlberechtigteDTO> getAWerteForWahlbezirkID(String wahlbezirkID) {
            val wb1 = new WahlberechtigteDTO();
            wb1.setWahlbezirkID(wahlbezirkID);
            wb1.setWahlID("wahlID");
            wb1.setA1(2L);
            wb1.setA2(3L);
            wb1.setA3(5L);
            return List.of(wb1);
        }
    }

}
