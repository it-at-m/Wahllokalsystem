package de.muenchen.oss.wahllokalsystem.eaiservice.domain.ergebnismeldung;

import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface ErgebnisRepository extends CrudRepository<Ergebnis, UUID> {

}
