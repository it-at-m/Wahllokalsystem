package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common;

import java.io.Serializable;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface NaturalIdRepository<T, ID extends Serializable, NaturalID extends Serializable>
    extends CrudRepository<T, ID> {
  Optional<T> findByNaturalId(NaturalID naturalId);
}
