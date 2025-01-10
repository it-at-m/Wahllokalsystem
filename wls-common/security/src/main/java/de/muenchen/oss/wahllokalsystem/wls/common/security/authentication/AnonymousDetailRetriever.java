package de.muenchen.oss.wahllokalsystem.wls.common.security.authentication;

import de.muenchen.oss.wahllokalsystem.wls.common.security.Profiles;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Profile(Profiles.NO_SECURITY)
public class AnonymousDetailRetriever implements AuthDetailRetriever {

    @Override
    public boolean canHandle(Authentication authentication) {
        Assert.notNull(authentication, "Authentication must not be null");
        return authentication instanceof AnonymousAuthenticationToken;
    }

    @Override
    public Optional<String> getDetail(String detailKey, Authentication authentication) {
        Assert.notNull(authentication, "Authentication must not be null");
        Assert.notNull(detailKey, "detailKey must not be null");
        return Optional.empty();
    }
}
