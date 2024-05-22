package de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.utils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.util.Assert;

/*
 * inspired by org.springframework.security.test.context.support.WithMockUserSecurityContextFactory
 */
final public class WithMockUserAsJwtSecurityContextFactory implements WithSecurityContextFactory<WithMockUserAsJwt> {

    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
            .getContextHolderStrategy();

    @Override
    public SecurityContext createSecurityContext(WithMockUserAsJwt withUser) {
        val username = withUser.value();
        Assert.notNull(username, () -> withUser + " cannot have null username on both username and value properties");

        val issuedAt = Instant.now().minus(withUser.issuedBeforeHours(), ChronoUnit.HOURS);
        val expiresAt = Instant.now().plus(withUser.expiredInHours(), ChronoUnit.HOURS);
        val headers = Map.of("jwtDummyHeader", (Object) "jwtDummyValue");

        val claims = new HashMap<String, Object>();
        claims.put("dummyClaim", "dummyClaimValue");
        claims.putAll(createClaimsMap(withUser.claimProperties(), withUser.claimPropertiesSeparator()));

        val jwt = new Jwt(username, issuedAt, expiresAt, headers, claims);

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (String authority : withUser.authorities()) {
            grantedAuthorities.add(new SimpleGrantedAuthority(authority));
        }

        val authentication = new JwtAuthenticationToken(jwt, grantedAuthorities);

        val context = this.securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }

    @Autowired(required = false)
    void setSecurityContextHolderStrategy(SecurityContextHolderStrategy securityContextHolderStrategy) {
        this.securityContextHolderStrategy = securityContextHolderStrategy;
    }

    private Map<String, Object> createClaimsMap(final String[] concatedClaimProperties, final String keyValueSeparator) {
        return Arrays.stream(concatedClaimProperties).map(concatedClaimProperty -> concatedClaimProperty.split(keyValueSeparator))
                .collect(Collectors.toMap(propertyAsArray -> propertyAsArray[0], propertyAsArray -> propertyAsArray[1]));
    }
}
