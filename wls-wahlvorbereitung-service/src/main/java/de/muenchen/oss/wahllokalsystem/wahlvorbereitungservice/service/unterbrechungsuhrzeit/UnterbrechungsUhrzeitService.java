package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.unterbrechungsuhrzeit;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.UnterbrechungsUhrzeitRepository;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.exception.ExceptionFactory;
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
public class UnterbrechungsUhrzeitService {

    private final UnterbrechungsUhrzeitRepository unterbrechungsUhrzeitRepository;
    private final UnterbrechungsUhrzeitModelMapper unterbrechungsUhrzeitModelMapper;
    private final UnterbrechungsUhrzeitValidator unterbrechungsUhrzeitValidator;
    private final ExceptionFactory exceptionFactory;

    @PreAuthorize(
        "hasAuthority('Wahlvorbereitung_BUSINESSACTION_UnterbrechungsUhrzeit')"
                + "and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#wahlbezirkID, authentication)"
    )
    public Optional<UnterbrechungsUhrzeitModel> getUnterbrechungsUhrzeit(@P("wahlbezirkID") final String wahlbezirkID) {
        log.debug("#getUnterbrechungsUhrzeit");
        log.debug("in: wahlbezirkID > {}", wahlbezirkID);

        unterbrechungsUhrzeitValidator.validWahlbezirkIDOrThrow(wahlbezirkID);

        val dataFromRepo = unterbrechungsUhrzeitRepository.findById(wahlbezirkID);

        log.debug("out: unterbrechungsUhrzeit > {}", dataFromRepo.orElse(null));

        return dataFromRepo.map(unterbrechungsUhrzeitModelMapper::toModel);
    }

    @PreAuthorize(
        "hasAuthority('Wahlvorbereitung_BUSINESSACTION_UnterbrechungsUhrzeit')"
                + "and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#wahlbezirkID, authentication)"
    )
    public void setUnterbrechungsUhrzeit(@P("unterbrechungsUhrzeitToSet") final UnterbrechungsUhrzeitModel unterbrechungsUhrzeitToSet) {
        log.debug("#postUnterbrechungsUhrzeit");
        log.debug("in: unterbrechungsUhrzeit > {}", unterbrechungsUhrzeitToSet);

        unterbrechungsUhrzeitValidator.validModelToSetOrThrow(unterbrechungsUhrzeitToSet);

        try {
            unterbrechungsUhrzeitRepository.save(unterbrechungsUhrzeitModelMapper.toEntity(unterbrechungsUhrzeitToSet));
        } catch (Exception e) {
            log.error("Fehler beim speichern: ", e);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.UNSAVEABLE);
        }
    }
}
