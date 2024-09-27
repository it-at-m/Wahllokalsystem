package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.ungueltigewahlscheine;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.common.WahltagIdUndWahlbezirksart;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasAuthority('Basisdaten_READ_Ungueltigews')")
public interface UngueltigeWahlscheineRepository extends CrudRepository<UngueltigeWahlscheine, WahltagIdUndWahlbezirksart> {

    String CACHE = "UNGUELTIGEWS_CACHE";

    @Override
    Iterable<UngueltigeWahlscheine> findAll();

    @Override
    @Cacheable(value = CACHE, key = "#p0")
    Optional<UngueltigeWahlscheine> findById(WahltagIdUndWahlbezirksart wahltagIdUndWahlbezirksart);

    @Override
    @CachePut(value = CACHE, key = "#p0.wahltagIdUndWahlbezirksart")
    @PreAuthorize("hasAuthority('Basisdaten_WRITE_Ungueltigews')")
    <S extends UngueltigeWahlscheine> S save(S ungueltigews);

    @Override
    @CacheEvict(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Ungueltigews')")
    void deleteById(WahltagIdUndWahlbezirksart wahltagIdUndWahlbezirksart);

    @Override
    @CacheEvict(value = CACHE, key = "#p0.wahltagIdUndWahlbezirksart")
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Ungueltigews')")
    void delete(UngueltigeWahlscheine entity);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Ungueltigews')")
    void deleteAll(Iterable<? extends UngueltigeWahlscheine> entities);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Ungueltigews')")
    void deleteAll();
}
