package de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahllokalzustand;

import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface WahllokalZustandRepository extends CrudRepository<WahllokalZustand, UUID> {}
