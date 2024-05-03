package de.muenchen.oss.wahllokalsystem.briefwahlservice.service.beanstandetewahlbriefe;

import de.muenchen.oss.wahllokalsystem.briefwahlservice.domain.BeanstandeteWahlbriefe;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.domain.BeanstandeteWahlbriefeRepository;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BeanstandeteWahlbriefeService {

    private final BeanstandeteWahlbriefeRepository beanstandeteWahlbriefeRepository;

    private final BeanstandeteWahlbriefeModelMapper beanstandeteWahlbriefeModelMapper;

    private final BeanstandeteWahlbriefeValidator beanstandeteWahlbriefeValidator;

    @PreAuthorize(
        "hasAuthority('Briefwahl_BUSINESSACTION_GetBeanstandeteWahlbriefe')"
                + " and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#param.wahlbezirkID(), authentication)"
    )
    public BeanstandeteWahlbriefeModel getBeanstandeteWahlbriefe(@P("param") @NotNull final BeanstandeteWahlbriefeReference beanstandeteWahlbriefeReference) {
        log.info("#getBeanstandeteWahlbriefe");
        beanstandeteWahlbriefeValidator.valideReferenceOrThrow(beanstandeteWahlbriefeReference);

        BezirkIDUndWaehlerverzeichnisNummer id = beanstandeteWahlbriefeModelMapper.toId(beanstandeteWahlbriefeReference);
        return beanstandeteWahlbriefeModelMapper.toModel(getOrNull(id));
    }

    @PreAuthorize(
        "hasAuthority('Briefwahl_BUSINESSACTION_PostBeanstandeteWahlbriefe')"
                + " and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#param.wahlbezirkID(), authentication)"
    )
    public void addBeanstandeteWahlbriefe(@P("param") @NotNull BeanstandeteWahlbriefeModel beanstandeteWahlbriefeToAdd) { //TODO sollte eventuell SET heißen
        log.info("#postBeanstandeteWahlbriefe");
        beanstandeteWahlbriefeValidator.valideModelOrThrow(beanstandeteWahlbriefeToAdd);

        beanstandeteWahlbriefeRepository.save(beanstandeteWahlbriefeModelMapper.toEntity(beanstandeteWahlbriefeToAdd));
    }

    private BeanstandeteWahlbriefe getOrNull(final BezirkIDUndWaehlerverzeichnisNummer entityId) {
        return beanstandeteWahlbriefeRepository.findById(entityId).orElse(null);
    }
}
