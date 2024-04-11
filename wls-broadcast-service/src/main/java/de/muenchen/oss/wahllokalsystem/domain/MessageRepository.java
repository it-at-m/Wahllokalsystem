package de.muenchen.oss.wahllokalsystem.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasAuthority('Broadcast_READ_Message')")
public interface MessageRepository extends CrudRepository<Message, UUID> {

    Optional<Message> findFirstByWahlbezirkIDOrderByEmpfangsZeit(String wahlbezirkID);

}
