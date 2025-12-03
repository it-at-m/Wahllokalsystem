package de.muenchen.refarch.gateway.filter;

import java.nio.charset.StandardCharsets;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * This {@link GlobalFilter} replaces the body by a generic authentication error body, when a server
 * responses with a {@link HttpStatus#UNAUTHORIZED}.
 * <p>
 * The header {@link HttpHeaders#WWW_AUTHENTICATE} containing the access token is removed by the
 * property 'RemoveResponseHeader' in the corresponding route
 * within 'application.yml'.
 */
@Component
@Slf4j
public class GlobalAuthenticationErrorFilter implements GlobalFilter, Ordered {

    public static final int ORDER_GLOBAL_FILTER = -3;
    private static final String GENERIC_AUTHENTICATION_ERROR = "{ \"status\":401, \"error\":\"Authentication Error\" }";

    @Override
    public int getOrder() {
        return ORDER_GLOBAL_FILTER;
    }

    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final GatewayFilterChain chain) {
        log.debug("Check for authentication errors");

        final ServerHttpResponse response = exchange.getResponse();
        final ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(response) {

            /**
             * This overridden method adds the response body given in the parameter of
             * the surrounding method when the http status given in the parameter of
             * the surrounding method is met otherwise the already appended body will be used.
             *
             * @param body The body received by the upstream response.
             * @return Either the body received by the upstream response or
             *         the body given by the parameter.
             */
            @Override
            @NonNull public Mono<Void> writeWith(@NonNull final Publisher<? extends DataBuffer> body) {
                final HttpStatusCode responseHttpStatus = getDelegate().getStatusCode();
                if (body instanceof Flux<? extends DataBuffer> flux && responseHttpStatus != null && responseHttpStatus.equals(HttpStatus.UNAUTHORIZED)) {
                    final DataBufferFactory dataBufferFactory = response.bufferFactory();
                    final DataBuffer newDataBuffer = dataBufferFactory.wrap(
                            GENERIC_AUTHENTICATION_ERROR.getBytes(StandardCharsets.UTF_8));

                    log.debug("Response from upstream {} get new response body: {}", HttpStatus.UNAUTHORIZED, GENERIC_AUTHENTICATION_ERROR);
                    getDelegate().getHeaders().setContentLength(newDataBuffer.readableByteCount());
                    getDelegate().getHeaders().setContentType(MediaType.APPLICATION_JSON);

                    return super.writeWith(flux.buffer().map(
                            // replace old body represented by dataBuffer by the new one
                            dataBuffer -> newDataBuffer));
                }
                return super.writeWith(body);
            }

        };

        final ServerWebExchange swe = exchange.mutate().response(decoratedResponse).build();
        return chain.filter(swe);
    }

}
