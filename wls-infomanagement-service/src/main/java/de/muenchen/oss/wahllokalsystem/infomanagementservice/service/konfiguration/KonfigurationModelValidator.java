package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KonfigurationModelValidator {

    private static final String CODE_GETKONFIGURATION_PARAMETER_UNVOLLSTAENDIG = "102";
    private static final String MSG_GETKONFIGURATION_PARAMETER_UNVOLLSTAENDIG = "getKonfiguration: Suchkriterien unvollständig.";

    @Value("${service.info.oid}")
    String serviceID;

    public void valideOrThrowGetKonfigurationByKey(final KonfigurationKonfigKey konfigurationKonfigKey) {
        if (konfigurationKonfigKey == null) {
            throw FachlicheWlsException.withCode(CODE_GETKONFIGURATION_PARAMETER_UNVOLLSTAENDIG).inService(serviceID)
                    .buildWithMessage(MSG_GETKONFIGURATION_PARAMETER_UNVOLLSTAENDIG);
        }
    }
}
