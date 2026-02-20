package de.muenchen.oss.wahllokalsystem.authservice.service;

import java.util.List;
import lombok.Data;

@Data
public class OAuthServerSessions {

  private List<OAuthServerSession> sessions;
}
