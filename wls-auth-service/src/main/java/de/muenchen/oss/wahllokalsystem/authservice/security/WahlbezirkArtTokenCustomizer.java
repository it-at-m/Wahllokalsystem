package de.muenchen.oss.wahllokalsystem.authservice.security;

import de.muenchen.oss.wahllokalsystem.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

@RequiredArgsConstructor
@Slf4j
public class WahlbezirkArtTokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    private final UserService userService;

    @Override
    public void customize(JwtEncodingContext context) {
        if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
            val user = userService.getUser(context.getPrincipal().getName());
            if (user.isPresent()) {
                context.getClaims().claims(claims -> claims.put("wahlbezirksArt", user.get().wahlbezirksArt()));
            } else {
                log.warn("no user found with {}", context.getPrincipal().getName());
            }
        }
    }
}
