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
public interface WLSWahlvorschlaegeRepository extends CrudRepository<WLSWahlvorschlaege, BezirkUndWahlID> {

    String CACHE = "WLSWAHLVORSCHLAEGE_CACHE";

    @Override
    List<WLSWahlvorschlaege> findAll();

    @Override
    @Cacheable(value = CACHE, key = "#p0")
    Optional<WLSWahlvorschlaege> findById(BezirkUndWahlID bezirkUndWahlID);

    @Override
    @CachePut(value = CACHE, key = "#p0.bezirkUndWahlID")
    @PreAuthorize("hasAuthority('Basisdaten_WRITE_WLSWahlvorschlaege')")
    <S extends WLSWahlvorschlaege> S save(S wLSWahlvorschlaege);

    @Override
    @CacheEvict(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_WLSWahlvorschlaege')")
    void deleteById(BezirkUndWahlID bezirkUndWahlID);

    @Override
    @CacheEvict(value = CACHE, key = "#p0.bezirkUndWahlID")
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_WLSWahlvorschlaege')")
    void delete(WLSWahlvorschlaege entity);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_WLSWahlvorschlaege')")
    void deleteAll(Iterable<? extends WLSWahlvorschlaege> entities);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_WLSWahlvorschlaege')")
    void deleteAll();

    void deleteAllByBezirkUndWahlID_WahlID(String wahlID);

    int countByBezirkUndWahlID(BezirkUndWahlID id);

}
