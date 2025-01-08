package de.muenchen.oss.wahllokalsystem.wls.common.security;

import java.io.IOException;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;

@ExtendWith(MockitoExtension.class)
class OAuth2TokenInterceptorTest {

    @Mock
    HttpRequest httpRequest;

    byte[] body = new byte[0];

    @Mock
    ClientHttpRequestExecution clientHttpRequestExecution;

    @InjectMocks
    OAuth2TokenInterceptor unitUnderTest;

    @Nested
    class Intercept {

        @Test
        void should_callExecuteWithoutChange_when_authenticationIsNull() throws IOException {
            SecurityContextHolder.getContext().setAuthentication(null);

            unitUnderTest.intercept(httpRequest, body, clientHttpRequestExecution);

            Mockito.verify(clientHttpRequestExecution).execute(httpRequest, body);
            Mockito.verifyNoInteractions(httpRequest);
            Assertions.assertThat(body).isEmpty();
        }

        @Test
        void should_callExecuteWithoutChange_when_authenticationCredentialsIsNotInstanceOfAbstractOAuth2Token() throws IOException {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("principal", "credentials"));

            unitUnderTest.intercept(httpRequest, body, clientHttpRequestExecution);

            Mockito.verify(clientHttpRequestExecution).execute(httpRequest, body);
            Mockito.verifyNoInteractions(httpRequest);
            Assertions.assertThat(body).isEmpty();
        }

        @Test
        void should_callExecuteBearerToken_when_authenticationCredentialsIsInstanceOfOAuth2Token() throws IOException {
            val tokenValue = "tokenValue";
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("principal", new AbstractOAuth2Token(tokenValue) {
            }));

            val mockedHttpHeaders = new HttpHeaders();
            Mockito.when(httpRequest.getHeaders()).thenReturn(mockedHttpHeaders);

            unitUnderTest.intercept(httpRequest, body, clientHttpRequestExecution);

            Mockito.verify(clientHttpRequestExecution).execute(httpRequest, body);
            Assertions.assertThat(mockedHttpHeaders.get("Authorization")).containsExactly("Bearer " + tokenValue);
            Assertions.assertThat(body).isEmpty();
        }
    }

}
