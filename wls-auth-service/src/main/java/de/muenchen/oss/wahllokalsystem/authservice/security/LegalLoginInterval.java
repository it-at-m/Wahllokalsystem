package de.muenchen.oss.wahllokalsystem.authservice.security;

import java.time.LocalDateTime;

public record LegalLoginInterval(
        LocalDateTime earliestLogin,
        LocalDateTime latestLogin
) {
}
