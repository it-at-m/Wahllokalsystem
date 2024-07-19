package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

@PreAuthorize("hasAuthority('Basisdaten_READ_Wahl')")
@Transactional
public interface WahlRepository extends CrudRepository<Wahl, String> {

    String CACHE = "WAHL_CACHE";

    @Override
    List<Wahl> findAll();

    @Override
    @Cacheable(value = CACHE, key = "#p0")
    Optional<Wahl> findById(String wahlID);

    @Override
    @CachePut(value = CACHE, key = "#p0.wahlID")
    @PreAuthorize("hasAuthority('Basisdaten_WRITE_Wahl')")
    <S extends Wahl> S save(S entity);

    @Override
    @PreAuthorize("hasAuthority('Basisdaten_WRITE_Wahl')")
    <S extends Wahl> Iterable<S> saveAll(Iterable<S> entities);

    @Override
    @CacheEvict(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Wahl')")
    void deleteById(String wahlID);

    @Override
    @CacheEvict(value = CACHE, key = "#p0.wahlID")
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Wahl')")
    void delete(Wahl entity);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Wahl')")
    void deleteAll(Iterable<? extends Wahl> entities);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Wahl')")
    void deleteAll();

    List<Wahl> findByWahltagOrderByReihenfolge(LocalDate wahltag);
}
