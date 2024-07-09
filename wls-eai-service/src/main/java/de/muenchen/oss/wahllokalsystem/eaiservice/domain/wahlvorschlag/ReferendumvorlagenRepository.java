package de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface ReferendumvorlagenRepository extends CrudRepository<Referendumvorlagen, UUID> {

    Optional<Referendumvorlagen> findFirstByWahlbezirkIDAndWahlID(String wahlbezirkID, String wahlID);
}
