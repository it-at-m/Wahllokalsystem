package de.muenchen.refarch.gateway.filter;

import java.nio.charset.StandardCharsets;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * This {@link GlobalFilter} replaces the body by a generic error body, when a server responses with
 * a {@link HttpStatus#INTERNAL_SERVER_ERROR}.
 */
@Component
@Slf4j
public class GlobalBackend5xxTo400Mapper implements GlobalFilter, Ordered {

    public static final int ORDER_GLOBAL_FILTER = -3;
    private static final String GENERIC_ERROR_400 = "{ \"status\":400, \"error\":\"Bad Request\" }";
    private static final String GENERIC_ERROR_500 = "{ \"status\":500, \"error\":\"Internal Server Error\" }";
    /**
     * Variable entscheidet, ob alle 5xx Fehler auf 400 gemappt werden sollen.
     **/
    @Value("${config.map5xxto400: true}")
    private boolean map5xxTo400;

    @Override
    public int getOrder() {
        return ORDER_GLOBAL_FILTER;
    }

    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final GatewayFilterChain chain) {
        final ServerHttpResponse response = exchange.getResponse();
        final ServerHttpRequest request = exchange.getRequest();
        final DataBufferFactory dataBufferFactory = response.bufferFactory();

        final ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(response) {

            @Override
            @NonNull public Mono<Void> writeWith(@NonNull final Publisher<? extends DataBuffer> body) {
                final HttpStatusCode responseHttpStatus = getDelegate().getStatusCode();

                if (body instanceof Flux<? extends DataBuffer> flux && responseHttpStatus != null && responseHttpStatus.is5xxServerError()) {
                    return super.writeWith(flux.buffer().map(
                            // replace old body represented by dataBuffer by the new one

                            dataBuffer -> {
                                // Log-Ausgabe
                                final DefaultDataBuffer joinedBuffers = new DefaultDataBufferFactory().join(dataBuffer);
                                final byte[] content = new byte[joinedBuffers.readableByteCount()];
                                joinedBuffers.read(content);
                                final String responseBody = new String(content, StandardCharsets.UTF_8);
                                log.error("Error: 5xx vom Backend:  requestId: {}, method: {}, url: {}, \nresponse body :{}, statusCode: {}", request.getId(),
                                        request.getMethod(), request.getURI(), responseBody, responseHttpStatus);

                                // Response manipulieren
                                final DataBuffer newDataBuffer;
                                if (map5xxTo400) {
                                    getDelegate().setStatusCode(HttpStatus.BAD_REQUEST);
                                    newDataBuffer = dataBufferFactory.wrap(
                                            GENERIC_ERROR_400.getBytes(StandardCharsets.UTF_8));
                                } else {
                                    getDelegate().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                                    newDataBuffer = dataBufferFactory.wrap(
                                            GENERIC_ERROR_500.getBytes(StandardCharsets.UTF_8));
                                }

                                getDelegate().getHeaders().setContentLength(newDataBuffer.readableByteCount());
                                getDelegate().getHeaders().setContentType(MediaType.APPLICATION_JSON);

                                return newDataBuffer;
                            }));
                }
                return super.writeWith(body);
            }
        };

        // replace response with decorator
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }
}
