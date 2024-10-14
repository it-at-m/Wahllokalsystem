package de.muenchen.oss.wahllokalsystem.authservice.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface AuthorityRepository extends CrudRepository<Authority, UUID> {

    Optional<Authority> findByAuthority(String authority);

}
