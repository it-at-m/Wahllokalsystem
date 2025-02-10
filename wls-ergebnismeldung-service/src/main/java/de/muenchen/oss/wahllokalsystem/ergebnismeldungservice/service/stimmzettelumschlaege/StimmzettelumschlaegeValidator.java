package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelumschlaege;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StimmzettelumschlaegeValidator {

    private final ExceptionFactory exceptionFactory;

    public void validBezirkUndWahlIdOrThrow(final BezirkUndWahlID bezirkUndWahlId, final FachlicheWlsException exceptionOnInvalid)
            throws FachlicheWlsException {
        if (bezirkUndWahlId == null || StringUtils.isBlank(bezirkUndWahlId.getWahlID()) || StringUtils.isBlank(bezirkUndWahlId.getWahlbezirkID())) {
            throw exceptionOnInvalid;
        }
    }

    public void validStimmzettelumschlaegeOrThrow(final StimmzettelumschlaegeModel stimmzettelumschlaegeModel) {
        if (stimmzettelumschlaegeModel == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_STIMMZETTELUMSCHLAEGE_PARAMETER_UNVOLLSTAENDIG);
        }
    }

    public void validHasBWBRequiredEroeffnungsUhrzeitOrThrow(final WahlbezirkArtModel wahlbezirkArt, LocalDateTime urnenEroeffnungsUhrzeit) {
        if (wahlbezirkArt.equals(WahlbezirkArtModel.BWB) && urnenEroeffnungsUhrzeit == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_STIMMZETTELUMSCHLAEGE_PARAMETER_UNVOLLSTAENDIG);
        }
    }
}
