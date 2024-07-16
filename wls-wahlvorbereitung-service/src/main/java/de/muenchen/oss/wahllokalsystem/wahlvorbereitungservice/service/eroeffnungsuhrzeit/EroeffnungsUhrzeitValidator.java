package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.eroeffnungsuhrzeit;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EroeffnungsUhrzeitValidator {

    private final ExceptionFactory exceptionFactory;

    public void validWahlbezirkIDOrThrow(final String wahlbezirkID) {
        if (wahlbezirkID == null || wahlbezirkID.isEmpty()) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.SUCHKRITERIEN_UNVOLLSTAENDIG);
        }
    }

    public void validModelToSetOrThrow(final EroeffnungsUhrzeitModel modelToValidate) {
        if (modelToValidate == null || StringUtils.isEmpty(modelToValidate.wahlbezirkID()) || modelToValidate.eroeffnungsuhrzeit() == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.PARAMS_UNVOLLSTAENDIG);
        }
    }
}
