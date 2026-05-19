package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common;

import jakarta.persistence.EntityManager;
import java.io.Serializable;
import java.util.Optional;
import org.hibernate.Session;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

public class NaturalIdRepositoryImpl<T, ID extends Serializable, NaturalID extends Serializable>
    extends SimpleJpaRepository<T, ID> implements NaturalIdRepository<T, ID, NaturalID> {
  private final EntityManager entityManager;

  public NaturalIdRepositoryImpl(
      JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
    super(entityInformation, entityManager);
    this.entityManager = entityManager;
  }

  public Optional<T> findByNaturalId(NaturalID naturalId) {
    return entityManager
        .unwrap(Session.class)
        .bySimpleNaturalId(this.getDomainClass())
        .loadOptional(naturalId);
  }
}
