package de.muenchen.refarch.gateway.configuration;

import de.muenchen.refarch.gateway.security.OAuth2AuthorizationCoordinator;
import de.muenchen.refarch.gateway.security.SingleFlightReactiveOAuth2AuthorizedClientManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.stereotype.Component;

@Component
public class SingleFlightReactiveOAuth2AuthorizedClientManagerPostProcessor implements BeanPostProcessor {

    private final ObjectProvider<OAuth2AuthorizationCoordinator> authorizationCoordinatorProvider;

    public SingleFlightReactiveOAuth2AuthorizedClientManagerPostProcessor(
            final ObjectProvider<OAuth2AuthorizationCoordinator> authorizationCoordinatorProvider) {
        this.authorizationCoordinatorProvider = authorizationCoordinatorProvider;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        if (bean instanceof ReactiveOAuth2AuthorizedClientManager manager
                && !(bean instanceof SingleFlightReactiveOAuth2AuthorizedClientManager)) {
            return new SingleFlightReactiveOAuth2AuthorizedClientManager(
                    manager,
                    authorizationCoordinatorProvider.getObject());
        }
        return bean;
    }
}
