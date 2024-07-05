package de.muenchen.oss.wahllokalsystem.briefwahlservice.service.beanstandetewahlbriefe;

import de.muenchen.oss.wahllokalsystem.briefwahlservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BeanstandeteWahlbriefeValidator {

    private final ExceptionFactory exceptionFactory;

    public void valideReferenceOrThrow(final BeanstandeteWahlbriefeReference beanstandeteWahlbriefeReference) {
        if (StringUtils.isBlank(
                beanstandeteWahlbriefeReference.wahlbezirkID()) || beanstandeteWahlbriefeReference.waehlerverzeichnisNummer() == null
                || beanstandeteWahlbriefeReference.waehlerverzeichnisNummer() == 0) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETBEANSTANDETEWAHLBRIEFE_PARAMETER_UNVOLLSTAENDIG);
        }
    }

    public void valideModelOrThrow(final BeanstandeteWahlbriefeModel beanstandeteWahlbriefeToAdd) {
        if (beanstandeteWahlbriefeToAdd.beanstandeteWahlbriefe() == null || StringUtils.isBlank(beanstandeteWahlbriefeToAdd.wahlbezirkID())
                || beanstandeteWahlbriefeToAdd.waehlerverzeichnisNummer() == null
                || beanstandeteWahlbriefeToAdd.waehlerverzeichnisNummer() == 0) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTBEANSTANDETEWAHLBRIEFE_PARAMETER_UNVOLLSTAENDIG);
        }
    }
}
