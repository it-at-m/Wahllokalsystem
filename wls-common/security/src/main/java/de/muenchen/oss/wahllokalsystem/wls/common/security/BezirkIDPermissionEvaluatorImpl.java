package de.muenchen.oss.wahllokalsystem.wls.common.security;

import de.muenchen.oss.wahllokalsystem.wls.common.security.authentication.AuthDetailRetriever;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component(value = "bezirkIdPermissionEvaluator")
@Profile("!" + Profiles.NO_BEZIRKS_ID_CHECK)
@RequiredArgsConstructor
public class BezirkIDPermissionEvaluatorImpl implements BezirkIDPermissionEvaluator {

    private static final Logger LOG = LoggerFactory.getLogger(BezirkIDPermissionEvaluatorImpl.class);

    private static final String AUTH_DETAILS_MAP_KEY_WAHLBEZIRK_ID = "wahlbezirkID";

    private static final String AUTH_DETAILS_MAP_KEY_WAHLBEZIRKID_WAHLNUMMER = "wahlbezirkid_wahlnummer";

    private final List<AuthDetailRetriever> authDetailRetrievers;

    @Override
    public boolean tokenUserBezirkIdMatches(String bezirkId, Authentication auth) {
        if (auth == null) {
            LOG.warn("No authentication object for bezirkId={}", bezirkId);
            return false;
        }
        LOG.debug("tokenUserBezirkIdMatches {}, {}", bezirkId, auth.getPrincipal());
        try {
            val bezirkIDFromToken = getBezirkID(auth).orElse(null);
            val wahlbezirkid_wahlnummer = getWahlbezirkid_wahlnummer(auth).orElse(null);
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

    private Optional<String> getBezirkID(final Authentication auth) {
        return getClaim(auth, AUTH_DETAILS_MAP_KEY_WAHLBEZIRK_ID);
    }

    private Optional<String> getWahlbezirkid_wahlnummer(final Authentication auth) {
        return getClaim(auth, AUTH_DETAILS_MAP_KEY_WAHLBEZIRKID_WAHLNUMMER);
    }

    private Optional<String> getClaim(final Authentication authentication, final String claimKey) {
        val retriever = authDetailRetrievers.stream().filter(r -> r.canHandle(authentication)).findFirst();
        return retriever.flatMap(authDetailRetriever -> authDetailRetriever.getDetail(claimKey, authentication));

    }
}
