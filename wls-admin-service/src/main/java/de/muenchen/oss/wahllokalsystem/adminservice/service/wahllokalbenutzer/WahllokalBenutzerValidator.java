package de.muenchen.oss.wahllokalsystem.adminservice.service.wahllokalbenutzer;

import de.muenchen.oss.wahllokalsystem.adminservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.adminservice.service.common.WahlbezirkModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WahllokalBenutzerValidator {

    private final ExceptionFactory exceptionFactory;

    public void validWahltagIDParamOrThrow(final String wahltagID) {
        if (StringUtils.isBlank(wahltagID)) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.MISSING_ARGUMENT);
        }
    }

    public void wahlbezirkeExistOrThrow(final List<WahlbezirkModel> wahlbezirke) {
        if (wahlbezirke == null || wahlbezirke.isEmpty()) {
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.INVALID_ARGUMENT);
        }
    }
}
