package de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten;

import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface WahlRepository extends CrudRepository<Wahl, UUID> {
}
