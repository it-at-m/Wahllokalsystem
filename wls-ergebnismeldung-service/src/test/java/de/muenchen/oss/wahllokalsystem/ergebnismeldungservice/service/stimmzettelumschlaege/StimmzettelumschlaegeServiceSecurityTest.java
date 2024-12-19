package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelumschlaege;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelumschlaege.StimmzettelumschlaegeRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.security.BezirkIDPermissionEvaluator;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.time.LocalDateTime;
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
class StimmzettelumschlaegeServiceSecurityTest {

    @MockBean
    BezirkIDPermissionEvaluator bezirkIDPermissionEvaluator;

    @Autowired
    StimmzettelumschlaegeService unitUnderTest;

    @Autowired
    StimmzettelumschlaegeRepository stimmzettelumschlaegeRepository;

    @BeforeEach
    void setup() {
        SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_STIMMZETTELUMSCHLAEGE);
        stimmzettelumschlaegeRepository.deleteAll();
    }

    @Nested
    class GetStimmzettelumschlaege {

        @Test
        void should_getAccess_when_allRequiredAuthoritiesArePresent() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_STIMMZETTELUMSCHLAEGE);

            val id = new BezirkUndWahlID("wahlID", "wahlbezirkID");

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.getStimmzettelumschlaege(id));
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariations")
        void should_throwAccessDeniedException_when_anyRequiredAuthorityIsMissing(final ArgumentsAccessor arguments) {
            SecurityUtils.runWith(arguments.get(0, String[].class));

            val id = new BezirkUndWahlID("wahlID", "wahlbezirkID");

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getStimmzettelumschlaege(id)).isInstanceOf(AccessDeniedException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariations() {
            return SecurityUtils
                    .buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_GET_STIMMZETTELUMSCHLAEGE);
        }
    }

    @Nested
    class SetStimmzettelumschlaege {

        @Test
        void should_getAccess_when_allRequiredAuthoritiesArePresent() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_SET_STIMMZETTELUMSCHLAEGE);

            val wahlbezirkID = "wahlbezirkID";
            val id = new BezirkUndWahlID("wahlID", wahlbezirkID);
            val stimmzettelumschlaege = createStimmzettelumschlaegeModel(id);

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(wahlbezirkID), notNull())).thenReturn(true);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.setStimmzettelumschlaege(id, stimmzettelumschlaege));
        }

        @Test
        void should_throwAccessDeniedException_when_allRequiredAuthoritiesArePresentButBezirkIDEvaluatorReturnsFalse() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_SET_STIMMZETTELUMSCHLAEGE);

            val wahlbezirkID = "wahlbezirkID";
            val id = new BezirkUndWahlID("wahlID", wahlbezirkID);
            val stimmzettelumschlaege = createStimmzettelumschlaegeModel(id);

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(wahlbezirkID), notNull())).thenReturn(false);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.setStimmzettelumschlaege(id, stimmzettelumschlaege))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @Test
        void should_throwAccessDeniedException_when_serviceSetStimmzettelumschlaegeAuthorityIsMissing() {
            SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_STIMMZETTELUMSCHLAEGE);

            val wahlbezirkID = "wahlbezirkID";
            val id = new BezirkUndWahlID("wahlID", wahlbezirkID);
            val stimmzettelumschlaege = createStimmzettelumschlaegeModel(id);

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(wahlbezirkID), notNull())).thenReturn(true);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.setStimmzettelumschlaege(id, stimmzettelumschlaege))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @Test
        void should_throwTechnischeWlsException_when_repositoryWriteStimmzettelumschlaegeAuthorityIsMissing() {
            SecurityUtils.runWith(Authorities.SERVICE_SET_STIMMZETTELUMSCHLAEGE);

            val wahlbezirkID = "wahlbezirkID";
            val id = new BezirkUndWahlID("wahlID", wahlbezirkID);
            val stimmzettelumschlaege = createStimmzettelumschlaegeModel(id);

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(wahlbezirkID), notNull())).thenReturn(true);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.setStimmzettelumschlaege(id, stimmzettelumschlaege))
                    .isInstanceOf(TechnischeWlsException.class);
        }

        private StimmzettelumschlaegeModel createStimmzettelumschlaegeModel(BezirkUndWahlID id) {
            val urneneroeffnungsUhrzeit = LocalDateTime.now();
            val anzahlWaehler = 47;
            val anzahlWaehler2 = 11;
            return new StimmzettelumschlaegeModel(id, urneneroeffnungsUhrzeit, anzahlWaehler, anzahlWaehler2);

        }
    }
}
