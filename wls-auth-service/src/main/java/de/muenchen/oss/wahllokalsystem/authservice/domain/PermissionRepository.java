package de.muenchen.oss.wahllokalsystem.authservice.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface PermissionRepository extends CrudRepository<Permission, UUID> {

    @Override
    Iterable<Permission> findAll();

    @Override
    Optional<Permission> findById(UUID oid);

    @SuppressWarnings("unchecked")
    @Override
    Permission save(Permission Permission);

    @Override
    void deleteById(UUID oid);

    @Override
    void delete(Permission entity);

    @Override
    void deleteAll(Iterable<? extends Permission> entities);

    @Override
    void deleteAll();

    Optional<Permission> findByPermission(String permission);
}
