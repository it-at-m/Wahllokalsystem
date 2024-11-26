package de.muenchen.oss.wahllokalsystem.authservice.rest;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import de.muenchen.oss.wahllokalsystem.authservice.domain.OAuthServerSession;
import de.muenchen.oss.wahllokalsystem.authservice.domain.OAuthServerSessions;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SessionController {

    @Autowired
    SessionRegistry sessionRegistry;

    /**
     * Is needed for explicit deleting Jdbc-Session from Table SPRING_SESSION, necessary because
     * removing Session from @v{ sessionRegistry } does not delete it from Database
     */
    @Autowired
    JdbcIndexedSessionRepository jdbcSessionRepository;

    /**
     * Lists all sessions which are not expired.
     *
     * @return OAuthServerSessions which contains a list of OAuthServerSession (sessionId and
     *         username)
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN_ADMIN')")
    @GetMapping(value = "/oauthsessions/")
    public ResponseEntity<OAuthServerSessions> listActiveSessions() {
        log.info("listActiveSessions");

        OAuthServerSessions allSessionInformation = new OAuthServerSessions();
        List<OAuthServerSession> sessions = new ArrayList<>();

        sessionRegistry.getAllPrincipals().forEach(
                principal -> sessionRegistry.getAllSessions(principal, false).forEach(currSessionInfo -> {
                    log.info("Principal is instanceof" + principal.getClass().getName());
                    OAuthServerSession currSession = new OAuthServerSession();
                    if (principal instanceof String) {
                        currSession.setUsername((String) principal);
                        log.info("PrincipalName String:" + principal);
                    } else if (principal instanceof org.springframework.security.core.userdetails.User) {
                        currSession.setUsername(((org.springframework.security.core.userdetails.User) principal).getUsername());
                        log.info("PrincipalName org.springframework.security.core.userdetails.User:"
                                + ((org.springframework.security.core.userdetails.User) principal).getUsername());
                    } else if (principal instanceof Principal) {
                        currSession.setUsername(((Principal) principal).getName());
                        log.info("PrincipalName Principal:" + ((Principal) principal).getName());
                    } else {
                        try {
                            currSession.setUsername((String) principal.getClass().getMethod("getUsername").invoke(principal));
                        } catch (Exception e) {
                            currSession.setUsername("<unknown>");
                            log.info("PrincipalName:" + "<unknown>");
                        }
                    }
                    currSession.setSessionId(currSessionInfo.getSessionId());
                    sessions.add(currSession);
                }));

        allSessionInformation.setSessions(sessions);
        return new ResponseEntity<>(allSessionInformation, OK);
    }

    /**
     * Kills sessions which are not expired.
     *
     * @param sessionID SessionId of session to kill.
     * @return HTTP ok if session was killed successfully, HTTP 404 if session to kill was not found
     *         or session was expired.
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN_ADMIN')")
    @RequestMapping(value = "/oauthsessions/{sessionID}/invalidate", method = POST)
    public ResponseEntity<?> killSession(@PathVariable("sessionID") String sessionID) {
        log.info("Attempt to kill session with id {}", sessionID);
        HttpStatus httpStatus = OK;
        SessionInformation sessionInformation = sessionRegistry.getSessionInformation(sessionID);
        if (sessionInformation != null && !sessionInformation.isExpired()) {
            log.info("Killing session with id {}", sessionID + " principal: " + sessionInformation.getPrincipal());
            sessionInformation.expireNow();
            jdbcSessionRepository.deleteById(sessionID);
        } else {
            log.info("Session with id {} not found.", sessionID);
            httpStatus = NOT_FOUND;
        }
        return new ResponseEntity<>(httpStatus);
    }

}
