package de.muenchen.oss.wahllokalsystem.monitoringservice.client.wahllokalzustand;

import de.muenchen.oss.wahllokalsystem.monitoringservice.client.common.TimeStampMapper;
import de.muenchen.oss.wahllokalsystem.monitoringservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.monitoringservice.eai.aou.client.WahllokalzustandControllerApi;
import de.muenchen.oss.wahllokalsystem.monitoringservice.eai.aou.model.DruckzustandDTO;
import de.muenchen.oss.wahllokalsystem.monitoringservice.eai.aou.model.WahllokalZustandDTO;
import de.muenchen.oss.wahllokalsystem.monitoringservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand.WahllokalZustandClient;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(Profiles.NOT + Profiles.DUMMY_CLIENTS)
@RequiredArgsConstructor
@Slf4j
public class WahllokalZustandClientImpl implements WahllokalZustandClient {

    private final ExceptionFactory exceptionFactory;

    private final WahllokalzustandControllerApi wahllokalzustandControllerApi;
    private final TimeStampMapper timeStampMapper;

    private void postWahllokalZustand(WahllokalZustandDTO wahllokalZustandDTO) throws WlsException {
        try {
            wahllokalzustandControllerApi.saveWahllokalZustand(wahllokalZustandDTO);
        } catch (final Exception exception) {
            log.info("Wahllokalzustand nicht gesendet. Exception: {}", exception.getMessage());
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.FAILED_COMMUNICATION_WITH_EAI);
        }
    }

    @Override
    public void postLastSeen(final String wahlbezirkID, LocalDateTime zuletztGesehen) throws WlsException {
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
    public void postSchnellmeldungSendungsuhrzeit(final BezirkUndWahlID bezirkUndWahlID, final LocalDateTime schnellmeldungSendungsuhrzeit)
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
    public void postSchnellmeldungDruckuhrzeit(final BezirkUndWahlID bezirkUndWahlID, final LocalDateTime schnellmeldungDruckuhrzeit) throws WlsException {
        WahllokalZustandDTO wahllokalZustandDTO = new WahllokalZustandDTO();
        wahllokalZustandDTO.setWahlbezirkID(bezirkUndWahlID.getWahlbezirkID());
        DruckzustandDTO druckzustandDTO = new DruckzustandDTO();
        druckzustandDTO.setWahlID(bezirkUndWahlID.getWahlID());
        druckzustandDTO.setSchnellmeldungDruckUhrzeit(timeStampMapper.localDateTimeToOffsetDateTime(schnellmeldungDruckuhrzeit));
        wahllokalZustandDTO.addDruckzustaendeItem(druckzustandDTO);
        postWahllokalZustand(wahllokalZustandDTO);
    }

    @Override
    public void postNiederschriftSendungsuhrzeit(final BezirkUndWahlID bezirkUndWahlID, final LocalDateTime niederschriftSendungsuhrzeit) throws WlsException {
        WahllokalZustandDTO wahllokalZustandDTO = new WahllokalZustandDTO();
        wahllokalZustandDTO.setWahlbezirkID(bezirkUndWahlID.getWahlbezirkID());
        DruckzustandDTO druckzustandDTO = new DruckzustandDTO();
        druckzustandDTO.setWahlID(bezirkUndWahlID.getWahlID());
        druckzustandDTO.setNiederschriftSendenUhrzeit(timeStampMapper.localDateTimeToOffsetDateTime(niederschriftSendungsuhrzeit));
        wahllokalZustandDTO.addDruckzustaendeItem(druckzustandDTO);
        postWahllokalZustand(wahllokalZustandDTO);
    }

    @Override
    public void postNiederschriftDruckuhrzeit(final BezirkUndWahlID bezirkUndWahlID, final LocalDateTime niederschriftDruckuhrzeit) throws WlsException {
        WahllokalZustandDTO wahllokalZustandDTO = new WahllokalZustandDTO();
        wahllokalZustandDTO.setWahlbezirkID(bezirkUndWahlID.getWahlbezirkID());
        DruckzustandDTO druckzustandDTO = new DruckzustandDTO();
        druckzustandDTO.setWahlID(bezirkUndWahlID.getWahlID());
        druckzustandDTO.setNiederschriftDruckUhrzeit(timeStampMapper.localDateTimeToOffsetDateTime(niederschriftDruckuhrzeit));
        wahllokalZustandDTO.addDruckzustaendeItem(druckzustandDTO);
        postWahllokalZustand(wahllokalZustandDTO);
    }
}
