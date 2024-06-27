package de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface WahlvorschlagRepository extends CrudRepository<Wahlvorschlaege, UUID> {

    Optional<Wahlvorschlaege> findFirstByWahlbezirkIDAndWahlID(String wahlbezirkID, String wahlID);
}
