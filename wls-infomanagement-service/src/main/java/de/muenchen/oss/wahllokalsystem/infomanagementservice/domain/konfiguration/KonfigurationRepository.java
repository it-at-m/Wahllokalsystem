package de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.konfiguration;

import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

public interface KonfigurationRepository extends CrudRepository<Konfiguration, String> {

    String CACHE = "KonfigurationCACHE";

    @Override
    @PreAuthorize("hasAuthority('Infomanagement_READ_Konfiguration')")
    List<Konfiguration> findAll();

    @Override
    @Cacheable(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('Infomanagement_READ_Konfiguration')")
    Optional<Konfiguration> findById(String key);

    @Override
    @CachePut(value = CACHE, key = "#p0.schluessel")
    @PreAuthorize("hasAuthority('Infomanagement_WRITE_Konfiguration')")
    <S extends Konfiguration> S save(S konfiguration);

    @Override
    @CacheEvict(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('Infomanagement_DELETE_Konfiguration')")
    void deleteById(String key);

    @Override
    @CacheEvict(value = CACHE, key = "#p0.schluessel")
    @PreAuthorize("hasAuthority('Infomanagement_DELETE_Konfiguration')")
    void delete(Konfiguration entity);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Infomanagement_DELETE_Konfiguration')")
    void deleteAll(Iterable<? extends Konfiguration> entities);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Infomanagement_DELETE_Konfiguration')")
    void deleteAll();

}
