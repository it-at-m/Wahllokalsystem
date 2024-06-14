package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MDCFilter implements Filter {

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String user = (authentication != null && authentication.getPrincipal() != null) ? authentication.getPrincipal().toString() : "unknown";
        MDC.put("principal", user);

        try {
            chain.doFilter(req, resp);
        } finally {
            MDC.remove("principal");
        }
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }
}
