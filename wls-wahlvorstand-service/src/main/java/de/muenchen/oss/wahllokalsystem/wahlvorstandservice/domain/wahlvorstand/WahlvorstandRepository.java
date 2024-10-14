package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.domain.wahlvorstand;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@PreAuthorize("hasAuthority('Wahlvorstand_READ_Wahlvorstand')")
public interface WahlvorstandRepository extends CrudRepository<Wahlvorstand, UUID> {

    String CACHE = "WAHLVORSTAND_CACHE";

    @Override
    Iterable<Wahlvorstand> findAll();

    @Cacheable(value = CACHE, key = "#p0")
    Wahlvorstand findByWahlbezirkID(String wahlbezirkID);

    @Override
    @CachePut(value = CACHE, key = "#p0.wahlbezirkID")
    @PreAuthorize("hasAuthority('Wahlvorstand_WRITE_Wahlvorstand')")
    <S extends Wahlvorstand> S save(S wahlvorstand);

    @Override
    @CacheEvict(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('Wahlvorstand_DELETE_Wahlvorstand')")
    void deleteById(UUID id);

    @Override
    @CacheEvict(value = CACHE, key = "#p0.wahlbezirkID")
    @PreAuthorize("hasAuthority('Wahlvorstand_DELETE_Wahlvorstand')")
    void delete(Wahlvorstand entity);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Wahlvorstand_DELETE_Wahlvorstand')")
    void deleteAll(Iterable<? extends Wahlvorstand> entities);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Wahlvorstand_DELETE_Wahlvorstand')")
    void deleteAll();

    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Wahlvorstand_DELETE_Wahlvorstand')")
    @Transactional
    void deleteByWahlbezirkID(String wahlbezirkID);
}
