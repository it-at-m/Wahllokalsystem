package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasAuthority('Basisdaten_READ_WLSReferendumvorschlaege')")
public interface ReferendumvorlagenRepository extends CrudRepository<Referendumvorlagen, BezirkUndWahlID> {

    String CACHE = "WLSREFERENDUMVORSCHLAEGE_CACHE";

    @Override
    List<Referendumvorlagen> findAll();

    @Override
    @Cacheable(value = CACHE, key = "#p0")
    Optional<Referendumvorlagen> findById(BezirkUndWahlID referendumvorlageId);

    @Override
    @CachePut(value = CACHE, key = "#p0.bezirkUndWahlID")
    @PreAuthorize("hasAuthority('Basisdaten_WRITE_WLSReferendumvorschlaege')")
    <S extends Referendumvorlagen> S save(S wLSReferendumvorschlaege);

    @Override
    @CacheEvict(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_WLSReferendumvorschlaege')")
    void deleteById(BezirkUndWahlID referendumvorlageId);

    @Override
    @CacheEvict(value = CACHE, key = "#p0.bezirkUndWahlID")
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_WLSReferendumvorschlaege')")
    void delete(Referendumvorlagen entity);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_WLSReferendumvorschlaege')")
    void deleteAll(Iterable<? extends Referendumvorlagen> entities);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_WLSReferendumvorschlaege')")
    void deleteAll();
}
