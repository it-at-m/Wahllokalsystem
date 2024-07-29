package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlagen;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReferendumvorlagenValidator {

    private final ExceptionFactory exceptionFactory;

    public void validReferumvorlageReferenceModelOrThrow(final ReferendumvorlagenReferenceModel referendumvorlagenReferenceModel) {
        if (referendumvorlagenReferenceModel == null || StringUtils.isEmpty(referendumvorlagenReferenceModel.wahlID()) || StringUtils.isEmpty(
                referendumvorlagenReferenceModel.wahlbezirkID())) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETREFERENDUMVORLAGEN_PARAMETER_UNVOLLSTAENDIG);
        }
    }
}
