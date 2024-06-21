package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

@PreAuthorize("hasAuthority('Basisdaten_READ_WLSWahlvorschlaege')")
@Transactional
public interface WahlvorschlaegeRepository extends CrudRepository<Wahlvorschlaege, BezirkUndWahlID> {

    String CACHE = "WLSWAHLVORSCHLAEGE_CACHE";

    @Override
    List<Wahlvorschlaege> findAll();

    @Override
    @Cacheable(value = CACHE, key = "#p0")
    Optional<Wahlvorschlaege> findById(BezirkUndWahlID bezirkUndWahlID);

    @Override
    @CachePut(value = CACHE, key = "#p0.bezirkUndWahlID")
    @PreAuthorize("hasAuthority('Basisdaten_WRITE_WLSWahlvorschlaege')")
    <S extends Wahlvorschlaege> S save(S wahlvorschlaege);

    @Override
    @CacheEvict(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_WLSWahlvorschlaege')")
    void deleteById(BezirkUndWahlID bezirkUndWahlID);

    @Override
    @CacheEvict(value = CACHE, key = "#p0.bezirkUndWahlID")
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_WLSWahlvorschlaege')")
    void delete(Wahlvorschlaege entity);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_WLSWahlvorschlaege')")
    void deleteAll(Iterable<? extends Wahlvorschlaege> entities);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_WLSWahlvorschlaege')")
    void deleteAll();

    void deleteAllByBezirkUndWahlID_WahlID(String wahlID);

    int countByBezirkUndWahlID(BezirkUndWahlID id);

}
