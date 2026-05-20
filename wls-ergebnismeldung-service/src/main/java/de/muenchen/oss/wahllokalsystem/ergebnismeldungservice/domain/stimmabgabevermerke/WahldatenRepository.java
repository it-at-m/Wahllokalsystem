package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.NaturalIdRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasAuthority('Ergebnismeldung_READ_Stimmabgabevermerke')")
public interface WahldatenRepository
    extends NaturalIdRepository<Wahldaten, UUID, BezirkUndWahlIDUndWaehlerverzeichnisnummer> {

  String CACHE = "STIMMABGABEVERMERKE_CACHE";

  @Override
  Optional<Wahldaten> findById(UUID id);

  @Override
  @PreAuthorize("hasAuthority('Ergebnismeldung_WRITE_Stimmabgabevermerke')")
  <S extends Wahldaten> Iterable<S> saveAll(Iterable<S> entities);

  @Override
  @CacheEvict(value = CACHE, key = "#bezirkIDUndWaehlerverzeichnisNummer")
  @PreAuthorize("hasAuthority('Ergebnismeldung_DELETE_Stimmabgabevermerke')")
  void deleteById(UUID id);

  @Override
  @CacheEvict(value = CACHE, key = "#p0.bezirkIDUndWaehlerverzeichnisNummer")
  @PreAuthorize("hasAuthority('Ergebnismeldung_DELETE_Stimmabgabevermerke')")
  void delete(Wahldaten entity);

  @Override
  @CacheEvict(value = CACHE, allEntries = true)
  @PreAuthorize("hasAuthority('Ergebnismeldung_DELETE_Stimmabgabevermerke')")
  void deleteAll(Iterable<? extends Wahldaten> entities);

  @Override
  @CacheEvict(value = CACHE, allEntries = true)
  @PreAuthorize("hasAuthority('Ergebnismeldung_DELETE_Stimmabgabevermerke')")
  void deleteAll();

  @CachePut(value = CACHE, key = "#p0.id")
  @PreAuthorize("hasAuthority('Ergebnismeldung_WRITE_Stimmabgabevermerke')")
  @NonNull <S extends Wahldaten> S save(@NonNull final S wahldaten);
}
