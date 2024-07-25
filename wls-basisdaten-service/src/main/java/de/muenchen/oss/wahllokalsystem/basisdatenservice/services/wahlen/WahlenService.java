package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Farbe;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Wahl;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.WahlRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WahlenService {

    private final WahlRepository wahlRepository;

    private final ExceptionFactory exceptionFactory;

    @PreAuthorize(
        "hasAuthority('Basisdaten_BUSINESSACTION_ResetWahlen')"
    )
    @Transactional
    public void resetWahlen() {
        log.info("#resetWahlen");
        try {
            val existingWahlenToReset = wahlRepository.findAll();
            existingWahlenToReset.forEach(this::resetWahl);
            wahlRepository.saveAll(existingWahlenToReset);
        } catch (final Exception e) {
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.RESET_WAHLEN_NICHT_ERFOLGREICH);
        }
    }

    private Wahl resetWahl(final Wahl wahl) {
        wahl.setFarbe(new Farbe(0, 0, 0));
        wahl.setReihenfolge(0);
        wahl.setWaehlerverzeichnisnummer(1);
        return wahl;
    }
}
