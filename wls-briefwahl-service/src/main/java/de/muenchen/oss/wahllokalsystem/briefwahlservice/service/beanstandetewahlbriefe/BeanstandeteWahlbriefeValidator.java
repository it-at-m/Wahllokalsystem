package de.muenchen.oss.wahllokalsystem.briefwahlservice.service.beanstandetewahlbriefe;

import de.muenchen.oss.wahllokalsystem.briefwahlservice.exception.ExceptionDataWrapper;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ServiceIDFormatter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BeanstandeteWahlbriefeValidator {

    private final ServiceIDFormatter serviceIDFormatter;

    private static final ExceptionDataWrapper GETBEANSTANDETEWAHLBRIEFE_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("100",
            "getBeanstandeteWahlbriefe: Suchkriterien unvollständig.");
    private static final ExceptionDataWrapper POSTBEANSTANDETEWAHLBRIEFE_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("101",
            "postBeanstandeteWahlbriefe: Suchkriterien unvollständig.");

    public void valideReferenceOrThrow(final BeanstandeteWahlbriefeReference beanstandeteWahlbriefeReference) {
        if (StringUtils.isBlank(
                beanstandeteWahlbriefeReference.wahlbezirkID()) || beanstandeteWahlbriefeReference.waehlerverzeichnisNummer() == null
                || beanstandeteWahlbriefeReference.waehlerverzeichnisNummer() == 0) {
            throw FachlicheWlsException.withCode(GETBEANSTANDETEWAHLBRIEFE_PARAMETER_UNVOLLSTAENDIG.code()).inService(serviceIDFormatter.getId())
                    .buildWithMessage(
                            GETBEANSTANDETEWAHLBRIEFE_PARAMETER_UNVOLLSTAENDIG.message());
        }
    }

    public void valideModelOrThrow(final BeanstandeteWahlbriefeModel beanstandeteWahlbriefeToAdd) {
        if (beanstandeteWahlbriefeToAdd.beanstandeteWahlbriefe() == null || StringUtils.isBlank(beanstandeteWahlbriefeToAdd.wahlbezirkID())
                || beanstandeteWahlbriefeToAdd.waehlerverzeichnisNummer() == null
                || beanstandeteWahlbriefeToAdd.waehlerverzeichnisNummer() == 0) {
            throw FachlicheWlsException.withCode(POSTBEANSTANDETEWAHLBRIEFE_PARAMETER_UNVOLLSTAENDIG.code()).inService(serviceIDFormatter.getId())
                    .buildWithMessage(
                            POSTBEANSTANDETEWAHLBRIEFE_PARAMETER_UNVOLLSTAENDIG.message());
        }
    }
}
