/**
 *
 */
package de.muenchen.oss.wahllokalsystem.authservice.domain;

import de.muenchen.oss.wahllokalsystem.authservice.service.EncryptionService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class UserRepositoryCryptoFacade {

    private static final int FIVE_MINUTES_IN_MILLIS = 5 * 60 * 1000; //TODO Issue: um dies Konfigurierbar zu machen

    private final EncryptionService encryptionService;
    private final UserRepository userRepository;

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

    public Optional<User> findFirstByUsername(final String username) {
        String encryptedUsername = encryptionService.encrypt(username);
        val findFirstByUsername = userRepository.findFirstByUsername(encryptedUsername);
        return findFirstByUsername.map(this::decrypt);
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
        String encrypted = encryptionService.encrypt(username);
        return userRepository.findByUsername(encrypted).map(this::decrypt);
    }

    public long countUsersByUsername(final String username) {
        String encryptedUsername = encryptionService.encrypt(username);
        return userRepository.countUsersByUsername(encryptedUsername);
    }

    public long countUsersLockedByUsername(final String username) {
        val encrypted = encryptionService.encrypt(username);
        return userRepository.countUsersLockedByUsername(encrypted);
    }

    public void deleteUsersByWahltagID(final String wahltagid) {
        userRepository.deleteUsersByWahltagID(wahltagid);
    }

    private void encryptUsernames() {
        val newEncryptedUsers = userRepository.findAll().stream()
                .filter(user -> !encryptionService.isEncrypted(user.getUsername()))
                .peek(this::encrypt)
                .toList();

        if (!newEncryptedUsers.isEmpty()) {
            userRepository.saveAll(newEncryptedUsers);
        }
    }

    private User encrypt(User user) {
        if (user == null) return null;

        val userNameToEncrypt = user.getUsername();
        val encryptedUsername = encryptionService.encrypt(userNameToEncrypt);
        user.setUsername(encryptedUsername);
        log.debug("encrypting: <{}> --> <{}>", userNameToEncrypt, encryptedUsername);

        return user;
    }

    private User decrypt(User user) {
        val username = user.getUsername();
        log.debug("decrypting user <{}>...", username);
        if (username != null) {
            encryptionService.decrypt(username);
        }
        return user;
    }

    private List<User> decrypt(final Collection<User> users) {
        return users.stream().map(this::decrypt).toList();
    }
}
