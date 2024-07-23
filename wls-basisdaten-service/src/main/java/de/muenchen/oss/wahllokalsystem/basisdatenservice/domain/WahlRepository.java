package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasAuthority('Basisdaten_READ_Wahl')")
public interface WahlRepository extends CrudRepository<Wahl, String> {

    @Override
    Iterable<Wahl> findAll();

    @Override
    @PreAuthorize("hasAuthority('Basisdaten_WRITE_Wahl')")
    <S extends Wahl> S save(S wahl);
}
