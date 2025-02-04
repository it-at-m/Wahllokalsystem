package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.client.briefwahl;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.briefwahl.client.BeanstandeteWahlbriefeControllerApi;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.briefwahl.model.BeanstandeteWahlbriefeDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.briefwahl.model.Zurueckweisungsgrund;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(Profiles.NOT + Profiles.DUMMY_CLIENTS)
@RequiredArgsConstructor
@Slf4j
public class BriefwahlClient implements de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.BriefwahlClient {

    private final BeanstandeteWahlbriefeControllerApi beanstandeteWahlbriefeControllerApi;

    private final ExceptionFactory exceptionFactory;

    @Override
    public long getAnzahlZurueckgewiesenerWahlbriefe(String wahlbezirkID, String wahlID, long waehlerverzeichnisNummer) {
        val beanstandeteWahlbriefe = getBeanstandeteWahlbriefe(wahlbezirkID, waehlerverzeichnisNummer);

        return beanstandeteWahlbriefe.getBeanstandeteWahlbriefe().get(wahlID).stream()
                .filter(zurueckweisungsgrund -> !zurueckweisungsgrund.equals(Zurueckweisungsgrund.ZUGELASSEN)).count();
    }

    private BeanstandeteWahlbriefeDTO getBeanstandeteWahlbriefe(final String wahlbezirkID, final long waehlerverzeichnisNummer) {
        final BeanstandeteWahlbriefeDTO beanstandeteWahlbriefeDTO;
        try {
            beanstandeteWahlbriefeDTO = beanstandeteWahlbriefeControllerApi.getBeanstandeteWahlbriefe(wahlbezirkID, waehlerverzeichnisNummer);
        } catch (final WlsException wlsException) {
            log.debug("found WlsException", wlsException);
            throw wlsException;
        } catch (final Exception e) {
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_BRIEFWAHL);
        }

        if (beanstandeteWahlbriefeDTO == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.BRIEFWAHL_BEANSTANDETEWAHLBRIEFE_NULL_OR_EMPTY);
        }

        return beanstandeteWahlbriefeDTO;
    }
}
