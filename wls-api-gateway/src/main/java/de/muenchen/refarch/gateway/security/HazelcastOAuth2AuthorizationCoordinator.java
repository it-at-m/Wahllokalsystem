package de.muenchen.refarch.gateway.security;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Coordinates OAuth2 authorization operations across gateway pods using an expiring Hazelcast
 * lease. The lease is not thread-bound and therefore safe to use around reactive operations.
 */
@Component
public class HazelcastOAuth2AuthorizationCoordinator implements OAuth2AuthorizationCoordinator {

    private static final String LEASE_MAP_NAME = "oauth2-authorization-leases";
    private static final Duration LEASE_DURATION = Duration.ofSeconds(30);
    private static final Duration LEASE_RETRY_DELAY = Duration.ofMillis(50);

    private final Supplier<HazelcastInstance> hazelcastInstanceSupplier;

    @Autowired
    public HazelcastOAuth2AuthorizationCoordinator(final ObjectProvider<HazelcastInstance> hazelcastInstanceProvider) {
        this(hazelcastInstanceProvider::getIfAvailable);
    }

    HazelcastOAuth2AuthorizationCoordinator(final HazelcastInstance hazelcastInstance) {
        this(() -> hazelcastInstance);
    }

    private HazelcastOAuth2AuthorizationCoordinator(final Supplier<HazelcastInstance> hazelcastInstanceSupplier) {
        this.hazelcastInstanceSupplier = hazelcastInstanceSupplier;
    }

    @Override
    public <T> Mono<T> execute(final String key, final Supplier<Mono<T>> operation) {
        final HazelcastInstance hazelcastInstance = hazelcastInstanceSupplier.get();
        if (hazelcastInstance == null) {
            return operation.get();
        }

        final IMap<String, String> leases = hazelcastInstance.getMap(LEASE_MAP_NAME);
        return Mono.usingWhen(
                acquireLease(leases, key),
                ignored -> operation.get(),
                owner -> releaseLease(leases, key, owner),
                (owner, error) -> releaseLease(leases, key, owner),
                owner -> releaseLease(leases, key, owner));
    }

    private Mono<String> acquireLease(final IMap<String, String> leases, final String key) {
        final String owner = UUID.randomUUID().toString();
        return Mono.fromCallable(() -> leases.putIfAbsent(
                key,
                owner,
                LEASE_DURATION.toMillis(),
                TimeUnit.MILLISECONDS) == null)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(acquired -> acquired
                        ? Mono.just(owner)
                        : Mono.delay(LEASE_RETRY_DELAY).then(acquireLease(leases, key)));
    }

    private Mono<Void> releaseLease(final IMap<String, String> leases, final String key, final String owner) {
        return Mono.fromRunnable(() -> leases.remove(key, owner))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}
