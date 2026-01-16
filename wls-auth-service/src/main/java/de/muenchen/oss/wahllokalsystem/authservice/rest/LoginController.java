package de.muenchen.oss.wahllokalsystem.authservice.rest;

import de.muenchen.oss.wahllokalsystem.authservice.service.ErrorMessageService;
import de.muenchen.oss.wahllokalsystem.authservice.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LoginController {

  private static final String TEMPLATE_OBJECT_KEY_ERROR_MESSAGE = "error";

  private final LoginService loginService;

  private final ErrorMessageService errorMessageService;

  @Operation(
      description = "Liefert die Login Ansicht zurück",
      responses = {
        @ApiResponse(responseCode = "200", description = "Login Ansicht erfolgreich geliefert.")
      })
  @RequestMapping(value = "/login", method = RequestMethod.GET)
  public ModelAndView login(
      HttpServletRequest request,
      HttpServletResponse response,
      final @RequestParam(value = "error", required = false) String errorParameter) {
    val loginView = new ModelAndView();

    val errorMessage = getErrorMessageWhenAuthenticationExceptionExists(request);
    if (errorMessage != null && errorParameter != null) {
      loginView.addObject(TEMPLATE_OBJECT_KEY_ERROR_MESSAGE, errorMessage);
    }

    if (hasParameterValue(request, response, "admin")) {
      loginView.setViewName("loginat");
    } else if (hasParameterValue(request, response, "no")) {
      loginView.setViewName("nologin");
    } else {
      setupLoginWLS(loginView);
    }

    return loginView;
  }

  private boolean hasParameterValue(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final String parameterValue) {
    val cacheHttpSessionRequest = new HttpSessionRequestCache().getRequest(request, response);

    if (cacheHttpSessionRequest != null) {
      return cacheHttpSessionRequest.getParameterValues(parameterValue) != null;
    } else {
      return request.getParameterValues(parameterValue) != null;
    }
  }

  private void setupLoginWLS(final ModelAndView modelAndView) {
    modelAndView.addObject("willkommensnachricht", loginService.getWelcomeMessage());
    modelAndView.setViewName("loginwls");
  }

  private String getErrorMessageWhenAuthenticationExceptionExists(
      final HttpServletRequest request) {
    if (request.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION) != null) {
      val exception =
          (Exception) request.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
      return errorMessageService.getErrorMessage(exception);
    } else {
      return null;
    }
  }
}
