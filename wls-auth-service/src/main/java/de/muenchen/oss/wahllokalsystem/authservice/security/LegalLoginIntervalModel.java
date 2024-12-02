package de.muenchen.oss.wahllokalsystem.authservice.security;

import java.time.LocalDateTime;

public record LegalLoginIntervalModel(
        LocalDateTime earliestLogin,
        LocalDateTime latestLogin
) {
}
