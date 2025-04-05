package de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag;

import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface KandidatRepository extends CrudRepository<Kandidat, UUID> {
}
