package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KonfigurationKonfigKey;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KonfigurationSetModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KonfigurationModelValidator {

    private static final String CODE_GETKONFIGURATION_PARAMETER_UNVOLLSTAENDIG = "102";
    private static final String MSG_GETKONFIGURATION_PARAMETER_UNVOLLSTAENDIG = "getKonfiguration: Suchkriterien unvollständig.";

    private static final String CODE_POSTKONFIGURATION_PARAMETER_UNVOLLSTAENDIG = "100";
    private static final String MSG_POSTKONFIGURATION_PARAMETER_UNVOLLSTAENDIG = "postKonfiguration: Suchkriterien unvollständig.";

    private final String serviceID;

    public KonfigurationModelValidator(@Value("${service.info.oid}") final String serviceID) {
        this.serviceID = serviceID;
    }

    public void validOrThrowGetKonfigurationByKey(final KonfigurationKonfigKey konfigurationKonfigKey) {
        if (konfigurationKonfigKey == null) {
            throw FachlicheWlsException.withCode(CODE_GETKONFIGURATION_PARAMETER_UNVOLLSTAENDIG).inService(serviceID)
                    .buildWithMessage(MSG_GETKONFIGURATION_PARAMETER_UNVOLLSTAENDIG);
        }
    }

    public void validOrThrowSetKonfiguration(final KonfigurationSetModel konfigurationSetModel) {
        if (konfigurationSetModel == null || konfigurationSetModel.schluessel() == null) {
            throw FachlicheWlsException.withCode(CODE_POSTKONFIGURATION_PARAMETER_UNVOLLSTAENDIG).inService(serviceID)
                    .buildWithMessage(MSG_POSTKONFIGURATION_PARAMETER_UNVOLLSTAENDIG);
        }
    }
}
