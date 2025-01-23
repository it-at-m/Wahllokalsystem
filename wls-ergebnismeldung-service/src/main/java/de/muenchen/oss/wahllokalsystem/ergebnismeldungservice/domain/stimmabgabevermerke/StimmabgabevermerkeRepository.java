package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasAuthority('Ergebnismeldung_READ_Stimmabgabevermerke')")
public interface StimmabgabevermerkeRepository extends CrudRepository<Stimmabgabevermerke, BezirkIDUndWaehlerverzeichnisNummer> {

    String CACHE = "STIMMABGABEVERMERKE_CACHE";

    @Override
    @Cacheable(value = CACHE, key = "#bezirkIDUndWaehlerverzeichnisNummer")
    Optional<Stimmabgabevermerke> findById(BezirkIDUndWaehlerverzeichnisNummer bezirkIDUndWaehlerverzeichnisNummer);

    @Override
    @CachePut(value = CACHE, key = "#p0.bezirkIDUndWaehlerverzeichnisNummer")
    @PreAuthorize("hasAuthority('Ergebnismeldung_WRITE_Stimmabgabevermerke')")
    <S extends Stimmabgabevermerke> S save(S stimmabgabevermerke);

    @Override
    @PreAuthorize("hasAuthority('Ergebnismeldung_WRITE_Stimmabgabevermerke')")
    <S extends Stimmabgabevermerke> Iterable<S> saveAll(Iterable<S> entities);

    @Override
    @CacheEvict(value = CACHE, key = "#bezirkIDUndWaehlerverzeichnisNummer")
    @PreAuthorize("hasAuthority('Ergebnismeldung_DELETE_Stimmabgabevermerke')")
    void deleteById(BezirkIDUndWaehlerverzeichnisNummer bezirkIDUndWaehlerverzeichnisNummer);

    @Override
    @CacheEvict(value = CACHE, key = "#p0.bezirkIDUndWaehlerverzeichnisNummer")
    @PreAuthorize("hasAuthority('Ergebnismeldung_DELETE_Stimmabgabevermerke')")
    void delete(Stimmabgabevermerke entity);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Ergebnismeldung_DELETE_Stimmabgabevermerke')")
    void deleteAll(Iterable<? extends Stimmabgabevermerke> entities);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Ergebnismeldung_DELETE_Stimmabgabevermerke')")
    void deleteAll();
}
