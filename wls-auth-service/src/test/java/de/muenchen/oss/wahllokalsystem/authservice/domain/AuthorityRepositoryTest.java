package de.muenchen.oss.wahllokalsystem.authservice.domain;

import de.muenchen.oss.wahllokalsystem.authservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.authservice.TestConstants;
import java.util.Collections;
import java.util.UUID;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles(TestConstants.SPRING_TEST_PROFILE)
class AuthorityRepositoryTest {

    @Autowired
    AuthorityRepository authorityRepository;

    @AfterEach
    void tearDown() {
        authorityRepository.deleteAll();
    }

    @Nested
    class FindByAuthority {

        @Test
        @Transactional
        void should_returnOptionalWithAuthority_when_foundByAuthority() {
            val authorityString = "authorityToFind";
            val authorityToGet = authorityRepository.save(createAuthorityWithAuthorityName(authorityString));

            val findByResult = authorityRepository.findByAuthority(authorityString);

            Assertions.assertThat(findByResult.get()).isEqualTo(authorityToGet);
        }

        @Test
        void should_returnEmptyOptional_when_notFoundByAuthority() {
            authorityRepository.save(createAuthorityWithAuthorityName(UUID.randomUUID().toString()));

            val findByResult = authorityRepository.findByAuthority(UUID.randomUUID().toString());

            Assertions.assertThat(findByResult).isEmpty();
        }
    }

    private Authority createAuthorityWithAuthorityName(final String authorityName) {
        val authority = new Authority();

        authority.setAuthority(authorityName);
        authority.setUsers(Collections.emptySet());
        authority.setPermissions(Collections.emptySet());

        return authority;
    }
}
