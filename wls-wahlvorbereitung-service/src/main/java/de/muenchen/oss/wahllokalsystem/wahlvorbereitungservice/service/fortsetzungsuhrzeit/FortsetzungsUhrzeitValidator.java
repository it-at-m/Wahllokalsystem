package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.fortsetzungsuhrzeit;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FortsetzungsUhrzeitValidator {

    private final ExceptionFactory exceptionFactory;

    public void validWahlbezirkIDOrThrow(final String wahlbezirkID) {
        if (wahlbezirkID == null || wahlbezirkID.isEmpty()) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.SUCHKRITERIEN_UNVOLLSTAENDIG);
        }
    }

    public void validModelToSetOrThrow(final FortsetzungsUhrzeitModel modelToValidate) {
        if (modelToValidate == null || StringUtils.isEmpty(modelToValidate.wahlbezirkID()) || modelToValidate.fortsetzungsUhrzeit() == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.PARAMS_UNVOLLSTAENDIG);
        }
    }
}
