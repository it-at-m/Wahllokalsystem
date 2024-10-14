package de.muenchen.oss.wahllokalsystem.authservice.domain;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

import de.muenchen.oss.wahllokalsystem.authservice.service.EncryptionService;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;
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
class UserRepositoryImplTest {

    @Mock
    EncryptionService encryptionService;

    @Mock
    CrudUserRepository userRepository;

    @InjectMocks
    UserRepositoryImpl unitUnderTest;

    @Captor
    ArgumentCaptor<List<User>> usersCaptor;

    @Nested
    class OnInit {

        @Test
        void should_updateWithNewUser_when_gotUsersFromRepository() {
            val mockedEncryptedUsername = "encryptedUsername";
            val mockedUsersFromRepo = List.of(createUserWithUsername("user1"), createUserWithUsername("user2"));

            Mockito.when(userRepository.findAll()).thenReturn(mockedUsersFromRepo);
            Mockito.when(encryptionService.encrypt("user1")).thenReturn(mockedEncryptedUsername);
            Mockito.when(encryptionService.encrypt("user2")).thenReturn(mockedEncryptedUsername);
            Mockito.when(encryptionService.isEncrypted(any())).thenReturn(false);

            unitUnderTest.onInit();

            Mockito.verify(userRepository).saveAll(usersCaptor.capture());
            Assertions.assertThat(usersCaptor.getAllValues()).hasSize(1);
            Assertions.assertThat(usersCaptor.getValue()).allSatisfy(user -> Assertions.assertThat(user.getUsername()).isEqualTo(mockedEncryptedUsername));
        }

        @Test
        void should_notSaveEmptyList_when_usernameIsAlreadyEncrypted() {
            val mockedUsersFromRepo = List.of(createUserWithUsername("user1"), createUserWithUsername("user2"));

            Mockito.when(userRepository.findAll()).thenReturn(mockedUsersFromRepo);
            Mockito.when(encryptionService.isEncrypted(any())).thenReturn(true);

            unitUnderTest.onInit();

            Mockito.verify(userRepository, times(0)).saveAll(any());
        }

    }

    @Nested
    class OnSchedule {
        @Test
        void should_updateWithNewUser_when_gotUsersFromRepository() {
            val mockedEncryptedUsername = "encryptedUsername";
            val mockedUsersFromRepo = List.of(createUserWithUsername("user1"), createUserWithUsername("user2"));

            Mockito.when(userRepository.findAll()).thenReturn(mockedUsersFromRepo);
            Mockito.when(encryptionService.encrypt("user1")).thenReturn(mockedEncryptedUsername);
            Mockito.when(encryptionService.encrypt("user2")).thenReturn(mockedEncryptedUsername);
            Mockito.when(encryptionService.isEncrypted(any())).thenReturn(false);

            unitUnderTest.onSchedule();

            Mockito.verify(userRepository).saveAll(usersCaptor.capture());
            Assertions.assertThat(usersCaptor.getAllValues()).hasSize(1);
            Assertions.assertThat(usersCaptor.getValue()).allSatisfy(user -> Assertions.assertThat(user.getUsername()).isEqualTo(mockedEncryptedUsername));
        }

        @Test
        void should_saveEmptyList_when_usernameIsAlreadyEncrypted() {
            val mockedUsersFromRepo = List.of(createUserWithUsername("user1"), createUserWithUsername("user2"));

            Mockito.when(userRepository.findAll()).thenReturn(mockedUsersFromRepo);
            Mockito.when(encryptionService.isEncrypted(any())).thenReturn(true);

            unitUnderTest.onSchedule();

            Mockito.verify(userRepository, times(0)).saveAll(any());
        }
    }

    @Nested
    class FindByWahltagID {

        @Test
        void should_decryptUsernames_when_foundUsers() {
            val wahltagID = "wahltagID";

            val decryptedUsername = "decryptedUsername";
            val mockedUsersFromRepo = List.of(createUserWithUsername("user1"), createUserWithUsername("user2"));

            Mockito.when(userRepository.findByWahltagID(wahltagID)).thenReturn(mockedUsersFromRepo);
            Mockito.when(encryptionService.decrypt("user1")).thenReturn(decryptedUsername);
            Mockito.when(encryptionService.decrypt("user2")).thenReturn(decryptedUsername);

            val result = unitUnderTest.findByWahltagID(wahltagID);

            Assertions.assertThat(result).hasSize(mockedUsersFromRepo.size());
            Assertions.assertThat(result).allSatisfy(user -> Assertions.assertThat(user.getUsername()).isEqualTo(decryptedUsername));
        }

        @Test
        void should_returnEmptyListwhen_foundNoUsers() {
            val wahltagID = "wahltagID";

            Mockito.when(userRepository.findByWahltagID(wahltagID)).thenReturn(Collections.emptyList());

            Assertions.assertThat(unitUnderTest.findByWahltagID(wahltagID)).isEmpty();
        }

    }

