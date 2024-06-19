package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
@RequiredArgsConstructor
public class ClientDemoRestController {

    private final DemoClient demoClient;

    @GetMapping
    public DemoDTO getDemo() {
        return demoClient.getDemo();
    }
}
