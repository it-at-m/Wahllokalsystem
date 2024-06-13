package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain;

import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasAuthority('Wahlvorbereitung_READ_FortsetzungsUhrzeit')")
public interface FortsetzungsUhrzeitRepository extends CrudRepository<FortsetzungsUhrzeit, String> {

    String CACHE = "FortsetzungsUhrzeitCACHE";

    @Override
    Iterable<FortsetzungsUhrzeit> findAll();

    @Override
    @Cacheable(value = CACHE, key = "#p0")
    Optional<FortsetzungsUhrzeit> findById(String wahlbezirkID);

    @Override
    @CachePut(value = CACHE, key = "#p0.wahlbezirkID")
    @PreAuthorize("hasAuthority('Wahlvorbereitung_WRITE_FortsetzungsUhrzeit')")
    <S extends FortsetzungsUhrzeit> S save(S fortsetzungsUhrzeit);

    @Override
    @CacheEvict(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('Wahlvorbereitung_DELETE_FortsetzungsUhrzeit')")
    void deleteById(String wahlbezirkID);

    @Override
    @CacheEvict(value = CACHE, key = "#p0.wahlbezirkID")
    @PreAuthorize("hasAuthority('Wahlvorbereitung_DELETE_FortsetzungsUhrzeit')")
    void delete(FortsetzungsUhrzeit entity);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Wahlvorbereitung_DELETE_FortsetzungsUhrzeit')")
    void deleteAll(Iterable<? extends FortsetzungsUhrzeit> entities);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Wahlvorbereitung_DELETE_FortsetzungsUhrzeit')")
    void deleteAll();
}
