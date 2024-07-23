package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasAuthority('Basisdaten_READ_Wahltag')")
public interface WahltagRepository extends CrudRepository<Wahltag, String> {
    

    @Override
    Iterable<Wahltag> findAll();

    @Override
    Optional<Wahltag> findById(String wahltagID);

    @Override
    @PreAuthorize("hasAuthority('Basisdaten_WRITE_Wahltag')")
    <S extends Wahltag> S save(S wahltag);

    @Override
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Wahltag')")
    void delete(Wahltag entity);

    @Override
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Wahltag')")
    void deleteAll(Iterable<? extends Wahltag> entities);

    @Override
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Wahltag')")
    void deleteAll();
}
