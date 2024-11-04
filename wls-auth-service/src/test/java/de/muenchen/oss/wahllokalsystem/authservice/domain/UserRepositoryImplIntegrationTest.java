package de.muenchen.oss.wahllokalsystem.authservice.domain;

import de.muenchen.oss.wahllokalsystem.authservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.authservice.TestConstants;
import jakarta.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.val;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest(classes = MicroServiceApplication.class, properties = { "serviceauth.crypto.key=secret" })
@ActiveProfiles(TestConstants.SPRING_TEST_PROFILE)
class UserRepositoryImplIntegrationTest {

    private static final String USERNAME_UNENCRYPTED = "username";
    private static final String USERNAME_ENCRYPTED = "ENCRYPTED:AcZZ7iVGyYoE-DTb9rNwgQ==";

    @Autowired
    UserRepositoryImpl userRepository;

    @Autowired
    CrudUserRepository crudUserRepository;

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    EntityManager entityManager;

    @Autowired
    TransactionTemplate transactionTemplate;
    @Autowired
    private PermissionRepository permissionRepository;

    @AfterEach
    void tearDown() {
        crudUserRepository.deleteAll();
        authorityRepository.deleteAll();
    }

    @Nested
    class FindByWahltagID {

        @Test
        void should_returnDecryptedUsername_when_gettingUserFromRepo() {
            val wahltagID = "wahltagID";
            val userToFind = createUser(USERNAME_ENCRYPTED, wahltagID);

            transactionTemplate.execute(status -> crudUserRepository.save(userToFind));

            val result = userRepository.findByWahltagID(wahltagID);

            Assertions.assertThat(result).hasSize(1);
            Assertions.assertThat(result).allSatisfy(user -> Assertions.assertThat(user.getUsername()).isEqualTo(USERNAME_UNENCRYPTED));
        }

        @Test
        void should_keepTheUsernameEncrypted_when_gettingUserFromRepo() {
            val wahltagID = "wahltagID";
            val userToFind = createUser(USERNAME_ENCRYPTED, wahltagID);

            val savedUser = transactionTemplate.execute(status -> crudUserRepository.save(userToFind));

            userRepository.findByWahltagID(wahltagID);

            val userInRepoAfterFindBy = crudUserRepository.findById(savedUser.getId());
            Assertions.assertThat(userInRepoAfterFindBy.get().getUsername()).isEqualTo(USERNAME_ENCRYPTED);
        }
    }

    @Nested
    class FindById {

        @Test
        void should_returnDecryptedUsername_when_gettingUserFromRepo() {
            val userToFind = createUser(USERNAME_ENCRYPTED);

            val savedUser = transactionTemplate.execute(status -> crudUserRepository.save(userToFind));

            val result = userRepository.findById(savedUser.getId());

            Assertions.assertThat(result.get().getUsername()).isEqualTo(USERNAME_UNENCRYPTED);
        }

        @Test
        void should_keepTheUsernameEncrypted_when_gettingUserFromRepo() {
            val userToFind = createUser(USERNAME_ENCRYPTED);

            val savedUser = transactionTemplate.execute(status -> crudUserRepository.save(userToFind));

            userRepository.findById(savedUser.getId());

            val userInRepoAfterFindBy = crudUserRepository.findById(savedUser.getId());
            Assertions.assertThat(userInRepoAfterFindBy.get().getUsername()).isEqualTo(USERNAME_ENCRYPTED);
        }

    }

    @Nested
    class Save {

        @Test
        void should_encryptUsername_when_savingUser() {
            val userToSave = new User();
            userToSave.setUsername(USERNAME_UNENCRYPTED);

            val savedUser = transactionTemplate.execute(status -> userRepository.save(userToSave));

            val result = (User) entityManager.createQuery("SELECT u FROM User u WHERE u.id = :id").setParameter("id", savedUser.getId()).getSingleResult();

            Assertions.assertThat(result.getUsername()).isEqualTo(USERNAME_ENCRYPTED);
        }

