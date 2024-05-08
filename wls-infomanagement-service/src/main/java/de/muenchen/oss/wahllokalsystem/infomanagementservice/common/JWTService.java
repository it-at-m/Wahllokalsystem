package de.muenchen.oss.wahllokalsystem.infomanagementservice.common;

import java.util.Optional;
import lombok.val;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class JWTService {

    public Optional<String> getDetail(String detailKey) {
        val jwtToken = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Optional.ofNullable(jwtToken.getClaimAsString(detailKey));
    }
}
