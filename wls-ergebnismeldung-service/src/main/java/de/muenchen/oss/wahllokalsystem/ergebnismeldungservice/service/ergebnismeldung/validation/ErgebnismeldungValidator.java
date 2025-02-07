package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.validation;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.MeldungsartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.ErgebnisseToSendCriteriaModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.WahlartModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ErgebnismeldungValidator {

    private final ExceptionFactory exceptionFactory;

    private final List<ElectionTypeValidation> electionTypeValidators;

    public void validBezirkUndWahlIDOrThrow(final BezirkUndWahlID bezirkUndWahlID) {
        if (bezirkUndWahlID == null || StringUtils.isBlank(bezirkUndWahlID.getWahlID()) || StringUtils.isBlank(bezirkUndWahlID.getWahlbezirkID())) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.SENDERGEBNISSE_PARAMETER_UNVOLLSTAENDIG);
        }
    }

    public void validErgebnisseToSendCriteriaOrThrow(final ErgebnisseToSendCriteriaModel ergebnisseToSendCriteria) {
        if (ergebnisseToSendCriteria == null || StringUtils.isBlank(ergebnisseToSendCriteria.wahlID()) || StringUtils.isBlank(
                ergebnisseToSendCriteria.wahlbezirkID()) || StringUtils.isBlank(ergebnisseToSendCriteria.hauptwahlbezirkID())) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.SENDERGEBNISSE_PARAMETER_UNVOLLSTAENDIG);
        }
    }

    public boolean checkValidation(final WahlartModel wahlart, final WahlbezirkArtModel wahlbezirksart, final String wahlbezirkID, final String wahlID,
            final Long waehlerverzeichnisNummer,
            final MeldungsartModel meldungsart) throws WlsException {
        val validator = findValidator(wahlart);
        if (wahlbezirksart.equals(WahlbezirkArtModel.UWB)) {
            return validator.isValidUwb(wahlbezirkID, wahlID, waehlerverzeichnisNummer, meldungsart);
        }
        return validator.isValidBwb(wahlbezirkID, wahlID, waehlerverzeichnisNummer, meldungsart);
    }

    private ElectionTypeValidation findValidator(final WahlartModel wahlart) {
        return electionTypeValidators.stream()
                .filter(v -> v.supportsWahlart(wahlart))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("#checkValidation kann mit Wahlart" + wahlart.name() + " nicht umgehen :("));
    }
}
