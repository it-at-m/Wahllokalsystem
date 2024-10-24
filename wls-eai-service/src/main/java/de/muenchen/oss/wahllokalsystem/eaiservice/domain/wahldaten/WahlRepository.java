package de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface WahlRepository extends CrudRepository<Wahl, UUID> {

    List<Wahl> findByWahltagTagAndWahltagNummer(LocalDate wahltag, String nummer);
}
