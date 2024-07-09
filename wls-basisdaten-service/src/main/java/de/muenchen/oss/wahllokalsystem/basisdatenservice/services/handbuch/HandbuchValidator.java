package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.handbuch;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HandbuchValidator {

    private final ExceptionFactory exceptionFactory;

    public void validHandbuchReferenceOrThrow(final HandbuchReferenceModel handbuchReference) {
        if (handbuchReference == null || StringUtils.isBlank(handbuchReference.wahltagID()) || handbuchReference.wahlbezirksart() == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETHANDBUCH_PARAMETER_UNVOLLSTAENDIG);
        }
    }

    public void validHandbuchWriteModelOrThrow(final HandbuchWriteModel handbuchWriteModel) {
        if (handbuchWriteModel == null || handbuchWriteModel.handbuchReferenceModel() == null || StringUtils.isBlank(
                handbuchWriteModel.handbuchReferenceModel().wahltagID()) || handbuchWriteModel.handbuchReferenceModel().wahlbezirksart() == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTHANDBUCH_PARAMETER_UNVOLLSTAENDIG);
        }
    }
}
