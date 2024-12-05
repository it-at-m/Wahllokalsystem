package de.muenchen.oss.wahllokalsystem.authservice.client;

import de.muenchen.oss.wahllokalsystem.authservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.authservice.security.LegalLoginIntervalModel;
import de.muenchen.oss.wahllokalsystem.authservice.security.LoginTimeClient;
import de.muenchen.oss.wahllokalsystem.authservice.service.WahltagClient;
import de.muenchen.oss.wahllokalsystem.authservice.service.WelcomeClient;
import java.time.LocalDateTime;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(Profiles.DUMMY_CLIENTS)
public class DummyClient implements WelcomeClient, LoginTimeClient, WahltagClient {

    @Override
    public String getWelcomeMessage() {
        return "greetings - powered by dummy client";
    }

    @Override
    public LegalLoginIntervalModel getLegalLoginInterval() {
        return new LegalLoginIntervalModel(LocalDateTime.MIN, LocalDateTime.MAX);
    }

    @Override
    public boolean isWahltagActive(final String wahltagID) {
        return true;
    }
}
