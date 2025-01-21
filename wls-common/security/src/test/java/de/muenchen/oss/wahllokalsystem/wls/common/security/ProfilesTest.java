package de.muenchen.oss.wahllokalsystem.wls.common.security;

import de.muenchen.oss.wahllokalsystem.wls.common.security.authentication.AnonymousDetailRetriever;
import de.muenchen.oss.wahllokalsystem.wls.common.security.authentication.AuthDetailRetriever;
import de.muenchen.oss.wahllokalsystem.wls.common.security.authentication.JWTDetailRetriever;
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
        private OAuth2TokenInterceptor oAuth2TokenInterceptor;

        @Autowired
        private Collection<AuthDetailRetriever> authDetailRetrievers;

        @Test
        void should_haveImplementationWithChecksInContext_when_noAdditionalProfilesAreActive() {
            Assertions.assertThat(permissionEvaluator).isExactlyInstanceOf(BezirkIDPermissionEvaluatorImpl.class);
        }

        @Test
        void should_findOnlyJwtHandlerAsAuthenticationHandler_when_contextIsInitalized() {
            Assertions.assertThat(authDetailRetrievers).hasSize(1);
            Assertions.assertThat(authDetailRetrievers).allMatch(handler -> handler instanceof JWTDetailRetriever);
        }
    }

    @SpringBootTest(
            properties = { "app.crypto.key = 770A8A65DA156D24EE2A093277530142", "service.info.oid=My app name" }
    )
    @ActiveProfiles(Profiles.NO_SECURITY)
    @Nested
    class NoSecurityProfile {

        @Autowired
        private Collection<AuthDetailRetriever> authDetailRetrievers;

        @Test
        void should_findJwtAndAnonymousHandler_when_contextIsInitialized() {
            Assertions.assertThat(authDetailRetrievers).hasSize(2);
            Assertions.assertThat(
                    authDetailRetrievers).allMatch(handler -> handler instanceof JWTDetailRetriever || handler instanceof AnonymousDetailRetriever);
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
