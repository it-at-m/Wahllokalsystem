package de.muenchen.oss.wahllokalsystem.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginService {

    private final WelcomeClient welcomeClient;

    public String getWelcomeMessage() {
        return welcomeClient.getWelcomeMessage();
    }
}
