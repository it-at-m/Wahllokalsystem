package de.muenchen.oss.wahllokalsystem.authservice.domain;

import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface AuthorityRepository extends CrudRepository<Authority, UUID> {

    Authority findByAuthority(String authority);

}
