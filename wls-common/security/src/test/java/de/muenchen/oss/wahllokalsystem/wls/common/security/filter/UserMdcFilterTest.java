package de.muenchen.oss.wahllokalsystem.wls.common.security.filter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

@ExtendWith(MockitoExtension.class)
class UserMdcFilterTest {

  private static final String MDC_USER_KEY = "user";

  @InjectMocks private UserMdcFilter userMdcFilter;
  @Mock private UsernameResolver usernameResolver;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private FilterChain filterChain;

  @BeforeEach
  void setup() {
    MDC.clear();
  }

  @AfterEach
  void teardown() {
    MDC.clear();
  }

  @Test
  void should_proceedWithoutMdc_when_resolverReturnsNullOrBlank()
      throws ServletException, IOException {
    when(usernameResolver.resolve()).thenReturn(null);

    userMdcFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    assertNull(MDC.get(MDC_USER_KEY));
  }

  @Test
  void should_setMdcDuringRequestAndClearAfterwards_when_usernameIsResolved()
      throws ServletException, IOException {
    when(usernameResolver.resolve()).thenReturn("test.user");

    doAnswer(
            invocation -> {
              // Überprüfen, dass der User WÄHREND der Filterkette im MDC existiert
              assertEquals("test.user", MDC.get(MDC_USER_KEY));
              return null;
            })
        .when(filterChain)
        .doFilter(request, response);

    userMdcFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    // Überprüfen, dass nach dem Request der MDC wieder sauber ist
    assertNull(MDC.get(MDC_USER_KEY));
  }

  @Test
  void should_stillClearMdc_when_filterChainThrowsException() throws ServletException, IOException {
    when(usernameResolver.resolve()).thenReturn("error.user");
    doThrow(new RuntimeException("Filter crash!")).when(filterChain).doFilter(request, response);

    assertThrows(
        RuntimeException.class,
        () -> userMdcFilter.doFilterInternal(request, response, filterChain));

    // Der finally-Block muss gegriffen haben
    assertNull(MDC.get(MDC_USER_KEY));
  }
}
