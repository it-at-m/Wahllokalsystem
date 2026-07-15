package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.security;

import org.springframework.security.core.Authentication;

public interface TeamIDPermissionEvaluator {

  String COMPONENT_NAME = "teamIDPermissionEvaluator";

  boolean tokenUserteamIdMatches(String requiredTeamID, Authentication authentication);
}
