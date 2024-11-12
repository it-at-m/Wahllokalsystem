package de.muenchen.oss.wahllokalsystem.authservice.client;

import de.muenchen.oss.wahllokalsystem.authservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.authservice.service.WelcomeClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(Profiles.DUMMY_CLIENTS)
public class DummyClient implements WelcomeClient {

    @Override
    public String getWelcomeMessage() {
        return "greetings - powered by dummy client";
    }
}
