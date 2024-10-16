package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.clients.aoueai;

import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.eai.aou.client.WahlvorstandControllerApi;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.eai.aou.model.WahlvorstandDTO;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.aoueaiClient.WahlvorstandEaiClient;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.aoueaiClient.WahlvorstandModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile(Profiles.NOT + Profiles.DUMMY_CLIENTS)
public class WahlvorstandEaiClientImpl implements WahlvorstandEaiClient {

    private final ExceptionFactory exceptionFactory;
    private final WahlvorstandControllerApi wahlvorstandControllerApi;
    private final WahlvorstandClientMapper wahlvorstandClientMapper;

    @Override
    public WahlvorstandModel getWahlvorstand(String wahlbezirkID, LocalDate wahltag) {
        WahlvorstandDTO wahlvorstandDTO;
        try {
            wahlvorstandDTO = wahlvorstandControllerApi.loadWahlvorstand(wahlbezirkID);
        } catch (final Exception exception) {
            log.error("Bei der Kommunikation mit der AOUEAI kam es zu einem Fehler: ", exception);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_AOUEAI);
        }
        if (wahlvorstandDTO == null) {
            log.error("Der geladene Wahlvorstand für den Bezirk {} ist null.", wahlbezirkID);
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.AOUEAI_WAHLVORSTAND_NULL);
        }
        return wahlvorstandClientMapper.toModel(wahlvorstandDTO);
    }

    @Override
    public void postWahlvorstand(WahlvorstandModel wahlvorstand) {
        val aktualisierungsDTO = wahlvorstandClientMapper.toWahlvorstandsaktualisierungDTO(wahlvorstand);
        try {
            wahlvorstandControllerApi.saveAnwesenheit(aktualisierungsDTO);
        } catch (Exception e) {
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_AOUEAI);
        }
    }
}
