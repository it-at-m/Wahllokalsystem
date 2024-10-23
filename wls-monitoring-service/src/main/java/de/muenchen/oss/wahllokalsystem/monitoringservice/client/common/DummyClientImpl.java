package de.muenchen.oss.wahllokalsystem.monitoringservice.client.common;

import de.muenchen.oss.wahllokalsystem.monitoringservice.client.waehleranzahl.WaehleranzahlClientMapper;
import de.muenchen.oss.wahllokalsystem.monitoringservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.monitoringservice.service.waehleranzahl.WaehleranzahlClient;
import de.muenchen.oss.wahllokalsystem.monitoringservice.service.waehleranzahl.WaehleranzahlModel;
import de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand.WahllokalZustandClient;
import de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand.WahllokalZustandModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
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

    @Override
    public void postWahlbeteiligung(WaehleranzahlModel waehleranzahlModel) throws WlsException {

        val wahlbeteiligungsMeldungDTO = waehleranzahlClientMapper.toDTO(waehleranzahlModel);
        log.info("Dummy client postWahlbeteiligung() called instead of EAI with: " + wahlbeteiligungsMeldungDTO);
    }

    @Override
    public void postWahllokalZustand(WahllokalZustandModel wahllokalzustandModel) throws WlsException {
        log.info("Dummy client postWahllokalZustand() called instead of EAI with: " + wahllokalzustandModel);
    }
}
