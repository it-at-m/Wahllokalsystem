package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltermindaten;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.kopfdaten.KopfdatenRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen.ReferendumvorlagenRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlbezirke.WahlbezirkRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlen.WahlRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahltag.WahltagRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlvorschlag.WahlvorschlaegeRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.BasisdatenDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.BasisstrukturdatenDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.KandidatDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.ReferendumoptionDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.ReferendumvorlageDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.ReferendumvorlagenDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.StimmzettelgebietDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlbezirkDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahltagDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlvorschlaegeDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlvorschlagDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = { MicroServiceApplication.class, WahltermindatenServiceSecurityTest.TestConfiguration.class })
@ActiveProfiles({ TestConstants.SPRING_TEST_PROFILE })
@AutoConfigureWireMock
public class WahltermindatenServiceSecurityTest {

    @Configuration
    static class TestConfiguration {

        @Bean
        @Primary
        public SyncTaskExecutor syncTaskExecutor() {
            return new SyncTaskExecutor();
        }
    }

    @Autowired
    WahltermindatenService unitUnderTest;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WahlvorschlaegeRepository wahlvorschlaegeRepository;

    @Autowired
    ReferendumvorlagenRepository referendumvorlagenRepository;

    @Autowired
    KopfdatenRepository kopfdatenRepository;

    @Autowired
    WahltagRepository wahltagRepository;

    @Autowired
    WahlRepository wahlRepository;

