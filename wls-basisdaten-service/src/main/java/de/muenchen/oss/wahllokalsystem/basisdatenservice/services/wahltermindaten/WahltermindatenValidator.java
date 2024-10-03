package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltermindaten;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WahltermindatenValidator {

    private final ExceptionFactory exceptionFactory;

    public void validateParameterToInitWahltermindaten(final String wahltagID) {
        if (StringUtils.isBlank(wahltagID)) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.CODE_PUTWAHLTERMINDATEN_PARAMETER_UNVOLLSTAENDIG);
        }
    }

    public void validateParameterToDeleteWahltermindaten(final String wahltagID) {
        if (StringUtils.isBlank(wahltagID)) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.CODE_DELETEWAHLTERMINDATEN_PARAMETER_UNVOLLSTAENDIG);
        }
    }

}
