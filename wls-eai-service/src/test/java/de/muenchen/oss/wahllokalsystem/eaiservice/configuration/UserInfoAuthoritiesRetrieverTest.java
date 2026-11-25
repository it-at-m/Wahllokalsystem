package de.muenchen.oss.wahllokalsystem.eaiservice.configuration;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class UserInfoAuthoritiesRetrieverTest {

  private static final String RESPONSEBODY_MAP_KEY_CLAIM_AUTHORITIES = "authorities";

  @Mock RestClient restClient;

  @Mock(answer = Answers.RETURNS_SELF)
  RestClient.RequestHeadersUriSpec<?> requestSpec;

  @Mock RestClient.ResponseSpec responseSpec;

  @Mock Jwt jwt;

  String userInfoUri = "http://localhost:8080/userinfo";

  UserInfoAuthoritiesRetriever unitUnderTest;

  @BeforeEach
  void setup() {
    unitUnderTest = new UserInfoAuthoritiesRetriever(userInfoUri, restClient);
  }

  @Nested
  class LoadAuthorities {

    @BeforeEach
    void setup() {
      Mockito.doReturn(requestSpec).when(restClient).get();
      Mockito.when(requestSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void should_loadAuthoritiesFromClient_when_givenAsCollection() {
      val jwtTokenValue = "myTokenValue";

      val authority1 = "auth1";
      val authority2 = "auth2";
      val authority3 = "auth3";
      val claimAuthorityValues = List.of(authority1, authority2, authority3);

      val responseEntityBody = new HashMap<String, Object>();
      responseEntityBody.put(RESPONSEBODY_MAP_KEY_CLAIM_AUTHORITIES, claimAuthorityValues);

      Mockito.when(jwt.getSubject()).thenReturn("subject");
      Mockito.when(jwt.getTokenValue()).thenReturn(jwtTokenValue);
      mockUserInfoResponse(responseEntityBody);

      val expectedAuthorities =
          List.of(
              new SimpleGrantedAuthority(authority1),
              new SimpleGrantedAuthority(authority2),
              new SimpleGrantedAuthority(authority3));

      val authorities = unitUnderTest.loadAuthorities(jwt);

      Assertions.assertThat(authorities).hasSize(claimAuthorityValues.size());
      Assertions.assertThat(authorities).containsAll(expectedAuthorities);
    }

    @Test
    void should_loadAuthoritiesFromClient_when_givenAsArray() {
      val jwtTokenValue = "myTokenValue";

      val authority1 = "auth1";
      val authority2 = "auth2";
      val authority3 = "auth3";
      val claimAuthorityValues = new String[] {authority1, authority2, authority3};

      val responseEntityBody = new HashMap<String, Object>();
      responseEntityBody.put(RESPONSEBODY_MAP_KEY_CLAIM_AUTHORITIES, claimAuthorityValues);

      Mockito.when(jwt.getSubject()).thenReturn("subject");
      Mockito.when(jwt.getTokenValue()).thenReturn(jwtTokenValue);
      mockUserInfoResponse(responseEntityBody);

      val expctedAuthorities =
          List.of(
              new SimpleGrantedAuthority(authority1),
              new SimpleGrantedAuthority(authority2),
              new SimpleGrantedAuthority(authority3));

      val authorities = unitUnderTest.loadAuthorities(jwt);

      Assertions.assertThat(authorities).hasSize(claimAuthorityValues.length);
      Assertions.assertThat(authorities).containsAll(expctedAuthorities);
    }

    @Test
    void should_returnEmptyList_when_givenAsUnhandledDataStructure() {
      val jwtTokenValue = "myTokenValue";

      val claimAuthorityValues = "list;of;claims;as;CSV;that;is;not;supported";

      val responseEntityBody = new HashMap<String, Object>();
      responseEntityBody.put(RESPONSEBODY_MAP_KEY_CLAIM_AUTHORITIES, claimAuthorityValues);

      Mockito.when(jwt.getSubject()).thenReturn("subject");
      Mockito.when(jwt.getTokenValue()).thenReturn(jwtTokenValue);
      mockUserInfoResponse(responseEntityBody);

      val authorities = unitUnderTest.loadAuthorities(jwt);

      Assertions.assertThat(authorities).isEmpty();
    }

    @Test
    void should_returnEmptyList_when_noAuthoritiesFound() {
      val jwtTokenValue = "myTokenValue";

      val responseEntityBody = new HashMap<String, Object>();

      Mockito.when(jwt.getSubject()).thenReturn("subject");
      Mockito.when(jwt.getTokenValue()).thenReturn(jwtTokenValue);
      mockUserInfoResponse(responseEntityBody);

      val authorities = unitUnderTest.loadAuthorities(jwt);

      Assertions.assertThat(authorities).isEmpty();
    }

    @Test
    void should_returnEmptyList_when_errorThrownWhileLoadingViaClient() {
      val jwtTokenValue = "myTokenValue";

      Mockito.when(jwt.getSubject()).thenReturn("subject");
      Mockito.when(jwt.getTokenValue()).thenReturn(jwtTokenValue);
      Mockito.when(responseSpec.body(Map.class)).thenThrow(new RuntimeException("sth happend"));

      val authorities = unitUnderTest.loadAuthorities(jwt);

      Assertions.assertThat(authorities).isEmpty();
    }

    @Test
    void should_loadAuthoritiesFromCache_when_givenValidJwtToken() {
      val jwtSubject = "subject";
      val jwtTokenValue = "myTokenValue";
      val jwtForCachMethodCall = Mockito.mock(Jwt.class);

      val authority1 = "auth1";
      val authority2 = "auth2";
      val authority3 = "auth3";
      val claimAuthorityValues = List.of(authority1, authority2, authority3);

      val responseEntityBody = new HashMap<String, Object>();
      responseEntityBody.put(RESPONSEBODY_MAP_KEY_CLAIM_AUTHORITIES, claimAuthorityValues);

      Mockito.when(jwt.getSubject()).thenReturn(jwtSubject);
      Mockito.when(jwtForCachMethodCall.getSubject()).thenReturn(jwtSubject);
      Mockito.when(jwt.getTokenValue()).thenReturn(jwtTokenValue);
      mockUserInfoResponse(responseEntityBody);

      val expectedAuthorities =
          List.of(
              new SimpleGrantedAuthority(authority1),
              new SimpleGrantedAuthority(authority2),
              new SimpleGrantedAuthority(authority3));

      val authorities = unitUnderTest.loadAuthorities(jwt);

      val authoritiesThatShouldComeFromCache = unitUnderTest.loadAuthorities(jwtForCachMethodCall);

      Assertions.assertThat(authorities).hasSize(claimAuthorityValues.size());
      Assertions.assertThat(authorities).containsAll(expectedAuthorities);
      Assertions.assertThat(authoritiesThatShouldComeFromCache).isSameAs(authorities);

      Mockito.verify(restClient, Mockito.times(1)).get();
      Mockito.verify(requestSpec).uri(userInfoUri);
      Mockito.verify(requestSpec).header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtTokenValue);
    }
  }

  MockRestServiceServer server;
  UserInfoAuthoritiesRetriever retriever;

  private void setupHttpClient() {
    val builder = RestClient.builder();
    server = MockRestServiceServer.bindTo(builder).build();
    retriever = new UserInfoAuthoritiesRetriever(userInfoUri, builder.build());
    Mockito.when(jwt.getSubject()).thenReturn("subject");
    Mockito.when(jwt.getTokenValue()).thenReturn("myTokenValue");
  }

  @Test
  void should_sendBearerTokenAndLoadAuthorities_when_userInfoResponds() {
    setupHttpClient();
    server
        .expect(requestTo(userInfoUri))
        .andExpect(method(HttpMethod.GET))
        .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer myTokenValue"))
        .andRespond(
            withSuccess("{\"authorities\":[\"auth1\",\"auth2\"]}", MediaType.APPLICATION_JSON));

    Assertions.assertThat(retriever.loadAuthorities(jwt))
        .containsExactly(new SimpleGrantedAuthority("auth1"), new SimpleGrantedAuthority("auth2"));
    server.verify();
  }

  @ParameterizedTest
  @EnumSource(
      value = HttpStatus.class,
      names = {"UNAUTHORIZED", "INTERNAL_SERVER_ERROR"})
  void should_retryWithoutAuthorities_when_userInfoReturnsError(HttpStatus status) {
    setupHttpClient();
    server.expect(requestTo(userInfoUri)).andRespond(withStatus(status));
    server
        .expect(requestTo(userInfoUri))
        .andRespond(withSuccess("{\"authorities\":[\"auth1\"]}", MediaType.APPLICATION_JSON));

    Assertions.assertThat(retriever.loadAuthorities(jwt)).isEmpty();
    Assertions.assertThat(retriever.loadAuthorities(jwt))
        .containsExactly(new SimpleGrantedAuthority("auth1"));
    server.verify();
  }

  private void mockUserInfoResponse(Map<String, Object> responseBody) {
    Mockito.when(responseSpec.body(Map.class)).thenReturn(responseBody);
  }
}
