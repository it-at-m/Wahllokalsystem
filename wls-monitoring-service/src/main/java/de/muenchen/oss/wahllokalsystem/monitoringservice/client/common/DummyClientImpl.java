package de.muenchen.oss.wahllokalsystem.monitoringservice.client.common;

import de.muenchen.oss.wahllokalsystem.monitoringservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.monitoringservice.eai.aou.model.WahllokalZustandDTO;
import de.muenchen.oss.wahllokalsystem.monitoringservice.service.waehleranzahl.WaehleranzahlClient;
import de.muenchen.oss.wahllokalsystem.monitoringservice.service.waehleranzahl.WaehleranzahlModel;
import de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand.WahllokalZustandClient;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(Profiles.DUMMY_CLIENTS)
@RequiredArgsConstructor
@Slf4j
public class DummyClientImpl implements WaehleranzahlClient, WahllokalZustandClient {

    @Override
    public void postWahlbeteiligung(WaehleranzahlModel waehleranzahlModel) throws WlsException {
        log.info("Dummy client postWahlbeteiligung() called instead of EAI with: {}", waehleranzahlModel);
    }


    private void postWahllokalZustand(WahllokalZustandDTO wahllokalZustandDTO) throws WlsException {
        log.info("Dummy client postWahllokalZustand() called instead of EAI with: " + wahllokalZustandDTO);
    }

    @Override
    public void postLastSeen(String wahlbezirkID, LocalDateTime zuletztGesehen) throws WlsException {
        log.info("Dummy client postLastSeen() called instead of EAI with: " + wahlbezirkID + " zuletztGesehen:" + zuletztGesehen);
    }

    @Override
    public void postLetzteAbmeldung(String wahlbezirkID, LocalDateTime letzteAbmeldung) throws WlsException {
        log.info("Dummy client postLetzteAbmeldung() called instead of EAI with: " + wahlbezirkID + " letzteAbmeldung:" + letzteAbmeldung);
    }

    @Override
    public void postSchnellmeldungSendungsuhrzeit(BezirkUndWahlID bezirkUndWahlID, LocalDateTime schnellmeldungSendungsuhrzeit)
            throws WlsException {
        log.info("Dummy client postSchnellmeldungSendungsuhrzeit() called instead of EAI with: " + bezirkUndWahlID + " schnellmeldungSendungsuhrzeit:" + schnellmeldungSendungsuhrzeit);
    }

    @Override
    public void postSchnellmeldungDruckuhrzeit(BezirkUndWahlID bezirkUndWahlID, LocalDateTime schnellmeldungDruckuhrzeit) throws WlsException {
        log.info("Dummy client postSchnellmeldungDruckuhrzeit() called instead of EAI with: " + bezirkUndWahlID + " schnellmeldungDruckuhrzeit:" + schnellmeldungDruckuhrzeit);
    }

    @Override
    public void postNiederschriftSendungsuhrzeit(BezirkUndWahlID bezirkUndWahlID, LocalDateTime niederschriftSendungsuhrzeit) throws WlsException {
        log.info("Dummy client postNiederschriftSendungsuhrzeit() called instead of EAI with: " + bezirkUndWahlID + " niederschriftSendungsuhrzeit:" + niederschriftSendungsuhrzeit);
    }

    @Override
    public void postNiederschriftDruckuhrzeit(BezirkUndWahlID bezirkUndWahlID, LocalDateTime niederschriftDruckuhrzeit) throws WlsException {
        log.info("Dummy client postNiederschriftDruckuhrzeit() called instead of EAI with: " + bezirkUndWahlID + " niederschriftDruckuhrzeit:" + niederschriftDruckuhrzeit);
    }
}
