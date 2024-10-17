package de.muenchen.oss.wahllokalsystem.authservice.domain;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    Optional<User> findByUsername(final String username);

    Collection<User> findByWahltagID(final String wahltagID);

    Optional<User> findById(final UUID oid);

    boolean exists(final String username);

    User save(final User user);

    Iterable<User> saveAll(final Iterable<User> users);

    void deleteUsersByWahltagID(final String wahltagid);
}
