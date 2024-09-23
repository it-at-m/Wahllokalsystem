package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EreignisValidator {

    private final ExceptionFactory exceptionFactory;

    public void validWahlbezirkIDOrThrow(final String wahlbezirkID) {
        if (StringUtils.isBlank(wahlbezirkID)) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETEREIGNIS_SUCHKRITERIEN_UNVOLLSTAENDIG);
        }
    }

    public void validEreignisAndWahlbezirkIDOrThrow(final EreignisseWriteModel ereignis) {
        if (ereignis == null || StringUtils.isBlank(ereignis.wahlbezirkID())) {
            log.warn("#postEreignis: Parameter unvollständig");
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTEREIGNIS_PARAMS_UNVOLLSTAENDIG);
        }
    }
}
