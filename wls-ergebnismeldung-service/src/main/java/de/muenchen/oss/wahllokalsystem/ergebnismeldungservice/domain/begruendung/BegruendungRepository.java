package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.begruendung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.BezirkUndWahlIDStapelart;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasAuthority('Ergebnismeldung_READ_Begruendung')")
public interface BegruendungRepository extends CrudRepository<Begruendung, BezirkUndWahlIDStapelart> {

    String CACHE = "BegruendungCACHE";

    @Override
    @NonNull
    Iterable<Begruendung> findAll();

    @Override
    @NonNull
    @CacheEvict(value = CACHE, key = "#p0")
    Optional<Begruendung> findById(@NonNull BezirkUndWahlIDStapelart id);

    @Override
    @CachePut(value = CACHE, key = "#p0.bezirkUndWahlIDStapelart")
    @PreAuthorize("hasAuthority('Ergebnismeldung_WRITE_Begruendung')")
    @NonNull
    <S extends Begruendung> S save(@NonNull S bedruendung);

    @Override
    @CacheEvict(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('Ergebnismeldung_DELETE_Begruendung')")
    void deleteById(@NonNull BezirkUndWahlIDStapelart bezirkUndWahlIDStapelart);

    @Override
    @CacheEvict(value = CACHE, key = "#p0.bezirkUndWahlIDStapelart")
    @PreAuthorize("hasAuthority('Ergebnismeldung_DELETE_Begruendung')")
    void delete(@NonNull Begruendung entity);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Ergebnismeldung_DELETE_Begruendung')")
    void deleteAll(@NonNull Iterable<? extends Begruendung> entities);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Ergebnismeldung_DELETE_Begruendung')")
    void deleteAll();
}
