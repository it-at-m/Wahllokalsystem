package de.muenchen.oss.wahllokalsystem.briefwahlservice.service.beanstandetewahlbriefe;

import de.muenchen.oss.wahllokalsystem.briefwahlservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.test.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.security.BezirkIDPermissionEvaluator;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.util.HashMap;
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
public class BeanstandeteWahlbriefeSecurityTest {

    @MockBean(name = "bezirkIdPermisionEvaluator")
    BezirkIDPermissionEvaluator bezirkIDPermissionEvaluator;

    @Autowired
    BeanstandeteWahlbriefeService beanstandeteWahlbriefeService;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    class GetBeanstandeteWahlbriefe {

        @Test
        void accessGranted() {
            val wahlbezirkID = "wahlbezirkID";
            val waehlerverzeichnummer = 13L;
            val beanstandeteWahlbriefeReference = new BeanstandeteWahlbriefeReference(wahlbezirkID, waehlerverzeichnummer);

            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_BEANSTANDETE_WAHLBRIEFE);
            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            beanstandeteWahlbriefeService.getBeanstandeteWahlbriefe(beanstandeteWahlbriefeReference);
        }

        @Test
        void bezirkIDPermissionEvaluatorFailed() {
            val wahlbezirkID = "wahlbezirkID";
            val waehlerverzeichnummer = 13L;
            val beanstandeteWahlbriefeReference = new BeanstandeteWahlbriefeReference(wahlbezirkID, waehlerverzeichnummer);

            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_BEANSTANDETE_WAHLBRIEFE);
            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(false);

            Assertions.assertThatThrownBy(() -> beanstandeteWahlbriefeService.getBeanstandeteWahlbriefe(beanstandeteWahlbriefeReference))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariations")
        void anyMissingAuthorityCausesFail(final ArgumentsAccessor argumentsAccessor) {
            SecurityUtils.runWith(argumentsAccessor.get(0, String[].class));

            val wahlbezirkID = "wahlbezirkID";
            val waehlerverzeichnummer = 13L;
            val beanstandeteWahlbriefeReference = new BeanstandeteWahlbriefeReference(wahlbezirkID, waehlerverzeichnummer);
            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatThrownBy(() -> beanstandeteWahlbriefeService.getBeanstandeteWahlbriefe(beanstandeteWahlbriefeReference))
                    .isInstanceOf(AccessDeniedException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariations() {
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_GET_BEANSTANDETE_WAHLBRIEFE);
        }

    }

    @Nested
    class SetBeanstandeteWahlbriefe {

        @Test
        void accessGranted() {
            val wahlbezirkID = "wahlbezirkID";
            val waehlerverzeichnummer = 13L;
            val beanstandeteWahlbriefeModel = new BeanstandeteWahlbriefeModel(wahlbezirkID, waehlerverzeichnummer, new HashMap<>());

            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_ADD_BEANSTANDETE_WAHLBRIEFE);
            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            beanstandeteWahlbriefeService.setBeanstandeteWahlbriefe(beanstandeteWahlbriefeModel);
        }

        @Test
        void bezirkIDPermissionEvaluatorFailed() {
            val wahlbezirkID = "wahlbezirkID";
            val waehlerverzeichnummer = 13L;
            val beanstandeteWahlbriefeModel = new BeanstandeteWahlbriefeModel(wahlbezirkID, waehlerverzeichnummer, new HashMap<>());

            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_ADD_BEANSTANDETE_WAHLBRIEFE);
            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(false);

            Assertions.assertThatThrownBy(() -> beanstandeteWahlbriefeService.setBeanstandeteWahlbriefe(beanstandeteWahlbriefeModel))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingAuthoritiesVariations")
        void anyMissingAuthorityCausesFail(final ArgumentsAccessor argumentsAccessor) {
            val wahlbezirkID = "wahlbezirkID";
            val waehlerverzeichnummer = 13L;
            val beanstandeteWahlbriefeModel = new BeanstandeteWahlbriefeModel(wahlbezirkID, waehlerverzeichnummer, new HashMap<>());

            SecurityUtils.runWith(argumentsAccessor.get(0, String[].class));
            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.eq(wahlbezirkID), Mockito.any())).thenReturn(true);

            Assertions.assertThatThrownBy(() -> beanstandeteWahlbriefeService.setBeanstandeteWahlbriefe(beanstandeteWahlbriefeModel))
                    .isInstanceOf(AccessDeniedException.class);
        }

        private static Stream<Arguments> getMissingAuthoritiesVariations() {
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(Authorities.ALL_AUTHORITIES_ADD_BEANSTANDETE_WAHLBRIEFE);
        }
    }

}
