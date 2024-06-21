package de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorstand;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface WahlvorstandRepository extends CrudRepository<Wahlvorstand, UUID> {

    Optional<Wahlvorstand> findFirstByWahlbezirkID(UUID wahlbezirkID);
}
