package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.security;

import org.springframework.security.core.Authentication;

import java.util.Optional;

// todo: ist das im richtigen package?
public interface AuthenticationHandler {

    boolean canHandle(Authentication authentication);

    Optional<String> getDetail(String detailKey, Authentication authentication);
}
