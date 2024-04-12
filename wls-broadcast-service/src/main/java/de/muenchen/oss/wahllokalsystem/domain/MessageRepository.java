package de.muenchen.oss.wahllokalsystem.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

@PreAuthorize("hasAuthority('Broadcast_READ_Message')")
@Repository
public interface MessageRepository extends CrudRepository<Message, UUID> {

    Optional<Message> findFirstByWahlbezirkIDOrderByEmpfangsZeit(String wahlbezirkID);

}
