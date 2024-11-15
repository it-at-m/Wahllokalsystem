package de.muenchen.oss.wahllokalsystem.authservice.service;

import de.muenchen.oss.wahllokalsystem.authservice.domain.AuthorityRepository;
import de.muenchen.oss.wahllokalsystem.authservice.domain.LoginAttempt;
import de.muenchen.oss.wahllokalsystem.authservice.domain.LoginAttemptRepository;
import de.muenchen.oss.wahllokalsystem.authservice.domain.User;
import de.muenchen.oss.wahllokalsystem.authservice.domain.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.StreamSupport;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    @Value("${service.config.user.csv.eol}")
    String EOL = "\r\n";

    @Value("${service.config.user.authority.wahlvorstand}")
    String wahlvorstandAuthorityName = "WLS_WAHLVORSTAND";

    @Value("${service.config.user.anzahlPinBloecke}")
    int anzahlPinBloecke;

    @Value("${service.config.user.prefixChars}")
    String prefixChars;

    @Value("${service.config.user.countCharsPrefix}")
    int countCharsPrefix;

    @Value("${service.config.user.countNumbersPin}")
    int countNumbersPin;

    private final UserRepository userRepository;

    private final AuthorityRepository authorityRepository;

    private final LoginAttemptRepository loginAttemptRepository;

    private final LoginAttemptModelMapper loginAttemptModelMapper;

    private final UserModelMapper userModelMapper;

    @Value("${service.config.maxLoginAttempts}")
    @Getter
    @Setter
    private int maxLoginAttempts;

    @Transactional
    public void updateFailAttempts(final String username) {
        log.debug("updateFailAttempts({})", username);

        val user = userRepository.findByUsername(username).orElseThrow(() -> {
            log.warn("No user found for given username. Skipping updateFailAttempts.");
            return new IllegalArgumentException("User with username " + username + " not found.");
        });

        val foundUserLoginAttempt = loginAttemptRepository.findByUsername(username);
        if (foundUserLoginAttempt.isEmpty()) {
            val newUserLoginAttempt = new LoginAttempt();
            newUserLoginAttempt.setUsername(username);
            newUserLoginAttempt.setAttempts(1);
            loginAttemptRepository.save(newUserLoginAttempt);
        } else {
            val existingLoginAttempt = foundUserLoginAttempt.get();
            int currentAttempts = existingLoginAttempt.getAttempts();
            existingLoginAttempt.setAttempts(currentAttempts + 1);
            existingLoginAttempt.setLastModified(LocalDateTime.now());
            loginAttemptRepository.save(existingLoginAttempt);
            // Wenn die Grenze überschritten ist den Benutzer sperren
            if (existingLoginAttempt.getAttempts() + 1 > maxLoginAttempts) {
                user.setAccountNonLocked(false);
                userRepository.save(user);
            }
        }
    }

    @Transactional
    public void resetFailAttempts(String username) {
        log.debug("resetFailAttempts({})", username);

        val user = userRepository.findByUsername(username).orElseThrow(() -> {
            log.warn("No user found for given username.");
            return new IllegalArgumentException("User with username " + username + " not found.");
        });

        val foundUserLoginAttempt = loginAttemptRepository.findByUsername(username);
        if (foundUserLoginAttempt.isPresent()) {
            log.debug("Execute reset LoginAttempts for user!");
            user.setAccountNonLocked(true);
            userRepository.save(user);
            val loginAttempEntity = foundUserLoginAttempt.get();
            loginAttempEntity.setAttempts(0);
            loginAttempEntity.setLastModified(null);
            loginAttemptRepository.save(loginAttempEntity);
        }
    }

    public Optional<LoginAttemptModel> getUserAttempts(String username) {
        log.debug("getUserAttempts({})", username);

        if (!userRepository.exists(username)) {
            log.warn("No user found for given username.");
            throw new IllegalArgumentException("User with username " + username + " not found.");
        }

        return loginAttemptRepository.findByUsername(username).map(loginAttemptModelMapper::toModel);
    }

    public boolean doesUserExist(final String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean isLocked(final String username) {
        val user = userRepository.findByUsername(username);
        return user.filter(value -> !value.isAccountNonLocked()).isPresent();
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN_ADMIN')")
    public void deleteWahllokalBenutzer(String wahltagid) {
        userRepository.deleteUsersByWahltagID(wahltagid);
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN_ADMIN')")
    public String exportWahllokalBenutzer(String wahltagID) {
        return usersToCSVString(userRepository.findByWahltagID(wahltagID));
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN_ADMIN')")
    public String generateWahllokalBenutzer(UsersOfWahltagModel usersOfWahltag) {
        val authorityWahlvorstand = authorityRepository.findByAuthority(wahlvorstandAuthorityName).orElseThrow(() -> new HttpServerErrorException(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Keine Authority <" + wahlvorstandAuthorityName + "> gefunden, kann keine Benutzer für Wahltag-ID <" + usersOfWahltag.wahltagID()
                + "> anlegen"));

        deleteWahllokalBenutzer(usersOfWahltag.wahltagID());

        val newUserAuthorities = Set.of(authorityWahlvorstand);
        val newUsers = usersOfWahltag.users().stream()
            .map(user -> userModelMapper.toUser(usersOfWahltag.wahltagID(), user, newUserAuthorities, generatePin(), generateName(user.wahlbezirknummer())))
            .toList();
        val persistedUsers = userRepository.saveAll(newUsers);
        return usersToCSVString(persistedUsers);
    }

    public Optional<UserModel> getUser(String name) {
        val user = userRepository.findByUsername(name);
        return user.map(userModelMapper::toModel);
    }

    private String generatePin() {
        val pinBuilder = new StringBuilder();
        for (int i = 0; i < anzahlPinBloecke; i++) {
            if (i != 0) pinBuilder.append('-');
            pinBuilder.append(RandomStringUtils.secure().nextNumeric(countNumbersPin));
        }
        return pinBuilder.toString();
    }

    private String generateName(final String wahlbezirkNummer) {
        val randomPrefix = RandomStringUtils.secure().next(countCharsPrefix, prefixChars).toLowerCase();
        return randomPrefix + "-" + wahlbezirkNummer;
    }

    private String usersToCSVString(Iterable<User> users) {
        return StreamSupport.stream(users.spliterator(), false)
            .map(User::getUsername)
            .reduce((s, s2) -> s + EOL + s2)
            .orElse("Keine Nutzer zum angegebenen Wahltag gefunden.");
    }
}
