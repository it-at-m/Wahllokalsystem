package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.clients.aoueai;

import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.domain.Wahlvorstand;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.EaiClient;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.WahlvorstandModelMapper;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class EaiClientImpl implements EaiClient {

    private static final String AOUEAi_HOSTNAME = "http://aoueai:8080";

    private final WahlvorstandModelMapper wahlvorstandModelMapper;
    private final ExceptionFactory exceptionFactory;

    @Autowired
    private OAuth2RestTemplate template;

    @Override
    @Timed(MonitoringConfig.CLIENT_REST)
    public Wahlvorstand getWahlvorstand(String wahlbezirkID, LocalDate wahltag) {
        log.debug("#getWahlvorstand");

        LoadWahlvorstandParams params = new LoadWahlvorstandParams();
        params.setWahlbezirkID(wahlbezirkID);
        params.setWahltag(wahltag);
        Wahlvorstand wahlvorstand;
        try {
            wahlvorstand = template.postForObject(AOUEAi_HOSTNAME + "/businessActions/loadWahlvorstand", params, Wahlvorstand.class);
        } catch (Exception e) {
            log.error("Bei der Kommunikation mit der AOUEAI kam es zu einem Fehler: ", e);
            return null;
        }
        if (wahlvorstand == null) {
            log.error("Der geladene Wahlvorstand für den Bezirk {} ist null.", wahlbezirkID);
            return null;
        }
        return wahlvorstand;
    }

    @Override
    @Timed(MonitoringConfig.CLIENT_REST)
    public void postWahlvorstand(Wahlvorstand wahlvorstand, LocalDate wahltag) {
        log.debug("#postWahlvorstand");

        final SaveAnwesenheitParams params = new SaveAnwesenheitParams();
        Wahlvorstandsaktualisierung aktualisierung = Mapping.toWahlvorstandsaktualisierung(wahlvorstand);
        aktualisierung.setWahltag(wahltag);
        params.setWahlvorstand(aktualisierung);

        try {
            template.postForObject(AOUEAi_HOSTNAME + "/businessActions/saveAnwesenheit", params, Void.class);
        } catch (Exception e) {
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_AOUEAI);
        }
    }
}
