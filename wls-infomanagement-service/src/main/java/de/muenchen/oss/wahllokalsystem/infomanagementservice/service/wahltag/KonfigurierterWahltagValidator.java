package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.wahltag;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.exception.ExceptionDataWrapper;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ServiceIDFormatter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KonfigurierterWahltagValidator {

    private final ServiceIDFormatter serviceIDFormatter;

    private static final ExceptionDataWrapper POST_KONFIGURIERTERWAHLTAG_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("100",
            "postKonfigurierterWahltag: Suchkriterien unvollständig.");

    private static final ExceptionDataWrapper DELETE_KONFIGURIERTERWAHLTAG_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("104",
            "deleteKonfigurierterWahltag: Suchkriterien unvollständig.");

    public void validPostModelOrThrow(KonfigurierterWahltagModel konfigurierterWahltag) {
        if (konfigurierterWahltag == null || konfigurierterWahltag.wahltag() == null || konfigurierterWahltag.wahltagID() == null) {
            throw FachlicheWlsException.withCode(POST_KONFIGURIERTERWAHLTAG_PARAMETER_UNVOLLSTAENDIG.code()).inService(serviceIDFormatter.getId())
                    .buildWithMessage(
                            POST_KONFIGURIERTERWAHLTAG_PARAMETER_UNVOLLSTAENDIG.message());
        }
    }

    public void validDeleteModelOrThrow(final String wahltagID) {
        if (StringUtils.isEmpty(wahltagID)) {
            throw FachlicheWlsException.withCode(DELETE_KONFIGURIERTERWAHLTAG_PARAMETER_UNVOLLSTAENDIG.code()).inService(serviceIDFormatter.getId())
                    .buildWithMessage(
                            DELETE_KONFIGURIERTERWAHLTAG_PARAMETER_UNVOLLSTAENDIG.message());
        }
    }

}
