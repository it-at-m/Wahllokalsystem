package de.muenchen.oss.wahllokalsystem.wls.common.security;

import org.springframework.security.core.Authentication;

public interface BezirkIDPermissionEvaluator {

    boolean tokenUserBezirkIdMatches(String bezirkId, Authentication authentication);
}
