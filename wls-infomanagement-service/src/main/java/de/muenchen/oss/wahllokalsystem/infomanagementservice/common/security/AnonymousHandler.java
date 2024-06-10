package de.muenchen.oss.wahllokalsystem.infomanagementservice.common.security;

import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@Profile("no-security")
public class AnonymousHandler implements AuthenticationHandler {
    @Override
    public boolean canHandle(Authentication authentication) {
        return authentication instanceof AnonymousAuthenticationToken;
    }

    @Override
    public Optional<String> getDetail(String detailKey, Authentication authentication) {
        return Optional.empty();
    }
}
