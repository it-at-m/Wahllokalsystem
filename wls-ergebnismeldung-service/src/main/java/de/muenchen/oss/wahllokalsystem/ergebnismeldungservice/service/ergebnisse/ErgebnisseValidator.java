package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ErgebnisseValidator {
    private final ExceptionFactory exceptionFactory;

    public void validReferenceOrThrow(final ErgebnisseReferenceModel ergebnisseReferenceModel, final FachlicheWlsException exceptionOnInvalid)
            throws FachlicheWlsException {
        if (StringUtils.isBlank(
                ergebnisseReferenceModel.wahlbezirkID()) ||
                StringUtils.isBlank(
                        ergebnisseReferenceModel.wahlID())
                || ergebnisseReferenceModel.stapelart() == null) {
            throw exceptionOnInvalid;
        }
    }

    public void validIDOrThrow(final String wahlID, String wahlbezirkID, final FachlicheWlsException exceptionOnInvalid)
            throws FachlicheWlsException {
        if (StringUtils.isBlank(wahlbezirkID) ||
                StringUtils.isBlank(wahlID)) {
            throw exceptionOnInvalid;
        }
    }

    public void validModelOrThrow(final ErgebnisseModel ergebnisseToAdd) throws FachlicheWlsException {
        if (ergebnisseToAdd.ergebnisse() == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_ERGEBNISSE_PARAMETER_UNVOLLSTAENDIG);
        }
    }
}
