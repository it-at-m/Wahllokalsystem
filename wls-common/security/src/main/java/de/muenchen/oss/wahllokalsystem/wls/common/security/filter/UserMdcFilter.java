package de.muenchen.oss.wahllokalsystem.wls.common.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserMdcFilter extends OncePerRequestFilter {

  private static final String MDC_USER_KEY = "user";
  private final UsernameResolver usernameResolver;

  @Override
  protected void doFilterInternal(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final FilterChain filterChain)
      throws ServletException, IOException {
    try {
      val username = usernameResolver.resolve();
      log.debug("resolved username={}", username);

      if (StringUtils.hasText(username)) {
        MDC.put(MDC_USER_KEY, username);
      }
      filterChain.doFilter(request, response);
    } finally {
      MDC.remove(MDC_USER_KEY);
    }
  }
}
