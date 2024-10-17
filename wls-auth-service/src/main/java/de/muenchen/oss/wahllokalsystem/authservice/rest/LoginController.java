package de.muenchen.oss.wahllokalsystem.authservice.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Slf4j
public class LoginController {

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(HttpServletRequest request, HttpServletResponse response) {
        val loginView = new ModelAndView();

        if (hasParameterValue(request, response, "admin")) {
            loginView.setViewName("loginat");
        } else if (hasParameterValue(request, response, "no")) {
            loginView.setViewName("nologin");
        } else if (hasParameterValue(request, response, "error")) {
            loginView.addObject("error", new RuntimeException("sth failed"));
            loginView.setViewName("error");
        } else {
            setupLoginWLS(loginView);
        }

        return loginView;
    }

    private boolean hasParameterValue(final HttpServletRequest request, final HttpServletResponse response, final String parameterValue) {
        val cacheHttpSessionRequest = new HttpSessionRequestCache().getRequest(request, response);

        if (cacheHttpSessionRequest != null) {
            return cacheHttpSessionRequest.getParameterValues(parameterValue) != null;
        } else {
            return request.getParameterValues(parameterValue) != null;
        }
    }

    private void setupLoginWLS(final ModelAndView modelAndView) {
        modelAndView.addObject("willkommensnachricht", "hello world");
        modelAndView.setViewName("loginwls");
    }
}
