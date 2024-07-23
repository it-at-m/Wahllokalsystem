package de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.kopfdaten;

import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.security.access.prepost.PreAuthorize;

@NoRepositoryBean
public interface KopfdatenRepository extends CrudRepository<Kopfdaten, BezirkUndWahlID> {

    String CACHE = "KOPFDATEN_CACHE";

    @Override
    List<Kopfdaten> findAll();

    @Override
    @Cacheable(value = CACHE, key = "#p0")
    Optional<Kopfdaten> findById(BezirkUndWahlID bezirkUndWahlID);

    @Override
    @CachePut(value = CACHE, key = "#p0.bezirkUndWahlID")
    @PreAuthorize("hasAuthority('Basisdaten_WRITE_Kopfdaten')")
    <S extends Kopfdaten> S save(S kopfdaten);

    @Override
    @CacheEvict(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Kopfdaten')")
    void deleteById(BezirkUndWahlID bezirkUndWahlID);

    @Override
    @CacheEvict(value = CACHE, key = "#p0.bezirkUndWahlID")
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Kopfdaten')")
    void delete(Kopfdaten entity);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Kopfdaten')")
    void deleteAll(Iterable<? extends Kopfdaten> entities);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Basisdaten_DELETE_Kopfdaten')")
    void deleteAll();
}
