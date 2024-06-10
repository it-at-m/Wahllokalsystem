/*
 * Copyright (c): it@M - Dienstleister für Informations- und Telekommunikationstechnik
 * der Landeshauptstadt München, 2024
 */
package de.muenchen.oss.wahllokalsystem.basisdaten.rest;

import de.muenchen.oss.wahllokalsystem.basisdaten.domain.TheEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Provides a Repository for {@link TheEntity}. This Repository is exported as a REST resource.
 * <p>
 * The Repository handles CRUD Operations. Every Operation is secured and takes care of the tenancy. For specific Documentation on how the generated REST point
 * behaves, please consider the Spring Data Rest Reference
 * <a href="http://docs.spring.io/spring-data/rest/docs/current/reference/html/">here</a>.
 * </p>
 */
@PreAuthorize("hasAuthority(T(de.muenchen.oss.wahllokalsystem.eaiservice.security.AuthoritiesEnum).WLS_EAI_SERVICE_READ_THEENTITY.name())")
public interface TheEntityRepository extends CrudRepository<TheEntity, UUID> { //NOSONAR

    /**
     * Name for the specific cache.
     */
    String CACHE = "THEENTITY_CACHE";

    /**
     * Get one specific {@link TheEntity} by its unique id.
     *
     * @param id The identifier of the {@link TheEntity}.
     * @return The {@link TheEntity} with the requested id.
     */
    @Override
    @Cacheable(value = CACHE, key = "#p0")
    Optional<TheEntity> findById(UUID id);

    /**
     * Create or update a {@link TheEntity}.
     * <p>
     * If the id already exists, the {@link TheEntity} will be overridden, hence update. If the id does not already exist, a new {@link TheEntity} will be
     * created, hence create.
     * </p>
     *
     * @param theEntity The {@link TheEntity} that will be saved.
     * @return the saved {@link TheEntity}.
     */
    @Override
    @CachePut(value = CACHE, key = "#p0.id")
    @PreAuthorize("hasAuthority(T(de.muenchen.oss.wahllokalsystem.eaiservice.security.AuthoritiesEnum).WLS_EAI_SERVICE_WRITE_THEENTITY.name())")
    <S extends TheEntity> S save(S theEntity);

    /**
     * Create or update a collection of {@link TheEntity}.
     * <p>
     * If the id already exists, the {@link TheEntity}s will be overridden, hence update. If the id does not already exist, the new {@link TheEntity}s will be
     * created, hence create.
     * </p>
     *
     * @param entities The {@link TheEntity} that will be saved.
     * @return the collection saved {@link TheEntity}.
     */
    @Override
    @PreAuthorize("hasAuthority(T(de.muenchen.oss.wahllokalsystem.eaiservice.security.AuthoritiesEnum).WLS_EAI_SERVICE_WRITE_THEENTITY.name())")
    <S extends TheEntity> Iterable<S> saveAll(Iterable<S> entities);

    /**
     * Delete the {@link TheEntity} by a specified id.
     *
     * @param id the unique id of the {@link TheEntity} that will be deleted.
     */
    @Override
    @CacheEvict(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority(T(de.muenchen.oss.wahllokalsystem.eaiservice.security.AuthoritiesEnum).WLS_EAI_SERVICE_DELETE_THEENTITY.name())")
    void deleteById(UUID id);

    /**
     * Delete a {@link TheEntity} by entity.
     *
     * @param entity The {@link TheEntity} that will be deleted.
     */
    @Override
    @CacheEvict(value = CACHE, key = "#p0.id")
    @PreAuthorize("hasAuthority(T(de.muenchen.oss.wahllokalsystem.eaiservice.security.AuthoritiesEnum).WLS_EAI_SERVICE_DELETE_THEENTITY.name())")
    void delete(TheEntity entity);

    /**
     * Delete multiple {@link TheEntity} entities by their id.
     *
     * @param entities The Iterable of {@link TheEntity} that will be deleted.
     */
    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority(T(de.muenchen.oss.wahllokalsystem.eaiservice.security.AuthoritiesEnum).WLS_EAI_SERVICE_DELETE_THEENTITY.name())")
    void deleteAll(Iterable<? extends TheEntity> entities);

    /**
     * Delete all {@link TheEntity} entities.
     */
    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority(T(de.muenchen.oss.wahllokalsystem.eaiservice.security.AuthoritiesEnum).WLS_EAI_SERVICE_DELETE_THEENTITY.name())")
    void deleteAll();

}
