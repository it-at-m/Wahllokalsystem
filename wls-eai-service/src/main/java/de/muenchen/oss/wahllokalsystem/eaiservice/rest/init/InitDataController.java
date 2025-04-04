package de.muenchen.oss.wahllokalsystem.eaiservice.rest.init;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.init.dto.WahltagInitRequestDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.service.init.InitDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/initData")
@RequiredArgsConstructor
public class InitDataController {

    private final InitDataService initDataService;

    @PostMapping("wahltage")
    public void initWahltage(@RequestBody WahltagInitRequestDTO request) {
        initDataService.initWahltage(request);
    }
}
