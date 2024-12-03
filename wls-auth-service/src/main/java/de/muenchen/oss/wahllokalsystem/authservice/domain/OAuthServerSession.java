package de.muenchen.oss.wahllokalsystem.authservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
public final class OAuthServerSession {

    private String username;
    private String sessionId;
}
