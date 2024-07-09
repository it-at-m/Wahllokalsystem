package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain;

import java.util.List;
import java.util.UUID;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

@PreAuthorize("hasAuthority('Basisdaten_READ_Wahltag')")
@Transactional
public interface WahltagRepository extends CrudRepository<Wahltag, UUID> {

    String CACHE = "WAHLTAG_CACHE";

    @Override
    @CachePut(value = CACHE, key = "#p0.wahltagID")
    @PreAuthorize("hasAuthority('Basisdaten_WRITE_Wahltag')")
    <S extends Wahltag> S save(S entity);

    @Override
    @PreAuthorize("hasAuthority('Basisdaten_WRITE_Wahltag')")
    <S extends Wahltag> Iterable<S> saveAll(Iterable<S> entities );

    @Override
    @CacheEvict(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Wahltag')")
    void deleteById(UUID id);

    @Override
    @CacheEvict(value = CACHE, key = "#p0.wahltagID")
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Wahltag')")
    void delete(Wahltag entity);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Wahltag')")
    void deleteAllById(Iterable<? extends UUID> strings);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Wahltag')")
    void deleteAll(Iterable<? extends Wahltag> entities);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Wahltag')")
    void deleteAll();

    List<Wahltag> findAllByOrderByWahltagAsc();
}
