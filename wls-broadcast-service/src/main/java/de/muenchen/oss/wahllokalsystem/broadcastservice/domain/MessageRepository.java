package de.muenchen.oss.wahllokalsystem.broadcastservice.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends CrudRepository<Message, UUID> {

    String CACHE = "MESSAGE_CACHE";

    @Override
    @CachePut(value = CACHE, key = "#p0.oid")
    @PreAuthorize("hasAuthority('Broadcast_WRITE_Message')")
    <S extends Message> S save(S message);

    @Override
    @PreAuthorize("hasAuthority('Broadcast_WRITE_Message')")
    <S extends Message> Iterable<S> saveAll(Iterable<S> messages);

    @Override
    @CacheEvict(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('Broadcast_DELETE_Message')")
    void deleteById(UUID oid);

    @Override
    @CacheEvict(value = CACHE, key = "#p0")
    @PreAuthorize("hasAuthority('Broadcast_DELETE_Message')")
    void deleteAllById(Iterable<? extends UUID> ids);

    @Override
    @CacheEvict(value = CACHE, key = "#p0.oid")
    @PreAuthorize("hasAuthority('Broadcast_DELETE_Message')")
    void delete(Message entity);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Broadcast_DELETE_Message')")
    void deleteAll(Iterable<? extends Message> messages);

    @Override
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize("hasAuthority('Broadcast_DELETE_Message')")
    void deleteAll();

    Optional<Message> findFirstByWahlbezirkIDOrderByEmpfangsZeit(String wahlbezirkID);
}
