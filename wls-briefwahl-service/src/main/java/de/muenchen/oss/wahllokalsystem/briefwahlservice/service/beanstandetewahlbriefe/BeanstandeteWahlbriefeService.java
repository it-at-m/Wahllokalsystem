package de.muenchen.oss.wahllokalsystem.briefwahlservice.service.beanstandetewahlbriefe;

import de.muenchen.oss.wahllokalsystem.briefwahlservice.domain.BeanstandeteWahlbriefe;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.domain.BeanstandeteWahlbriefeRepository;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.exception.ExceptionDataWrapper;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@PreAuthorize(
        "hasAuthority('Briefwahl_BUSINESSACTION_PostBeanstandeteWahlbriefe')"
                + " and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#beanstandeteWahlbriefe?.getBezirkIDUndWaehlerverzeichnisNummer()?.getWahlbezirkID(), authentication)"
)
@Slf4j
public class BeanstandeteWahlbriefeService {

    private static final ExceptionDataWrapper GETBEANSTANDETEWAHLBRIEFE_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("100",
            "getBeanstandeteWahlbriefe: Suchkriterien unvollständig.");
    private static final ExceptionDataWrapper POSTBEANSTANDETEWAHLBRIEFE_PARAMETER_UNVOLLSTAENDIG = new ExceptionDataWrapper("101",
            "postBeanstandeteWahlbriefe: Suchkriterien unvollständig.");

    private final BeanstandeteWahlbriefeRepository beanstandeteWahlbriefeRepository;

    private final BeanstandeteWahlbriefeModelMapper beanstandeteWahlbriefeModelMapper;

    public BeanstandeteWahlbriefeModel getBeanstandeteWahlbriefe(@NotNull final BeanstandeteWahlbriefeReference beanstandeteWahlbriefeReference) {
        log.info("#getBeanstandeteWahlbriefe");
        valideOrThrow(beanstandeteWahlbriefeReference);

        BezirkIDUndWaehlerverzeichnisNummer id = beanstandeteWahlbriefeModelMapper.toId(beanstandeteWahlbriefeReference);
        return beanstandeteWahlbriefeModelMapper.toModel(getOrNull(id));
    }

    public void addBeanstandeteWahlbriefe(@NotNull BeanstandeteWahlbriefeModel beanstandeteWahlbriefeToAdd) {
        log.info("#postBeanstandeteWahlbriefe");
        valideOrThrow(beanstandeteWahlbriefeToAdd);

        beanstandeteWahlbriefeRepository.save(beanstandeteWahlbriefeModelMapper.toEntity(beanstandeteWahlbriefeToAdd));
    }

    private BeanstandeteWahlbriefe getOrNull(final BezirkIDUndWaehlerverzeichnisNummer entityId) {
        return beanstandeteWahlbriefeRepository.findById(entityId).orElse(null);
    }

    private void valideOrThrow(final BeanstandeteWahlbriefeReference beanstandeteWahlbriefeReference) {
        if (StringUtils.isBlank(
                beanstandeteWahlbriefeReference.wahlbezirkID()) || beanstandeteWahlbriefeReference.waehlerverzeichnisNummer() == null
                || beanstandeteWahlbriefeReference.waehlerverzeichnisNummer() == 0) {
            throw FachlicheWlsException.withCode(GETBEANSTANDETEWAHLBRIEFE_PARAMETER_UNVOLLSTAENDIG.code()).buildWithMessage(
                    GETBEANSTANDETEWAHLBRIEFE_PARAMETER_UNVOLLSTAENDIG.message());
        }
    }

    private void valideOrThrow(final BeanstandeteWahlbriefeModel beanstandeteWahlbriefeToAdd) {
        if (beanstandeteWahlbriefeToAdd.beanstandeteWahlbriefe() == null || StringUtils.isBlank(beanstandeteWahlbriefeToAdd.wahlbezirkID())
                || beanstandeteWahlbriefeToAdd.waehlerverzeichnisNummer() == null
                || beanstandeteWahlbriefeToAdd.waehlerverzeichnisNummer() == 0) {
            throw FachlicheWlsException.withCode(POSTBEANSTANDETEWAHLBRIEFE_PARAMETER_UNVOLLSTAENDIG.code()).buildWithMessage(
                    POSTBEANSTANDETEWAHLBRIEFE_PARAMETER_UNVOLLSTAENDIG.message());
        }
    }
}
