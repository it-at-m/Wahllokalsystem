package de.muenchen.refarch.gateway.security;

import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

public interface ReactiveSessionStore {

    /**
     * Registriert eine neue Session für einen User und sorgt dafür, dass höchstens maxSessions aktiv bleiben (alte ggf. invalidieren/markieren).
     */
    Mono<Void> registerSession(String username, WebSession session, int maxSessions);

    Mono<Boolean> isValid(String username, String sessionId);
}
