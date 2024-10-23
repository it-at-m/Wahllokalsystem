package de.muenchen.oss.wahllokalsystem.monitoringservice.rest.wahllokalzustand;

import de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand.SendungsdatenModel;
import de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand.WahllokalZustandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businessActions")
@RequiredArgsConstructor
@Slf4j
public class WahllokalZustandController {

    private final WahllokalZustandService wahllokalZustandService;

    private static final String LAST_SEEN = "/lastSeen/{wahlbezirkID}";
    private static final String LAST_LOGOUT = "/letzteAbmeldung/{wahlbezirkID}";
    private static final String SCHNELLMELDUNG_SENDUNGSUHRZEIT = "/schnellmeldungSendungsuhrzeit";
    private static final String SCHNELLMELDUNG_DRUCKUHRZEIT = "/schnellmeldungDruckuhrzeit";
    private static final String NIEDERSCHRIFT_SENDUNGSUHRZEIT = "/niederschriftSendungsuhrzeit";
    private static final String NIEDERSCHRIFT_DRUCKUHRZEIT = "/niederschriftDruckuhrzeit";

    private final SendungsdatenDTOMapper sendungsdatenDTOMapper;
    private final DruckdatenDTOMapper druckdatenDTOMapper;
    private final WahllokalZustandDTOMapper wahllokalZustandDTOMapper;

    @PostMapping(LAST_SEEN)
    @Async
    public void postLastSeen(@PathVariable("wahlbezirkID") String wahlbezirkID) {
        log.info("postLastSeen {}", wahlbezirkID);
        wahllokalZustandService.postLastSeen(wahlbezirkID);
    }

    @PostMapping(LAST_LOGOUT)
    @Async
    public void postLetzteAbmeldung(@PathVariable("wahlbezirkID") String wahlbezirkID) {
        log.info("postLetzteAbmeldung {}", wahlbezirkID);
        wahllokalZustandService.postLetzteAbmeldung(wahlbezirkID);
    }

    @PostMapping(SCHNELLMELDUNG_SENDUNGSUHRZEIT)
    @Async
    public void postSchnellmeldungsSendungsuhrzeit(@RequestBody SendungsdatenDTO sendungsdatenDTO) {
        log.info("postSchnellmeldungsSendungsuhrzeit {}", sendungsdatenDTO);
        wahllokalZustandService.postSchnellmeldungSendungsuhrzeit(sendungsdatenDTOMapper.toSendungsdatenModel(sendungsdatenDTO));
    }

    @PostMapping(SCHNELLMELDUNG_DRUCKUHRZEIT)
    @Async
    public void postSchnellmeldungDruckuhrzeit(@RequestBody DruckdatenDTO druckdatenDTO) {
        log.info("postSchnellmeldungDruckuhrzeit {}", druckdatenDTO);
        wahllokalZustandService.postSchnellmeldungDruckuhrzeit(druckdatenDTOMapper.toDruckdatenModel(druckdatenDTO));
    }

    @PostMapping(NIEDERSCHRIFT_SENDUNGSUHRZEIT)
    @Async
    public void postNiederschriftSendungsuhrzeit(@RequestBody SendungsdatenDTO sendungsdatenDTO) {
        log.info("postNiederschriftSendungsuhrzeit {}", sendungsdatenDTO);
        wahllokalZustandService.postNiederschriftSendungsuhrzeit(sendungsdatenDTOMapper.toSendungsdatenModel(sendungsdatenDTO));
    }

    @PostMapping(NIEDERSCHRIFT_DRUCKUHRZEIT)
    @Async
    public void postNiederschriftDruckuhrzeit(@RequestBody DruckdatenDTO druckdatenDTO) {
        log.info("postNiederschriftDruckuhrzeit {}", druckdatenDTO);
        wahllokalZustandService.postNiederschriftDruckuhrzeit(druckdatenDTOMapper.toDruckdatenModel(druckdatenDTO));
    }

}
