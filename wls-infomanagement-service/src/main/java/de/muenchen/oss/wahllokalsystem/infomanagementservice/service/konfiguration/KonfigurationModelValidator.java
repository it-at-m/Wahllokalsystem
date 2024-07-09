package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KonfigurationKonfigKey;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KonfigurationSetModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KonfigurationModelValidator {

    private final ExceptionFactory exceptionFactory;

    public void validOrThrowGetKonfigurationByKey(final KonfigurationKonfigKey konfigurationKonfigKey) {
        if (konfigurationKonfigKey == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETKONFIGURATION_PARAMETER_UNVOLLSTAENDIG);
        }
    }

    public void validOrThrowSetKonfiguration(final KonfigurationSetModel konfigurationSetModel) {
        if (konfigurationSetModel == null || konfigurationSetModel.schluessel() == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTKONFIGURATION_PARAMETER_UNVOLLSTAENDIG);
        }
    }
}
