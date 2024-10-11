package de.muenchen.oss.wahllokalsystem.authservice.domain;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, UUID> {

    @Override
    Collection<User> findAll();

    @Override
    Optional<User> findById(UUID oid);

    @Override
    User save(User User);

    @Override
    void deleteById(UUID oid);

    @Override
    void delete(User entity);

    @Override
    void deleteAll(Iterable<? extends User> entities);

    @Override
    void deleteAll();

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    Optional<User> findFirstByUsername(String username);

    long countUsersByUsername(String username);

    @Query("select count(u) from User u where u.username = :username and u.accountNonLocked = false")
    long countUsersLockedByUsername(String username);

    void deleteUsersByWahltagID(String wahltagID);

    Collection<User> findByWahltagID(String wahltagID);
}
