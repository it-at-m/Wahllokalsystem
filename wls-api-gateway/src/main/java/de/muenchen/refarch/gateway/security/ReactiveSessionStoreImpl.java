package de.muenchen.refarch.gateway.security;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class ReactiveSessionStoreImpl implements ReactiveSessionStore {

    // username -> FIFO-Liste von gültigen Session-IDs
    private final Map<String, Deque<WebSession>> sessionsByUser = new ConcurrentHashMap<>();

    // explizit ungültige Session-IDs
    private final Set<String> invalidSessions = ConcurrentHashMap.newKeySet();

    @Override
    public Mono<Void> registerSession(String username, WebSession session, int maxSessions) {
        return Mono.fromRunnable(() -> {
            log.info("Registering session {} for user {}", session.getId(), username);
            Deque<WebSession> sessions =
                    sessionsByUser.computeIfAbsent(username, u -> new ArrayDeque<>());
            log.info("Existing sessions count for user {}: {}", username, sessions.size());

            // neue Session hinzufügen
            sessions.addLast(session);

            // wenn zu viele Sessions: älteste invalidieren
            while (sessions.size() > maxSessions) {
                WebSession old = sessions.removeFirst();
                invalidSessions.add(old.getId());
                old.invalidate();
            }
        });
    }

    @Override
    public Mono<Boolean> isValid(String username, String sessionId) {
        return Mono.fromSupplier(() -> {
            log.info("Checking validity of session {} for user {}", sessionId, username);
            // 1. Wenn Session nie registriert wurde → gültig (frisch)
            Deque<WebSession> sessions = sessionsByUser.get(username);
            if (sessions == null || !sessions.stream().map(WebSession::getId).toList().contains(sessionId)) {
                log.debug("Session {} is valid", sessionId);
                return true;
            }

            // 2. Wenn explizit invalidiert → ungültig
            boolean isInvalid = !invalidSessions.contains(sessionId);
            log.info("Session {} is {}valid", sessionId, isInvalid ? "" : "not ");
            return isInvalid;
        });
    }

}
