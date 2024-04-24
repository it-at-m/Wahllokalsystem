package de.muenchen.oss.wahllokalsystem.wls.common.security;

import java.util.Map;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component(value = "bezirkIdPermisionEvaluator")
@Profile("!" + Profiles.NO_BEZIRKS_ID_CHECK)
public class BezirkIDPermissionEvaluatorImpl implements BezirkIDPermissionEvaluator {

    private static final Logger LOG = LoggerFactory.getLogger(BezirkIDPermissionEvaluatorImpl.class);

    private static final String AUTH_DETAILS_MAP_KEY_WAHLBEZIRK_ID = "wahlbezirkID";

    private static final String AUTH_DETAILS_MAP_KEY_WAHLBEZIRKID_WAHLNUMMER = "wahlbezirkid_wahlnummer";

    @Override
    public boolean tokenUserBezirkIdMatches(String bezirkId, Authentication auth) {
        if (auth == null) {
            LOG.warn("No authentication object for bezirkId={}", bezirkId);
            return false;
        }
        LOG.debug("tokenUserBezirkIdMatches {}, {}", bezirkId, auth.getPrincipal());
        try {
            val bezirkIDFromToken = getBezirkID(auth);
            val wahlbezirkid_wahlnummer = getWahlbezirkid_wahlnummer(auth);
            val bezirkIdMatches = (bezirkId != null)
                    && (bezirkId.equals(bezirkIDFromToken) || (wahlbezirkid_wahlnummer != null && wahlbezirkid_wahlnummer.contains(bezirkId)));
            LOG.debug("Check bezirkId {} from request against username {}, bezirkId {} from token or wahlbezirkid_wahlnummer {}. RESULT = {}",
                    bezirkId,
                    auth.getPrincipal(),
                    bezirkIDFromToken,
                    wahlbezirkid_wahlnummer,
                    bezirkIdMatches);
            return bezirkIdMatches;
        } catch (Exception e) {
            LOG.error("Error while checking bezirkId.", e);
            return false;
        }
    }

    private String getBezirkID(final Authentication auth) {
        val details = (Map) auth.getDetails();
        return (String) details.get(AUTH_DETAILS_MAP_KEY_WAHLBEZIRK_ID);
    }

    private String getWahlbezirkid_wahlnummer(final Authentication auth) {
        val details = (Map) auth.getDetails();
        return (String) details.get(AUTH_DETAILS_MAP_KEY_WAHLBEZIRKID_WAHLNUMMER);
    }
}
