package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain;

import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

public interface UrnenwahlVorbereitungRepository extends CrudRepository<UrnenwahlVorbereitung, String> {

    String CACHE = "UrnenwahlVorbereitungCACHE";

    @Override
    Iterable<UrnenwahlVorbereitung> findAll();

    @Override
    @Cacheable(value = CACHE, key = "#p0")
    Optional<UrnenwahlVorbereitung> findById(String wahlbezirkID);

    @Override
    @CachePut(value = CACHE, key = "#p0.wahlbezirkID")
    @PreAuthorize("hasAuthority('Wahlvorbereitung_WRITE_UrnenwahlVorbereitung')")
    <S extends UrnenwahlVorbereitung> S save(S urnenwahlVorbereitung);

    @Override
    @CacheEvict(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('Wahlvorbereitung_DELETE_UrnenwahlVorbereitung')")
    void deleteById(String wahlbezirkID);

    @Override
    @CacheEvict(value = CACHE, key = "#p0.wahlbezirkID")
    @PreAuthorize("hasAuthority('Wahlvorbereitung_DELETE_UrnenwahlVorbereitung')")
    void delete(UrnenwahlVorbereitung entity);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Wahlvorbereitung_DELETE_UrnenwahlVorbereitung')")
    void deleteAll(Iterable<? extends UrnenwahlVorbereitung> entities);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Wahlvorbereitung_DELETE_UrnenwahlVorbereitung')")
    void deleteAll();
}
