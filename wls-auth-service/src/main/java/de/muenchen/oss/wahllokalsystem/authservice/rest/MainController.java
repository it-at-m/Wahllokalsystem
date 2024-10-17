package de.muenchen.oss.wahllokalsystem.authservice.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("demo")
public class MainController {

    private final String WLS_VIEW = "loginwls";

    @GetMapping
    public String hanelGetMapping() {
        return "we got it - yeah";
    }
}
