package de.muenchen.oss.wahllokalsystem.authservice.rest;

import static de.muenchen.oss.wahllokalsystem.authservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.authservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.authservice.configuration.CacheConfig;
import de.muenchen.oss.wahllokalsystem.authservice.domain.User;
import de.muenchen.oss.wahllokalsystem.authservice.domain.UserRepository;
import de.muenchen.oss.wahllokalsystem.authservice.service.UserService;
import de.muenchen.oss.wahllokalsystem.authservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.util.Collections;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest(classes = MicroServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE })
public class UserControllerIntegrationTest {

    @Autowired
    MockMvc api;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TransactionTemplate transactionTemplate;

    @Autowired
    CacheManager cacheManager;

    @SpyBean
    private UserService userService;

    @AfterEach
    void tearDown() {
        cacheManager.getCache(CacheConfig.USER_CACHE).clear();
        SecurityUtils.runWith(Authorities.CONTROLLER_UNLOCK_USER);
        transactionTemplate.executeWithoutResult(status -> {
            SecurityUtils.runWith(Authorities.CONTROLLER_UNLOCK_USER);
            userRepository.deleteUsersByWahltagID("wahltagID");
        });
    }

    @Nested
    class UserMethod {

        @WithMockUser(username = "Hansi")
        @Test
        void should_returnNoContent_whenNoUserFound() throws Exception {
            val request = MockMvcRequestBuilders.get("/user");
            api.perform(request).andExpect(status().isNoContent());
        }

        @WithMockUser(username = "Hansi")
        @Test
        void should_returnUserDTO_whenUserFound() throws Exception {
            userRepository.save(new User("Hansi", null, null, true, true, "wahltagID", null, null, null, null, null, null, null));

            val request = MockMvcRequestBuilders.get("/user");
            api.perform(request).andExpect(status().isOk());

            val response = api.perform(request).andExpect(status().isOk()).andReturn();
            val responseBody = objectMapper.readValue(response.getResponse().getContentAsString(), UserDTO.class);
            val expectedResponseBody = new UserDTO("Hansi", null, true, "wahltagID", null, null, null, null, null, Collections.emptySet(), null);

            Assertions.assertThat(responseBody).isEqualTo(expectedResponseBody);
        }

        @WithMockUser(username = "Hansi")
        @Test
        void should_cacheUser_whenControllerIsCalledMoreThanOnce() throws Exception {
            userRepository.save(new User("Hansi", null, null, true, true, "wahltagID", null, null, null, null, null, null, null));

            val request = MockMvcRequestBuilders.get("/user");
            // first call
            api.perform(request).andExpect(status().isOk());

            val response = api.perform(request).andExpect(status().isOk()).andReturn();
            val responseBody = objectMapper.readValue(response.getResponse().getContentAsString(), UserDTO.class);
            val expectedResponseBody = new UserDTO("Hansi", null, true, "wahltagID", null, null, null, null, null, Collections.emptySet(), null);

            Assertions.assertThat(responseBody).isEqualTo(expectedResponseBody);
            // do two more calls with same user
            api.perform(request).andExpect(status().isOk());
            api.perform(request).andExpect(status().isOk());
            // service should be called only once, other two responses come  from cache
            Mockito.verify(userService, Mockito.times(1)).getUser("Hansi");
        }

    }

    @Nested
    class UnlockUser {

    }

}
