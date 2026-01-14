package de.muenchen.oss.wahllokalsystem.authservice.configuration;

import de.muenchen.oss.wahllokalsystem.authservice.security.CustomUsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

/**
 * alternative implementation of {@link org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer}
 *
 * Implementation was required to use our own AuthenticationFilter and show authentication exception on the form.
 * With the existing formLoginConfigurer it was not possible to change the AuthenticationFilter
 */
@Component
class WlsFormLoginConfigurer<H extends HttpSecurityBuilder<H>> extends
        AbstractAuthenticationFilterConfigurer<H, WlsFormLoginConfigurer<H>, CustomUsernamePasswordAuthenticationFilter> {

    public WlsFormLoginConfigurer(CustomUsernamePasswordAuthenticationFilter customUsernamePasswordAuthenticationFilter) {
        super(customUsernamePasswordAuthenticationFilter, null);

        usernameParameter("username");
        passwordParameter("password");

        permitAll();
    }

    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, loginProcessingUrl);
    }

    public WlsFormLoginConfigurer<H> passwordParameter(String passwordParameter) {
        getAuthenticationFilter().setPasswordParameter(passwordParameter);
        return this;
    }

    public WlsFormLoginConfigurer<H> usernameParameter(String usernameParameter) {
        getAuthenticationFilter().setUsernameParameter(usernameParameter);
        return this;
    }

    @Override
    public void init(H http) throws Exception {
        super.init(http);

        loginPage("/login");
        initDefaultLoginFilter(http);
    }

    private String getUsernameParameter() {
        return getAuthenticationFilter().getUsernameParameter();
    }

    private String getPasswordParameter() {
        return getAuthenticationFilter().getPasswordParameter();
    }

    private void initDefaultLoginFilter(H http) {
        DefaultLoginPageGeneratingFilter loginPageGeneratingFilter = http
                .getSharedObject(DefaultLoginPageGeneratingFilter.class);
        if (loginPageGeneratingFilter != null && !isCustomLoginPage()) {
            loginPageGeneratingFilter.setFormLoginEnabled(true);
            loginPageGeneratingFilter.setUsernameParameter(getUsernameParameter());
            loginPageGeneratingFilter.setPasswordParameter(getPasswordParameter());
            loginPageGeneratingFilter.setLoginPageUrl(getLoginPage());
            loginPageGeneratingFilter.setFailureUrl(getFailureUrl());
            loginPageGeneratingFilter.setAuthenticationUrl(getLoginProcessingUrl());
        }
    }
}
