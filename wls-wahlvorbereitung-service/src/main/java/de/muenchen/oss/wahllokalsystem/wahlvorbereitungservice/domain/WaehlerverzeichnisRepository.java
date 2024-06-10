package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasAuthority('Wahlvorbereitung_READ_Waehlerverzeichnis')")
public interface WaehlerverzeichnisRepository extends CrudRepository<Waehlerverzeichnis, BezirkIDUndWaehlerverzeichnisNummer> {

    String CACHE = "WaehlerverzeichnisCACHE";

    @Override
    Iterable<Waehlerverzeichnis> findAll();

    @Override
    @Cacheable(value = CACHE, key = "#p0")
    Optional<Waehlerverzeichnis> findById(BezirkIDUndWaehlerverzeichnisNummer bezirkIDUndWaehlerverzeichnisNummer);

    @Override
    @CachePut(value = CACHE, key = "#p0.waehlerverzeichnisReference")
    @PreAuthorize("hasAuthority('Wahlvorbereitung_WRITE_Waehlerverzeichnis')")
    <S extends Waehlerverzeichnis> S save(S waehlerverzeichnis);

    @Override
    @CacheEvict(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('Wahlvorbereitung_DELETE_Waehlerverzeichnis')")
    void deleteById(BezirkIDUndWaehlerverzeichnisNummer bezirkIDUndWaehlerverzeichnisNummer);

    @Override
    @CacheEvict(value = CACHE, key = "#p0.waehlerverzeichnisReference")
    @PreAuthorize("hasAuthority('Wahlvorbereitung_DELETE_Waehlerverzeichnis')")
    void delete(Waehlerverzeichnis entity);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Wahlvorbereitung_DELETE_Waehlerverzeichnis')")
    void deleteAll(Iterable<? extends Waehlerverzeichnis> entities);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Wahlvorbereitung_DELETE_Waehlerverzeichnis')")
    void deleteAll();
}
