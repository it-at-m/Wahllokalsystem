package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.urnenwahlschliessungsuhrzeit;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.exception.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UrnenwahlSchliessungsUhrzeitValidator {

    private final ExceptionFactory exceptionFactory;

    public void validWahlbezirkIDOrThrow(final String wahlbezirkID) {
        if (wahlbezirkID == null || wahlbezirkID.isEmpty()) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.SUCHKRITERIEN_UNVOLLSTAENDIG);
        }
    }

    public void validModelToSetOrThrow(final UrnenwahlSchliessungsUhrzeitModel modelToValidate) {
        if (modelToValidate == null || StringUtils.isEmpty(modelToValidate.wahlbezirkID()) || modelToValidate.urnenwahlSchliessungsUhrzeit() == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.PARAMS_UNVOLLSTAENDIG);
        }
    }
}
