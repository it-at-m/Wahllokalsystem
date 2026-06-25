package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.nachlieferungsbezirke;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.common.WahltagIdUndWahlbezirkId;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasAuthority('Basisdaten_READ_Nachlieferungsbezirke')")
public interface NachlieferungsbezirkeRepository
    extends CrudRepository<Nachlieferungsbezirk, WahltagIdUndWahlbezirkId> {

  String CACHE = "NACHLIEFERUNGSBEZIRKE_CACHE";

  List<Nachlieferungsbezirk> findByWahltagIdUndWahlbezirkId_WahltagID(String wahltagID);

  @Override
  Iterable<Nachlieferungsbezirk> findAll();

  @Override
  @Cacheable(value = CACHE, key = "#p0")
  Optional<Nachlieferungsbezirk> findById(WahltagIdUndWahlbezirkId wahltagIdUndWahlbezirkId);

  @Override
  @CachePut(value = CACHE, key = "#p0.wahltagIdUndWahlbezirkId")
  @PreAuthorize("hasAuthority('Basisdaten_WRITE_Nachlieferungsbezirke')")
  <S extends Nachlieferungsbezirk> S save(S nachlieferungsbezirk);

  @Override
  @CacheEvict(value = CACHE, allEntries = true)
  @PreAuthorize("hasAuthority('Basisdaten_WRITE_Nachlieferungsbezirke')")
  <S extends Nachlieferungsbezirk> Iterable<S> saveAll(Iterable<S> entities);

  @Override
  @CacheEvict(value = CACHE, key = "#p0")
  @PreAuthorize("hasAuthority('Basisdaten_DELETE_Nachlieferungsbezirke')")
  void deleteById(WahltagIdUndWahlbezirkId wahltagIdUndWahlbezirkId);

  @Override
  @CacheEvict(value = CACHE, key = "#p0.wahltagIdUndWahlbezirkId")
  @PreAuthorize("hasAuthority('Basisdaten_DELETE_Nachlieferungsbezirke')")
  void delete(Nachlieferungsbezirk entity);

  @Override
  @CacheEvict(value = CACHE, allEntries = true)
  @PreAuthorize("hasAuthority('Basisdaten_DELETE_Nachlieferungsbezirke')")
  void deleteAll(Iterable<? extends Nachlieferungsbezirk> entities);

  @Override
  @CacheEvict(value = CACHE, allEntries = true)
  @PreAuthorize("hasAuthority('Basisdaten_DELETE_Nachlieferungsbezirke')")
  void deleteAll();
}
