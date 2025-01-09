package de.muenchen.oss.wahllokalsystem.wls.common.security;

import de.muenchen.oss.wahllokalsystem.wls.common.security.authentication.AnonymousHandler;
import de.muenchen.oss.wahllokalsystem.wls.common.security.authentication.AuthenticationHandler;
import de.muenchen.oss.wahllokalsystem.wls.common.security.authentication.JWTHandler;
import java.util.Collection;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

class ProfilesTest {

    @SpringBootTest(
            properties = { "app.crypto.key = 770A8A65DA156D24EE2A093277530142", "service.info.oid=My app name" }
    )
    @ActiveProfiles(Profiles.NO_BEZIRKS_ID_CHECK)
    @Nested
    class NoBezirksIdCheck {

        @Autowired
        private BezirkIDPermissionEvaluator permissionEvaluator;

        @Test
        void should_haveDummyEvaluatorInContext_when_noBezirkIdCheckIsActive() {
            Assertions.assertThat(permissionEvaluator).isExactlyInstanceOf(DummyBezirkIdPermissionEvaluatorImpl.class);
        }
    }

    @SpringBootTest(
            properties = { "app.crypto.key = 770A8A65DA156D24EE2A093277530142", "service.info.oid=My app name" }
    )
    @Nested
    class NoSpecialProfile {

        @Autowired
        private BezirkIDPermissionEvaluator permissionEvaluator;

        @Autowired
        private Collection<AuthenticationHandler> authenticationHandlers;

        @Test
        void should_haveImplementationWithChecksInContext_when_noAdditionalProfilesAreActive() {
            Assertions.assertThat(permissionEvaluator).isExactlyInstanceOf(BezirkIDPermissionEvaluatorImpl.class);
        }

        @Test
        void should_findOnlyJwtHandlerAsAuthenticationHandler_when_contextIsInitalized() {
            Assertions.assertThat(authenticationHandlers).hasSize(1);
            Assertions.assertThat(authenticationHandlers).allMatch(handler -> handler instanceof JWTHandler);
        }
    }

    @SpringBootTest(
            properties = { "app.crypto.key = 770A8A65DA156D24EE2A093277530142", "service.info.oid=My app name" }
    )
    @ActiveProfiles(Profiles.NO_SECURITY)
    @Nested
    class NoSecurityProfile {

        @Autowired
        private Collection<AuthenticationHandler> authenticationHandlers;

        @Test
        void should_findJwtAndAnonymousHandler_when_contextIsInitialized() {
            Assertions.assertThat(authenticationHandlers).hasSize(2);
            Assertions.assertThat(authenticationHandlers).allMatch(handler -> handler instanceof JWTHandler || handler instanceof AnonymousHandler);
        }

    }

    @SpringBootApplication(
            scanBasePackages = {
                    "de.muenchen.oss.wahllokalsystem.wls.common.security",
                    "de.muenchen.oss.wahllokalsystem.wls.common.exception"
            }
    ) //all BezirkIDPermissionEvaluator impl classes should be found
    public static class TestConfiguration {

    }
}
