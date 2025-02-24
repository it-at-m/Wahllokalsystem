package de.muenchen.oss.wahllokalsystem.authservice.rest;

import static de.muenchen.oss.wahllokalsystem.authservice.rest.WahllokalBenutzerControllerIntegrationTest.PROP_USER_AUTHORITY_WAHLVORSTAND;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.authservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.authservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.authservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.authservice.domain.Authority;
import de.muenchen.oss.wahllokalsystem.authservice.domain.AuthorityRepository;
import de.muenchen.oss.wahllokalsystem.authservice.domain.PermissionRepository;
import de.muenchen.oss.wahllokalsystem.authservice.domain.User;
import de.muenchen.oss.wahllokalsystem.authservice.domain.UserRepository;
import de.muenchen.oss.wahllokalsystem.authservice.service.CryptoService;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest(
        classes = MicroServiceApplication.class,
        properties = {"service.config.user.authority.wahlvorstand=" + PROP_USER_AUTHORITY_WAHLVORSTAND}
)
@AutoConfigureMockMvc
@ActiveProfiles({TestConstants.SPRING_TEST_PROFILE, TestConstants.SPRING_NO_SECURITY_PROFILE, Profiles.DUMMY_CLIENTS})
class WahllokalBenutzerControllerIntegrationTest {

    public static final String PROP_USER_AUTHORITY_WAHLVORSTAND = "WLS_USER_AUTHORITY_WAHLVORSTAND";

    @Autowired
    EntityManager entityManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    PermissionRepository permissionRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TransactionTemplate transactionTemplate;

    @Autowired
    CryptoService cryptoService;

    @AfterEach
    void tearDown() {
        transactionTemplate.executeWithoutResult(status -> {
            entityManager.createQuery("DELETE FROM User").executeUpdate();
            permissionRepository.deleteAll();
            authorityRepository.deleteAll();
        });
    }

    @Nested
    class CreateAndExportWahllokalBenutzer {

        @Test
        void should_persistUsers_when_usersAreGiven() throws Exception {
            val wahltagID = "wahltagID";
            val wahltag = LocalDate.now();
            val user1 = new WahllokalUserInfoDTO("1", wahltag, "wbzID1", WahlbezirksartDTO.UWB, "1_1");
            val user2 = new WahllokalUserInfoDTO("2", wahltag, "wbzID2", WahlbezirksartDTO.BWB, "2_1");
            val requestBody = List.of(user1, user2);

            authorityRepository.save(new Authority(PROP_USER_AUTHORITY_WAHLVORSTAND, Collections.emptySet(), Collections.emptySet()));

            val request = MockMvcRequestBuilders.post("/generateAndExportWahllokalbenutzer/" + wahltagID).contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(requestBody));

            val result = mockMvc.perform(request).andExpect(status().isCreated()).andReturn();

            final List<User> persistedUsers = (List) entityManager.createQuery("SELECT u FROM User u").getResultList();
            val username1 = cryptoService.decrypt(persistedUsers.get(0).getUsername());
            val username2 = cryptoService.decrypt(persistedUsers.get(1).getUsername());
            val expectedResult = username1 + "\r\n" + username2;

            Assertions.assertThat(persistedUsers).hasSize(2);
            Assertions.assertThat(result.getResponse().getContentAsString()).isEqualTo(expectedResult);
        }

        @Test
        void should_deleteOldUsersWithWahltagIDAndPersistNewUsers_when_usersAreGiven() throws Exception {
            val wahltagID = "wahltagID";

            val oldUser1 = new User();
            oldUser1.setUsername("oldUser1");
            oldUser1.setWahltagID(wahltagID);
            val oldUser1Saved = userRepository.save(oldUser1);

            val oldUser2 = new User();
            oldUser2.setUsername("oldUser2");
            oldUser2.setWahltagID(wahltagID);
            val oldUser2Saved = userRepository.save(oldUser2);

            val oldUserToKeep = new User();
            oldUserToKeep.setUsername("userToKeep");
            oldUserToKeep.setWahltagID("other" + wahltagID);
            val oldUserToKeepSaved = userRepository.save(oldUserToKeep);

            val wahltag = LocalDate.now();
            val user1 = new WahllokalUserInfoDTO("1", wahltag, "wbzID1", WahlbezirksartDTO.UWB, "1_1");
            val user2 = new WahllokalUserInfoDTO("2", wahltag, "wbzID2", WahlbezirksartDTO.BWB, "2_1");
            val requestBody = List.of(user1, user2);

            authorityRepository.save(new Authority(PROP_USER_AUTHORITY_WAHLVORSTAND, Collections.emptySet(), Collections.emptySet()));

            val request = MockMvcRequestBuilders.post("/generateAndExportWahllokalbenutzer/" + wahltagID).contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(requestBody));

            mockMvc.perform(request).andExpect(status().isCreated());

            final List<User> persistedUsers = (List) entityManager.createQuery("SELECT u FROM User u").getResultList();

            Assertions.assertThat(persistedUsers).hasSize(3);
            Assertions.assertThat(userRepository.exists(oldUser1Saved.getUsername())).isFalse();
            Assertions.assertThat(userRepository.exists(oldUser2Saved.getUsername())).isFalse();
            Assertions.assertThat(userRepository.exists(oldUserToKeepSaved.getUsername())).isTrue();
        }

    }

    @Nested
    class ExportWahllokalBenutzer {

        @Test
        void should_returnStringWithUsernames_when_usersFound() throws Exception {
            val wahltagIDToFind = "wahltagID";
            val wahltagIDNotToFind = "anotherWahltagID";

            val savedUser1 = userRepository.save(createUser("user1", wahltagIDToFind));
            val savedUser2 = userRepository.save(createUser("user2", wahltagIDToFind));
            val savedUser3 = userRepository.save(createUser("user3", wahltagIDNotToFind));

            val request = MockMvcRequestBuilders.get("/exportWahllokalbenutzer/" + wahltagIDToFind);

            val result = mockMvc.perform(request).andExpect(status().isCreated()).andReturn();
            val resultBodyAsString = result.getResponse().getContentAsString();

            Assertions.assertThat(resultBodyAsString).contains(savedUser1.getUsername());
            Assertions.assertThat(resultBodyAsString).contains(savedUser2.getUsername());
            Assertions.assertThat(resultBodyAsString).doesNotContain(savedUser3.getUsername());
        }
    }

    @Nested
    class DeleteWahllokalBenutzer {

        @Test
        void should_removeOnlyUsersOfWahltag_when_wahltagIsGiven() throws Exception {
            val wahltagIDToFind = "wahltagID";
            val wahltagIDNotToFind = "anotherWahltagID";

            val savedUser1 = userRepository.save(createUser("user1", wahltagIDToFind));
            val savedUser2 = userRepository.save(createUser("user2", wahltagIDToFind));
            val savedUser3 = userRepository.save(createUser("user3", wahltagIDNotToFind));

            val request = MockMvcRequestBuilders.delete("/deleteWahllokalbenutzer/" + wahltagIDToFind);

            mockMvc.perform(request).andExpect(status().isOk());

            Assertions.assertThat(userRepository.exists(savedUser1.getUsername())).isFalse();
            Assertions.assertThat(userRepository.exists(savedUser2.getUsername())).isFalse();
            Assertions.assertThat(userRepository.exists(savedUser3.getUsername())).isTrue();
        }
    }

    private User createUser(final String username, final String wahltagID) {
        val user = new User();

        user.setUsername(username);
        user.setWahltagID(wahltagID);

        return user;
    }
}
