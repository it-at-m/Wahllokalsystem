package de.muenchen.oss.wahllokalsystem.authservice.utils;

import de.muenchen.oss.wahllokalsystem.authservice.domain.User;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface TestCrudUserRepository extends CrudRepository<User, UUID> {}
