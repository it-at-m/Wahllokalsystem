package de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WahllokalZustandValidator {

    public void validWahlbezirkIDOrThrow(String wahlbezirkID, final FachlicheWlsException wlsException) {
        if (StringUtils.isBlank(wahlbezirkID)) {
            throw wlsException;
        }
    }

    public void validWahlIdUndWahlbezirkIDOrThrow(final BezirkUndWahlID bezirkUndWahlID, final FachlicheWlsException wlsException) throws WlsException {
        if (bezirkUndWahlID == null || StringUtils.isBlank(bezirkUndWahlID.getWahlID()) || StringUtils.isBlank(bezirkUndWahlID.getWahlbezirkID())) {
            throw wlsException;
        }
    }
}
