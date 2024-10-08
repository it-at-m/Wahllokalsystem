package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.domain;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.UUID;

public interface WahlvorstandRepository extends CrudRepository<Wahlvorstand, UUID> {

    String CACHE = "WAHLVORSTAND_CACHE";

    @Override
    @PreAuthorize("hasAuthority('Wahlvorstand_READ_Wahlvorstand')")
    Iterable<Wahlvorstand> findAll();

    @Cacheable(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('Wahlvorstand_READ_Wahlvorstand')")
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
}