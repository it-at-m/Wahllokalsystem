package de.muenchen.wls.common.monitoring.metrics.filter;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.util.NestedServletException;

public class HttpRequestMetricFilter extends OncePerRequestFilter {

    private Pattern businessActionsPattern = Pattern.compile("(/businessActions/[^/.]+)(/.*)?");
    private List<String> endpointWhitelist = new ArrayList<>(Collections.singletonList("/businessActions/"));

    private String timerName;
    private MeterRegistry registry;
    private List<String> endpointBlacklist;

    public HttpRequestMetricFilter(String timerName, MeterRegistry registry, List<String> endpointWhitelist, List<String> endpointBlacklist) {
        this.timerName = timerName;
        this.registry = registry;
        this.endpointBlacklist = endpointBlacklist;
        this.endpointWhitelist.addAll(endpointWhitelist);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!isTimedEndpoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        Timer.Sample sample = Timer.start(registry);
        try {
            filterChain.doFilter(request, response);
            Throwable exception = (Throwable) request.getAttribute(DispatcherServlet.EXCEPTION_ATTRIBUTE);
            stopTimer(sample, request, response, exception);
        } catch (NestedServletException ex) {
            stopTimer(sample, request, response, ex.getCause());
            throw ex;
        } catch (Exception ex) {
            stopTimer(sample, request, response, ex);
            throw ex;
        }
    }

    private boolean isTimedEndpoint(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (endpointBlacklist.stream().anyMatch(uri::contains)) {
            return false;
        }
        return endpointWhitelist.stream().anyMatch(uri::startsWith);
    }

    private void stopTimer(Timer.Sample sample, HttpServletRequest request, HttpServletResponse response, Throwable exception) {
        sample.stop(Timer.builder(timerName)
                .tags("endpoint", normalizeUri(request.getRequestURI()))
                .tags("method", request.getMethod())
                .tags("status", String.valueOf(response.getStatus()))
                .tags("outcome", HttpStatus.valueOf(response.getStatus()).series().name())
                .tags("exception", exception != null ? exception.getClass().getSimpleName() : "none")
                .register(registry));
    }

    private String normalizeUri(String endpointUri) {
        Matcher baMatcher = businessActionsPattern.matcher(endpointUri);
        if (baMatcher.matches()) {
            return baMatcher.replaceAll("$1");
        }
        return endpointUri;
    }

}
