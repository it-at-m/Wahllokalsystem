package de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.wahltag;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.wahltag.WahltagStatus;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;

public interface KonfigurierterWahltagRepository extends CrudRepository<KonfigurierterWahltag, String> {

    String CACHE = "KONFIGURIERTERWAHLTAG_CACHE";

    @Override
    @NonNull
    Iterable<KonfigurierterWahltag> findAll();

    @Override
    @CachePut(value = CACHE, key = "#p0.wahltagID")
    @PreAuthorize("hasAuthority('Infomanagement_WRITE_KonfigurierterWahltag')")
    <S extends KonfigurierterWahltag> S save(S konfigurierterWahltag);

    @Override
    @CacheEvict(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('Infomanagement_DELETE_KonfigurierterWahltag')")
    void deleteById(@NonNull String wahltagID);

    @Override
    @CacheEvict(value = CACHE, key = "#p0.wahltagID")
    @PreAuthorize("hasAuthority('Infomanagement_DELETE_KonfigurierterWahltag')")
    void delete(@NonNull KonfigurierterWahltag entity);

    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Infomanagement_DELETE_KonfigurierterWahltag')")
    void deleteAll(@NonNull Iterable<? extends KonfigurierterWahltag> entities);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Infomanagement_DELETE_KonfigurierterWahltag')")
    void deleteAll();

    KonfigurierterWahltag findByWahltagStatus(@Param(value = "wahltagStatus") WahltagStatus wahltagStatus);

}
