package de.muenchen.oss.wahllokalsystem.wls.common.security;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

class ProfilesTest {

    @SpringBootTest(classes = { DummyBezirkIdPermissionEvaluatorImpl.class, BezirkIDPermissionEvaluatorImpl.class })
    @ActiveProfiles(Profiles.NO_BEZIRKS_ID_CHECK)
    @ComponentScan
    @Nested
    class NoBezirksIdCheck {

        @Autowired
        private BezirkIDPermissionEvaluator permissionEvaluator;

        @Test
        void evaluatorIsInstanceOfDummyImpl() {
            Assertions.assertThat(permissionEvaluator).isExactlyInstanceOf(DummyBezirkIdPermissionEvaluatorImpl.class);
        }
    }

    @SpringBootTest(classes = { DummyBezirkIdPermissionEvaluatorImpl.class, BezirkIDPermissionEvaluatorImpl.class })
    @ComponentScan
    @Nested
    class NoSpecialProfile {

        @Autowired
        private BezirkIDPermissionEvaluator permissionEvaluator;

        @Test
        void evaluatorIsInstanceOfBezirkIDPermissionEvaluator() {
            Assertions.assertThat(permissionEvaluator).isExactlyInstanceOf(BezirkIDPermissionEvaluatorImpl.class);
        }
    }
}
