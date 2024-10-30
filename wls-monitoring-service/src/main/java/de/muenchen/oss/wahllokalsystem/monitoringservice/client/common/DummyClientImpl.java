package de.muenchen.oss.wahllokalsystem.monitoringservice.client.common;

import de.muenchen.oss.wahllokalsystem.monitoringservice.client.waehleranzahl.WaehleranzahlClientMapper;
import de.muenchen.oss.wahllokalsystem.monitoringservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.monitoringservice.eai.aou.model.DruckzustandDTO;
import de.muenchen.oss.wahllokalsystem.monitoringservice.eai.aou.model.WahllokalZustandDTO;
import de.muenchen.oss.wahllokalsystem.monitoringservice.service.waehleranzahl.WaehleranzahlClient;
import de.muenchen.oss.wahllokalsystem.monitoringservice.service.waehleranzahl.WaehleranzahlModel;
import de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand.WahllokalZustandClient;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(Profiles.DUMMY_CLIENTS)
@RequiredArgsConstructor
@Slf4j
public class DummyClientImpl implements WaehleranzahlClient, WahllokalZustandClient {

    private final WaehleranzahlClientMapper waehleranzahlClientMapper;
    private final TimeStampMapper timeStampMapper;

    @Override
    public void postWahlbeteiligung(WaehleranzahlModel waehleranzahlModel) throws WlsException {

        val wahlbeteiligungsMeldungDTO = waehleranzahlClientMapper.toDTO(waehleranzahlModel);
        log.info("Dummy client postWahlbeteiligung() called instead of EAI with: " + wahlbeteiligungsMeldungDTO);
    }

    @Override
    public void postWahllokalZustand(WahllokalZustandDTO wahllokalZustandDTO) throws WlsException {
        log.info("Dummy client postWahllokalZustand() called instead of EAI with: " + wahllokalZustandDTO);

    }

    @Override
    public void postLastSeen(String wahlbezirkID, LocalDateTime zuletztGesehen) throws WlsException {
        WahllokalZustandDTO wahllokalZustandDTO = new WahllokalZustandDTO();
        wahllokalZustandDTO.setWahlbezirkID(wahlbezirkID);
        wahllokalZustandDTO.setZuletztGesehen(timeStampMapper.localDateTimeToOffsetDateTime(zuletztGesehen));
        postWahllokalZustand(wahllokalZustandDTO);
    }

    @Override
    public void postLetzteAbmeldung(String wahlbezirkID, LocalDateTime letzteAbmeldung) throws WlsException {
        WahllokalZustandDTO wahllokalZustandDTO = new WahllokalZustandDTO();
        wahllokalZustandDTO.setWahlbezirkID(wahlbezirkID);
        wahllokalZustandDTO.setLetzteAbmeldung(timeStampMapper.localDateTimeToOffsetDateTime(letzteAbmeldung));
        postWahllokalZustand(wahllokalZustandDTO);
    }

    @Override
    public void postSchnellmeldungSendungsuhrzeit(BezirkUndWahlID bezirkUndWahlID, LocalDateTime schnellmeldungSendungsuhrzeit)
            throws WlsException {
        WahllokalZustandDTO wahllokalZustandDTO = new WahllokalZustandDTO();
        wahllokalZustandDTO.setWahlbezirkID(bezirkUndWahlID.getWahlbezirkID());
        DruckzustandDTO druckzustandDTO = new DruckzustandDTO();
        druckzustandDTO.setWahlID(bezirkUndWahlID.getWahlID());
        druckzustandDTO.setSchnellmeldungSendenUhrzeit(timeStampMapper.localDateTimeToOffsetDateTime(schnellmeldungSendungsuhrzeit));
        wahllokalZustandDTO.addDruckzustaendeItem(druckzustandDTO);
        postWahllokalZustand(wahllokalZustandDTO);
    }

    @Override
    public void postSchnellmeldungDruckuhrzeit(BezirkUndWahlID bezirkUndWahlID, LocalDateTime schnellmeldungDruckuhrzeit) throws WlsException {
        WahllokalZustandDTO wahllokalZustandDTO = new WahllokalZustandDTO();
        wahllokalZustandDTO.setWahlbezirkID(bezirkUndWahlID.getWahlbezirkID());
        DruckzustandDTO druckzustandDTO = new DruckzustandDTO();
        druckzustandDTO.setWahlID(bezirkUndWahlID.getWahlID());
        druckzustandDTO.setSchnellmeldungDruckUhrzeit(timeStampMapper.localDateTimeToOffsetDateTime(schnellmeldungDruckuhrzeit));
        wahllokalZustandDTO.addDruckzustaendeItem(druckzustandDTO);
        postWahllokalZustand(wahllokalZustandDTO);
    }

    @Override
    public void postNiederschriftSendungsuhrzeit(BezirkUndWahlID bezirkUndWahlID, LocalDateTime niederschriftSendungsuhrzeit) throws WlsException {
        WahllokalZustandDTO wahllokalZustandDTO = new WahllokalZustandDTO();
        wahllokalZustandDTO.setWahlbezirkID(bezirkUndWahlID.getWahlbezirkID());
        DruckzustandDTO druckzustandDTO = new DruckzustandDTO();
        druckzustandDTO.setWahlID(bezirkUndWahlID.getWahlID());
        druckzustandDTO.setNiederschriftSendenUhrzeit(timeStampMapper.localDateTimeToOffsetDateTime(niederschriftSendungsuhrzeit));
        wahllokalZustandDTO.addDruckzustaendeItem(druckzustandDTO);
        postWahllokalZustand(wahllokalZustandDTO);
    }

    @Override
    public void postNiederschriftDruckuhrzeit(BezirkUndWahlID bezirkUndWahlID, LocalDateTime niederschriftDruckuhrzeit) throws WlsException {
        WahllokalZustandDTO wahllokalZustandDTO = new WahllokalZustandDTO();
        wahllokalZustandDTO.setWahlbezirkID(bezirkUndWahlID.getWahlbezirkID());
        DruckzustandDTO druckzustandDTO = new DruckzustandDTO();
        druckzustandDTO.setWahlID(bezirkUndWahlID.getWahlID());
        druckzustandDTO.setNiederschriftDruckUhrzeit(timeStampMapper.localDateTimeToOffsetDateTime(niederschriftDruckuhrzeit));
        wahllokalZustandDTO.addDruckzustaendeItem(druckzustandDTO);
        postWahllokalZustand(wahllokalZustandDTO);
    }
}
