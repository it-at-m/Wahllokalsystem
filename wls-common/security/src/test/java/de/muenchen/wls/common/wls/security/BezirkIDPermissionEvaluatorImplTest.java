package de.muenchen.wls.common.wls.security;

import de.muenchen.wls.common.security.BezirkIDPermissionEvaluatorImpl;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

class BezirkIDPermissionEvaluatorImplTest {

    private final BezirkIDPermissionEvaluatorImpl unitUnderTest = new BezirkIDPermissionEvaluatorImpl();

    @Test
    void tokenUserBezirkIdMatchesIsFalse() {
        val bezirkId = "57863";
        UsernamePasswordAuthenticationToken authUser = new UsernamePasswordAuthenticationToken("max", "muster");
        val authentication = authUser;

        val result = unitUnderTest.tokenUserBezirkIdMatches(bezirkId, authUser);

        Assertions.assertThat(result).isFalse();
    }

    @Test
    void tokenUserBezirkIdMatchesIsTrue() {
        val bezirkId = "57863";
        UsernamePasswordAuthenticationToken authUser = new UsernamePasswordAuthenticationToken("57863", "muster");
        val authentication = authUser;

        val result = unitUnderTest.tokenUserBezirkIdMatches(bezirkId, authUser);

        Assertions.assertThat(result).isTrue();
    }
}
