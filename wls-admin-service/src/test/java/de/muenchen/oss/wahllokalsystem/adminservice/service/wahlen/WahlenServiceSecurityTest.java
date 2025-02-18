package de.muenchen.oss.wahllokalsystem.adminservice.service.wahlen;

import de.muenchen.oss.wahllokalsystem.adminservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.adminservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.adminservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.adminservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.time.LocalDate;
import java.util.List;
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
class WahlenServiceSecurityTest {

    @Autowired
    WahlenService unitUnderTest;

    @Nested
    class GetWahlen {

        @Test
        void should_getAccess_when_allRequiredAuthoritiesArePresent() {
            SecurityUtils.runWith(Authorities.ADMIN_GETWAHLEN);

            val wahltagID = "wahltagID";

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.getWahlen(wahltagID));
        }

        @Test
        void should_throwAccessDeniedException_when_requiredAuthorityIsMissing() {
            SecurityUtils.runWith("wrong_authority");

            val wahltagID = "wahltagID";

            Assertions.assertThatException()
                    .isThrownBy(() -> unitUnderTest.getWahlen(wahltagID))
                    .isInstanceOf(AccessDeniedException.class);
        }
    }

    @Nested
    class UpdateWahlen {

        @Test
        void should_getAccess_when_allRequiredAuthoritiesArePresent() {
            SecurityUtils.runWith(Authorities.ADMIN_UPDATEWAHLEN);

            val wahltagID = "wahltagID";
            val wahlen = List.of(
                    new WahlModel("wahlID", "name" + "wahl1", 1L,
                            1L, LocalDate.now().plusMonths(1),
                            WahlartModel.BAW, new FarbeModel(1, 1, 1)));

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.updateWahlen(wahlen, wahltagID));
        }

        @Test
        void should_throwAccessDeniedException_when_requiredAuthorityIsMissing() {
            SecurityUtils.runWith("wrong_authority");

            val wahltagID = "wahltagID";

            Assertions.assertThatException()
                    .isThrownBy(() -> unitUnderTest.updateWahlen(null, wahltagID))
                    .isInstanceOf(AccessDeniedException.class);
        }
    }

}
