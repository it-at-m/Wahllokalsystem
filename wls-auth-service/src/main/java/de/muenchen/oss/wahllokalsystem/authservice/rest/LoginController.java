package de.muenchen.oss.wahllokalsystem.authservice.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Slf4j
public class LoginController {

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(HttpServletRequest request, HttpServletResponse response) {
        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);

        val loginView = new ModelAndView();
        if (savedRequest.getParameterValues("admin") != null) {
            loginView.setViewName("loginat");
        } else if (savedRequest.getParameterValues("no") != null) {
            loginView.setViewName("nologin");
        } else if (savedRequest.getParameterValues("amt") != null) {
            loginView.setViewName("loginamt");
        } else {
            loginView.addObject("willkommensnachricht", "hello world");
            loginView.setViewName("loginwls");
        }

        return loginView;
    }

    @RequestMapping(value = "/loginAdmin", method = RequestMethod.GET)
    public ModelAndView loginAt() {
        val loginView = new ModelAndView();
        loginView.setViewName("loginat");

        return loginView;
    }
}
