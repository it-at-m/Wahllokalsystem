package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelumschlaege;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasAuthority('Ergebnismeldung_READ_Stimmzettelumschlaege')")
public interface StimmzettelumschlaegeRepository extends CrudRepository<Stimmzettelumschlaege, BezirkUndWahlID> {

    String CACHE = "STIMMZETTELUMSCHLAEGE_CACHE";

    @Override
    Iterable<Stimmzettelumschlaege> findAll();

    @Override
    @Cacheable(value = CACHE, key = "#p0")
    Optional<Stimmzettelumschlaege> findById(BezirkUndWahlID bezirkUndWahlID);

    @Override
    @CachePut(value = CACHE, key = "#p0.bezirkUndWahlID")
    @PreAuthorize("hasAuthority('Ergebnismeldung_WRITE_Stimmzettelumschlaege')")
    <S extends Stimmzettelumschlaege> S save(S stimmzettelumschlaege);

    @Override
    @CacheEvict(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('Ergebnismeldung_DELETE_Stimmzettelumschlaege')")
    void deleteById(BezirkUndWahlID bezirkUndWahlID);

    @Override
    @CacheEvict(value = CACHE, key = "#p0.bezirkUndWahlID")
    @PreAuthorize("hasAuthority('Ergebnismeldung_DELETE_Stimmzettelumschlaege')")
    void delete(Stimmzettelumschlaege entity);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Ergebnismeldung_DELETE_Stimmzettelumschlaege')")
    void deleteAll(Iterable<? extends Stimmzettelumschlaege> entities);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Ergebnismeldung_DELETE_Stimmzettelumschlaege')")
    void deleteAll();
}
