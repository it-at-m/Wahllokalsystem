package de.muenchen.oss.wahllokalsystem.wls.common.security;

import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component(TeamIDPermissionEvaluatorImpl.COMPONENT_NAME)
@Profile(Profiles.NO_BEZIRKS_ID_CHECK)
public class DummyTeamIDPermissionEvaluatorImpl implements TeamIDPermissionEvaluator {

  @Override
  public boolean tokenUserteamIdMatches(String bezirkId, Authentication authentication) {
    return true;
  }
}
