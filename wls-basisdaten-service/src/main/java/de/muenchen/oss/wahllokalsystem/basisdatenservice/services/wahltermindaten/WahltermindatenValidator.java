package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltermindaten;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahltag;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltag.WahltagModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WahltermindatenValidator {

    private final ExceptionFactory exceptionFactory;

    public void validWahltagIDParamOrThrow(final String wahltagID, HttpMethod httpMethod) {
        if (wahltagID == null || StringUtils.isBlank(wahltagID) || StringUtils.isEmpty(wahltagID)) {
            switch (httpMethod.toString()) {
            case "PUT" -> throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.CODE_PUTWAHLTERMINDATEN_PARAMETER_UNVOLLSTAENDIG);
            case "DELETE" -> throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.CODE_DELETEWAHLTERMINDATEN_PARAMETER_UNVOLLSTAENDIG);
            }
        }
    }

    public void validateWahltagForSearchingWahltagID(final Wahltag wahltag, HttpMethod httpMethod) {
        if (null == wahltag || null == wahltag.getWahltag()) {
            switch (httpMethod.toString()) {
            case "PUT" -> throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.CODE_PUTWAHLTERMINDATEN_NO_WAHLTAG);
            case "DELETE" -> throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.CODE_DELETEWAHLTERMINDATEN_LOESCHEN_UNVOLLSTAENDIG);
            }
        }
    }

}
