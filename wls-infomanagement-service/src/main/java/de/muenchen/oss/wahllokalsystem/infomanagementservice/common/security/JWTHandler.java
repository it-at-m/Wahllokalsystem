package de.muenchen.oss.wahllokalsystem.infomanagementservice.common.security;

import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class JWTHandler implements AuthenticationHandler {

    @Override
    public boolean canHandle(final Authentication authentication) {
        return authentication instanceof JwtAuthenticationToken;
    }

    public Optional<String> getDetail(final String detailKey, final Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken jwtToken) {
            return Optional.ofNullable(jwtToken.getToken().getClaimAsString(detailKey));
        } else {
            return Optional.empty();
        }
    }
}
