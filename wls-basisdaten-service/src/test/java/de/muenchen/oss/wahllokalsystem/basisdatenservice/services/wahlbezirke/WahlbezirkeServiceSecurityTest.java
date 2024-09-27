package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlbezirke;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlbezirk.WahlbezirkRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.common.WahltagRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.WahlRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.MockDataFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
public class WahlbezirkeServiceSecurityTest {

    @Autowired
    WahlbezirkeService wahlbezirkeService;

    @Autowired
    WahlbezirkRepository wahlbezirkRepository;

    @Autowired
    WahltagRepository wahltagRepository;

    @Autowired
    WahlRepository wahlRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Nested
    class GetWahlbezirke {

        @AfterEach
        void tearDown() {
            SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_WAHLBEZIRK, Authorities.REPOSITORY_DELETE_WAHL, Authorities.REPOSITORY_DELETE_WAHLTAG);
            wahlbezirkRepository.deleteAll();
            wahlRepository.deleteAll();
            wahltagRepository.deleteAll();
        }

        @Test
        void accessGranted() throws Exception {
            initRepositoryForSearchingWahlbezirke(true);
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_WAHLBEZIRKE);

            val forWahltagDate = LocalDate.now().minusMonths(2);
            val wahltagID = "_identifikatorWahltag1";
            val wahltagNummer = "nummerWahltag1";

            val eaiWahlbezirke = MockDataFactory.createSetOfClientWahlbezirkDTO(forWahltagDate);
            WireMock.stubFor(WireMock.get("/wahldaten/wahlbezirk?forDate=" + forWahltagDate + "&withNummer=" + wahltagNummer)
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(eaiWahlbezirke))));

            Assertions.assertThatNoException().isThrownBy(() -> wahlbezirkeService.getWahlbezirke(wahltagID));
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariationsRepoEmpty")
        void missingAuthorityCausesFailWithAccessDeniedCaseEmptyRepo(final ArgumentsAccessor argumentsAccessor) throws Exception {
            initRepositoryForSearchingWahlbezirke(false);
            SecurityUtils.runWith(argumentsAccessor.get(0, String[].class));

            val forWahltagDate = LocalDate.now().minusMonths(2);
            val wahltagID = "_identifikatorWahltag1";
            val wahltagNummer = "nummerWahltag1";

            val eaiWahlbezirke = MockDataFactory.createSetOfClientWahlbezirkDTO(forWahltagDate);
            WireMock.stubFor(WireMock.get("/wahldaten/wahlbezirk?forDate=" + forWahltagDate + "&withNummer=" + wahltagNummer)
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(eaiWahlbezirke))));

            Assertions.assertThatException().isThrownBy(() -> wahlbezirkeService.getWahlbezirke(wahltagID))
                    .isInstanceOf(
                            AccessDeniedException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariationsRepoEmpty() {
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_GET_WAHLBEZIRKE);
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariationsRepoHasData")
        void missingAuthorityCausesFailWithAccessDeniedCaseRepoHasData(final ArgumentsAccessor argumentsAccessor) throws Exception {
            initRepositoryForSearchingWahlbezirke(true);
            SecurityUtils.runWith(argumentsAccessor.get(0, String[].class));

            val forWahltagDate = LocalDate.now().minusMonths(2);
            val wahltagID = "_identifikatorWahltag3";
            val wahltagNummer = "nummerWahltag1";

            val eaiWahlbezirke = MockDataFactory.createSetOfClientWahlbezirkDTO(forWahltagDate);
            WireMock.stubFor(WireMock.get("/wahldaten/wahlbezirk?forDate=" + forWahltagDate + "&withNummer=" + wahltagNummer)
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(eaiWahlbezirke))));

            Assertions.assertThatException().isThrownBy(() -> wahlbezirkeService.getWahlbezirke(wahltagID))
                    .isInstanceOf(
                            AccessDeniedException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariationsRepoHasData() {
            List<String> onlyNecessaryAuthorities = new ArrayList<>(Arrays.asList(Authorities.ALL_AUTHORITIES_GET_WAHLBEZIRKE));
            onlyNecessaryAuthorities.remove(Authorities.REPOSITORY_WRITE_WAHLBEZIRK);
            onlyNecessaryAuthorities.remove(Authorities.REPOSITORY_READ_WAHL);
            onlyNecessaryAuthorities.remove(Authorities.SERVICE_GET_WAHLEN);
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(onlyNecessaryAuthorities.toArray(new String[0]));
        }

        private void initRepositoryForSearchingWahlbezirke(boolean hasDataWahlbezirkRepo) {
            SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_WAHLBEZIRK, Authorities.REPOSITORY_WRITE_WAHLTAG, Authorities.REPOSITORY_WRITE_WAHL,
                    Authorities.REPOSITORY_WRITE_WAHLBEZIRK);
            wahlbezirkRepository.deleteAll();
            if (hasDataWahlbezirkRepo) {
                wahlbezirkRepository.saveAll(MockDataFactory.createListOfWahlbezirkEntity("", LocalDate.now().plusMonths(1)));
            }
            val repoWahltage = MockDataFactory.createWahltagList("");
            wahltagRepository.saveAll(repoWahltage);
            val wahlen = MockDataFactory.createWahlEntityList();
            wahlRepository.saveAll(wahlen);
        }
    }
}
