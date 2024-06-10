package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.waehlerverzeichnis;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.utils.SecurityUtils;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.security.BezirkIDPermissionEvaluator;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles({ TestConstants.SPRING_TEST_PROFILE })
public class WaehlerverzeichnisServiceSecurityTest {

    @Autowired
    WaehlerverzeichnisService unitUnderTest;

    @MockBean(name = "bezirkIdPermisionEvaluator")
    BezirkIDPermissionEvaluator bezirkIDPermissionEvaluator;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    class GetWaehlerverzeichnis {

        @Test
        void accessGranted() {
            SecurityUtils.runAs(Authorities.SERVICE_GET_WAEHLERVERZEICHNIS);

            val waehlerverzeichnisReference = new BezirkIDUndWaehlerverzeichnisNummer("wahlbezirkID", 89L);
            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(waehlerverzeichnisReference.getWahlbezirkID()), any())).thenReturn(true);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.getWaehlerverzeichnis(waehlerverzeichnisReference));
        }

        @Test
        void bezirkIDPermissionEvaluatorFailed() {
            SecurityUtils.runAs(Authorities.SERVICE_GET_WAEHLERVERZEICHNIS);

            val waehlerverzeichnisReference = new BezirkIDUndWaehlerverzeichnisNummer("wahlbezirkID", 89L);
            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(waehlerverzeichnisReference.getWahlbezirkID()), any())).thenReturn(false);

            Assertions.assertThatThrownBy(() -> unitUnderTest.getWaehlerverzeichnis(waehlerverzeichnisReference)).isInstanceOf(AccessDeniedException.class);
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariations")
        void anyMissingAuthorityCausesFail(final ArgumentsAccessor argumentsAccessor) {
            SecurityUtils.runAs(argumentsAccessor.get(0, String[].class));

            val waehlerverzeichnisReference = new BezirkIDUndWaehlerverzeichnisNummer("wahlbezirkID", 89L);
            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(waehlerverzeichnisReference.getWahlbezirkID()), any())).thenReturn(true);

            Assertions.assertThatThrownBy(() -> unitUnderTest.getWaehlerverzeichnis(waehlerverzeichnisReference)).isInstanceOf(AccessDeniedException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariations() {
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_GET_WAEHLERVERZEICHNIS);
        }
    }

    @Nested
    class SetWaehlerverzeichnis {

        @Test
        void accessGranted() {
            SecurityUtils.runAs(Authorities.ALL_AUTHORITIES_POST_WAEHLERVERZEICHNIS);

            val modelToSet = new WaehlerverzeichnisModel(new BezirkIDUndWaehlerverzeichnisNummer("wahlbezirkID", 233L), true, true, false, true);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.setWaehlerverzeichnis(modelToSet));
        }

        @Test
        void acccessDeniedOnMissingServiceAuthority() {
            SecurityUtils.runAs(Authorities.REPOSITORY_WRITE_WAEHLERVERZEICHNIS);

            val modelToSet = new WaehlerverzeichnisModel(new BezirkIDUndWaehlerverzeichnisNummer("wahlbezirkID", 233L), true, true, false, true);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.setWaehlerverzeichnis(modelToSet)).isInstanceOf(AccessDeniedException.class);
        }

        @Test
        void wlsExceptionOnMissingRepositoryWriteAuthority() {
            SecurityUtils.runAs(Authorities.SERVICE_POST_WAEHLERVERZEICHNIS);

            val modelToSet = new WaehlerverzeichnisModel(new BezirkIDUndWaehlerverzeichnisNummer("wahlbezirkID", 233L), true, true, false, true);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.setWaehlerverzeichnis(modelToSet)).isInstanceOf(TechnischeWlsException.class);
        }

    }
}
