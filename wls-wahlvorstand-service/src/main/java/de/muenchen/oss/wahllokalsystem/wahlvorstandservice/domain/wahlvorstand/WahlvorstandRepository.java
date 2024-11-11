package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.domain.wahlvorstand;

import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasAuthority('Wahlvorstand_READ_Wahlvorstand')")
public interface WahlvorstandRepository extends CrudRepository<Wahlvorstand, String> {

    String CACHE = "WAHLVORSTAND_CACHE";

    @Override
    Iterable<Wahlvorstand> findAll();

    @Override
    @Cacheable(value = CACHE, key = "#p0")
    Optional<Wahlvorstand> findById(String wahlbezirkID);

    @Override
    @CachePut(value = CACHE, key = "#p0.wahlbezirkID")
    @PreAuthorize("hasAuthority('Wahlvorstand_WRITE_Wahlvorstand')")
    <S extends Wahlvorstand> S save(S wahlvorstand);

    @Override
    @CacheEvict(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('Wahlvorstand_DELETE_Wahlvorstand')")
    void deleteById(String id);

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
