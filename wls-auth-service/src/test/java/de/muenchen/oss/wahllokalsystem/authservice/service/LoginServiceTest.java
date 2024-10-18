package de.muenchen.oss.wahllokalsystem.authservice.service;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    WelcomeClient welcomeClient;

    @InjectMocks
    LoginService unitUnderTest;

    @Nested
    class GetWelcomeMessage {

        @Test
        void should_returnMessageOfTheClient_when_requestingTheMessage() {
            val welcomeMessageFromClient = "hello world";

            Mockito.when(welcomeClient.getWelcomeMessage()).thenReturn(welcomeMessageFromClient);

            val result = unitUnderTest.getWelcomeMessage();

            Assertions.assertThat(result).isEqualTo(welcomeMessageFromClient);
        }
    }

}
