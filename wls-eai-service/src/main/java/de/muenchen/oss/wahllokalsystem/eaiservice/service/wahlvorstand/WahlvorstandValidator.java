package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahlvorstand;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.dto.WahlvorstandsaktualisierungDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorstand.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WahlvorstandValidator {

    private final ExceptionFactory exceptionFactory;

    public void validateWahlbezirkIDOrThrow(final String wahlbezirkID) {
        if (StringUtils.isBlank(wahlbezirkID)) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.LOADWAHLVORSTAND_SUCHKRITERIEN_UNVOLLSTAENDIG);
        }
    }

    public void validateSaveAnwesenheitDataOrThrow(final WahlvorstandsaktualisierungDTO saveAnwesenheitData) {
        if (saveAnwesenheitData == null) {
            throw exceptionFactory.createFachlicheWlsException(
                    de.muenchen.oss.wahllokalsystem.eaiservice.rest.common.exception.ExceptionConstants.DATENALLGEMEIN_PARAMETER_FEHLEN);
        }

        if (StringUtils.isBlank(saveAnwesenheitData.wahlbezirkID())) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.SAVEANWESENHEIT_BEZIRKID_FEHLT);
        }

        if (saveAnwesenheitData.anwesenheitBeginn() == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.SAVEANWESENHEIT_ANWESENHEITBEGINN_FEHLT);
        }

        if (saveAnwesenheitData.mitglieder() != null && saveAnwesenheitData.mitglieder().stream()
                .anyMatch(mitglied -> StringUtils.isBlank(mitglied.identifikator()))) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.SAVEANWESENHEIT_IDENTIFIKATOR_FEHLT);
        }
    }
}
