package de.muenchen.oss.wahllokalsystem.authservice.domain;

import lombok.Data;

@Data
public final class OAuthServerSession {

    private String username;
    private String sessionId;
}
