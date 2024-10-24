package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain;

import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasAuthority('Wahlvorbereitung_READ_Briefwahlvorbereitung')")
public interface BriefwahlvorbereitungRepository extends CrudRepository<Briefwahlvorbereitung, String> {

    String CACHE = "BriefwahlvorbereitungCACHE";

    @Override
    Iterable<Briefwahlvorbereitung> findAll();

    @Override
    @Cacheable(value = CACHE, key = "#p0")
    Optional<Briefwahlvorbereitung> findById(String wahlbezirkID);

    @Override
    @CachePut(value = CACHE, key = "#p0.wahlbezirkID")
    @PreAuthorize("hasAuthority('Wahlvorbereitung_WRITE_Briefwahlvorbereitung')")
    <S extends Briefwahlvorbereitung> S save(S briefwahlvorbereitung);

    @Override
    @CacheEvict(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('Wahlvorbereitung_DELETE_Briefwahlvorbereitung')")
    void deleteById(String wahlbezirkID);

    @Override
    @CacheEvict(value = CACHE, key = "#p0.wahlbezirkID")
    @PreAuthorize("hasAuthority('Wahlvorbereitung_DELETE_Briefwahlvorbereitung')")
    void delete(Briefwahlvorbereitung entity);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Wahlvorbereitung_DELETE_Briefwahlvorbereitung')")
    void deleteAll(Iterable<? extends Briefwahlvorbereitung> entities);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Wahlvorbereitung_DELETE_Briefwahlvorbereitung')")
    void deleteAll();
}
