package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.resetwahlen;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Farbe;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahlRepository;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResetWahlenService {

    private final WahlRepository wahlRepository;

    @PreAuthorize(
        "hasAuthority('Basisdaten_BUSINESSACTION_ResetWahlen')"
    )
    public void resetWahlen() {
        log.info("#resetWahlen");
        wahlRepository.saveAll(wahlRepository.findAll().stream()
                .peek(wahl -> {
                    wahl.setFarbe(new Farbe(0, 0, 0));
                    wahl.setReihenfolge(0);
                    wahl.setWaehlerverzeichnisNummer(1);
                })
                .collect(Collectors.toList()));
    }
}
