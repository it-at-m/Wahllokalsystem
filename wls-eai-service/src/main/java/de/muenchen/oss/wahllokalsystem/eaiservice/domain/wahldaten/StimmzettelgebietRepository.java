package de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface StimmzettelgebietRepository extends CrudRepository<Stimmzettelgebiet, UUID> {

    List<Stimmzettelgebiet> findByWahlWahltagTagAndWahlWahltagNummer(LocalDate wahltag, String nummer);
}
