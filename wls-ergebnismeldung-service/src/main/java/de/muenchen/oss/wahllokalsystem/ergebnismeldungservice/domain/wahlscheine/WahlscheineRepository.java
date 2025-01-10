package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.wahlscheine;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasAuthority('Ergebnismeldung_READ_Wahlscheine')")
public interface WahlscheineRepository extends CrudRepository<Wahlscheine, BezirkUndWahlID> {

    String CACHE = "WAHLSCHEINE_CACHE";

    @Override
    Iterable<Wahlscheine> findAll();

    @Override
    @Cacheable(value = CACHE, key = "#p0")
    Optional<Wahlscheine> findById(BezirkUndWahlID bezirkUndWahlID);

    @Override
    @CachePut(value = CACHE, key = "#p0.bezirkUndWahlID")
    @PreAuthorize("hasAuthority('Ergebnismeldung_WRITE_Wahlscheine')")
    <S extends Wahlscheine> S save(S entity);

    @Override
    @CacheEvict(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('Ergebnismeldung_DELETE_Wahlscheine')")
    void deleteById(BezirkUndWahlID bezirkUndWahlID);

    @Override
    @CacheEvict(value = CACHE, key = "#p0.bezirkUndWahlID")
    @PreAuthorize("hasAuthority('Ergebnismeldung_DELETE_Wahlscheine')")
    void delete(Wahlscheine entity);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Ergebnismeldung_DELETE_Wahlscheine')")
    void deleteAll(Iterable<? extends Wahlscheine> entities);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Ergebnismeldung_DELETE_Wahlscheine')")
    void deleteAll();
}
