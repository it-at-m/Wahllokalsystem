package de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.konfiguration;

import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;

public interface KonfigurationRepository extends CrudRepository<Konfiguration, String> {

    String CACHE = "KonfigurationCACHE";

    @Override
    @PreAuthorize("hasAuthority('Infomanagement_READ_Konfiguration')")
    @NonNull
    List<Konfiguration> findAll();

    @Override
    @Cacheable(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('Infomanagement_READ_Konfiguration')")
    @NonNull
    Optional<Konfiguration> findById(@NonNull String key);

    @Override
    @CachePut(value = CACHE, key = "#p0.schluessel")
    @PreAuthorize("hasAuthority('Infomanagement_WRITE_Konfiguration')")
    @NonNull
    <S extends Konfiguration> S save(@NonNull S konfiguration);

    @Override
    @CacheEvict(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('Infomanagement_DELETE_Konfiguration')")
    void deleteById(@NonNull String key);

    @Override
    @CacheEvict(value = CACHE, key = "#p0.schluessel")
    @PreAuthorize("hasAuthority('Infomanagement_DELETE_Konfiguration')")
    void delete(@NonNull Konfiguration entity);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Infomanagement_DELETE_Konfiguration')")
    void deleteAll(@NonNull Iterable<? extends Konfiguration> entities);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Infomanagement_DELETE_Konfiguration')")
    void deleteAll();

    @Query("SELECT k FROM Konfiguration k WHERE k.schluessel = 'FRUEHESTE_LOGIN_UHRZEIT'")
    Optional<Konfiguration> getFruehesteLoginUhrzeit();

    @Query("SELECT k FROM Konfiguration k WHERE k.schluessel = 'SPAETESTE_LOGIN_UHRZEIT'")
    Optional<Konfiguration> getSpaetesteLoginUhrzeit();

    @Query("SELECT k FROM Konfiguration k WHERE k.schluessel = 'WILLKOMMENSTEXT'")
    Optional<Konfiguration> getWillkommenstext();

}