        @Test
        void should_returnUnencryptedUsername_when_savingUser() {
            val userToSave = new User();
            userToSave.setUsername("username");

            val savedUser = transactionTemplate.execute(status -> userRepository.save(userToSave));

            Assertions.assertThat(savedUser.getUsername()).isEqualTo(USERNAME_UNENCRYPTED);
        }
    }

    @Nested
    class SaveAll {

        @Test
        void should_encryptUsername_when_savingUser() {
            val userToSave = new User();
            userToSave.setUsername(USERNAME_UNENCRYPTED);

            val savedUser = transactionTemplate.execute(status -> userRepository.saveAll(List.of(userToSave)));

            val result = (User) entityManager.createQuery("SELECT u FROM User u WHERE u.id = :id")
                    .setParameter("id", IterableUtils.toList(savedUser).get(0).getId())
                    .getSingleResult();

            Assertions.assertThat(result.getUsername()).isEqualTo(USERNAME_ENCRYPTED);
        }

        @Test
        void should_returnUnencryptedUsername_when_savingUser() {
            val userToSave = new User();
            userToSave.setUsername("username");

            val savedUser = transactionTemplate.execute(status -> userRepository.saveAll(List.of(userToSave)));

            Assertions.assertThat(savedUser).allSatisfy(user -> Assertions.assertThat(user.getUsername()).isEqualTo(USERNAME_UNENCRYPTED));
        }
    }

    @Nested
    class FindByUsername {

        @Test
        void should_returnUser_when_searchingWithUnencryptedUsername() {
            val userToSave = new User();
            userToSave.setUsername(USERNAME_UNENCRYPTED);

            val savedUser = transactionTemplate.execute(status -> userRepository.save(userToSave));

            val result = userRepository.findByUsername(USERNAME_UNENCRYPTED);

            Assertions.assertThat(result.get().getId()).isEqualTo(savedUser.getId());
        }
    }

    @Nested
    class Exists {

        @Test
        void should_returnTrue_when_searchingWithUnencryptedUsernameForExistingUser() {
            val userToSave = new User();
            userToSave.setUsername(USERNAME_UNENCRYPTED);

            transactionTemplate.execute(status -> userRepository.save(userToSave));

            val result = userRepository.exists(USERNAME_UNENCRYPTED);
            Assertions.assertThat(result).isTrue();
        }
    }

    @Nested
    class DeleteUsersByWahltagID {

        @Test
        void should_do_sth() {
            val wahltagID = "wahltagID";

            val savedUsers = transactionTemplate.execute(status -> {
                val permission1 = permissionRepository.save(new Permission("permission1"));
                val permission2 = permissionRepository.save(new Permission("permission2"));
                val permission3 = permissionRepository.save(new Permission("permission3"));

                val authority = authorityRepository.save(new Authority("authority1", Set.of(permission1, permission2, permission3), Collections.emptySet()));

                val user1 = userRepository.save(createUser("user1", wahltagID, authority));
                val user2 = userRepository.save(createUser("user2", wahltagID, authority));
                val user3 = userRepository.save(createUser("user3", wahltagID, authority));

                return List.of(user1, user2, user3);
            });

            transactionTemplate.executeWithoutResult(status -> userRepository.deleteUsersByWahltagID(wahltagID));

            Assertions.assertThat(savedUsers).allSatisfy(user -> Assertions.assertThat(crudUserRepository.existsById(user.getId())).isFalse());
            Assertions.assertThat(authorityRepository.count()).isEqualTo(1);
            Assertions.assertThat(permissionRepository.count()).isEqualTo(3);
        }

        private User createUser(final String username, final String wahltagID, final Authority authority) {
            val user = new User();

            user.setUsername(username);
            user.setWahltagID(wahltagID);
            user.setAuthorities(Set.of(authority));

            return user;
        }
    }

    private User createUser(final String encryptedUsername) {
        return createUser(encryptedUsername, null);
    }

    private User createUser(final String encryptedUsername, final String wahltagID) {
        val user = new User();
        user.setUsername(encryptedUsername);
        user.setWahltagID(wahltagID);

        return user;
    }
}
