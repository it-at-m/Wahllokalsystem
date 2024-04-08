package de.muenchen.wls.common.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component(value = "bezirkIdPermisionEvaluator")
@Profile(Profiles.NO_BEZIRKS_ID_CHECK)
public class DummyBezirkIdPermissionEvaluatorImpl implements BezirkIDPermissionEvaluator {

    private static final Logger LOG = LoggerFactory.getLogger(DummyBezirkIdPermissionEvaluatorImpl.class);

    @Override
    public boolean tokenUserBezirkIdMatches(String bezirkId, Authentication auth) {
        LOG.info("tokenUserBezirkIdMatches {}, {}", bezirkId, auth.getPrincipal());
        return true;
    }

    private String loadBezirkID(String username) {
        LOG.debug("#loadBezirkID {}", username);
        return "123";
    }
}
