package de.muenchen.oss.wahllokalsystem.wls.common.security.authentication;

import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class JWTDetailRetriever implements AuthDetailRetriever {

    @Override
    public boolean canHandle(final Authentication authentication) {
        Assert.notNull(authentication, "authentication must not be null");
        return authentication instanceof JwtAuthenticationToken;
    }

    public Optional<String> getDetail(final String detailKey, final Authentication authentication) {
        Assert.notNull(authentication, "authentication must not be null");
        Assert.notNull(detailKey, "detailKey must not be null");
        if (authentication instanceof JwtAuthenticationToken jwtToken) {
            return Optional.ofNullable(jwtToken.getToken().getClaimAsString(detailKey));
        } else {
            return Optional.empty();
        }
    }
}
