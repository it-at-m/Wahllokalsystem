package de.muenchen.oss.wahllokalsystem.briefwahlservice.service.wahlbriefdaten;

import de.muenchen.oss.wahllokalsystem.briefwahlservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WahlbriefdatenValidator {

    private final ExceptionFactory exceptionFactory;

    public void validWahlbezirkIDOrThrow(final String wahlbezirkIDToValidate) {
        if (StringUtils.isBlank(wahlbezirkIDToValidate)) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETWAHLBRIEFDATEN_PARAMETER_UNVOLLSTAENDIG);
        }
    }

    public void validWahlbriefdatenToSetOrThrow(final WahlbriefdatenModel wahlbriefdatenToSet) {
        if (wahlbriefdatenToSet == null || StringUtils.isEmpty(wahlbriefdatenToSet.wahlbezirkID())) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTWAHLBRIEFDATEN_PARAMETER_UNVOLLSTAENDIG);
        }
    }
}
