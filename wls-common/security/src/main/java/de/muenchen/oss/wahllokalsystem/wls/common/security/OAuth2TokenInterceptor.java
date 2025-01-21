package de.muenchen.oss.wahllokalsystem.wls.common.security;

import java.io.IOException;
import lombok.val;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.stereotype.Component;

@Component
public class OAuth2TokenInterceptor implements ClientHttpRequestInterceptor {

    @Override
    @NonNull
    public ClientHttpResponse intercept(@NonNull final HttpRequest request, @NonNull final byte[] body, @NonNull ClientHttpRequestExecution execution)
            throws IOException {
        val authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return execution.execute(request, body);
        }

        if (!(authentication.getCredentials() instanceof AbstractOAuth2Token token)) {
            return execution.execute(request, body);
        }

        request.getHeaders().setBearerAuth(token.getTokenValue());
        return execution.execute(request, body);
    }
}
