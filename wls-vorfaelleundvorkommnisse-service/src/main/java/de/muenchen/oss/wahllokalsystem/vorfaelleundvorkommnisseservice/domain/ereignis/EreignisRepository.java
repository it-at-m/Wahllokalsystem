package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis;

import java.util.List;
import java.util.UUID;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

@PreAuthorize("hasAuthority('VorfaelleUndVorkommnisse_READ_Ereignisse')")
public interface EreignisRepository extends CrudRepository<Ereignis, UUID> {

    String CACHE = "EreignisCACHE";

    @Override
    Iterable<Ereignis> findAll();

    @Cacheable(value = CACHE, key = "#p0")
    List<Ereignis> findByWahlbezirkID(String wahlbezirkID);

    @Override
    @CachePut(value = CACHE, key = "#p0.wahlbezirkID")
    @PreAuthorize("hasAuthority('VorfaelleUndVorkommnisse_WRITE_Ereignisse')")
    <S extends Ereignis> S save(S ereignis);

    @Override
    @CachePut(value = CACHE, key = "#p0.wahlbezirkID")
    @PreAuthorize("hasAuthority('VorfaelleUndVorkommnisse_WRITE_Ereignisse')")
    <S extends Ereignis> Iterable<S> saveAll(Iterable<S> iterable);

    @Override
    @CacheEvict(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('VorfaelleUndVorkommnisse_DELETE_Ereignisse')")
    void deleteById(UUID id);

    @Override
    @CacheEvict(value = CACHE, key = "#p0.wahlbezirkID")
    @PreAuthorize("hasAuthority('VorfaelleUndVorkommnisse_DELETE_Ereignisse')")
    void delete(Ereignis entity);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('VorfaelleUndVorkommnisse_DELETE_Ereignisse')")
    void deleteAll(Iterable<? extends Ereignis> entities);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('VorfaelleUndVorkommnisse_DELETE_Ereignisse')")
    void deleteAll();

    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('VorfaelleUndVorkommnisse_DELETE_Ereignisse')")
    @Transactional
    void deleteByWahlbezirkID(String wahlbezirkID);
}
