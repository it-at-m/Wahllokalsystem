package de.muenchen.oss.wahllokalsystem.monitoringservice.domain.waehleranzahl;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasAuthority('Monitoring_READ_Waehleranzahl')")
public interface WaehleranzahlRepository extends CrudRepository<Waehleranzahl, BezirkUndWahlID> {

    String CACHE = "WAEHLERANZAHL_CACHE";

    @Override
    Iterable<Waehleranzahl> findAll();

    @Override
    @Cacheable(value = CACHE, key = "#p0")
    Optional<Waehleranzahl> findById(BezirkUndWahlID bezirkUndWahlID);

    @Override
    @CachePut(value = CACHE, key = "#p0.bezirkUndWahlID")
    @PreAuthorize("hasAuthority('Monitoring_WRITE_Waehleranzahl')")
    <S extends Waehleranzahl> S save(S waehleranzahl);

    @Override
    @CacheEvict(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('Monitoring_DELETE_Waehleranzahl')")
    void deleteById(BezirkUndWahlID bezirkUndWahlID);

    @Override
    @CacheEvict(value = CACHE, key = "#p0.bezirkUndWahlID")
    @PreAuthorize("hasAuthority('Monitoring_DELETE_Waehleranzahl')")
    void delete(Waehleranzahl entity);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Monitoring_DELETE_Waehleranzahl')")
    void deleteAll(Iterable<? extends Waehleranzahl> entities);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Monitoring_DELETE_Waehleranzahl')")
    void deleteAll();

    Waehleranzahl findFirstByBezirkUndWahlIDOrderByUhrzeitDesc(BezirkUndWahlID bezirkUndWahlID);

}
