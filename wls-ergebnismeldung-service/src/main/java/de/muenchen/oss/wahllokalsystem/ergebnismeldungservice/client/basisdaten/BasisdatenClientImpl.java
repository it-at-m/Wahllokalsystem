package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.client.basisdaten;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.basisdaten.client.WahlenControllerApi;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.basisdaten.model.WahlDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.infomanagement.client.KonfigurierterWahltagControllerApi;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.infomanagement.model.KonfigurierterWahltagDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.WahlartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.WahlenClient;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(Profiles.NOT + Profiles.DUMMY_CLIENTS)
@RequiredArgsConstructor
@Slf4j
public class BasisdatenClientImpl implements WahlenClient {

    private final KonfigurierterWahltagControllerApi konfigurierterWahltagControllerApi;
    private final WahlenControllerApi wahlenControllerApi;

    private final ExceptionFactory exceptionFactory;

    private final BasisdatenClientMapper basisdatenClientMapper;

    @Override
    public WahlartModel getWahlartOfCurrentWahltag(final String wahlID) {
        val currentWahltagID = getConfiguredWahltagID();
        val wahlartOfWahl = getWahlen(currentWahltagID, wahlID);

        return basisdatenClientMapper.toModel(wahlartOfWahl);
    }

    private String getConfiguredWahltagID() {
        final KonfigurierterWahltagDTO konfigurierterWahltag;
        try {
            konfigurierterWahltag = konfigurierterWahltagControllerApi.getKonfigurierterWahltag();
        } catch (final WlsException wlsException) {
            log.debug("found WlsException", wlsException);
            throw wlsException;
        } catch (final Exception e) {
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_INFOMANAGEMENT);
        }

        if (konfigurierterWahltag == null || konfigurierterWahltag.getWahltagID() == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.INFOMANAGEMENT_WAHLTAG_NULL_OR_EMPTY);
        }

        return konfigurierterWahltag.getWahltagID();
    }

    private WahlDTO.WahlartEnum getWahlen(final String wahltagID, final String wahlID) {
        final List<WahlDTO> wahlen;
        try {
            wahlen = wahlenControllerApi.getWahlen(wahltagID);
        } catch (final WlsException wlsException) {
            log.debug("found WlsException", wlsException);
            throw wlsException;
        } catch (final Exception e) {
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_BASISDATEN);
        }

        if (CollectionUtils.isEmpty(wahlen)) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.BASISDATEN_WAHLEN_EMPTY);
        }

        return wahlen.stream().filter(wahl -> wahlID.equals(wahl.getWahlID())).findFirst().map(WahlDTO::getWahlart).orElseThrow(
                () -> exceptionFactory.createFachlicheWlsException(ExceptionConstants.BASISDATEN_WAHL_NOT_FOUND));
    }
}
