package de.muenchen.refarch.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ServiceIDFormatter;
import java.nio.charset.StandardCharsets;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
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
@RequiredArgsConstructor
public class GlobalBackendErrorFilter implements GlobalFilter, Ordered {

    public static final int ORDER_GLOBAL_FILTER = -3;
    private static final String GENERIC_WLS_EXCEPTION_FALLBACK_CONTENT = "{\"category\": \"T\", \"code\": \"000\", \"service\": \"WLS-API-GATEWAY\", \"message\": \"Internal Server Error\"}";

    private final ObjectMapper objectMapper;

    private final ServiceIDFormatter serviceIDFormatter;

    @Value("${service.generic500Exception.code:000}")
    private String generic500ExceptionCode;
    @Value("${service.generic500Exception.message:Internal Server Error}")
    private String generic500ExceptionMessage;

    private String genericError500Content;

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

                                if (isWlsException(responseBody)) {
                                    //return old body;
                                    return dataBufferFactory.wrap(responseBody.getBytes(StandardCharsets.UTF_8));
                                } else {
                                    final DataBuffer newDataBuffer = dataBufferFactory.wrap(
                                            getGenericError500Content().getBytes(StandardCharsets.UTF_8));
                                    getDelegate().getHeaders().setContentLength(newDataBuffer.readableByteCount());
                                    getDelegate().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                                    return newDataBuffer;
                                }
                            }));
                }
                return super.writeWith(body);
            }
        };

        // replace response with decorator
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    private boolean isWlsException(final String responseBody) {
        try {
            objectMapper.readValue(responseBody, WlsExceptionDTO.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String getGenericError500Content() {
        if (genericError500Content == null) {
            val genericWlsException = new WlsExceptionDTO(WlsExceptionCategory.T, generic500ExceptionCode, serviceIDFormatter.getId(),
                    generic500ExceptionMessage);
            try {
                genericError500Content = objectMapper.writeValueAsString(genericWlsException);
            } catch (JsonProcessingException e) {
                genericError500Content = GENERIC_WLS_EXCEPTION_FALLBACK_CONTENT;
            }
        }

        return genericError500Content;
    }
}
