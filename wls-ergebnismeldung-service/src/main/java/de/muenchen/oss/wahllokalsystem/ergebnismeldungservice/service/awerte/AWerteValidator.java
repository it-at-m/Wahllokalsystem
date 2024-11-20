package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AWerteValidator {

    private final ExceptionFactory exceptionFactory;

    public void validWahlbezirkIDParamOrThrow(final String wahlbezirkID) {
        if (wahlbezirkID == null || StringUtils.isBlank(wahlbezirkID) || StringUtils.isEmpty(wahlbezirkID)) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETAWERTE_PARAMETER_UNVOLLSTAENDIG);
        }
    }

}
