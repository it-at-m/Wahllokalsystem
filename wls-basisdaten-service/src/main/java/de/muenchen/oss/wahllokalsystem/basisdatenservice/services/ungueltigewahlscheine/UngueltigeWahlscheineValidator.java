package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.ungueltigewahlscheine;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UngueltigeWahlscheineValidator {

    private final ExceptionFactory exceptionFactory;

    public void validUngueltigeWahlscheineReferenceOrThrow(final UngueltigeWahlscheineReferenceModel ungueltigeWahlscheineReferenceModel) {
        if (ungueltigeWahlscheineReferenceModel == null || StringUtils.isBlank(
                ungueltigeWahlscheineReferenceModel.wahltagID()) || ungueltigeWahlscheineReferenceModel.wahlbezirksart() == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETUNGUELTIGEWAHLSCHEINE_PARAMETER_UNVOLLSTAENDIG);
        }
    }

    public void validUngueltigeWahlscheineWriteModelOrThrow(final UngueltigeWahlscheineWriteModel ungueltigeWahlscheineWriteModel) {
        if (ungueltigeWahlscheineWriteModel == null || ungueltigeWahlscheineWriteModel.ungueltigeWahlscheineReferenceModel() == null || StringUtils.isBlank(
                ungueltigeWahlscheineWriteModel.ungueltigeWahlscheineReferenceModel()
                        .wahltagID()) || ungueltigeWahlscheineWriteModel.ungueltigeWahlscheineReferenceModel()
                .wahlbezirksart() == null || ungueltigeWahlscheineWriteModel.ungueltigeWahlscheineData() == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTUNGUELTIGEWS_PARAMETER_UNVOLLSTAENDIG);
        }
    }
}