    @Autowired
    WahlbezirkRepository wahlbezirkRepository;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void teardown() {
        SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_WAHLTAG, Authorities.REPOSITORY_DELETE_WAHL, Authorities.REPOSITORY_DELETE_KOPFDATEN,
                Authorities.REPOSITORY_DELETE_WAHLBEZIRK,
                Authorities.REPOSITORY_DELETE_WAHLVORSCHLAEGE,
                Authorities.REPOSITORY_DELETE_REFERENDUMVORLAGEN);
        wahlvorschlaegeRepository.deleteAll();
        referendumvorlagenRepository.deleteAll();
        kopfdatenRepository.deleteAll();
        wahltagRepository.deleteAll();
        wahlRepository.deleteAll();
        wahlbezirkRepository.deleteAll();
    }

    @Nested
    class PutWahltermindaten {

        @Test
        void should_throwNoException_when_allRequiredAuthoritiesArePresent() throws Exception {
            SecurityUtils.runWith(ArrayUtils.addAll(Authorities.ALL_AUTHORITIES_PUT_WAHLTERMINDATEN_THAT_GOT_CATCHED_ON_MISSING,
                    Authorities.ALL_AUTHORITIES_PUT_WAHLTERMINDATEN_THAT_GOT_NOT_CATCHED_ON_MISSING));

            val wahltagID = "wahltagID";
            val stimmzettelgebietID = "sgzID";

            //WireMock für getWahltag
            setupWireMockForWahltagClient(wahltagID);
            val wahlbezirkID = "wahlbezirkID";
            //WireMock für getBasisdaten
            final String wahlID = "wahlID";
            setupWireMockForWahldatenClient(wahlID, wahlbezirkID, stimmzettelgebietID);

            //WireMock für getWahlvorschlaege
            setupWireMockForWahlvorschlaege(wahlID, wahlbezirkID, stimmzettelgebietID);
            //WireMock für getReferendumvorlagen
            setupWireMockForReferendumvorlagen(wahlID);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.putWahltermindaten(wahltagID));
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariationsWithUncatchedException")
        void should_throwAccessDeniedException_when_oneRequiredAuthorityIsMissing(final ArgumentsAccessor argumentsAccessor) throws Exception {
            SecurityUtils.runWith(
                    ArrayUtils.addAll(Authorities.ALL_AUTHORITIES_PUT_WAHLTERMINDATEN_THAT_GOT_CATCHED_ON_MISSING, argumentsAccessor.get(0, String[].class)));

            val wahltagID = "wahltagID";
            val wahlID = "wahlID";
            val stimmzettelgebietID = "sgzID";
            val wahlbezirkID = "wahlbezirkID";

            setupWireMockForWahltagClient(wahltagID);
            setupWireMockForWahldatenClient(wahlID, wahlbezirkID, stimmzettelgebietID);
            setupWireMockForWahlvorschlaege(wahlID, wahlbezirkID, stimmzettelgebietID);
            setupWireMockForReferendumvorlagen(wahlID);

            Assertions.assertThatThrownBy(() -> unitUnderTest.putWahltermindaten(wahltagID))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariationsWithCatchedException")
        void should_notThrowAccessDeniedException_when_oneRequiredAuthorityIsMissingThatGotCatched(final ArgumentsAccessor argumentsAccessor) throws Exception {
            SecurityUtils.runWith(
                    ArrayUtils.addAll(Authorities.ALL_AUTHORITIES_PUT_WAHLTERMINDATEN_THAT_GOT_NOT_CATCHED_ON_MISSING,
                            argumentsAccessor.get(0, String[].class)));

            val wahltagID = "wahltagID";
            val wahlID = "wahlID";
            val stimmzettelgebietID = "sgzID";
            val wahlbezirkID = "wahlbezirkID";

            setupWireMockForWahltagClient(wahltagID);
            setupWireMockForWahldatenClient(wahlID, wahlbezirkID, stimmzettelgebietID);
            setupWireMockForWahlvorschlaege(wahlID, wahlbezirkID, stimmzettelgebietID);
            setupWireMockForReferendumvorlagen(wahlID);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.putWahltermindaten(wahltagID));
        }

        public static Stream<Arguments> getMissingAuthoritiesVariationsWithUncatchedException() {
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_PUT_WAHLTERMINDATEN_THAT_GOT_NOT_CATCHED_ON_MISSING);
        }

        public static Stream<Arguments> getMissingAuthoritiesVariationsWithCatchedException() {
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_PUT_WAHLTERMINDATEN_THAT_GOT_CATCHED_ON_MISSING);
        }

    }

    @Nested
    class DeleteWahltermindaten {

        @Test
        void should_throwNoException_when_allRequiredAuthoritiesArePresent() throws Exception {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_DELETE_WAHLTERMINDTEN);

            val wahltagID = "wahltagID";
            val wahlID = "wahlID";
            val stimmzettelgebietID = "sgzID";
            val wahlbezirkID = "wahlbezirkID";

            setupWireMockForWahltagClient(wahltagID);
            setupWireMockForWahldatenClient(wahlID, wahlbezirkID, stimmzettelgebietID);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.deleteWahltermindaten(wahltagID));
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariationsWithUncatchedException")
        void should_throwAccessDeniedException_when_oneRequiredAuthorityIsMissing(final ArgumentsAccessor argumentsAccessor) throws Exception {
            SecurityUtils.runWith(
                    ArrayUtils.addAll(Authorities.ALL_AUTHORITIES_PUT_WAHLTERMINDATEN_THAT_GOT_CATCHED_ON_MISSING, argumentsAccessor.get(0, String[].class)));

            val wahltagID = "wahltagID";
            val wahlID = "wahlID";
            val stimmzettelgebietID = "sgzID";
            val wahlbezirkID = "wahlbezirkID";

            setupWireMockForWahltagClient(wahltagID);
            setupWireMockForWahldatenClient(wahlID, wahlbezirkID, stimmzettelgebietID);
            setupWireMockForWahlvorschlaege(wahlID, wahlbezirkID, stimmzettelgebietID);
            setupWireMockForReferendumvorlagen(wahlID);

            Assertions.assertThatThrownBy(() -> unitUnderTest.putWahltermindaten(wahltagID))
                    .isInstanceOf(AccessDeniedException.class);
        }

        public static Stream<Arguments> getMissingAuthoritiesVariationsWithUncatchedException() {
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_DELETE_WAHLTERMINDTEN);
        }

    }

    private void setupWireMockForWahltagClient(final String wahltagID) throws JsonProcessingException {
        val wahltagClientResponse = Set.of(new WahltagDTO().identifikator(wahltagID).tag(LocalDate.now())
                .nummer("0"));
        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/wahldaten/wahltage"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(wahltagClientResponse))
                        .withStatus(HttpStatus.OK.value())));
    }

    private void setupWireMockForWahldatenClient(final String wahlID, final String wahlbezirkID, final String stimmzettelgebietID)
            throws JsonProcessingException {
        val wahltagDate = LocalDate.now();
        val wahlclientResponse = new BasisdatenDTO().basisstrukturdaten(
                Set.of(new BasisstrukturdatenDTO().wahlID(wahlID).wahlbezirkID(wahlbezirkID).stimmzettelgebietID(stimmzettelgebietID)))
                .wahlbezirke(Set.of(new WahlbezirkDTO().identifikator(wahlbezirkID).wahltag(wahltagDate).wahlID(wahlID)
                        .wahlbezirkArt(WahlbezirkDTO.WahlbezirkArtEnum.UWB).nummer("0")
                        .wahlnummer("0")))
                .wahlen(Set.of(new WahlDTO().nummer("0").wahltag(wahltagDate).name("wahl").wahlart(WahlDTO.WahlartEnum.BTW).identifikator(wahlID)))
                .stimmzettelgebiete(
                        Set.of(new StimmzettelgebietDTO().stimmzettelgebietsart(StimmzettelgebietDTO.StimmzettelgebietsartEnum.WK).wahltag(wahltagDate)
                                .nummer("0").identifikator(stimmzettelgebietID).name("name")));
        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/wahldaten/basisdaten"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(wahlclientResponse))
                        .withStatus(HttpStatus.OK.value())));
    }

    private void setupWireMockForWahlvorschlaege(final String wahlID, final String wahlbezirkID, final String stimmzettelgebietID)
            throws JsonProcessingException {
        val wahlvorschlaege = new WahlvorschlaegeDTO().wahlID(wahlID)
                .stimmzettelgebietID(stimmzettelgebietID)
                .wahlbezirkID(wahlbezirkID)
                .wahlvorschlaege(Set.of(new WahlvorschlagDTO().identifikator(UUID.randomUUID().toString())
                        .kurzname("kurzname")
                        .addKandidatenItem(new KandidatDTO().identifikator(UUID.randomUUID().toString()).name("kandidat").direktkandidat(true))));
        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/vorschlaege/wahl/.*/.*"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(wahlvorschlaege))
                        .withStatus(HttpStatus.OK.value())));
    }

    private void setupWireMockForReferendumvorlagen(final String stimmzettelgebietID) throws JsonProcessingException {
        val referendumvorlagen = new ReferendumvorlagenDTO().stimmzettelgebietID(stimmzettelgebietID)
                .referendumvorlagen(
                        Set.of(new ReferendumvorlageDTO().frage("frage")
                                .addReferendumoptionenItem(new ReferendumoptionDTO().id(UUID.randomUUID().toString()).name("option"))));
        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/vorschlaege/referendum/.*/.*"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(referendumvorlagen))
                        .withStatus(HttpStatus.OK.value())));
    }
}
