package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis;

import java.util.Optional;
import java.util.UUID;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

@PreAuthorize("hasAuthority('VorfaelleUndVorkommnisse_READ_Ereignisse')")
public interface EreignisseRepository extends CrudRepository<Ereignisse, UUID> {

  String CACHE = "EreignisCACHE";

  @Override
  Iterable<Ereignisse> findAll();

  @Cacheable(value = CACHE, key = "#p0")
  Optional<Ereignisse> findByWahlbezirkID(String wahlbezirkID);

  @Override
  @CachePut(value = CACHE, key = "#p0.wahlbezirkID")
  @PreAuthorize("hasAuthority('VorfaelleUndVorkommnisse_WRITE_Ereignisse')")
  <S extends Ereignisse> S save(S ereignisse);

  @Override
  @CachePut(value = CACHE, key = "#p0.wahlbezirkID")
  @PreAuthorize("hasAuthority('VorfaelleUndVorkommnisse_WRITE_Ereignisse')")
  <S extends Ereignisse> Iterable<S> saveAll(Iterable<S> iterable);

  @Override
  @CacheEvict(value = CACHE, key = "#p0")
  @PreAuthorize("hasAuthority('VorfaelleUndVorkommnisse_DELETE_Ereignisse')")
  void deleteById(UUID id);

  @Override
  @CacheEvict(value = CACHE, key = "#p0.wahlbezirkID")
  @PreAuthorize("hasAuthority('VorfaelleUndVorkommnisse_DELETE_Ereignisse')")
  void delete(Ereignisse entity);

  @Override
  @CacheEvict(value = CACHE, allEntries = true)
  @PreAuthorize("hasAuthority('VorfaelleUndVorkommnisse_DELETE_Ereignisse')")
  void deleteAll(Iterable<? extends Ereignisse> entities);

  @Override
  @CacheEvict(value = CACHE, allEntries = true)
  @PreAuthorize("hasAuthority('VorfaelleUndVorkommnisse_DELETE_Ereignisse')")
  void deleteAll();

  @CacheEvict(value = CACHE, allEntries = true)
  @PreAuthorize("hasAuthority('VorfaelleUndVorkommnisse_DELETE_Ereignisse')")
  @Transactional
  void deleteByWahlbezirkID(String wahlbezirkID);
}
