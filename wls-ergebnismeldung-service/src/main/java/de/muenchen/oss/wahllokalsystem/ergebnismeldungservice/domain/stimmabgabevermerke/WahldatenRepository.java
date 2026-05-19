package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.NaturalIdRepository;
import java.util.UUID;
import org.springframework.lang.NonNull;

public interface WahldatenRepository
    extends NaturalIdRepository<Wahldaten, UUID, BezirkUndWahlIDUndWaehlerverzeichnisnummer> {

  @NonNull <S extends Wahldaten> S save(@NonNull final S wahldaten);
}
