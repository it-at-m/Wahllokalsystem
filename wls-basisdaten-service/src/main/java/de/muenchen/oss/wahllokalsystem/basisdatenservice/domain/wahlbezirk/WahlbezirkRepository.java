package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlbezirk;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

@PreAuthorize("hasAuthority('Basisdaten_READ_Wahlbezirk')")
@Transactional
public interface WahlbezirkRepository extends CrudRepository<Wahlbezirk, String> {

    String CACHE = "WAHLBEZIRK_CACHE";

    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Wahlbezirk')")
    void deleteByWahltag(LocalDate wahltag);

    boolean existsByWahltag(LocalDate wahltag);

    List<Wahlbezirk> findByWahltag(LocalDate wahltag);

    @Override
    List<Wahlbezirk> findAll();

    @Override
    @Cacheable(value = CACHE, key = "#p0")
    Optional<Wahlbezirk> findById(String wahlbezirkID);

    @Override
    @CachePut(value = CACHE, key = "#p0.wahlbezirkID")
    @PreAuthorize("hasAuthority('Basisdaten_WRITE_Wahlbezirk')")
    <S extends Wahlbezirk> S save(S entity);

    @Override
    @PreAuthorize("hasAuthority('Basisdaten_WRITE_Wahlbezirk')")
    <S extends Wahlbezirk> Iterable<S> saveAll(Iterable<S> entities);

    @Override
    @CacheEvict(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Wahlbezirk')")
    void deleteById(String wahlbezirkID);

    @Override
    @CacheEvict(value = CACHE, key = "#p0.wahlbezirkID")
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Wahlbezirk')")
    void delete(Wahlbezirk entity);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Wahlbezirk')")
    void deleteAll(Iterable<? extends Wahlbezirk> entities);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Wahlbezirk')")
    void deleteAll();
}
