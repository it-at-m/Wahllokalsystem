package de.muenchen.oss.wahllokalsystem.authservice.configuration;

import de.muenchen.oss.wahllokalsystem.authservice.service.UserService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
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
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
class JwtUserInfoAuthenticationConverterTest {

    @Mock
    UserService userService;

    @InjectMocks
    JwtUserInfoAuthenticationConverter unitUnderTest;

    @Nested
    class Convert {

        @Test
        void should_jwtAuthenticationTokenWithAuthorities_when_convertingJwt() {
            val username = "username";
            val now = Instant.now();
            val jwt = new Jwt("tokenValue", now, now.plus(30, ChronoUnit.DAYS), Map.of("key1", "value1"), Map.of("sub", username));

            val mockedUserAuthorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
            val mockedUserDetails = new User(username, "password", mockedUserAuthorities);

            Mockito.when(userService.getUserDetails(username)).thenReturn(mockedUserDetails);

            val result = unitUnderTest.convert(jwt);

            Assertions.assertThat(result.getAuthorities()).isEqualTo(mockedUserAuthorities);
        }
    }
}