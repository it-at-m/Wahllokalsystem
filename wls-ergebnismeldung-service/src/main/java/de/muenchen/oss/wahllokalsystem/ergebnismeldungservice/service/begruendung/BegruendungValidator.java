package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.begruendung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BegruendungValidator {

    private final ExceptionFactory exceptionFactory;

    public void validReferenceOrThrow(final BegruendungReference begruendungReference) throws FachlicheWlsException {
        if (StringUtils.isBlank(
                begruendungReference.wahlbezirkID()) ||
                StringUtils.isBlank(
                        begruendungReference.wahlID())
                || begruendungReference.stapelart() == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.GET_BEGRUENDUNG_PARAMETER_UNVOLLSTAENDIG);
        }
    }

    public void validModelOrThrow(final BegruendungModel begruendungToAdd) {
        if (begruendungToAdd.grund1() == null && begruendungToAdd.grund2() == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_BEGRUENDUNG_PARAMETER_UNVOLLSTAENDIG);
        }
    }
}
