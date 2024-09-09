package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlbezirke;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahltag;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WahlbezirkeValidator {

    private final ExceptionFactory exceptionFactory;

    public void validWahltagIDParamOrThrow(final String wahltagID) {
        if (wahltagID == null || StringUtils.isBlank(wahltagID) || StringUtils.isEmpty(wahltagID)) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.CODE_GETWAHLBEZIRKE_PARAMETER_UNVOLLSTAENDIG);
        }
    }

    public void validateWahltagForSearchingWahltagID(final Optional<Wahltag> wahltag) {
        if (wahltag.isEmpty() || null == wahltag.get().getWahltag()) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETWAHLBEZIRKE_NO_WAHLTAG);
        }
    }

}
