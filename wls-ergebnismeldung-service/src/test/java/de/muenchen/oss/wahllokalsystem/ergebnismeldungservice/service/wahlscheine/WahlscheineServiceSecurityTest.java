package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.wahlscheine;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.wahlscheine.WahlscheineRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.security.BezirkIDPermissionEvaluator;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.util.stream.Stream;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
class WahlscheineServiceSecurityTest {

    @MockBean
    BezirkIDPermissionEvaluator bezirkIDPermissionEvaluator;

    @Autowired
    WahlscheineService unitUnderTest;

    @Autowired
    WahlscheineRepository wahlscheineRepository;

    @BeforeEach
    void setup() {
        SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_WAHLSCHEINE);
        wahlscheineRepository.deleteAll();
    }

    @Nested
    class GetWahlscheine {

        @Test
        void should_getAccess_when_allRequiredAuthoritiesArePresent() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_WAHLSCHEINE);

            val id = new BezirkUndWahlID("wahlID", "wahlbezirkID");

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.getWahlscheine(id));
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariations")
        void should_throwAccessDeniedException_when_anyRequiredAuthorityIsMissing(final ArgumentsAccessor arguments) {
            SecurityUtils.runWith(arguments.get(0, String[].class));

            val id = new BezirkUndWahlID("wahlID", "wahlbezirkID");

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahlscheine(id)).isInstanceOf(AccessDeniedException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariations() {
            return SecurityUtils
                    .buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_GET_WAHLSCHEINE);
        }
    }

    @Nested
    class SetWahlscheine {

        @Test
        void should_getAccess_when_allRequiredAuthoritiesArePresent() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_SET_WAHLSCHEINE);

            val wahlbezirkID = "wahlbezirkID";
            val id = new BezirkUndWahlID("wahlID", wahlbezirkID);
            val stimmabgabevermerke = 33L;
            val newWahlscheine = new WahlscheineModel(id, stimmabgabevermerke);

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(wahlbezirkID), notNull())).thenReturn(true);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.setWahlscheine(id, newWahlscheine));
        }

        @Test
        void should_throwAccessDeniedException_when_allRequiredAuthoritiesArePresentButBezirkIDEvaluatorReturnsFalse() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_SET_WAHLSCHEINE);

            val wahlbezirkID = "wahlbezirkID";
            val id = new BezirkUndWahlID("wahlID", wahlbezirkID);
            val stimmabgabevermerke = 33L;
            val wahlscheineModel = new WahlscheineModel(id, stimmabgabevermerke);

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(wahlbezirkID), notNull())).thenReturn(false);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.setWahlscheine(id, wahlscheineModel)).isInstanceOf(AccessDeniedException.class);
        }

        @Test
        void should_throwAccessDeniedException_when_serviceSetWahlscheineAuthorityIsMissing() {
            SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_WAHLSCHEINE);

            val wahlbezirkID = "wahlbezirkID";
            val id = new BezirkUndWahlID("wahlID", wahlbezirkID);
            val stimmabgabevermerke = 33L;
            val wahlscheineModel = new WahlscheineModel(id, stimmabgabevermerke);

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(wahlbezirkID), notNull())).thenReturn(true);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.setWahlscheine(id, wahlscheineModel)).isInstanceOf(AccessDeniedException.class);
        }

       @Test
        void should_throwTechnischeWlsException_when_repositoryWriteWahlscheineAuthorityIsMissing() {
            SecurityUtils.runWith(Authorities.SERVICE_SET_WAHLSCHEINE);

            val wahlbezirkID = "wahlbezirkID";
            val id = new BezirkUndWahlID("wahlID", wahlbezirkID);
            val stimmabgabevermerke = 33L;
            val wahlscheineModel = new WahlscheineModel(id, stimmabgabevermerke);

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(wahlbezirkID), notNull())).thenReturn(true);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.setWahlscheine(id, wahlscheineModel)).isInstanceOf(TechnischeWlsException.class);
        }
   }
}
