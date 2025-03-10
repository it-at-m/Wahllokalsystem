package de.muenchen.oss.wahllokalsystem.authservice.security;

import de.muenchen.oss.wahllokalsystem.authservice.service.UserModel;
import de.muenchen.oss.wahllokalsystem.authservice.service.UserService;
import de.muenchen.oss.wahllokalsystem.authservice.service.WahlbezirksartModel;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;

@ExtendWith(MockitoExtension.class)
class WlsUserTokenCustomizerTest {

    @Mock
    UserService userService;

    @InjectMocks
    WlsUserTokenCustomizer unitUnderTest;

    @Nested
    class Customize {

        @Test
        void should_doNothing_when_tokenTypeIsNotAccessToken() {
            val mockedJwtContext = Mockito.mock(JwtEncodingContext.class);

            Mockito.when(mockedJwtContext.getTokenType()).thenReturn(OAuth2TokenType.REFRESH_TOKEN);

            unitUnderTest.customize(mockedJwtContext);

            Mockito.verifyNoMoreInteractions(mockedJwtContext);
            Mockito.verifyNoInteractions(userService);
        }

        @Test
        void should_addWahlbezirkArtOfUser_when_tokenTypeIsAccessTokenAndUserIsFound() {
            val username = "username";
            val wahlbezirkArt = WahlbezirksartModel.BWB;

            val jwtContext = JwtEncodingContext.with(JwsHeader.with(MacAlgorithm.HS256), JwtClaimsSet.builder()).context(context -> {
                context.put(OAuth2TokenType.class, OAuth2TokenType.ACCESS_TOKEN);
                context.put(Authentication.class.getName().concat(".PRINCIPAL"), new TestingAuthenticationToken(username, ""));
            }).build();

            val mockedUser = new UserModel(username, "", true, "", LocalDate.now(), "", "", wahlbezirkArt, "", Collections.emptySet(), "");

            Mockito.when(userService.getUser(username)).thenReturn(Optional.of(mockedUser));

            unitUnderTest.customize(jwtContext);

            jwtContext.getClaims().claims(claims -> Assertions.assertThat(claims).contains(Assertions.entry("wahlbezirksArt", wahlbezirkArt)));
        }

        @Test
        void should_addWahlbezirkIDOfUser_when_tokenTypeIsAccessTokenAndUserIsFound() {
            val username = "username";
            val wahlbezirkID = "wahlbezirkID";

            val jwtContext = JwtEncodingContext.with(JwsHeader.with(MacAlgorithm.HS256), JwtClaimsSet.builder()).context(context -> {
                context.put(OAuth2TokenType.class, OAuth2TokenType.ACCESS_TOKEN);
                context.put(Authentication.class.getName().concat(".PRINCIPAL"), new TestingAuthenticationToken(username, ""));
            }).build();

            val mockedUser = new UserModel(username, "", true, "", LocalDate.now(), wahlbezirkID, "", null, "", Collections.emptySet(), "");

            Mockito.when(userService.getUser(username)).thenReturn(Optional.of(mockedUser));

            unitUnderTest.customize(jwtContext);

            jwtContext.getClaims().claims(claims -> Assertions.assertThat(claims).contains(Assertions.entry("wahlbezirkID", wahlbezirkID)));
        }

        @Test
        void should_doNothing_when_tokenTypeIsAccessTokenAndUserIsNotFound() {
            val username = "username";
            val wahlbezirkArt = WahlbezirksartModel.BWB;

            val jwtContext = JwtEncodingContext.with(JwsHeader.with(MacAlgorithm.HS256), JwtClaimsSet.builder()).context(context -> {
                context.put(OAuth2TokenType.class, OAuth2TokenType.ACCESS_TOKEN);
                context.put(Authentication.class.getName().concat(".PRINCIPAL"), new TestingAuthenticationToken(username, ""));
            }).build();

            Mockito.when(userService.getUser(username)).thenReturn(Optional.empty());

            unitUnderTest.customize(jwtContext);

            jwtContext.getClaims().claims(claims -> Assertions.assertThat(claims).doesNotContain(Assertions.entry("wahlbezirksArt", wahlbezirkArt)));
        }
    }

}