    @Nested
    class FindById {

        @Test
        void should_decryptUsername_when_foundUserWithID() {
            val oid = UUID.randomUUID();

            val mockedDecryptedUsername = "decryptedUsername";
            val mockedFoundUser = createUserWithUsername("user1");

            Mockito.when(userRepository.findById(oid)).thenReturn(Optional.of(mockedFoundUser));
            Mockito.when(encryptionService.decrypt("user1")).thenReturn(mockedDecryptedUsername);

            val result = unitUnderTest.findById(oid);

            Assertions.assertThat(result.get().getUsername()).isEqualTo(mockedDecryptedUsername);
        }

        @Test
        void should_returnEmptyOptional_when_foundNoUser() {

            val oid = UUID.randomUUID();

            Mockito.when(userRepository.findById(oid)).thenReturn(Optional.empty());

            Assertions.assertThat(unitUnderTest.findById(oid)).isEmpty();
        }
    }

    @Nested
    class SaveAll {

        @Test
        void should_callEncryptOnAllUser_when_savingMultipleUsers() {
            val username1 = "user1";
            val username2 = "user2";
            val usersToSave = List.of(createUserWithUsername(username1), createUserWithUsername(username2));

            val mockedEncryptedUsername1 = "encryptedUsername1";
            val mockedEncryptedUsername2 = "encryptedUsername2";

            Mockito.when(encryptionService.encrypt(username1)).thenReturn(mockedEncryptedUsername1);
            Mockito.when(encryptionService.encrypt(username2)).thenReturn(mockedEncryptedUsername2);
            val savedUsernames = new LinkedList<>();
            Mockito.doAnswer(invocation -> {
                val users = (List<User>) invocation.getArgument(0, List.class);
                users.forEach(user -> savedUsernames.add(user.getUsername()));
                return users;
            }).when(userRepository).saveAll(usersToSave);

            unitUnderTest.saveAll(usersToSave);

            Assertions.assertThat(savedUsernames).hasSize(2);
            Assertions.assertThat(savedUsernames).containsExactlyInAnyOrder(mockedEncryptedUsername1, mockedEncryptedUsername2);
        }

        @Test
        void should_returnUnEncryptedUsername_when_gettingUsers() {
            val username1 = "user1";
            val username2 = "user2";
            val usersToSave = List.of(createUserWithUsername(username1), createUserWithUsername(username2));

            val mockedEncryptedUsername1 = "encryptedUsername1";
            val mockedEncryptedUsername2 = "encryptedUsername2";

            Mockito.when(encryptionService.encrypt(username1)).thenReturn(mockedEncryptedUsername1);
            Mockito.when(encryptionService.encrypt(username2)).thenReturn(mockedEncryptedUsername2);
            Mockito.when(encryptionService.decrypt(mockedEncryptedUsername1)).thenReturn(username1);
            Mockito.when(encryptionService.decrypt(mockedEncryptedUsername2)).thenReturn(username2);
            Mockito.when(userRepository.saveAll(usersToSave)).then(invocationOnMock -> invocationOnMock.getArgument(0, List.class));

            val result = unitUnderTest.saveAll(usersToSave);
            val usernames = StreamSupport.stream(result.spliterator(), false).map(User::getUsername).toList();

            Assertions.assertThat(usernames).containsExactlyInAnyOrder(username1, username2);
        }
    }

    @Nested
    class Save {

        @Test
        void should_encryptUsernamen_when_gettingUser() {
            val username = "username";
            val userToSave = createUserWithUsername(username);

            val mockedEncryptedUsername1 = "encryptedUsername1";
            Mockito.when(encryptionService.encrypt(username)).thenReturn(mockedEncryptedUsername1);

            Mockito.when(userRepository.save(userToSave)).then(invocationOnMock -> {
                val user = invocationOnMock.getArgument(0, User.class);
                Assertions.assertThat(user.getUsername()).isEqualTo(mockedEncryptedUsername1);
                return user;
            });

            unitUnderTest.save(userToSave);
        }

        @Test
        void should_returnUncryptedUsername_when_gettingUser() {
            val username = "username";
            val userToSave = createUserWithUsername(username);

            val mockedEncryptedUsername1 = "encryptedUsername1";
            Mockito.when(encryptionService.encrypt(username)).thenReturn(mockedEncryptedUsername1);
            Mockito.when(encryptionService.decrypt(mockedEncryptedUsername1)).thenReturn(username);

            Mockito.when(userRepository.save(userToSave)).then(invocationOnMock -> invocationOnMock.getArgument(0, User.class));

            val result = unitUnderTest.save(userToSave);

            Assertions.assertThat(result.getUsername()).isEqualTo(username);
        }
    }

    @Nested
    class FindByUsername {

