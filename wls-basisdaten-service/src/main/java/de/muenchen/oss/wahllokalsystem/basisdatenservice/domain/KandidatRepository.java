package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain;

import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasAuthority('Basisdaten_READ_Kandidat')")
public interface KandidatRepository extends CrudRepository<Kandidat, UUID> {

    @Override
    @PreAuthorize("hasAuthority('Basisdaten_SAVE_Kandidat')")
    <S extends Kandidat> S save(S entity);

    @Override
    @PreAuthorize("hasAuthority('Basisdaten_SAVE_Kandidat')")
    <S extends Kandidat> Iterable<S> saveAll(Iterable<S> entities);

    @Override
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Kandidat')")
    void deleteById(UUID id);

    @Override
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Kandidat')")
    void delete(Kandidat entity);

    @Override
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Kandidat')")
    void deleteAllById(Iterable<? extends UUID> strings);

    @Override
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Kandidat')")
    void deleteAll(Iterable<? extends Kandidat> entities);

    @Override
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Kandidat')")
    void deleteAll();
}
