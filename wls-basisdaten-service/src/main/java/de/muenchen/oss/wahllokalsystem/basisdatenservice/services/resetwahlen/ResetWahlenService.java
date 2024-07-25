package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.resetwahlen;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Farbe;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.WahlRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
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

    private final ExceptionFactory exceptionFactory;

    @PreAuthorize(
        "hasAuthority('Basisdaten_BUSINESSACTION_ResetWahlen')"
    )
    public void resetWahlen() {
        log.info("#resetWahlen");
        try {
            wahlRepository.saveAll(
                    wahlRepository.findAll().stream()
                            .peek(wahl -> {
                                wahl.setFarbe(new Farbe(0, 0, 0));
                                wahl.setReihenfolge(0);
                                wahl.setWaehlerverzeichnisnummer(1);
                            })
                            .collect(Collectors.toList()));
        } catch (final Exception e) {
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.RESET_WAHLEN_NICHT_ERFOLGREICH);
        }
    }
}
