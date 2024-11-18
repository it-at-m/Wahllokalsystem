package de.muenchen.oss.wahllokalsystem.authservice.domain;

import java.util.List;

public class OAuthServerSessions {

    private List<OAuthServerSession> sessions;

    public List<OAuthServerSession> getSessions() {
        return sessions;
    }

    public void setSessions(List<OAuthServerSession> sessions) {
        this.sessions = sessions;
    }
}
