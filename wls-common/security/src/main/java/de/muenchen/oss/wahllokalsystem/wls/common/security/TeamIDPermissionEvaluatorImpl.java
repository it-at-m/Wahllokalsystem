package de.muenchen.oss.wahllokalsystem.wls.common.security;

import de.muenchen.oss.wahllokalsystem.wls.common.security.authentication.AuthDetailRetriever;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component(TeamIDPermissionEvaluatorImpl.COMPONENT_NAME)
@Profile("!" + Profiles.NO_BEZIRKS_ID_CHECK)
@RequiredArgsConstructor
@Slf4j
public class TeamIDPermissionEvaluatorImpl implements TeamIDPermissionEvaluator {

  private static final String AUTH_DETAILS_MAP_KEY_TEAM_ID = "teamID";

  private final List<AuthDetailRetriever> authDetailRetrievers;

  @Override
  public boolean tokenUserteamIdMatches(
      final String requiredTeamID, final Authentication authentication) {
    if (authentication == null) {
      log.warn("No authentication object for teamID = {}", requiredTeamID);
      return false;
    }

    if (requiredTeamID == null) {
      log.warn("Required teamID is null");
      return false;
    }

    val teamIDFromToken = getTeamID(authentication);
    return teamIDFromToken.map(s -> s.equals(requiredTeamID)).orElse(false);
  }

  private Optional<String> getTeamID(final Authentication auth) {
    return getClaim(auth, AUTH_DETAILS_MAP_KEY_TEAM_ID);
  }

  private Optional<String> getClaim(final Authentication authentication, final String claimKey) {
    val retriever =
        authDetailRetrievers.stream().filter(r -> r.canHandle(authentication)).findFirst();
    return retriever.flatMap(
        authDetailRetriever -> authDetailRetriever.getDetail(claimKey, authentication));
  }
}
