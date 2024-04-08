package de.muenchen.wls.common.security;

import org.springframework.security.core.Authentication;

/**
 * Interface um eine BezirkId gegen die BezirkId aus dem wls-infomanagement-service zu prüfen.
 *
 */
public interface BezirkIDPermissionEvaluator {
    /**
     * Checkt die BezirkId.
     *
     * @param bezirkId BezirkId aus dem Request
     * @param authentication Current authentication object
     * @return true wenn die BezirkId aus dem Request mit der BezirkId des users übereinstimmt, sonst
     *         false
     */
    public boolean tokenUserBezirkIdMatches(String bezirkId, Authentication authentication);
}
