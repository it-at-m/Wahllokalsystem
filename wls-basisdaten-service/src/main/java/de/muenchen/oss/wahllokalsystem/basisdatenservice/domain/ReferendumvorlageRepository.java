package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasAuthority('Basisdaten_READ_Referendumvorlage')")
public interface ReferendumvorlageRepository extends CrudRepository<Referendumvorlage, UUID> {

    String CACHE = "WLSREFERENDUMVORSCHLAEGE_CACHE";

    @Override
    List<Referendumvorlage> findAll();

    @Override
    @Cacheable(value = CACHE, key = "#p0")
    Optional<Referendumvorlage> findById(UUID referendumvorlageId);

    @Override
    @CachePut(value = CACHE, key = "#p0.bezirkUndWahlID")
    @PreAuthorize("hasAuthority('Basisdaten_WRITE_Referendumvorlage')")
    <S extends Referendumvorlage> S save(S wLSReferendumvorschlaege);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Basisdaten_WRITE_Referendumvorlage')")
    <S extends Referendumvorlage> Iterable<S> saveAll(Iterable<S> wLSReferendumvorschlaege);

    @Override
    @CacheEvict(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Referendumvorlage')")
    void deleteById(UUID referendumvorlageId);

    @Override
    @CacheEvict(value = CACHE, key = "#p0.bezirkUndWahlID")
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Referendumvorlage')")
    void delete(Referendumvorlage entity);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Referendumvorlage')")
    void deleteAll(Iterable<? extends Referendumvorlage> entities);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Referendumvorlage')")
    void deleteAll();

}
