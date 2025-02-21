package de.muenchen.oss.wahllokalsystem.adminservice.service.wahlen;

import de.muenchen.oss.wahllokalsystem.adminservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WahlenValidator {

    private final ExceptionFactory exceptionFactory;

    public void validWahlIDParamOrThrow(final String wahlID) {
        if (StringUtils.isBlank(wahlID)) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.MISSING_ARGUMENT);
        }
    }

    public void validWahlModelListOrThrow(final List<WahlModel> wahlModelList) {
        if (wahlModelList == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.MISSING_ARGUMENT);
        }
    }
}
