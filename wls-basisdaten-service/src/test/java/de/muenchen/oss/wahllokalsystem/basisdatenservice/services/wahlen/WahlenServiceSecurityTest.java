package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.WahlRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.util.stream.Stream;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootTest(classes = MicroServiceApplication.class)
public class WahlenServiceSecurityTest {

    @Autowired
    WahlenService wahlenService;

    @Autowired
    WahlRepository wahlRepository;

    @BeforeEach
    void setup() {
        SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_WAHL);
        wahlRepository.deleteAll();
        SecurityContextHolder.clearContext();
    }

    @Nested
    class ResetWahlen {

        @Test
        void accessGranted() {
            SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_RESET_WAHLEN);
            Assertions.assertThatNoException().isThrownBy(() -> wahlenService.resetWahlen());
        }

        @Test
        void accessDeniedWhenServiceAuthoritiyIsMissing() {
            SecurityUtils.runWith(Authorities.REPOSITORY_READ_WAHL, Authorities.REPOSITORY_WRITE_WAHL);
            Assertions.assertThatThrownBy(() -> wahlenService.resetWahlen()).isInstanceOf(AccessDeniedException.class);
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingRepoAuthoritiesVariations")
        void technischeWlsExceptionWhenRepoAuthorityIsMissing(final ArgumentsAccessor argumentsAccessor) {
            SecurityUtils.runWith(ArrayUtils.add(argumentsAccessor.get(0, String[].class), Authorities.SERVICE_RESET_WAHLEN));
            Assertions.assertThatThrownBy(() -> wahlenService.resetWahlen()).isInstanceOf(TechnischeWlsException.class);
        }

        private static Stream<Arguments> getMissingRepoAuthoritiesVariations() {
            return SecurityUtils
                    .buildArgumentsForMissingAuthoritiesVariations(new String[] { Authorities.REPOSITORY_WRITE_WAHL, Authorities.REPOSITORY_READ_WAHL });
        }
    }
}
