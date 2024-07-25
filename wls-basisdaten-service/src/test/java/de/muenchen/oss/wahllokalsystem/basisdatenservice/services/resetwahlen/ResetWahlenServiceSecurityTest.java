package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.resetwahlen;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.WahlRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;
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
public class ResetWahlenServiceSecurityTest {

    @Autowired
    ResetWahlenService resetWahlenService;

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
            Assertions.assertThatNoException().isThrownBy(() -> resetWahlenService.resetWahlen());
        }

        @Test
        void accessDeniedWhenServiceAuthoritiyIsMissing() {
            SecurityUtils.runWith(Authorities.REPOSITORY_READ_WAHL, Authorities.REPOSITORY_WRITE_WAHL);
            Assertions.assertThatThrownBy(() -> resetWahlenService.resetWahlen()).isInstanceOf(AccessDeniedException.class);
        }

        @ParameterizedTest(name = "{index} - {1} missing")
        @MethodSource("getMissingRepoAuthoritiesVariations")
        void technischeWlsExceptionWhenRepoAuthorityIsMissing(final ArgumentsAccessor argumentsAccessor) {
            ArrayList<String> mList = new ArrayList<>(Arrays.asList(argumentsAccessor.get(0, String[].class)));
            mList.add(Authorities.SERVICE_RESET_WAHLEN);
            String[] strArray = new String[mList.size()];
            for(int i = 0; i < mList.size(); i++) {
                strArray[i] = mList.get(i);
            }
            SecurityUtils.runWith(strArray);
            Assertions.assertThatThrownBy(() -> resetWahlenService.resetWahlen()).isInstanceOf(TechnischeWlsException.class);
        }

        private static Stream<Arguments> getMissingRepoAuthoritiesVariations() {
            return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(new String[]{Authorities.REPOSITORY_WRITE_WAHL, Authorities.REPOSITORY_READ_WAHL});
        }
    }
}
