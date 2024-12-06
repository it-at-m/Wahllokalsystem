package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.client;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte.AWerteClient;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte.AWerteModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.sender.StatusClient;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(Profiles.DUMMY_CLIENTS)
@Slf4j
public class DummyClientImpl
        implements AWerteClient, StatusClient {

    @Override
    public List<AWerteModel> getAWerte(final String wahlbezirkID) throws WlsException {
        return List.of(
                new AWerteModel(new BezirkUndWahlID("wahlID01", wahlbezirkID), 25L, 26L));
    }

    @Override
    public void postSchnellmeldungSendungsuhrzeit(BezirkUndWahlID bezirkUndWahlID, LocalDateTime schnellmeldungSendungsuhrzeit)
            throws WlsException {
        log.info("postSchnellmeldungSendungsuhrzeit of {} on {}", bezirkUndWahlID, schnellmeldungSendungsuhrzeit);
    }

    @Override
    public void postSchnellmeldungDruckuhrzeit(BezirkUndWahlID bezirkUndWahlID, LocalDateTime schnellmeldungDruckuhrzeit) throws WlsException {
        log.info("postSchnellmeldungDruckuhrzeit of {} on {}", bezirkUndWahlID, schnellmeldungDruckuhrzeit);
    }

    @Override
    public void postNiederschriftSendungsuhrzeit(BezirkUndWahlID bezirkUndWahlID, LocalDateTime niederschriftSendungsuhrzeit) throws WlsException {
        log.info("postNiederschriftSendungsuhrzeit of {} on {}", bezirkUndWahlID, niederschriftSendungsuhrzeit);
    }

    @Override
    public void postNiederschriftDruckuhrzeit(BezirkUndWahlID bezirkUndWahlID, LocalDateTime niederschriftDruckuhrzeit) throws WlsException {
        log.info("postNiederschriftDruckuhrzeit of {} on {}", bezirkUndWahlID, niederschriftDruckuhrzeit);
    }

}
