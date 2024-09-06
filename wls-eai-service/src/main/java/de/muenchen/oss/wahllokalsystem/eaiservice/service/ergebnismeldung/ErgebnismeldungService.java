package de.muenchen.oss.wahllokalsystem.eaiservice.service.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung.ErgebnisRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung.Ergebnismeldung;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung.ErgebnismeldungRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung.UngueltigeStimmzettelRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlergebnis.dto.ErgebnismeldungDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ErgebnismeldungService {

    private final ErgebnisRepository ergebnisRepository;

    private final ErgebnismeldungRepository ergebnismeldungRepository;

    private final UngueltigeStimmzettelRepository ungueltigeStimmzettelRepository;

    private final ErgebnismeldungMapper ergebnismeldungMapper;

    private final ErgebnismeldungValidator ergebnismeldungValidator;

    @PreAuthorize("hasAuthority('aoueai_BUSINESSACTION_SaveErgebnismeldung')")
    public void saveErgebnismeldung(@P("ergebnismeldungToSet") final ErgebnismeldungDTO ergebnismeldungToSet) {
        log.debug("#saveErgebnismeldung");

        ergebnismeldungValidator.validDTOToSetOrThrow(ergebnismeldungToSet);
        Ergebnismeldung ergebnismeldung = ergebnismeldungMapper.toEntity(ergebnismeldungToSet);

        ergebnismeldungRepository.save(ergebnismeldung);

        ergebnismeldung.getErgebnisse().stream().map(ergebnisRepository::save);

        ergebnismeldung.getUngueltigeStimmzettels().stream().map(ungueltigeStimmzettelRepository::save);

    }
}
