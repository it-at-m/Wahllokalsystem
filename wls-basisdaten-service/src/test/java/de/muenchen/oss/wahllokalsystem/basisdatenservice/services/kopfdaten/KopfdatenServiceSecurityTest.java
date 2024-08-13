package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.kopfdaten.KopfdatenRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.kopfdaten.Stimmzettelgebietsart;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.BasisdatenDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.infomanagement.model.KonfigurierterWahltagDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.MockDataFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.time.LocalDate;
import java.util.stream.Stream;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles(TestConstants.SPRING_TEST_PROFILE)
@AutoConfigureWireMock
public class KopfdatenServiceSecurityTest {

    @Autowired
    KopfdatenService kopfdatenService;

    @Autowired
    KopfdatenRepository kopfdatenRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Nested
    class GetKopfdaten {

        @AfterEach
        void tearDown() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_DELETE_KOPFDATEN);
            kopfdatenRepository.deleteAll();
        }

        @Test
        void accessGranted() throws Exception {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_READ_KOPFDATEN);

            // mock infomanagement konfigurierterWahltag
            KonfigurierterWahltagDTO infomanagementKonfigurierterWahltag = MockDataFactory.createClientKonfigurierterWahltagDTO(LocalDate.now().plusMonths(1));
            WireMock.stubFor(WireMock.get("/businessActions/konfigurierterWahltag")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(infomanagementKonfigurierterWahltag))));

            val wahlID = "wahlID1";
            val wahlbezirkID = "wahlbezirkID1_1";

            BasisdatenDTO eaiBasisdaten = MockDataFactory.createClientBasisdatenDTO(LocalDate.now().plusMonths(1));
            WireMock.stubFor(WireMock.get("/wahldaten/basisdaten?forDate=" + LocalDate.now().plusMonths(1) + "&withNummer=nummerWahltag")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(eaiBasisdaten))));

            Assertions.assertThatNoException().isThrownBy(() -> kopfdatenService.getKopfdaten(new BezirkUndWahlID(wahlID, wahlbezirkID)));
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariationsRepoEmpty")
        void missingAuthorityCausesFailWithAccessDeniedCaseEmptyRepo(final ArgumentsAccessor argumentsAccessor) throws Exception {
            SecurityUtils.runWith(argumentsAccessor.get(0, String[].class));
            emptyTheRepository();
            // mock infomanagement konfigurierterWahltag
            KonfigurierterWahltagDTO infomanagementKonfigurierterWahltag = MockDataFactory.createClientKonfigurierterWahltagDTO(LocalDate.now().plusMonths(1));
            WireMock.stubFor(WireMock.get("/businessActions/konfigurierterWahltag")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(infomanagementKonfigurierterWahltag))));

            val wahlID = "wahlID1";
            val wahlbezirkID = "wahlbezirkID1_1";

            BasisdatenDTO eaiBasisdaten = MockDataFactory.createClientBasisdatenDTO(LocalDate.now().plusMonths(1));
            WireMock.stubFor(WireMock.get("/wahldaten/basisdaten?forDate=" + LocalDate.now().plusMonths(1) + "&withNummer=nummerWahltag")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(eaiBasisdaten))));

            Assertions.assertThatException().isThrownBy(() -> kopfdatenService.getKopfdaten(new BezirkUndWahlID(wahlID, wahlbezirkID)))
                    .isInstanceOf(
                            AccessDeniedException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariationsRepoEmpty() {
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_READ_KOPFDATEN);
        }

        private void emptyTheRepository() {
            SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_KOPFDATEN);
            kopfdatenRepository.deleteAll();
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariationsRepoHasData")
        void missingAuthorityCausesFailWithAccessDeniedCaseRepoHasData(final ArgumentsAccessor argumentsAccessor) throws Exception {
            SecurityUtils.runWith(argumentsAccessor.get(0, String[].class));
            writeDataToRepository();

            // mock infomanagement konfigurierterWahltag
            KonfigurierterWahltagDTO infomanagementKonfigurierterWahltag = MockDataFactory.createClientKonfigurierterWahltagDTO(LocalDate.now().plusMonths(1));
            WireMock.stubFor(WireMock.get("/businessActions/konfigurierterWahltag")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(infomanagementKonfigurierterWahltag))));

            val wahlID = "wahlID1";
            val wahlbezirkID = "wahlbezirkID1_1";

            BasisdatenDTO eaiBasisdaten = MockDataFactory.createClientBasisdatenDTO(LocalDate.now().plusMonths(1));
            WireMock.stubFor(WireMock.get("/wahldaten/basisdaten?forDate=" + LocalDate.now().plusMonths(1) + "&withNummer=nummerWahltag")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(eaiBasisdaten))));

            Assertions.assertThatException().isThrownBy(() -> kopfdatenService.getKopfdaten(new BezirkUndWahlID(wahlID, wahlbezirkID)))
                    .isInstanceOf(
                            AccessDeniedException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariationsRepoHasData() {
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_READ_KOPFDATEN_IF_DATA_EXISTS_IN_REPO);
        }

        private void writeDataToRepository() {
            SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_KOPFDATEN);
            kopfdatenRepository.save(MockDataFactory.createKopfdatenEntityFor("wahlID1", "wahlbezirkID1_1",
                    Stimmzettelgebietsart.SG, "Munich", "120",
                    "Bundestagswahl", "1201"));
        }
    }
}
