package de.muenchen.oss.wahllokalsystem.authservice.domain;

import java.util.List;
import lombok.Data;

@Data
public class OAuthServerSessions {

    private List<OAuthServerSession> sessions;

}
