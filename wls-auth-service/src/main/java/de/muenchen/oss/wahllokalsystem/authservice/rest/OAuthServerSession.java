package de.muenchen.oss.wahllokalsystem.authservice.rest;

import lombok.Data;

@Data
public final class OAuthServerSession {

    private String username;
    private String sessionId;
}
