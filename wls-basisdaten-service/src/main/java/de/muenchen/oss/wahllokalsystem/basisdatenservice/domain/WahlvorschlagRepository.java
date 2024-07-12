package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain;

import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasAuthority('Basisdaten_READ_Wahlvorschlag')")
public interface WahlvorschlagRepository extends CrudRepository<Wahlvorschlag, UUID> {

    @Override
    @PreAuthorize("hasAuthority('Basisdaten_WRITE_Wahlvorschlag')")
    <S extends Wahlvorschlag> S save(S entity);

    @Override
    @PreAuthorize("hasAuthority('Basisdaten_WRITE_Wahlvorschlag')")
    <S extends Wahlvorschlag> Iterable<S> saveAll(Iterable<S> entities);

    @Override
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Wahlvorschlag')")
    void deleteById(UUID id);

    @Override
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Wahlvorschlag')")
    void delete(Wahlvorschlag entity);

    @Override
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Wahlvorschlag')")
    void deleteAllById(Iterable<? extends UUID> uuids);

    @Override
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Wahlvorschlag')")
    void deleteAll(Iterable<? extends Wahlvorschlag> entities);

    @Override
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Wahlvorschlag')")
    void deleteAll();
}
