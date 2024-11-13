package de.muenchen.oss.wahllokalsystem.authservice.security.ldap;

import de.muenchen.oss.wahllokalsystem.authservice.domain.Authority;
import de.muenchen.oss.wahllokalsystem.authservice.domain.User;
import de.muenchen.oss.wahllokalsystem.authservice.domain.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@ExtendWith(MockitoExtension.class)
class CustomLdapAuthoritiesPopulatorTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    CustomLdapAuthoritiesPopulator unitUnderTest;

    @Nested
    class GetGrantedAuthorities {

        @Test
        void should_returnCollectionOfGrantedAuthorities_when_userIsFound() {
            val username = "username";

            val mockedUser = new User();
            mockedUser.setAuthorities(Set.of(new Authority("auth1", null, null), new Authority("auth2", null, null)));
            Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockedUser));

            val result = unitUnderTest.getGrantedAuthorities(null, username);

            val expectedResult = List.of(new SimpleGrantedAuthority("auth1"), new SimpleGrantedAuthority("auth2"));
            Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);

        }

        @Test
        void should_returnEmptyCollectionOfGrantedAuthorities_when_userIsNotFound() {
            val username = "username";

            Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
            
            val result = unitUnderTest.getGrantedAuthorities(null, username);

            Assertions.assertThat(result).isEmpty();
        }
    }

}
