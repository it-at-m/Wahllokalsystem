package de.muenchen.oss.wahllokalsystem.authservice.service;

import de.muenchen.oss.wahllokalsystem.authservice.rest.OAuthServerSession;
import de.muenchen.oss.wahllokalsystem.authservice.rest.OAuthServerSessions;
import java.lang.reflect.InvocationTargetException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionService {

    @Autowired
    SessionRegistry sessionRegistry;

    public OAuthServerSessions getActiveSessions() {
        log.info("getActiveSessions()");

        OAuthServerSessions allSessionInformation = new OAuthServerSessions();
        List<OAuthServerSession> sessions = new ArrayList<>();

        sessionRegistry.getAllPrincipals().forEach(
                principal -> sessionRegistry.getAllSessions(principal, false).forEach(currSessionInfo -> {
                    OAuthServerSession currSession = new OAuthServerSession();
                    if (principal instanceof String principalString) {
                        currSession.setUsername(principalString);
                    } else if (principal instanceof org.springframework.security.core.userdetails.User principalUser) {
                        currSession.setUsername(principalUser.getUsername());
                    } else if (principal instanceof Principal principalPrincipal) {
                        currSession.setUsername(principalPrincipal.getName());
                    } else if (principal instanceof LdapUserDetailsImpl principalLdap) {
                        currSession.setUsername(principalLdap.getUsername());
                    } else if (principal instanceof Jwt principalJwt) {
                        currSession.setUsername(principalJwt.getSubject());
                    } else {
                        log.warn("Principal was not instance of expected datatypes, but is an Object of Class: {}.", principal.getClass().getName());
                        try {
                            currSession.setUsername((String) principal.getClass().getMethod("getUsername").invoke(principal));
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                            currSession.setUsername("<unknown>");
                            log.warn("Failed to retrieve username via reflection for principal class {}.", principal.getClass().getName(), e);
                        }
                    }
                    currSession.setSessionId(currSessionInfo.getSessionId());
                    sessions.add(currSession);
                }));

        allSessionInformation.setSessions(sessions);
        return allSessionInformation;
    }

    public boolean killSession(String sessionId) {
        log.info("killSession({})", sessionId);
        SessionInformation sessionInformation = sessionRegistry.getSessionInformation(sessionId);
        if (sessionInformation != null && !sessionInformation.isExpired()) {
            if (sessionInformation.getPrincipal() != null) {
                log.info("Killing session with id: {} principal: {}", sessionId, sessionInformation.getPrincipal());
            } else {
                log.info("Killing session with id: {} principal is null", sessionId);
            }
            sessionInformation.expireNow();
            return true;
        } else {
            log.info("Session with id {} not found.", sessionId);
            return false;
        }
    }
}
