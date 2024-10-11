package de.muenchen.oss.wahllokalsystem.authservice.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface LoginAttemptRepository extends CrudRepository<LoginAttempt, UUID> {

    Optional<LoginAttempt> findFirstByUsername(String username);

    @Override
    Iterable<LoginAttempt> findAll();

    @Override
    Optional<LoginAttempt> findById(UUID oid);

    @SuppressWarnings("unchecked")
    @Override
    LoginAttempt save(LoginAttempt loginAttempts);

    @Override
    void deleteById(UUID oid);

    @Override
    void delete(LoginAttempt entity);

    @Override
    void deleteAll(Iterable<? extends LoginAttempt> entities);

    @Override
    void deleteAll();

}
