package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.client;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.monitoring.client.WahllokalZustandControllerApi;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.sender.StatusClient;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(Profiles.NOT + Profiles.DUMMY_CLIENTS)
@RequiredArgsConstructor
@Slf4j
public class MonitoringClient implements StatusClient {

    private final WahllokalZustandControllerApi wahllokalZustandControllerApi;
    private final StatusClientMapper statusClientMapper;
    private final ExceptionFactory exceptionFactory;

    @Override
    public void postSchnellmeldungSendungsuhrzeit(BezirkUndWahlID bezirkUndWahlID, LocalDateTime schnellmeldungSendungsuhrzeit)
            throws WlsException {
        val schnellmeldungGesendet = statusClientMapper.toSendungsdatenDTO(bezirkUndWahlID, schnellmeldungSendungsuhrzeit);
        callApiWithExceptionMapping(() -> wahllokalZustandControllerApi.postSchnellmeldungSendungsuhrzeit(schnellmeldungGesendet));
    }

    @Override
    public void postSchnellmeldungDruckuhrzeit(BezirkUndWahlID bezirkUndWahlID, LocalDateTime schnellmeldungDruckuhrzeit) throws WlsException {
        val schellmeldungGedruckt = statusClientMapper.toDruckdatenDTO(bezirkUndWahlID, schnellmeldungDruckuhrzeit);
        callApiWithExceptionMapping(() -> wahllokalZustandControllerApi.postSchnellmeldungDruckuhrzeit(schellmeldungGedruckt));
    }

    @Override
    public void postNiederschriftSendungsuhrzeit(BezirkUndWahlID bezirkUndWahlID, LocalDateTime niederschriftSendungsuhrzeit) throws WlsException {
        val niederschriftGesendet = statusClientMapper.toSendungsdatenDTO(bezirkUndWahlID, niederschriftSendungsuhrzeit);
        callApiWithExceptionMapping(() -> wahllokalZustandControllerApi.postNiederschriftSendungsuhrzeit(niederschriftGesendet));
    }

    @Override
    public void postNiederschriftDruckuhrzeit(BezirkUndWahlID bezirkUndWahlID, LocalDateTime niederschriftDruckuhrzeit) throws WlsException {
        val niederschriftGedruckt = statusClientMapper.toDruckdatenDTO(bezirkUndWahlID, niederschriftDruckuhrzeit);
        callApiWithExceptionMapping(() -> wahllokalZustandControllerApi.postNiederschriftDruckuhrzeit(niederschriftGedruckt));
    }

    private void callApiWithExceptionMapping(final Runnable apiCall) {
        try {
            apiCall.run();
        } catch (final WlsException wlsException) {
            log.debug("found WlsException: {}", wlsException.getMessage());
            throw wlsException;
        } catch (final Exception exception) {
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_MONITORING);
        }
    }
}
