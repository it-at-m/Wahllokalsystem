package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahlbeteiligung;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlbeteiligung.WahlbeteiligungRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlbeteiligung.dto.WahlbeteiligungsMeldungDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WahlbeteiligungService {

    private final WahlbeteiligungRepository wahlbeteiligungRepository;

    private final WahlbeteiligungMapper wahlbeteiligungMapper;

    private final WahlbeteiligungValidator wahlbeteiligungValidator;

    @PreAuthorize("hasAuthority('aoueai_BUSINESSACTION_SaveWahlbeteiligung')")
    public void saveWahlbeteiligung(@P("wahlbeteiligungToSet") final WahlbeteiligungsMeldungDTO wahlbeteiligungToSet) {
        log.debug("#saveWahlbeteiligung");

        wahlbeteiligungValidator.validDTOToSetOrThrow(wahlbeteiligungToSet);

        wahlbeteiligungRepository.save(wahlbeteiligungMapper.toEntity(wahlbeteiligungToSet));
    }
}
