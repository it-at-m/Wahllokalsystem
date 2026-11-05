package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlvorschlag;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

@PreAuthorize("hasAuthority('Basisdaten_READ_WLSWahlvorschlaege')")
@Transactional
public interface WahlvorschlaegeRepository extends CrudRepository<Wahlvorschlaege, UUID> {

  String CACHE = "WLSWAHLVORSCHLAEGE_CACHE";

  @Override
  List<Wahlvorschlaege> findAll();

  @Override
  @Cacheable(value = CACHE, key = "#p0")
  Optional<Wahlvorschlaege> findById(UUID bezirkUndWahlID);

  @EntityGraph(attributePaths = {"wahlvorschlaege", "wahlvorschlaege.kandidaten"})
  Optional<Wahlvorschlaege> findByBezirkUndWahlID(BezirkUndWahlID bezirkUndWahlID);

  @Override
  @CachePut(value = CACHE, key = "#p0.bezirkUndWahlID")
  @PreAuthorize("hasAuthority('Basisdaten_WRITE_WLSWahlvorschlaege')")
  <S extends Wahlvorschlaege> S save(S wahlvorschlaege);

  @Override
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("DELETE FROM Wahlvorschlaege w WHERE w.bezirkUndWahlID = :bezirkUndWahlID")
  @CacheEvict(value = CACHE, key = "#p0")
  @PreAuthorize("hasAuthority('Basisdaten_DELETE_WLSWahlvorschlaege')")
  void deleteById(@Param("bezirkUndWahlID") UUID bezirkUndWahlID);

  @Override
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("DELETE FROM Wahlvorschlaege w WHERE w.id = :#{#entity.id}")
  @CacheEvict(value = CACHE, key = "#p0.id")
  @PreAuthorize("hasAuthority('Basisdaten_DELETE_WLSWahlvorschlaege')")
  void delete(@Param("entity") Wahlvorschlaege entity);

  @Override
  @CacheEvict(value = CACHE, allEntries = true)
  @PreAuthorize("hasAuthority('Basisdaten_DELETE_WLSWahlvorschlaege')")
  void deleteAll(Iterable<? extends Wahlvorschlaege> entities);

  @Override
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("DELETE FROM Wahlvorschlaege")
  @CacheEvict(value = CACHE, allEntries = true)
  @PreAuthorize("hasAuthority('Basisdaten_DELETE_WLSWahlvorschlaege')")
  void deleteAll();

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("DELETE FROM Wahlvorschlaege w WHERE w.bezirkUndWahlID.wahlID = :wahlID")
  @CacheEvict(value = CACHE, allEntries = true)
  @PreAuthorize("hasAuthority('Basisdaten_DELETE_WLSWahlvorschlaege')")
  void deleteAllByBezirkUndWahlID_WahlID(@Param("wahlID") String wahlID);
}
