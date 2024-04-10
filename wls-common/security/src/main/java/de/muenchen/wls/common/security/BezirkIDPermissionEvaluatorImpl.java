package de.muenchen.wls.common.security;

import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component(value = "bezirkIdPermisionEvaluator")
@Profile("!" + Profiles.NO_BEZIRKS_ID_CHECK)
public class BezirkIDPermissionEvaluatorImpl implements BezirkIDPermissionEvaluator {

    private static final Logger LOG = LoggerFactory.getLogger(BezirkIDPermissionEvaluatorImpl.class);

    private static final String WAHLBEZIRK_ID = "wahlbezirkID";

    private static final String WBID_WAHLNUMMER = "wbid_wahlnummer";

    @Override
    public boolean tokenUserBezirkIdMatches(String bezirkId, Authentication auth) {
        if (auth == null) {
            LOG.warn("No authentication object for bezirkId={}", bezirkId);
            return false;
        }
        LOG.debug("tokenUserBezirkIdMatches {}, {}", bezirkId, auth.getPrincipal());
        try {
            val bezirkIDFromToken = getBezirkID(auth);
            val wbid_wnr = getWbid_wahlnummer(auth);
            boolean bezirkIdMatches = (bezirkId != null) && (bezirkId.equals(bezirkIDFromToken) || (wbid_wnr != null && wbid_wnr.contains(bezirkId)));
            LOG.debug("Check bezirkId {} from request against username {}, bezirkId {} from token or wbid_wahlnummer {}. RESULT = {}",
                    bezirkId,
                    auth.getPrincipal(),
                    bezirkIDFromToken,
                    wbid_wnr,
                    bezirkIdMatches);
            return bezirkIdMatches;
        } catch (Exception e) {
            LOG.error("Error while checking bezirkId.", e);
            return false;
        }
    }
    private String getBezirkID(final Authentication auth) {
        val details = (Map) auth.getDetails();
        return (String) details.get(WAHLBEZIRK_ID);
    }

    private String getWbid_wahlnummer(Authentication auth) {
        val details = (Map) auth.getDetails();
        return (String) details.get(WBID_WAHLNUMMER);
    }
}
