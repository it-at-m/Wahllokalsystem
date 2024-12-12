package de.muenchen.oss.wahllokalsystem.authservice.rest;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import de.muenchen.oss.wahllokalsystem.authservice.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SessionController {

    private final SessionService sessionService;

    /**
     * Lists all sessions which are not expired.
     *
     * @return OAuthServerSessions which contains a list of OAuthServerSession (sessionId and
     *         username)
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN_ADMIN')")
    @GetMapping(value = "/oauthsessions/")
    public ResponseEntity<OAuthServerSessions> listActiveSessions() {
        return ResponseEntity.ok(sessionService.getActiveSessions());
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
        return sessionService.killSession(sessionID) ? new ResponseEntity<>(OK) : new ResponseEntity<>(NOT_FOUND);
    }

}
