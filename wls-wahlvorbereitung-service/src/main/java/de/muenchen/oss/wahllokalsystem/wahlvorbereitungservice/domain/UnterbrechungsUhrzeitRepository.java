package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain;

import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasAuthority('Wahlvorbereitung_READ_UnterbrechungsUhrzeit')")
public interface UnterbrechungsUhrzeitRepository extends CrudRepository<UnterbrechungsUhrzeit, String> {

    String CACHE = "UnterbrechungsUhrzeitCACHE";

    @Override
    Iterable<UnterbrechungsUhrzeit> findAll();

    @Override
    @Cacheable(value = CACHE, key = "#p0")
    Optional<UnterbrechungsUhrzeit> findById(String wahlbezirkID);

    @Override
    @CachePut(value = CACHE, key = "#p0.wahlbezirkID")
    @PreAuthorize("hasAuthority('Wahlvorbereitung_WRITE_UnterbrechungsUhrzeit')")
    <S extends UnterbrechungsUhrzeit> S save(S unterbrechungsUhrzeit);

    @Override
    @CacheEvict(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('Wahlvorbereitung_DELETE_UnterbrechungsUhrzeit')")
    void deleteById(String wahlbezirkID);

    @Override
    @CacheEvict(value = CACHE, key = "#p0.wahlbezirkID")
    @PreAuthorize("hasAuthority('Wahlvorbereitung_DELETE_UnterbrechungsUhrzeit')")
    void delete(UnterbrechungsUhrzeit entity);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Wahlvorbereitung_DELETE_UnterbrechungsUhrzeit')")
    void deleteAll(Iterable<? extends UnterbrechungsUhrzeit> entities);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Wahlvorbereitung_DELETE_UnterbrechungsUhrzeit')")
    void deleteAll();
}
