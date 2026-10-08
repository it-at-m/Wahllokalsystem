package de.muenchen.refarch.gateway.security;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class HazelcastOAuth2AuthorizationCoordinatorTest {

    private HazelcastInstance hazelcastInstance;

    @AfterEach
    void tearDown() {
        if (hazelcastInstance != null) {
            hazelcastInstance.shutdown();
        }
    }

    @Nested
    class Execute {
        @Test
        void should_executeOperationsSequentially_when_twoCoordinatorsUseSameLeaseKey() {
            hazelcastInstance = Hazelcast.newHazelcastInstance(new Config().setClusterName("oauth2-lease-test-" + UUID.randomUUID()));
            final HazelcastOAuth2AuthorizationCoordinator firstCoordinator = new HazelcastOAuth2AuthorizationCoordinator(hazelcastInstance);
            final HazelcastOAuth2AuthorizationCoordinator secondCoordinator = new HazelcastOAuth2AuthorizationCoordinator(hazelcastInstance);
            final AtomicInteger activeOperations = new AtomicInteger();
            final AtomicInteger maximumActiveOperations = new AtomicInteger();

            Flux.merge(
                    firstCoordinator.execute("user:sso", () -> operation(activeOperations, maximumActiveOperations)),
                    secondCoordinator.execute("user:sso", () -> operation(activeOperations, maximumActiveOperations)))
                    .collectList()
                    .block();

            assertEquals(1, maximumActiveOperations.get());
        }
    }

    private Mono<Integer> operation(final AtomicInteger activeOperations, final AtomicInteger maximumActiveOperations) {
        return Mono.defer(() -> {
            final int activeOperationCount = activeOperations.incrementAndGet();
            maximumActiveOperations.accumulateAndGet(activeOperationCount, Math::max);
            return Mono.delay(Duration.ofMillis(50))
                    .thenReturn(activeOperationCount)
                    .doFinally(signalType -> activeOperations.decrementAndGet());
        });
    }
}
