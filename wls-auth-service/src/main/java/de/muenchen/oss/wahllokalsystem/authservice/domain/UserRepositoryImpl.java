/**
 *
 */
package de.muenchen.oss.wahllokalsystem.authservice.domain;

import de.muenchen.oss.wahllokalsystem.authservice.service.CryptoService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private static final int FIVE_MINUTES_IN_MILLIS = 5 * 60 * 1000; //TODO Issue: um dies Konfigurierbar zu machen

    private final CryptoService cryptoService;
    private final CrudUserRepository userRepository;

    @PostConstruct
    @Transactional
    public void onInit() {
        log.debug("POST-CONSTRUCT: Encrypting any unencrypted existing user names...");
        encryptUsernames();
        log.debug("POST-CONSTRUCT: Done encrypting any unencrypted existing user names.");
    }

    @Scheduled(fixedRate = FIVE_MINUTES_IN_MILLIS)
    public void onSchedule() {
        log.info("SCHEDULED: Encrypting any newly added unencrypted user names...");
        encryptUsernames();
        log.info("SCHEDULED: Done encrypting any newly added unencrypted user names.");
    }

    public Collection<User> findByWahltagID(final String wahltagID) {
        Collection<User> findBy = userRepository.findByWahltagID(wahltagID);
        return decrypt(findBy);
    }

    public Optional<User> findById(final UUID oid) {
        val findOne = userRepository.findById(oid);
        return findOne.map(this::decrypt);
    }

    public Iterable<User> saveAll(final Iterable<User> users) {
        users.forEach(this::encrypt);

        val savedUsers = userRepository.saveAll(users);

        savedUsers.forEach(this::decrypt);
        return savedUsers;
    }

    public User save(final User user) {
        val encryptedUser = encrypt(user);
        val saveUser = userRepository.save(encryptedUser);
        return decrypt(saveUser);
    }

    public Optional<User> findByUsername(final String username) {
        String encrypted = cryptoService.encrypt(username);
        return userRepository.findByUsername(encrypted).map(this::decrypt);
    }

    public boolean exists(final String username) {
        val encryptedUsername = cryptoService.encrypt(username);
        return userRepository.existsByUsername(encryptedUsername);
    }

    public boolean isLocked(final String username) {
        val encryptedUsername = cryptoService.encrypt(username);
        return userRepository.countUsersLockedByUsername(encryptedUsername) > 0;
    }

    public void deleteUsersByWahltagID(final String wahltagid) {
        userRepository.deleteUsersByWahltagID(wahltagid);
    }

    private void encryptUsernames() {
        val newEncryptedUsers = userRepository.findAll().stream()
                .filter(user -> !cryptoService.isEncrypted(user.getUsername()))
                .peek(this::encrypt)
                .toList();

        if (!newEncryptedUsers.isEmpty()) {
            userRepository.saveAll(newEncryptedUsers);
        }
    }

    private User encrypt(User user) {
        if (user == null) return null;

        val userNameToEncrypt = user.getUsername();
        val encryptedUsername = cryptoService.encrypt(userNameToEncrypt);
        user.setUsername(encryptedUsername);
        log.debug("encrypting: <{}> --> <{}>", userNameToEncrypt, encryptedUsername);

        return user;
    }

    private User decrypt(User user) {
        val username = user.getUsername();
        log.debug("decrypting user <{}>...", username);
        if (username != null) {
            user.setUsername(cryptoService.decrypt(username));
        }
        return user;
    }

    private List<User> decrypt(final Collection<User> users) {
        return users.stream().map(this::decrypt).toList();
    }
}

interface CrudUserRepository extends CrudRepository<User, UUID> {

    @Override
    Collection<User> findAll();

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query("select count(u) from User u where u.username = :username and u.accountNonLocked = false")
    long countUsersLockedByUsername(String username);

    void deleteUsersByWahltagID(String wahltagID);

    Collection<User> findByWahltagID(String wahltagID);
}
