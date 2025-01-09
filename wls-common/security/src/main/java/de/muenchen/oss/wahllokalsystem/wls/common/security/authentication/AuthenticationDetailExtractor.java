package de.muenchen.oss.wahllokalsystem.wls.common.security.authentication;

import java.util.Optional;
import org.springframework.security.core.Authentication;

public interface AuthenticationDetailExtractor {

    /**
     * @throws IllegalArgumentException when authentication is null
     */
    boolean canHandle(Authentication authentication);

    /**
     * @throws IllegalArgumentException when any parameter is null
     */
    Optional<String> getDetail(String detailKey, Authentication authentication);
}
