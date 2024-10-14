package de.muenchen.oss.wahllokalsystem.authservice.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface AuthorityRepository extends CrudRepository<Authority, UUID> {

    @Override
    Iterable<Authority> findAll();

    @Override
    Optional<Authority> findById(UUID oid);

    @Override
    Authority save(Authority Authority);

    @Override
    void deleteById(UUID oid);

    @Override
    void delete(Authority entity);

    @Override
    void deleteAll(Iterable<? extends Authority> entities);

    @Override
    void deleteAll();

    Authority findByAuthority(String authority);

}
