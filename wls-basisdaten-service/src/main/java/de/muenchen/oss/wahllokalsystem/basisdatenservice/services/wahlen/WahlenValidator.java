package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
@RequiredArgsConstructor
public class WahlenValidator {

    private final ExceptionFactory exceptionFactory;

    public void validWahlenCriteriaOrThrow(final String wahltagID) {
        if (StringUtils.isBlank(wahltagID)) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.CODE_GETWAHLEN_PARAMETER_UNVOLLSTAENDIG);
        }
    }

    public void validWahlenWriteModelOrThrow(final WahlenWriteModel wahlenWriteModel) {
        if (wahlenWriteModel == null || StringUtils.isBlank(wahlenWriteModel.wahltagID()) || CollectionUtils.isEmpty(wahlenWriteModel.wahlen())) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.CODE_POSTWAHLEN_PARAMETER_UNVOLLSTAENDIG);
        }
    }

}
