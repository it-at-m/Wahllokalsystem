package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.awerte;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasAuthority('Ergebnismeldung_BUSINESSACTION_GetAWerte') OR hasAuthority('Admin_BUSINESSACTION_LoadWahltermindaten')")
public interface AWerteRepository extends CrudRepository<AWerte, BezirkUndWahlID> {

    String CACHE = "AWERTE_CACHE";

    @Override
    @Cacheable(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('Ergebnismeldung_READ_AWerte')")
    Optional<AWerte> findById(BezirkUndWahlID bezirkUndWahlID);

    @PreAuthorize("hasAuthority('Ergebnismeldung_READ_AWerte') OR hasAuthority('Admin_BUSINESSACTION_LoadWahltermindaten')")
    List<AWerte> findByBezirkUndWahlID_WahlbezirkID(String wahlbezirkID);

    @Override
    @CachePut(value = CACHE, key = "#p0.bezirkUndWahlID")
    @PreAuthorize("hasAuthority('Ergebnismeldung_WRITE_AWerte') OR hasAuthority('Admin_BUSINESSACTION_LoadWahltermindaten')")
    <S extends AWerte> S save(S aWerte);

    @Override
    @PreAuthorize("hasAuthority('Ergebnismeldung_WRITE_AWerte') OR hasAuthority('Admin_BUSINESSACTION_LoadWahltermindaten')")
    <S extends AWerte> Iterable<S> saveAll(Iterable<S> entities);

    @Override
    @CacheEvict(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('Monitoring_DELETE_Waehleranzahl')")
    void deleteById(BezirkUndWahlID bezirkUndWahlID);

    @Override
    @CacheEvict(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('Ergebnismeldung_DELETE_AWerte')")
    void delete(AWerte entity);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Ergebnismeldung_DELETE_AWerte')")
    void deleteAll(Iterable<? extends AWerte> entities);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Ergebnismeldung_DELETE_AWerte')")
    void deleteAll();
}
