package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasAuthority('Basisdaten_READ_Wahl')")
public interface WahlRepository extends CrudRepository<Wahl, String> {

    @Override
    Iterable<Wahl> findAll();

    Optional<Wahl> findById(UUID wahlID);

    @Override
    @PreAuthorize("hasAuthority('Basisdaten_WRITE_Wahl')")
    <S extends Wahl> S save(S wahl);

    int countByWahltag(LocalDate wahltag);

    List<Wahl> findByWahltagOrderByReihenfolge(LocalDate wahltag);

}
