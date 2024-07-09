package de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface WahlvorschlaegeListeRepository extends CrudRepository<WahlvorschlaegeListe, UUID> {

    Optional<WahlvorschlaegeListe> findFirstByWahltagAndWahlID(LocalDate wahltag, String wahlID);

}
