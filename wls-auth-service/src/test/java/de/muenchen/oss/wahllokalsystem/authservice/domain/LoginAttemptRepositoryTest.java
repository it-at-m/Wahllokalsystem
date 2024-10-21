package de.muenchen.oss.wahllokalsystem.authservice.domain;

import de.muenchen.oss.wahllokalsystem.authservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.authservice.TestConstants;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles(TestConstants.SPRING_TEST_PROFILE)
class LoginAttemptRepositoryTest {

    @Autowired
    LoginAttemptRepository loginAttemptRepository;

    @AfterEach
    void tearDown() {
        loginAttemptRepository.deleteAll();
    }

    @Nested
    class FindByUsername {

        @Test
        void should_returnLoginAttempt_when_attempWithUsernameExists() {
            val username = "username";
            val loginAttemptToFind = loginAttemptRepository.save(createLoginAttemptWithUsername(username));

            val findByResult = loginAttemptRepository.findByUsername(username);

            Assertions.assertThat(findByResult.get()).isEqualTo(loginAttemptToFind);
        }

        @Test
        void should_returnEmptyOptional_when_noAttempWithUsernameExists() {
            val findByResult = loginAttemptRepository.findByUsername("username");

            Assertions.assertThat(findByResult).isEmpty();
        }
    }

    private LoginAttempt createLoginAttemptWithUsername(final String username) {
        val loginAttempt = new LoginAttempt();

        loginAttempt.setUsername(username);

        return loginAttempt;
    }
}
