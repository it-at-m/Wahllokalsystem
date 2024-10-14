package de.muenchen.oss.wahllokalsystem.authservice.service;

import de.muenchen.oss.wahllokalsystem.authservice.domain.LoginAttempt;
import de.muenchen.oss.wahllokalsystem.authservice.domain.LoginAttemptRepository;
import de.muenchen.oss.wahllokalsystem.authservice.domain.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final LoginAttemptRepository loginAttemptRepository;

    private final LoginAttemptModelMapper loginAttemptModelMapper;

    @Value("${serviceauth.maxLoginAttempts}")
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
}
