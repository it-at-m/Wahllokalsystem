package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain;

import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasAuthority('Basisdaten_READ_Handbuch')")
public interface HandbuchRepository extends CrudRepository<Handbuch, WahltagIdUndWahlbezirksart> {

    String CACHE = "HANDBUCH_CACHE";

    @Override
    Iterable<Handbuch> findAll();

    @Override
    @Cacheable(value = CACHE, key = "#p0")
    Optional<Handbuch> findById(WahltagIdUndWahlbezirksart wahltagIdUndWahlbezirksart);

    @Override
    @CachePut(value = CACHE, key = "#p0.wahltagIdUndWahlbezirksart")
    @PreAuthorize("hasAuthority('Basisdaten_WRITE_Handbuch')")
    <S extends Handbuch> S save(S handbuch);

    @Override
    @CacheEvict(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Handbuch')")
    void deleteById(WahltagIdUndWahlbezirksart wahltagIdUndWahlbezirksart);

    @Override
    @CacheEvict(value = CACHE, key = "#p0.wahltagIdUndWahlbezirksart")
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Handbuch')")
    void delete(Handbuch entity);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Handbuch')")
    void deleteAll(Iterable<? extends Handbuch> entities);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Handbuch')")
    void deleteAll();
}
