package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.AusdruckRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.Meldungsart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.WahlUndBezirkIDUndMeldungsart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.security.BezirkIDPermissionEvaluator;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.time.Instant;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles({ TestConstants.SPRING_TEST_PROFILE })
class AusdruckServiceSecurityTest {

    @MockBean
    BezirkIDPermissionEvaluator bezirkIDPermissionEvaluator;

    @Autowired
    AusdruckService unitUnderTest;

    @Autowired
    AusdruckRepository ausdruckRepository;

    @AfterEach
    void tearDown() {
        ausdruckRepository.deleteAll();
    }

    @Nested
    class GetAusdruck {

        @Test
        void should_getAccess_when_requiredAuthorityIsPresent() {
            SecurityUtils.runWith(Authorities.SERVICE_GET_AUSDRUCK);

            val id = new WahlUndBezirkIDUndMeldungsart("wahlbezirkID", "wahlID", Meldungsart.V1);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.getAusdruck(id));
        }

        @Test
        @WithMockUser
        void should_throwAccessDeniedException_when_serviceGetAusdruckAuthorityIsMissing() {

            val id = new WahlUndBezirkIDUndMeldungsart("wahlbezirkID", "wahlID", Meldungsart.V1);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getAusdruck(id)).isInstanceOf(AccessDeniedException.class);
        }
    }

    @Nested
    class GetAll {

        @Test
        void should_getAccess_when_requiredAuthorityIsPresent() {
            SecurityUtils.runWith(Authorities.SERVICE_GET_AUSDRUCK);

            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "wahlID";

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.getAll(wahlID, wahlbezirkID));
        }

        @Test
        @WithMockUser
        void should_throwAccessDeniedException_when_serviceGetAusdruckAuthorityIsMissing() {
            val wahlbezirkID = "wahlbezirkID";
            val wahlID = "wahlID";

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getAll(wahlID, wahlbezirkID)).isInstanceOf(AccessDeniedException.class);
        }
    }

    @Nested
    class SaveAusdruck {

        @Test
        void should_getAccess_when_requiredAuthorityIsPresent() {
            SecurityUtils.runWith(Authorities.SERVICE_POST_AUSDRUCK);

            val wahlbezirkID = "wahlbezirkID";
            val ausdruckModel = new AusdruckModel(new WahlUndBezirkIDUndMeldungsart(wahlbezirkID, "wahlID", Meldungsart.V1), "content", Instant.now());

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(wahlbezirkID), notNull())).thenReturn(true);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.saveAusdruck(ausdruckModel));
        }

        @Test
        void should_throwAccessDeniedException_when_requiredAuthorityIsPresentButBezirkIDEvaluatorReturnsFalse() {
            SecurityUtils.runWith(Authorities.SERVICE_POST_AUSDRUCK);

            val wahlbezirkID = "wahlbezirkID";
            val ausdruckModel = new AusdruckModel(new WahlUndBezirkIDUndMeldungsart(wahlbezirkID, "wahlID", Meldungsart.V1), "content", Instant.now());

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(wahlbezirkID), notNull())).thenReturn(false);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.saveAusdruck(ausdruckModel)).isInstanceOf(AccessDeniedException.class);
        }

        @Test
        @WithMockUser
        void should_throwAccessDeniedException_when_servicePostAusdruckAuthorityIsMissing() {
            val wahlbezirkID = "wahlbezirkID";
            val ausdruckModel = new AusdruckModel(new WahlUndBezirkIDUndMeldungsart(wahlbezirkID, "wahlID", Meldungsart.V1), "content", Instant.now());

            Mockito.when(bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(wahlbezirkID), notNull())).thenReturn(true);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.saveAusdruck(ausdruckModel)).isInstanceOf(AccessDeniedException.class);
        }
    }
}
