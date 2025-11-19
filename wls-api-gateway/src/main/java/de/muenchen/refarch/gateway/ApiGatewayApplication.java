package de.muenchen.refarch.gateway;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import reactor.core.publisher.Hooks;

/**
 * To do some base configuration for the non blocking client-server framework named Netty via
 * properties use the properties listed in the link down below:
 * <p>
 * <a href=
 * "https://projectreactor.io/docs/netty/release/api/constant-values.html">https://projectreactor.io/docs/netty/release/api/constant-values.html</a>
 * <p>
 * As listed below, this above-mentioned properties should be set before the application startup:
 *
 * <ul>
 * <li>As command line argument: e.g. -Dreactor.netty.pool.maxConnections=1000.
 * <li>As environmental property in Openshift: e.g. with key REACTOR_NETTY_POOL_MAXCONNECTIONS
 * and value 1000.
 * <li>As programatically set property before call {@link SpringApplication#run} in
 * {@link ApiGatewayApplication#main}: e.g.
 * <code>System.setProperty("reactor.netty.pool.maxConnections", "1000");</code>.
 * </ul>
 * <p>
 * To get more information about Spring Cloud Gateway visit the following link:
 * <a href=
 * "https://cloud.spring.io/spring-cloud-gateway/reference/html/">https://cloud.spring.io/spring-cloud-gateway/reference/html/</a>
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class ApiGatewayApplication {

    public static void main(final String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    /**
     * Setup reactive tracing for context propagation between threads.
     */
    @PostConstruct
    public void initReactiveTracing() {
        Hooks.enableAutomaticContextPropagation();
    }
}
