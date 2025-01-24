package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.BezirkUndWahlIDStapelart;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasAuthority('Ergebnismeldung_READ_Ergebnisse')")
public interface ErgebnisseRepository extends CrudRepository<Ergebnisse, BezirkUndWahlIDStapelart> {

    String CACHE = "ErgebnisseCACHE";

    @Override
    @Cacheable(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('Ergebnismeldung_READ_Ergebnisse')")
    Optional<Ergebnisse> findById(BezirkUndWahlIDStapelart bezirkUndWahlIDStapelart);

    @Override
    @CachePut(value = CACHE, key = "#p0.bezirkUndWahlIDStapelart")
    @PreAuthorize("hasAuthority('Ergebnismeldung_WRITE_Ergebnisse')")
    <S extends Ergebnisse> S save(S ergebnisse);

    @Override
    @CacheEvict(value = CACHE, key = "#p0.bezirkUndWahlIDStapelart")
    @PreAuthorize("hasAuthority('Ergebnismeldung_DELETE_Ergebnisse')")
    void delete(Ergebnisse entity);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Ergebnismeldung_DELETE_Ergebnisse')")
    void deleteAll();

    @Query("SELECT e FROM Ergebnisse e WHERE e.bezirkUndWahlIDStapelart.wahlbezirkID = (:wahlbezirkID) AND e.bezirkUndWahlIDStapelart.wahlID = (:wahlID)")
    List<Ergebnisse> findByWahlbezirkIDAndWahlD(@Param("wahlbezirkID") String wahlbezirkID, @Param("wahlID") String wahlID);
}
