package de.muenchen.oss.wahllokalsystem.adminservice.service.wahllokalbenutzer;

import de.muenchen.oss.wahllokalsystem.adminservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.adminservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.adminservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.adminservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles({ TestConstants.SPRING_TEST_PROFILE, Profiles.DUMMY_CLIENTS })
class WahllokalBenutzerServiceSecurityTest {

    @Autowired
    WahllokalBenutzerService unitUnderTest;

    @Nested
    class GenerateWahllokalbenutzer {

        @Test
        void should_getAccess_when_allRequiredAuthoritiesArePresent() {
            SecurityUtils.runWith(Authorities.ADMIN_GENERATEEXPORTWAHLLOKALBENUTZER);

            val wahltagID = "wahltagID";

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.generateWahllokalbenutzer(wahltagID));
        }

        @Test
        void should_throwAccessDeniedException_when_requiredAuthorityIsMissing() {
            SecurityUtils.runWith("wrong_authority");

            val wahltagID = "wahltagID";

            Assertions.assertThatException()
                    .isThrownBy(() -> unitUnderTest.generateWahllokalbenutzer(wahltagID))
                    .isInstanceOf(AccessDeniedException.class);
        }
    }

    @Nested
    class ExportWahllokalBenutzer {

        @Test
        void should_getAccess_when_allRequiredAuthoritiesArePresent() {
            SecurityUtils.runWith(Authorities.ADMIN_EXPORTWAHLLOKALBENUTZER);

            val wahltagID = "wahltagID";

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.exportWahllokalBenutzer(wahltagID));
        }

        @Test
        void should_throwAccessDeniedException_when_requiredAuthorityIsMissing() {
            SecurityUtils.runWith("wrong_authority");

            val wahltagID = "wahltagID";

            Assertions.assertThatException()
                    .isThrownBy(() -> unitUnderTest.exportWahllokalBenutzer(wahltagID))
                    .isInstanceOf(AccessDeniedException.class);
        }
    }

    @Nested
    class DeleteWahllokalBenutzer {

        @Test
        void should_getAccess_when_allRequiredAuthoritiesArePresent() {
            SecurityUtils.runWith(Authorities.ADMIN_DELETEWAHLLOKALBENUTZER);

            val wahltagID = "wahltagID";

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.deleteWahllokalBenutzer(wahltagID));
        }

        @Test
        void should_throwAccessDeniedException_when_requiredAuthorityIsMissing() {
            SecurityUtils.runWith("wrong_authority");

            val wahltagID = "wahltagID";

            Assertions.assertThatException()
                    .isThrownBy(() -> unitUnderTest.deleteWahllokalBenutzer(wahltagID))
                    .isInstanceOf(AccessDeniedException.class);
        }
    }
}
