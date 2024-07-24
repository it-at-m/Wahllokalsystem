package de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlbeteiligung;

import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface WahlbeteiligungRepository extends CrudRepository<Wahlbeteiligung, UUID> {
}
