package de.muenchen.oss.wahllokalsystem.authservice.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface PermissionRepository extends CrudRepository<Permission, UUID> {

    Optional<Permission> findByPermission(String permission);
}
