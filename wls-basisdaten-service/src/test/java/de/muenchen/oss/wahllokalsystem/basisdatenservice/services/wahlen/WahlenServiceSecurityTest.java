package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahltag;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahltagRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Farbe;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Wahl;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.WahlRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Wahlart;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import org.assertj.core.api.Assertions;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles(TestConstants.SPRING_TEST_PROFILE)
@AutoConfigureWireMock
public class WahlenServiceSecurityTest {

    @Autowired
    WahlenService wahlenService;

    @Autowired
    WahltagRepository wahltagRepository;

    @Autowired
    WahlRepository wahlRepository;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_WAHL);
        wahlRepository.deleteAll();
        SecurityContextHolder.clearContext();
    }

    @Nested
    class GetWahlen {

        @Test
        void accessGranted() {
            SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_WAHLTAG, Authorities.REPOSITORY_READ_WAHLTAG, Authorities.REPOSITORY_WRITE_WAHL);
            var searchingForWahltag = new Wahltag("wahltagID", LocalDate.now().plusMonths(1), "beschreibung1", "1");
            List<Wahl> mockedListOfEntities = createWahlEntities();
            wahltagRepository.save(searchingForWahltag);
            wahlRepository.saveAll(mockedListOfEntities);
            SecurityUtils.runWith(Authorities.REPOSITORY_READ_WAHLTAG, Authorities.SERVICE_GET_WAHLEN, Authorities.REPOSITORY_READ_WAHL,
                    Authorities.REPOSITORY_WRITE_WAHL);
            Assertions.assertThatNoException().isThrownBy(() -> wahlenService.getWahlen(searchingForWahltag.getWahltagID()));
        }

        @Test
        void accessDeniedWhenServiceAuthoritiyIsMissing() {
            SecurityUtils.runWith(Authorities.REPOSITORY_READ_WAHL, Authorities.REPOSITORY_WRITE_WAHL);
            var searchingForWahltag = new Wahltag("wahltagID", LocalDate.now(), "beschreibung1", "1");
            Assertions.assertThatThrownBy(() -> wahlenService.getWahlen(searchingForWahltag.getWahltagID())).isInstanceOf(AccessDeniedException.class);
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariations")
        void anyMissingAuthorityCausesFail(final ArgumentsAccessor argumentsAccessor) throws Exception {
            SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_WAHLTAG, Authorities.REPOSITORY_WRITE_WAHL);
            var searchingForWahltag = new Wahltag("wahltagID", LocalDate.now(), "beschreibung1", "1");
            wahltagRepository.save(searchingForWahltag);
            val mockedListOfEntities = createWahlEntities();
            wahlRepository.saveAll(mockedListOfEntities);
            val eaiWahlen = createWahlModels();

            SecurityUtils.runWith(
                    ArrayUtils.addAll(argumentsAccessor.get(0, String[].class), Authorities.REPOSITORY_READ_WAHLTAG, Authorities.REPOSITORY_WRITE_WAHLTAG));
            WireMock.stubFor(WireMock.get("/wahldaten/wahlen?forDate=" + searchingForWahltag.getWahltag().toString() + "&withNummer=1")
                    .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json")
                            .withStatus(HttpStatus.OK.value())
                            .withBody(objectMapper.writeValueAsBytes(eaiWahlen))));

            Assertions.assertThatThrownBy(() -> wahlenService.getWahlen(searchingForWahltag.getWahltagID()))
                    .isInstanceOf(AccessDeniedException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariations() {
            return SecurityUtils
                    .buildArgumentsForMissingAuthoritiesVariations(
                            new String[] { Authorities.SERVICE_GET_WAHLEN, Authorities.REPOSITORY_READ_WAHL, Authorities.REPOSITORY_WRITE_WAHL });
        }
    }

    @Nested
    class PostWahlen {

        @Test
        void accessGranted() {
            SecurityUtils.runWith(Authorities.SERVICE_POST_WAHLEN, Authorities.REPOSITORY_WRITE_WAHL);
            var searchingForWahltag = new Wahltag("wahltagID", LocalDate.now(), "beschreibung1", "1");
            List<WahlModel> mockedListOfModels = createWahlModels();
            Assertions.assertThatNoException().isThrownBy(() -> wahlenService.postWahlen(searchingForWahltag.getWahltagID(), mockedListOfModels));
        }

        @Test
        void accessDeniedWhenServiceAuthorityIsMissing() {
            SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_WAHL);
            var searchingForWahltag = new Wahltag("wahltagID", LocalDate.now(), "beschreibung1", "1");
            List<WahlModel> mockedListOfModels = createWahlModels();
            Assertions.assertThatThrownBy(() -> wahlenService.postWahlen(searchingForWahltag.getWahltagID(), mockedListOfModels))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @Test
        void fachlicheWlsExceptionWhenRepoAuthorityIsMissing() {
            SecurityUtils.runWith(Authorities.SERVICE_POST_WAHLEN);
            var searchingForWahltag = new Wahltag("wahltagID", LocalDate.now(), "beschreibung1", "1");
            List<WahlModel> mockedListOfModels = createWahlModels();
            Assertions.assertThatThrownBy(() -> wahlenService.postWahlen(searchingForWahltag.getWahltagID(), mockedListOfModels)).isInstanceOf(
                    FachlicheWlsException.class);
        }
    }

    private List<Wahl> createWahlEntities() {
        Wahl wahl1 = new Wahl();
        wahl1.setWahlID("wahlid1");
        wahl1.setName("wahl1");
        wahl1.setNummer("0");
        wahl1.setFarbe(new Farbe(1, 1, 1));
        wahl1.setWahlart(Wahlart.BAW);
        wahl1.setReihenfolge(1);
        wahl1.setWaehlerverzeichnisnummer(1);
        wahl1.setWahltag(LocalDate.now().plusMonths(1));

        Wahl wahl2 = new Wahl();
        wahl2.setWahlID("wahlid2");
        wahl2.setName("wahl2");
        wahl2.setNummer("1");
        wahl2.setFarbe(new Farbe(2, 2, 2));
        wahl2.setWahlart(Wahlart.LTW);
        wahl2.setReihenfolge(2);
        wahl2.setWaehlerverzeichnisnummer(2);
        wahl2.setWahltag(LocalDate.now().plusMonths(2));

        Wahl wahl3 = new Wahl();
        wahl3.setWahlID("wahlid3");
        wahl3.setName("wahl3");
        wahl3.setNummer("2");
        wahl3.setFarbe(new Farbe(3, 3, 3));
        wahl3.setWahlart(Wahlart.EUW);
        wahl3.setReihenfolge(3);
        wahl3.setWaehlerverzeichnisnummer(3);
        wahl3.setWahltag(LocalDate.now().plusMonths(3));
        List<Wahl> lw = new ArrayList<>();
        lw.add(wahl1);
        lw.add(wahl2);
        lw.add(wahl3);

        return lw;
    }

    private List<WahlModel> createWahlModels() {
        WahlModel wahl1 = new WahlModel("wahlid1", "wahl1", 1L,
                1L, LocalDate.now().plusMonths(1),
                Wahlart.BAW, new Farbe(1, 1, 1), "0");
        WahlModel wahl2 = new WahlModel("wahlid2", "wahl2", 2L,
                2L, LocalDate.now().plusMonths(2),
                Wahlart.LTW, new Farbe(2, 2, 2), "1");
        WahlModel wahl3 = new WahlModel("wahlid3", "wahl3", 3L,
                3L, LocalDate.now().plusMonths(3),
                Wahlart.LTW, new Farbe(3, 3, 3), "2");
        List<WahlModel> lw = new ArrayList<>();
        lw.add(wahl1);
        lw.add(wahl2);
        lw.add(wahl3);
        return lw;
    }

}
