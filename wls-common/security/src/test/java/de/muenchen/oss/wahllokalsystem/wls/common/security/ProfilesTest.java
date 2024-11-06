package de.muenchen.oss.wahllokalsystem.wls.common.security;

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
        void evaluatorIsInstanceOfDummyImpl() {
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

        @Test
        void evaluatorIsInstanceOfBezirkIDPermissionEvaluator() {
            Assertions.assertThat(permissionEvaluator).isExactlyInstanceOf(BezirkIDPermissionEvaluatorImpl.class);
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
