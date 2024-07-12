package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.wahltag;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KonfigurierterWahltagValidator {

    private final ExceptionFactory exceptionFactory;

    public void validPostModelOrThrow(KonfigurierterWahltagModel konfigurierterWahltag) {
        if (konfigurierterWahltag == null || konfigurierterWahltag.wahltag() == null || konfigurierterWahltag.wahltagID() == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_KONFIGURIERTERWAHLTAG_PARAMETER_UNVOLLSTAENDIG);
        }
    }

    public void validDeleteModelOrThrow(final String wahltagID) {
        if (StringUtils.isEmpty(wahltagID)) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.DELETE_KONFIGURIERTERWAHLTAG_PARAMETER_UNVOLLSTAENDIG);
        }
    }

}