        @Test
        void should_returnUserWithDecryptedUsername_when_foundUser() {
            val username = "username";

            val encryptedUsername = "encryptedUsername";
            val mockedUserFromRepo = createUserWithUsername(encryptedUsername);

            Mockito.when(encryptionService.encrypt(username)).thenReturn(encryptedUsername);
            Mockito.when(encryptionService.decrypt(encryptedUsername)).thenReturn(username);
            Mockito.when(userRepository.findByUsername(encryptedUsername)).thenReturn(Optional.of(mockedUserFromRepo));

            val result = unitUnderTest.findByUsername(username);

            Assertions.assertThat(result.get().getUsername()).isEqualTo(username);
        }

        @Test
        void should_returnEmptyOptional_when_foundNoUser() {
            val username = "username";

            val encryptedUsername = "encryptedUsername";

            Mockito.when(encryptionService.encrypt(username)).thenReturn(encryptedUsername);

            Mockito.when(userRepository.findByUsername(encryptedUsername)).thenReturn(Optional.empty());

            val result = unitUnderTest.findByUsername(username);

            Assertions.assertThat(result).isEmpty();
        }
    }

    @Nested
    class Exists {

        @Test
        void should_searchWithEncryptedUsername_when_searching() {
            val username = "username";

            val mockedEncryptedUsername = "encryptedUsername";
            Mockito.when(encryptionService.encrypt(username)).thenReturn(mockedEncryptedUsername);

            unitUnderTest.exists(username);

            Mockito.verify(userRepository).existsByUsername(mockedEncryptedUsername);
        }

        @Test
        void should_returnTrue_when_repoExistsReturnTrue() {
            val username = "username";

            val mockedEncryptedUsername = "encryptedUsername";
            Mockito.when(encryptionService.encrypt(username)).thenReturn(mockedEncryptedUsername);
            Mockito.when(userRepository.existsByUsername(mockedEncryptedUsername)).thenReturn(true);

            Assertions.assertThat(unitUnderTest.exists(username)).isTrue();
        }

        @Test
        void should_returnFalse_when_repoExistsReturnFalse() {
            val username = "username";

            val mockedEncryptedUsername = "encryptedUsername";
            Mockito.when(encryptionService.encrypt(username)).thenReturn(mockedEncryptedUsername);
            Mockito.when(userRepository.existsByUsername(mockedEncryptedUsername)).thenReturn(false);

            Assertions.assertThat(unitUnderTest.exists(username)).isFalse();
        }
    }

    @Nested
    class IsLocked {

        @Test
        void should_searchWithEncryptedUsername_when_searching() {
            val username = "username";

            val mockedEncryptedUsername = "encryptedUsername";
            Mockito.when(encryptionService.encrypt(username)).thenReturn(mockedEncryptedUsername);

            unitUnderTest.isLocked(username);

            Mockito.verify(userRepository).countUsersLockedByUsername(mockedEncryptedUsername);
        }

        @Test
        void should_returnFalse_when_countResultIsZero() {
            val username = "username";

            val mockedEncryptedUsername = "encryptedUsername";
            Mockito.when(encryptionService.encrypt(username)).thenReturn(mockedEncryptedUsername);
            Mockito.when(userRepository.countUsersLockedByUsername(mockedEncryptedUsername)).thenReturn(0L);

            val result = unitUnderTest.isLocked(username);

            Assertions.assertThat(result).isFalse();
        }

        @Test
        void should_returnFalse_when_countResultIsOne() {
            val username = "username";

            val mockedEncryptedUsername = "encryptedUsername";
            Mockito.when(encryptionService.encrypt(username)).thenReturn(mockedEncryptedUsername);
            Mockito.when(userRepository.countUsersLockedByUsername(mockedEncryptedUsername)).thenReturn(1L);

            val result = unitUnderTest.isLocked(username);

            Assertions.assertThat(result).isTrue();
        }

        @Test
        void should_returnFalse_when_countResultIsAboveOne() {
            val username = "username";

            val mockedEncryptedUsername = "encryptedUsername";
            Mockito.when(encryptionService.encrypt(username)).thenReturn(mockedEncryptedUsername);
            Mockito.when(userRepository.countUsersLockedByUsername(mockedEncryptedUsername)).thenReturn(2L);

            val result = unitUnderTest.isLocked(username);

            Assertions.assertThat(result).isTrue();
        }
    }

    @Nested
    class DeleteUsersByWahltagID {

        @Test
        void should_callRepositoryWithWahltagID_when_calledWithWahltagID() {
            val wahltagID = "wahltagID";

            unitUnderTest.deleteUsersByWahltagID(wahltagID);

            Mockito.verify(userRepository).deleteUsersByWahltagID(wahltagID);
        }
    }

    private User createUserWithUsername(String username) {
        val user = new User();

        user.setUsername(username);

        return user;
    }

}
