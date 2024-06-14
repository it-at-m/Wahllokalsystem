package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain;

import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasAuthority('Wahlvorbereitung_READ_Eroeffnungsuhrzeit')")
public interface EroeffnungsUhrzeitRepository extends CrudRepository<EroeffnungsUhrzeit, String> {

    String CACHE = "EroeffnungsUhrzeitCACHE";

    @Override
    Iterable<EroeffnungsUhrzeit> findAll();

    @Override
    @Cacheable(value = CACHE, key = "#p0")
    Optional<EroeffnungsUhrzeit> findById(String wahlbezirkID);

    @Override
    @CachePut(value = CACHE, key = "#p0.wahlbezirkID")
    @PreAuthorize("hasAuthority('Wahlvorbereitung_WRITE_Eroeffnungsuhrzeit')")
    <S extends EroeffnungsUhrzeit> S save(S eroeffnungsUhrzeit);

    @Override
    @CacheEvict(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('Wahlvorbereitung_DELETE_Eroeffnungsuhrzeit')")
    void deleteById(String wahlbezirkID);

    @Override
    @CacheEvict(value = CACHE, key = "#p0.wahlbezirkID")
    @PreAuthorize("hasAuthority('Wahlvorbereitung_DELETE_Eroeffnungsuhrzeit')")
    void delete(EroeffnungsUhrzeit entity);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Wahlvorbereitung_DELETE_Eroeffnungsuhrzeit')")
    void deleteAll(Iterable<? extends EroeffnungsUhrzeit> entities);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Wahlvorbereitung_DELETE_Eroeffnungsuhrzeit')")
    void deleteAll();
}
