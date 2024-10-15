package de.muenchen.oss.wahllokalsystem.authservice.service;

import de.muenchen.oss.wahllokalsystem.authservice.domain.LoginAttempt;
import de.muenchen.oss.wahllokalsystem.authservice.domain.LoginAttemptRepository;
import de.muenchen.oss.wahllokalsystem.authservice.domain.User;
import de.muenchen.oss.wahllokalsystem.authservice.domain.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    LoginAttemptRepository loginAttemptRepository;

    @Mock
    LoginAttemptModelMapper loginAttemptModelMapper;

    @InjectMocks
    UserService unitUnderTest;

    @Captor
    ArgumentCaptor<LoginAttempt> loginAttemptCaptor;

    @Captor
    ArgumentCaptor<User> userCaptor;

    @Nested
    class updateFailAttempts {

        @Test
        void should_throwExceptionWithUsername_when_userWithUsernameDoesNotExist() {
            val username = "username";

            Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

            Assertions.assertThatThrownBy(() -> unitUnderTest.updateFailAttempts(username)).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(username);
        }

        @Test
        void should_createNewLoginAttemptInRepo_when_userWithUsernameExistsButHasNoLoginAttempt() {
            val username = "username";

            val mockedUserFromRepo = new User();

            Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockedUserFromRepo));
            Mockito.when(loginAttemptRepository.findByUsername(username)).thenReturn(Optional.empty());

            unitUnderTest.updateFailAttempts(username);

            Mockito.verify(loginAttemptRepository).save(loginAttemptCaptor.capture());

            val expectedLoginAttempt = new LoginAttempt(username, 1, null);
            Assertions.assertThat(loginAttemptCaptor.getValue()).isEqualTo(expectedLoginAttempt);

        }

        @Test
        void should_updateExistingLoginAttemptInRepo_when_userWithUsernameExistsWithLoginAttempt() {
            val username = "username";

            val mockedUserFromRepo = new User();
            val mockedLoginAttemptFromRepo = new LoginAttempt(username, 1, null);

            Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockedUserFromRepo));
            Mockito.when(loginAttemptRepository.findByUsername(username)).thenReturn(Optional.of(mockedLoginAttemptFromRepo));

            unitUnderTest.updateFailAttempts(username);

            Mockito.verify(loginAttemptRepository).save(loginAttemptCaptor.capture());

            val expectedLoginAttempt = new LoginAttempt(username, 2, null);
            Assertions.assertThat(loginAttemptCaptor.getValue()).usingRecursiveComparison().ignoringFields("lastModified").isEqualTo(expectedLoginAttempt);
            Assertions.assertThat(loginAttemptCaptor.getValue().getLastModified()).isNotNull();
        }

        @Test
        void should_updateUserAsLocked_when_existingLoginAttemptsReachesMaxLoginAttempts() {
            val username = "username";
            unitUnderTest.setMaxLoginAttempts(3);

            val mockedUserFromRepo = new User();
            val mockedLoginAttemptFromRepo = new LoginAttempt();
            mockedLoginAttemptFromRepo.setAttempts(2);

            Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockedUserFromRepo));
            Mockito.when(loginAttemptRepository.findByUsername(username)).thenReturn(Optional.of(mockedLoginAttemptFromRepo));

            unitUnderTest.updateFailAttempts(username);

            Mockito.verify(userRepository).save(userCaptor.capture());

            Assertions.assertThat(userCaptor.getValue().isAccountNonLocked()).isFalse();
        }

        @Test
        void should_notUpdateUserAsLocked_when_existingLoginAttemptsNotReachesMaxLoginAttempts() {
            val username = "username";
            unitUnderTest.setMaxLoginAttempts(4);

            val mockedUserFromRepo = new User();
            val mockedLoginAttemptFromRepo = new LoginAttempt();
            mockedLoginAttemptFromRepo.setAttempts(2);

            Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockedUserFromRepo));
            Mockito.when(loginAttemptRepository.findByUsername(username)).thenReturn(Optional.empty());

            unitUnderTest.updateFailAttempts(username);

            Mockito.verifyNoMoreInteractions(userRepository);
        }

    }

    @Nested
    class resetFailAttempts {

        @Test
        void should_throwExceptionWithUsername_when_userWithUsernameDoesNotExist() {
            val username = "username";

            Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

            Assertions.assertThatThrownBy(() -> unitUnderTest.resetFailAttempts(username)).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(username);
        }

        @Test
        void should_updateExistingLoginAttemptInRepo_when_userWithUsernameExistsWithLoginAttempt() {
            val username = "username";

            val mockedUserFromRepo = createUserWithNonLocked(false);
            val mockedLoginAttemptFromRepo = new LoginAttempt();
            mockedLoginAttemptFromRepo.setLastModified(LocalDateTime.now());
            mockedLoginAttemptFromRepo.setAttempts(10);

            Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockedUserFromRepo));
            Mockito.when(loginAttemptRepository.findByUsername(username)).thenReturn(Optional.of(mockedLoginAttemptFromRepo));

            unitUnderTest.resetFailAttempts(username);

            Mockito.verify(loginAttemptRepository).save(loginAttemptCaptor.capture());
            Assertions.assertThat(loginAttemptCaptor.getValue().getLastModified()).isNull();
            Assertions.assertThat(loginAttemptCaptor.getValue().getAttempts()).isEqualTo(0);
        }

        @Test
        void should_unlockUser_when_userWithUsernameDoesExist() {
            val username = "username";

            val mockedUserFromRepo = createUserWithNonLocked(false);
            val mockedLoginAttemptFromRepo = new LoginAttempt();

            Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockedUserFromRepo));
            Mockito.when(loginAttemptRepository.findByUsername(username)).thenReturn(Optional.of(mockedLoginAttemptFromRepo));

            unitUnderTest.resetFailAttempts(username);

            Mockito.verify(userRepository).save(userCaptor.capture());
            Assertions.assertThat(userCaptor.getValue().isAccountNonLocked()).isTrue();
        }
    }

    @Nested
    class getUserAttempts {

        @Test
        void should_returnLoginAttemptModel_when_userAndAttemptExistsForThatUsername() {
            val username = "username";

            val mockedLoginAttemptFromRepo = new LoginAttempt();
            val mockedLoginAttemptAsModel = createLoginAttemptModel();

            Mockito.when(userRepository.exists(username)).thenReturn(true);
            Mockito.when(loginAttemptRepository.findByUsername(username)).thenReturn(Optional.of(mockedLoginAttemptFromRepo));
            Mockito.when(loginAttemptModelMapper.toModel(mockedLoginAttemptFromRepo)).thenReturn(mockedLoginAttemptAsModel);

            val result = unitUnderTest.getUserAttempts(username);

            Assertions.assertThat(result.get()).isEqualTo(mockedLoginAttemptAsModel);
        }

        @Test
        void should_throwExceptionWithUsername_when_userWithThatUsernameDoesNotExist() {
            val username = "username";

            Mockito.when(userRepository.exists(username)).thenReturn(false);

            Assertions.assertThatThrownBy(() -> unitUnderTest.getUserAttempts(username)).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(username);
        }

        @Test
        void should_returnEmptyOptional_when_userUserExistsButHasNoLoginAttempt() {
            val username = "username";

            Mockito.when(userRepository.exists(username)).thenReturn(true);
            Mockito.when(loginAttemptRepository.findByUsername(username)).thenReturn(Optional.empty());

            val result = unitUnderTest.getUserAttempts(username);

            Assertions.assertThat(result).isEmpty();
        }

    }

    @Nested
    class doesUserExist {

        @Test
        void should_returnTrue_when_userExists() {
            val username = "username";

            Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User()));

            Assertions.assertThat(unitUnderTest.doesUserExist(username)).isTrue();
        }

        @Test
        void should_returnFalse_when_userDoesNotExist() {
            val username = "username";

            Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

            Assertions.assertThat(unitUnderTest.doesUserExist(username)).isFalse();
        }
    }

    @Nested
    class IsLocked {

        @Test
        void should_returnTrue_when_userNonLockedIsFalse() {
            val username = "username";

            val mockedRepoUser = createUserWithNonLocked(false);
            Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockedRepoUser));

            val result = unitUnderTest.isLocked(username);

            Assertions.assertThat(result).isTrue();
        }

        @Test
        void should_returnFalse_when_userDoesNotExist() {
            val username = "username";

            Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

            val result = unitUnderTest.isLocked(username);

            Assertions.assertThat(result).isFalse();
        }

        @Test
        void should_returnFalse_when_userNonLockedIsTrue() {
            val username = "username";

            val mockedRepoUser = createUserWithNonLocked(true);
            Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockedRepoUser));

            val result = unitUnderTest.isLocked(username);

            Assertions.assertThat(result).isFalse();
        }
    }

    private User createUserWithNonLocked(final boolean nonLocked) {
        val user = new User();

        user.setAccountNonLocked(nonLocked);

        return user;
    }

    private LoginAttemptModel createLoginAttemptModel() {
        return new LoginAttemptModel(UUID.randomUUID(), "", 0, LocalDateTime.now());
    }

}
