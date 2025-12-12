package de.muenchen.oss.wahllokalsystem.authservice.service;

import lombok.Data;

@Data
public final class OAuthServerSession {

  private String username;
  private String sessionId;
}
