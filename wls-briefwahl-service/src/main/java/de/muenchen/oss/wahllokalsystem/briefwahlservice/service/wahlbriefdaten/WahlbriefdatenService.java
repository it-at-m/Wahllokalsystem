package de.muenchen.oss.wahllokalsystem.briefwahlservice.service.wahlbriefdaten;

import de.muenchen.oss.wahllokalsystem.briefwahlservice.domain.WahlbriefdatenRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WahlbriefdatenService {

    private final WahlbriefdatenRepository wahlbriefdatenRepository;

    private final WahlbriefdatenModelMapper wahlbriefdatenModelMapper;

    private final WahlbriefdatenValidator wahlbriefdatenValidator;

    @PreAuthorize(
            "hasAuthority('Briefwahl_BUSINESSACTION_GetWahlbriefdaten')"
                    + " and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#wahlbezirkID, authentication)"
    )
    public Optional<WahlbriefdatenModel> getWahlbriefdaten(final String wahlbezirkID) {
        log.info("#getBeanstandeteWahlbriefe");
        wahlbriefdatenValidator.validWahlbezirkIDOrThrow(wahlbezirkID);

        return wahlbriefdatenRepository.findById(wahlbezirkID).map(wahlbriefdatenModelMapper::toModel);
    }

    @PreAuthorize(
            "hasAuthority('Briefwahl_BUSINESSACTION_PostWahlbriefdaten')"
                    + " and @bezirkIdPermisionEvaluator.tokenUserBezirkIdMatches(#wahlbriefdaten?.getWahlbezirkID(), authentication)"
    )
    public void setWahlbriefdaten(final WahlbriefdatenModel wahlbriefdatenToSet) {
        log.info("#postBeanstandeteWahlbriefe");
        wahlbriefdatenValidator.validWahlbriefdatenToSetOrThrow(wahlbriefdatenToSet);

        wahlbriefdatenRepository.save(wahlbriefdatenModelMapper.toEntity(wahlbriefdatenToSet));
    }

}
