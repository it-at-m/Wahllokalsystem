package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung;

import org.springframework.data.repository.CrudRepository;

public interface StimmzettelRepository extends CrudRepository<Stimmzettel, StimmzettelCombinedID> {
}