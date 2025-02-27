package de.muenchen.oss.wahllokalsystem.adminservice.service.konfigurierterwahltag;

import de.muenchen.oss.wahllokalsystem.adminservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.adminservice.service.common.KonfigurierterWahltagModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KonfigurierterWahltagValidator {

    private final ExceptionFactory exceptionFactory;

    public void validateModel(final KonfigurierterWahltagModel konfigurierterWahltagModel) {
        if (konfigurierterWahltagModel == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.MISSING_ARGUMENT);
        }
    }
}
