package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.common.security;

import org.springframework.security.core.Authentication;

import java.util.Optional;

public interface AuthenticationHandler {

    boolean canHandle(Authentication authentication);

    Optional<String> getDetail(String detailKey, Authentication authentication);
}
