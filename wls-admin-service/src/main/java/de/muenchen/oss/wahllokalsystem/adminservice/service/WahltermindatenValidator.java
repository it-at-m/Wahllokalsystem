package de.muenchen.oss.wahllokalsystem.adminservice.service;

import de.muenchen.oss.wahllokalsystem.adminservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WahltermindatenValidator {

    private final ExceptionFactory exceptionFactory;

    public void validWahltagIDParamOrThrow(final String wahltagID) {
        if (wahltagID == null || StringUtils.isEmpty(wahltagID) || StringUtils.isBlank(wahltagID)) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.MISSING_ARGUMENT);
        }
    }
}
