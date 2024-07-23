package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahlbeteiligung;

import de.muenchen.oss.wahllokalsystem.eaiservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlbeteiligung.dto.WahlbeteiligungsMeldungDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WahlbeteiligungValidator {

    private final ExceptionFactory exceptionFactory;

    public void validDTOToSetOrThrow(WahlbeteiligungsMeldungDTO wahlbeteiligungToSet) {
        if (wahlbeteiligungToSet == null) {
            throw exceptionFactory.createFachlicheWlsException(
                    de.muenchen.oss.wahllokalsystem.eaiservice.rest.common.exception.ExceptionConstants.DATENALLGEMEIN_PARAMETER_FEHLEN);
        }

        if (StringUtils.isBlank(wahlbeteiligungToSet.wahlbezirkID())) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.SAVEWAHLBETEILIGUNG_WAHLBEZIRKID_FEHLT);
        }

        if (StringUtils.isBlank(wahlbeteiligungToSet.wahlID())) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.SAVEWAHLBETEILIGUNG_WAHLID_FEHLT);
        }

        if (wahlbeteiligungToSet.meldeZeitpunkt() == null || StringUtils.isBlank(wahlbeteiligungToSet.meldeZeitpunkt().toString())) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.SAVEWAHLBETEILIGUNG_MELDEZEITPUNKT_FEHLT);
        }
    }
}
