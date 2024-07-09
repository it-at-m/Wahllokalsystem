package de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface WahltageRepository extends CrudRepository<Wahltag, UUID> {

    List<Wahltag> findByTagAfterOrTagEquals(LocalDate afterDate, LocalDate equalsDate);
}
