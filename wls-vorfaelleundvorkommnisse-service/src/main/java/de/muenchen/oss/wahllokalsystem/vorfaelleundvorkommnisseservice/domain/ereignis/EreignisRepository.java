package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis;

import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Provides a Repository for a {@link Ereignis}. This Repository can be exported as a REST Resource.
 * <p>
 * The Repository handles CRUD Operations. Every Operation is secured and takes care of the tenancy.
 * For specific Documentation on how the generated REST point behaves, please consider the Spring
 * Data Rest Reference
 * <a href="http://docs.spring.io/spring-data/rest/docs/current/reference/html/">here</a>.
 * </p>
 */
@PreAuthorize("hasAuthority('VorfaelleUndVorkommnisse_READ_Ereignisse')")
public interface EreignisRepository extends CrudRepository<Ereignis, String> {
    /**
     * Name for the specific cache.
     */
    String CACHE = "EreignisCACHE";
    static final String WRITE_EREIGNIS = "hasAuthority('VorfaelleUndVorkommnisse_WRITE_Ereignisse')";
    static final String DELETE_EREIGNIS = "hasAuthority('VorfaelleUndVorkommnisse_DELETE_Ereignisse')";

    /**
     * Get all the Ereignis entities.
     *
     * @return an Iterable of the Ereignis entities with the same Tenancy.
     */
    @Override
    Iterable<Ereignis> findAll();

    /**
     * Get one specific Ereignis by its unique oid.
     *
     * @param wahlbezirkID The identifier of the Ereignis.
     * @return The Ereignis with the requested oid.
     */
    @Override
    @Cacheable(value = CACHE, key = "#p0")
    Optional<Ereignis> findById(String wahlbezirkID);

    /**
     * Create or update a Ereignis.
     * <p>
     * If the oid already exists, the Ereignis will be overridden, hence update.
     * If the oid does no already exist, a new Ereignis will be created, hence create.
     * </p>
     *
     * @param ereignis The Ereignis that will be saved.
     * @return the saved Ereignis.
     */
    @Override
    @CachePut(value = CACHE, key = "#p0.wahlbezirkID")
    @PreAuthorize(WRITE_EREIGNIS)
    <S extends Ereignis> S save(S ereignis);

    /**
     * Delete the Ereignis by a specified oid.
     *
     * @param wahlbezirkID the unique oid of the Ereignis that will be deleted.
     */
    @Override
    @CacheEvict(value = CACHE, key = "#p0")
    @PreAuthorize(DELETE_EREIGNIS)
    void deleteById(String wahlbezirkID);

    /**
     * Delete a Ereignis by entity.
     *
     * @param entity The Ereignis that will be deleted.
     */
    @Override
    @CacheEvict(value = CACHE, key = "#p0.wahlbezirkID")
    @PreAuthorize(DELETE_EREIGNIS)
    void delete(Ereignis entity);

    /**
     * Delete multiple Ereignis entities by their oid.
     *
     * @param entities The Iterable of Ereignis entities that will be deleted.
     */
    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize(DELETE_EREIGNIS)
    void deleteAll(Iterable<? extends Ereignis> entities);

    /**
     * Delete all Ereignis entities.
     */
    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize(DELETE_EREIGNIS)
    void deleteAll();
}
