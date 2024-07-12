package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.urnenwahlvorbereitung;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.UrnenwahlVorbereitungRepository;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrnenwahlvorbereitungService {

    private final UrnenwahlVorbereitungRepository urnenwahlVorbereitungRepository;

    private final UrnenwahlvorbereitungModelMapper urnenwahlvorbereitungModelMapper;

    private final UrnenwahlvorbereitungValidator urnenwahlvorbereitungValidator;
    private final ExceptionFactory exceptionFactory;

    @PreAuthorize(
        "hasAuthority('Wahlvorbereitung_BUSINESSACTION_GetUrnenwahlVorbereitung')"
                + "and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#wahlbezirkID, authentication)"
    )
    public Optional<UrnenwahlvorbereitungModel> getUrnenwahlvorbereitung(@P("wahlbezirkID") final String wahlbezirkID) {
        log.debug("#getUrnenwahlVorbereitung");
        log.debug("in: wahlbezirkID > {}", wahlbezirkID);

        urnenwahlvorbereitungValidator.validWahlbezirkIDOrThrow(wahlbezirkID);

        val dataFromRepo = urnenwahlVorbereitungRepository.findById(wahlbezirkID);
        log.debug("out: urnenwahlVorbereitung > {}", dataFromRepo.orElse(null));

        return dataFromRepo.map(urnenwahlvorbereitungModelMapper::toModel);
    }

    @PreAuthorize(
        "hasAuthority('Wahlvorbereitung_BUSINESSACTION_PostUrnenwahlVorbereitung')"
                + "and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#vorbereitungToSet.wahlbezirkID, authentication)"
    )
    public void setUrnenwahlvorbereitung(@P("vorbereitungToSet") final UrnenwahlvorbereitungModel vorbereitungToSet) {
        log.debug("#postUrnenwahlVorbereitung");
        log.debug("in: urnenwahlVorbereitung > {}", vorbereitungToSet);

        urnenwahlvorbereitungValidator.validModelToSetOrThrow(vorbereitungToSet);

        try {
            urnenwahlVorbereitungRepository.save(urnenwahlvorbereitungModelMapper.toEntity(vorbereitungToSet));
        } catch (final Exception onSaveException) {
            log.error("Fehler beim speichern: ", onSaveException);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.UNSAVEABLE);
        }
    }
}
