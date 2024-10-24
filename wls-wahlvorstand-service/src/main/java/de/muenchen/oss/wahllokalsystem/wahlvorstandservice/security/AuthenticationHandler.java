package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.security;

import java.util.Optional;
import org.springframework.security.core.Authentication;

public interface AuthenticationHandler {

    boolean canHandle(Authentication authentication);

    Optional<String> getDetail(String detailKey, Authentication authentication);
}
