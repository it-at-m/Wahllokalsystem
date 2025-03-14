package de.muenchen.oss.wahllokalsystem.adminservice.service.wahltage;

import de.muenchen.oss.wahllokalsystem.adminservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.adminservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.adminservice.configuration.Profiles;
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
class WahltageServiceSecurityTest {

    @Autowired
    WahltageService unitUnderTest;

    @Nested
    class GetWahltage {

        @Test
        void should_getAccess_when_allRequiredAuthoritiesArePresent() {
            SecurityUtils.runWith(Authorities.ADMIN_GETWAHLTAGE);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.getWahltage());
        }

        @Test
        void should_throwAccessDeniedException_when_requiredAuthorityIsMissing() {
            SecurityUtils.runWith("wrong_authority");

            Assertions.assertThatException()
                    .isThrownBy(() -> unitUnderTest.getWahltage())
                    .isInstanceOf(AccessDeniedException.class);
        }
    }
}
