package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis;

import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Provides a Repository for a {@link Ereignis}. This Repository can be exported as a REST Resource.
 * <p>
 * The Repository handles CRUD Operations. Every Operation is secured and takes care of the tenancy.
 * For specific Documentation on how the generated REST point behaves, please consider the Spring Data Rest Reference
 * <a href="http://docs.spring.io/spring-data/rest/docs/current/reference/html/">here</a>.
 * </p>
 */
// todo: kommentare löschen
@NoRepositoryBean   // todo: was macht das?
@PreAuthorize("hasAuthority('VorfaelleUndVorkommnisse_READ_Ereignisse')")
public interface EreignisRepository extends CrudRepository<Ereignis, String> {
    /**
     * Name for the specific cache.
     */
    String CACHE = "EreignisCACHE";

    /**
     * Get all the Ereignis entities.
     *
     * @return an Iterable of the Ereignis entities with the same Tenancy.
     */
    @Override
    List<Ereignis> findAll();   // todo: oder Iterable?

    /**
     * Get one specific Ereignis by its unique oid.
     *
     * @param wahlbezirkID The identifier of the Ereignis.
     * @return The Ereignis with the requested oid.
     */
    @Override
    @Cacheable(value = CACHE, key = "#p0") // #p0 bedeutet, dass der parameter durch das erste argument der methode (also `String key`) ersetzt wird ?
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
    @CachePut(value = CACHE, key = "#p0.wahlbezirkID")  // #p0 wird ersetzt durch das übergebene Argument `ereignis` und wahlbezirkID ist eine variable eines ereignisses die als key verwendet werden kann
    @PreAuthorize("hasAuthority('VorfaelleUndVorkommnisse_WRITE_Ereignis')")  // todo: warum kann man die nicht als konstanten übergeben?
    <S extends Ereignis> S save(S ereignis); // Die generische Schreibweise <S extends Ereignis> bedeutet, dass S ein beliebiges Subtyp von Ereignis sein kann.

    /**
     * Delete the Ereignis by a specified oid.
     *
     * @param wahlbezirkID the unique oid of the Ereignis that will be deleted.
     */
    @Override
    @CacheEvict(value = CACHE, key = "#p0") // cacheEvict bedeutet, dass ein eintrag wieder aus dem cache gelöscht wird
    @PreAuthorize("hasAuthority('VorfaelleUndVorkommnisse_DELETE_Ereignis')")
    void deleteById(String wahlbezirkID);

    /**
     * Delete a Ereignis by entity.
     *
     * @param entity The Ereignis that will be deleted.
     */
    @Override
    @CacheEvict(value = CACHE, key = "#p0.wahlbezirkID")
    @PreAuthorize("hasAuthority('VorfaelleUndVorkommnisse_DELETE_Ereignis')")
    void delete(Ereignis entity);

    /**
     * Delete multiple Ereignis entities by their oid.
     *
     * @param entities The Iterable of Ereignis entities that will be deleted.
     */
    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('VorfaelleUndVorkommnisse_DELETE_Ereignis')")
    void deleteAll(Iterable<? extends Ereignis> entities);

    /**
     * Delete all Ereignis entities.
     */
    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('VorfaelleUndVorkommnisse_DELETE_Ereignis')")
    void deleteAll();
}
