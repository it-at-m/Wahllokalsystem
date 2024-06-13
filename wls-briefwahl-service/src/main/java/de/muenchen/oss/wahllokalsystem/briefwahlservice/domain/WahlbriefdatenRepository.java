package de.muenchen.oss.wahllokalsystem.briefwahlservice.domain;

import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasAuthority('Briefwahl_READ_Wahlbriefdaten')")
public interface WahlbriefdatenRepository extends CrudRepository<Wahlbriefdaten, String> {

    String CACHE = "WahlbriefdatenCACHE";

    @Override
    Iterable<Wahlbriefdaten> findAll();

    @Override
    @Cacheable(value = CACHE, key = "#p0")
    Optional<Wahlbriefdaten> findById(String wahlbezirkID);

    /**
     * Create or update a Wahlbriefdaten.
     * <p>
     * If the oid already exists, the Wahlbriefdaten will be overridden, hence update. If the oid does
     * no already exist, a new Wahlbriefdaten will be created,
     * hence create.
     * </p>
     *
     * @param wahlbriefdaten The Wahlbriefdaten that will be saved.
     * @return the saved Wahlbriefdaten.
     */
    @Override
    @CachePut(value = CACHE, key = "#p0.wahlbezirkID")
    @PreAuthorize("hasAuthority('Briefwahl_WRITE_Wahlbriefdaten')")
    <S extends Wahlbriefdaten> S save(S wahlbriefdaten);

    /**
     * Delete the Wahlbriefdaten by a specified oid.
     *
     * @param wahlbezirkID the unique oid of the Wahlbriefdaten that will be deleted.
     */
    @Override
    @CacheEvict(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('Briefwahl_DELETE_Wahlbriefdaten')")
    void deleteById(String wahlbezirkID);

    /**
     * Delete a Wahlbriefdaten by entity.
     *
     * @param entity The Wahlbriefdaten that will be deleted.
     */
    @Override
    @CacheEvict(value = CACHE, key = "#p0.wahlbezirkID")
    @PreAuthorize("hasAuthority('Briefwahl_DELETE_Wahlbriefdaten')")
    void delete(Wahlbriefdaten entity);

    /**
     * Delete multiple Wahlbriefdaten entities by their oid.
     *
     * @param entities The Iterable of Wahlbriefdaten entities that will be deleted.
     */
    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Briefwahl_DELETE_Wahlbriefdaten')")
    void deleteAll(Iterable<? extends Wahlbriefdaten> entities);

    /**
     * Delete all Wahlbriefdaten entities.
     */
    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Briefwahl_DELETE_Wahlbriefdaten')")
    void deleteAll();
}
