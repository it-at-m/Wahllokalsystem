package de.muenchen.oss.wahllokalsystem.wls.common.security.authentication;

import java.util.Optional;
import org.springframework.security.core.Authentication;

public interface AuthenticationHandler {

    boolean canHandle(Authentication authentication);

    Optional<String> getDetail(String detailKey, Authentication authentication);
}
