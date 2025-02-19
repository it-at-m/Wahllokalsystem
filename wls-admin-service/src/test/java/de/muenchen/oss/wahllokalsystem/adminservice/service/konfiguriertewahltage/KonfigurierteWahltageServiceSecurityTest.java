package de.muenchen.oss.wahllokalsystem.adminservice.service.konfiguriertewahltage;

import de.muenchen.oss.wahllokalsystem.adminservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.adminservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.adminservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.adminservice.service.konfigurierterwahltag.KonfigurierteWahltageService;
import de.muenchen.oss.wahllokalsystem.adminservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles({ TestConstants.SPRING_TEST_PROFILE, Profiles.DUMMY_CLIENTS })
public class KonfigurierteWahltageServiceSecurityTest {

    @Autowired
    KonfigurierteWahltageService unitUnderTest;

    @Nested
    class GetKonfigurierteWahltage {

        @Test
        void should_getAccess_when_allRequiredAuthoritiesArePresent() {
            SecurityUtils.runWith(Authorities.ADMIN_READ_KONFIGURIERTEWAHLTAGE);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.getKonfigurierteWahltage());
        }

        @Test
        void should_throwAccessDeniedException_when_requiredAuthorityIsMissing() {
            SecurityUtils.runWith("wrong_authority");

            Assertions.assertThatException()
                    .isThrownBy(() -> unitUnderTest.getKonfigurierteWahltage())
                    .isInstanceOf(AccessDeniedException.class);
        }
    }
}
