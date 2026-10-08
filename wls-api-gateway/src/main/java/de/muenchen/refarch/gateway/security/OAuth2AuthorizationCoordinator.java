package de.muenchen.refarch.gateway.security;

import java.util.function.Supplier;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface OAuth2AuthorizationCoordinator {

    <T> Mono<T> execute(String key, Supplier<Mono<T>> operation);
}
