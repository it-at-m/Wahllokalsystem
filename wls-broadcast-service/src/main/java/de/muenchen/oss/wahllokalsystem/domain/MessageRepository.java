package de.muenchen.oss.wahllokalsystem.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface MessageRepository extends CrudRepository<Message, UUID> {

    Optional<Message> findFirstByWahlbezirkIDOrderByEmpfangsZeit(String wahlbezirkID);

}
