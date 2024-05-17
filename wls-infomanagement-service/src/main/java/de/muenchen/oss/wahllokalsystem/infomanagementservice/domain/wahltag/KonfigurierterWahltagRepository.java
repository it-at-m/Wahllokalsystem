package de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.wahltag;

import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;

public interface KonfigurierterWahltagRepository extends CrudRepository<KonfigurierterWahltag, String> {

    String CACHE = "KonfigurierterWahltagCACHE";

    @Override
    List<KonfigurierterWahltag> findAll();

    @Override
    @Cacheable(value = CACHE, key = "#p0")
    Optional<KonfigurierterWahltag> findById(String key);

    @Override
    @CachePut(value = CACHE, key = "#p0.wahltagID")
    @PreAuthorize("hasAuthority('Infomanagement_WRITE_KonfigurierterWahltag')")
    <S extends KonfigurierterWahltag> S save(S konfigurierterWahltag);

    @Override
    @CacheEvict(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('Infomanagement_DELETE_KonfigurierterWahltag')")
    void deleteById(String wahltagID);

    @Override
    @CacheEvict(value = CACHE, key = "#p0.wahltagID")
    @PreAuthorize("hasAuthority('Infomanagement_DELETE_KonfigurierterWahltag')")
    void delete(KonfigurierterWahltag entity);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Infomanagement_DELETE_KonfigurierterWahltag')")
    void deleteAll(Iterable<? extends KonfigurierterWahltag> entities);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Infomanagement_DELETE_KonfigurierterWahltag')")
    void deleteAll();

    KonfigurierterWahltag findByActive(@Param(value = "active") boolean isActive);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE KonfigurierterWahltag w SET w.active = false")
    @PreAuthorize("hasAuthority('Infomanagement_WRITE_KonfigurierterWahltag')")
    void setExistingKonfigurierteWahltageInaktiv();

}
