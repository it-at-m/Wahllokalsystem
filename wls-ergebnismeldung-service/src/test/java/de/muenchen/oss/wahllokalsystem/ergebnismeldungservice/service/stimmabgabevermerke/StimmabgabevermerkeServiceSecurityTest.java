package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.StimmabgabevermerkeRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.security.BezirkIDPermissionEvaluator;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles({ TestConstants.SPRING_TEST_PROFILE })
public class StimmabgabevermerkeServiceSecurityTest {

    @MockBean
    BezirkIDPermissionEvaluator bezirkIDPermissionEvaluator;

    @Autowired
    StimmabgabevermerkeService stimmabgabevermerkeService;

    @Autowired
    StimmabgabevermerkeRepository stimmabgabevermerkeRepository;

    @AfterEach
    void tearDown() {
        SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_STIMMABGABEVERMEKE);
        stimmabgabevermerkeRepository.deleteAll();
    }

    @Nested
    class GetStimmabgabevermerke {

        @Test
        void should_getAccess_when_allRequiredAuthoritiesArePresent() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_STIMMABGABEVERMEKE);

            Assertions.assertThatNoException()
                    .isThrownBy(() -> stimmabgabevermerkeService.getStimmabgabevermerke(new BezirkIDUndWaehlerverzeichnisNummer("wahlbezirkID", 0L)));
        }

        @ParameterizedTest(name = "{index} = {1} missing")
        @MethodSource("getMissingAuthoritiesVariations")
        void should_throwAccessDeniedException_when_anyRequiredAuthorityIsMissing(final ArgumentsAccessor arguments) {
            SecurityUtils.runWith(arguments.get(0, String[].class));

            Assertions.assertThatThrownBy(() -> stimmabgabevermerkeService.getStimmabgabevermerke(new BezirkIDUndWaehlerverzeichnisNummer("wahlbezirkID", 0L)))
                    .isInstanceOf(AccessDeniedException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariations() {
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_GET_STIMMABGABEVERMEKE);
        }

    }

    @Nested
    class PostStimmabgabevermerke {

        @Test
        void should_getAccess_when_allRequiredAuthoritiesArePresent() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_SET_STIMMABGABEVERMEKE);

            val wahlbezirkID = "wahlbezirkID";
            val id = new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, 0L);

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(wahlbezirkID), any())).thenReturn(true);

            Assertions.assertThatNoException()
                    .isThrownBy(() -> stimmabgabevermerkeService.postStimmabgabevermerke(id, createSavableModel(id)));
        }

        @Test
        void should_throwAccessDeniedException_when_allRequiredAuthoritiesArePresentButBezirkIDCheckReturnsFalse() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_SET_STIMMABGABEVERMEKE);

            val wahlbezirkID = "wahlbezirkID";
            val id = new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, 0L);

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(wahlbezirkID), any())).thenReturn(false);

            Assertions.assertThatThrownBy(
                    () -> stimmabgabevermerkeService.postStimmabgabevermerke(id, createSavableModel(id)))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @ParameterizedTest(name = "{index} = {1} missing")
        @MethodSource("getMissingAuthoritiesVariationsThatThrowAccessDenied")
        void should_throwAccessDeniedException_when_anyRequiredAuthorityIsMissing(final ArgumentsAccessor arguments) {
            SecurityUtils.runWith(ArrayUtils.addAll(new String[] { Authorities.REPOSITORY_WRITE_STIMMABGABEVERMEKE }, arguments.get(0, String[].class)));

            val wahlbezirkID = "wahlbezirkID";
            val id = new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, 0L);

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(wahlbezirkID), any())).thenReturn(true);

            Assertions.assertThatThrownBy(
                    () -> stimmabgabevermerkeService.postStimmabgabevermerke(id, createSavableModel(id)))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @ParameterizedTest(name = "{index} = {1} missing")
        @MethodSource("getMissingAuthoritiesVariationsThatThrowTechnischeWlsException")
        void should_throwTechnischeWlsException_when_anyRequiredAuthorityIsMissing(final ArgumentsAccessor arguments) {
            SecurityUtils.runWith(ArrayUtils.addAll(new String[] { Authorities.SERVICE_SET_STIMMABGABEVERMERKE }, arguments.get(0, String[].class)));

            val wahlbezirkID = "wahlbezirkID";
            val id = new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, 0L);

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(wahlbezirkID), any())).thenReturn(true);

            Assertions.assertThatThrownBy(
                    () -> stimmabgabevermerkeService.postStimmabgabevermerke(id, createSavableModel(id)))
                    .isInstanceOf(TechnischeWlsException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariationsThatThrowAccessDenied() {
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(
                    ArrayUtils.removeElement(Authorities.ALL_AUTHORITIES_SET_STIMMABGABEVERMEKE, Authorities.REPOSITORY_WRITE_STIMMABGABEVERMEKE));
        }

        private static Stream<Arguments> getMissingAuthoritiesVariationsThatThrowTechnischeWlsException() {
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(
                    ArrayUtils.removeElement(Authorities.ALL_AUTHORITIES_SET_STIMMABGABEVERMEKE, Authorities.SERVICE_SET_STIMMABGABEVERMERKE));
        }

        private StimmabgabevermerkeModel createSavableModel(final BezirkIDUndWaehlerverzeichnisNummer id) {
            val eingenommeneWahlscheine = Set.of(new EingenommenerWahlscheinModel(0L, StimmzettelartModel.BEIDE));

            return new StimmabgabevermerkeModel(id, 0L, Set.of(
                    new WahldatenModel(id.getWahlbezirkID(), "wahlID", id.getWaehlerverzeichnisNummer(),
                            Collections.emptySet(), eingenommeneWahlscheine)));
        }
    }

}
