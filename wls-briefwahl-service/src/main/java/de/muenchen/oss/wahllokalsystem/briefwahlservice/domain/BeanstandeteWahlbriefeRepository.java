package de.muenchen.oss.wahllokalsystem.briefwahlservice.domain;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasAuthority('Briefwahl_READ_BeanstandeteWahlbriefe')")
public interface BeanstandeteWahlbriefeRepository extends CrudRepository<BeanstandeteWahlbriefe, BezirkIDUndWaehlerverzeichnisNummer> {

    String CACHE = "BeanstandeteWahlbriefeCACHE";

    @Override
    @NonNull
    Iterable<BeanstandeteWahlbriefe> findAll();

    @Override
    @CachePut(value = CACHE, key = "#p0.bezirkIDUndWaehlerverzeichnisNummer")
    @PreAuthorize("hasAuthority('Briefwahl_WRITE_BeanstandeteWahlbriefe')")
    @NonNull
    <S extends BeanstandeteWahlbriefe> S save(@NonNull S beanstandeteWahlbriefe);

    @Override
    @CacheEvict(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('Briefwahl_DELETE_BeanstandeteWahlbriefe')")
    void deleteById(@NonNull BezirkIDUndWaehlerverzeichnisNummer bezirkIDUndWaehlerverzeichnisNummer);

    @Override
    @CacheEvict(value = CACHE, key = "#p0.bezirkIDUndWaehlerverzeichnisNummer")
    @PreAuthorize("hasAuthority('Briefwahl_DELETE_BeanstandeteWahlbriefe')")
    void delete(@NonNull BeanstandeteWahlbriefe entity);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Briefwahl_DELETE_BeanstandeteWahlbriefe')")
    void deleteAll(@NonNull Iterable<? extends BeanstandeteWahlbriefe> entities);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Briefwahl_DELETE_BeanstandeteWahlbriefe')")
    void deleteAll();
}
