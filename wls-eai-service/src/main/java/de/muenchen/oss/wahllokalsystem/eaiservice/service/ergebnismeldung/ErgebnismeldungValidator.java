package de.muenchen.oss.wahllokalsystem.eaiservice.service.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.eaiservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlergebnis.dto.ErgebnismeldungDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ErgebnismeldungValidator {

    private final ExceptionFactory exceptionFactory;

    public void validDTOToSetOrThrow(ErgebnismeldungDTO ergebnismeldungToSet) {
        if (ergebnismeldungToSet == null) {
            throw exceptionFactory.createFachlicheWlsException(
                    de.muenchen.oss.wahllokalsystem.eaiservice.rest.common.exception.ExceptionConstants.DATENALLGEMEIN_PARAMETER_FEHLEN);
        }

        if (StringUtils.isBlank(ergebnismeldungToSet.wahlbezirkID())) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.SAVEWAHLERGEBNISMELDUNG_WAHLBEZIRKID_FEHLT);
        }

        if (StringUtils.isBlank(ergebnismeldungToSet.wahlID())) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.SAVEWAHLERGEBNISMELDUNG_WAHLID_FEHLT);
        }
    }
}
