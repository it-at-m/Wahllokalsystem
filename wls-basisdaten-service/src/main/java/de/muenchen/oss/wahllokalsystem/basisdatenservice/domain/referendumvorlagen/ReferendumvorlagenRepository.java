package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasAuthority('Basisdaten_READ_Referendumvorlagen')")
public interface ReferendumvorlagenRepository extends CrudRepository<Referendumvorlagen, UUID> {

    String CACHE = "WLSREFERENDUMVORSCHLAEGE_CACHE";

    @Override
    List<Referendumvorlagen> findAll();

    @Override
    @Cacheable(value = CACHE, key = "#p0")
    Optional<Referendumvorlagen> findById(UUID referendumvorlagenId);

    Optional<Referendumvorlagen> findByBezirkUndWahlID(BezirkUndWahlID bezirkUndWahlID);

    @Override
    @CachePut(value = CACHE, key = "#p0.bezirkUndWahlID")
    @PreAuthorize("hasAuthority('Basisdaten_WRITE_Referendumvorlagen')")
    <S extends Referendumvorlagen> S save(S entity);

    @Override
    @CacheEvict(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Referendumvorlagen')")
    void deleteById(UUID referendumvorlagenId);

    @Override
    @CacheEvict(value = CACHE, key = "#p0.bezirkUndWahlID")
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Referendumvorlagen')")
    void delete(Referendumvorlagen entity);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Referendumvorlagen')")
    void deleteAll(Iterable<? extends Referendumvorlagen> entities);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Referendumvorlagen')")
    void deleteAll();

    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Referendumvorlagen')")
    void deleteAllByBezirkUndWahlID_WahlID(String wahlID);
}
