package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain;

import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasAuthority('Wahlvorbereitung_READ_UrnenwahlSchliessungsUhrzeit')")
public interface UrnenwahlSchliessungsUhrzeitRepository extends CrudRepository<UrnenwahlSchliessungsUhrzeit, String> {

    String CACHE = "UrnenwahlSchliessungsUhrzeitCACHE";

    @Override
    Iterable<UrnenwahlSchliessungsUhrzeit> findAll();

    @Override
    @Cacheable(value = CACHE, key = "#p0")
    Optional<UrnenwahlSchliessungsUhrzeit> findById(String wahlbezirkID);

    @Override
    @CachePut(value = CACHE, key = "#p0.wahlbezirkID")
    @PreAuthorize("hasAuthority('Wahlvorbereitung_WRITE_UrnenwahlSchliessungsUhrzeit')")
    <S extends UrnenwahlSchliessungsUhrzeit> S save(S urnenwahlSchliessungsUhrzeit);

    @Override
    @CacheEvict(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('Wahlvorbereitung_DELETE_UrnenwahlSchliessungsUhrzeit')")
    void deleteById(String wahlbezirkID);

    @Override
    @CacheEvict(value = CACHE, key = "#p0.wahlbezirkID")
    @PreAuthorize("hasAuthority('Wahlvorbereitung_DELETE_UrnenwahlSchliessungsUhrzeit')")
    void delete(UrnenwahlSchliessungsUhrzeit entity);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Wahlvorbereitung_DELETE_UrnenwahlSchliessungsUhrzeit')")
    void deleteAll(Iterable<? extends UrnenwahlSchliessungsUhrzeit> entities);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Wahlvorbereitung_DELETE_UrnenwahlSchliessungsUhrzeit')")
    void deleteAll();

}
