package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.wahltag;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ServiceIDFormatter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KonfigurierterWahltagValidator {

    private final ServiceIDFormatter serviceIDFormatter;

    public void validPostModelOrThrow(KonfigurierterWahltagModel konfigurierterWahltag) {
        if (konfigurierterWahltag == null || konfigurierterWahltag.wahltag() == null || konfigurierterWahltag.wahltagID() == null) {
            throw FachlicheWlsException.withCode(ExceptionConstants.POST_KONFIGURIERTERWAHLTAG_PARAMETER_UNVOLLSTAENDIG.code())
                    .inService(serviceIDFormatter.getId())
                    .buildWithMessage(
                            ExceptionConstants.POST_KONFIGURIERTERWAHLTAG_PARAMETER_UNVOLLSTAENDIG.message());
        }
    }

    public void validDeleteModelOrThrow(final String wahltagID) {
        if (StringUtils.isEmpty(wahltagID)) {
            throw FachlicheWlsException.withCode(ExceptionConstants.DELETE_KONFIGURIERTERWAHLTAG_PARAMETER_UNVOLLSTAENDIG.code())
                    .inService(serviceIDFormatter.getId())
                    .buildWithMessage(
                            ExceptionConstants.DELETE_KONFIGURIERTERWAHLTAG_PARAMETER_UNVOLLSTAENDIG.message());
        }
    }

}
