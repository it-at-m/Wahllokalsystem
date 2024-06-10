package de.muenchen.oss.wahllokalsystem.basisdaten.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class UserInfoAuthoritiesServiceTest {

    private static final String RESPONSEBODY_MAP_KEY_CLAIM_AUTHORITIES = "authorities";

    @Mock
    RestTemplate restTemplate;

    @Mock
    ResponseEntity<Map> responseEntity;

    @Mock
    Jwt jwt;

    String userInfoUri = "http://localhost:8080/userinfo";

    UserInfoAuthoritiesService unitUnderTest;

    @BeforeEach
    void setUp() {
        val restTemplateBuilder = new RestTemplateBuilder(new RestTemplateCustomizer[0]) {
            @Override
            public RestTemplate build() {
                return restTemplate;
            }
        };

        unitUnderTest = new UserInfoAuthoritiesService(userInfoUri, restTemplateBuilder);
    }

    @Nested
    class LoadAuthorities {

        @Test
        void buildAuthoritiesFromTemplateResponseWithCollection() {
            val jwtTokenValue = "myTokenValue";

            val expectedRequestHeaders = new HttpHeaders();
            expectedRequestHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwtTokenValue);
            val expectedRequestEntity = new HttpEntity<>(expectedRequestHeaders);

            val authority1 = "auth1";
            val authority2 = "auth2";
            val authority3 = "auth3";
            val claimAuthorityValues = List.of(authority1, authority2, authority3);

            val responseEntityBody = new HashMap<String, Object>();
            responseEntityBody.put(RESPONSEBODY_MAP_KEY_CLAIM_AUTHORITIES, claimAuthorityValues);

            Mockito.when(jwt.getSubject()).thenReturn("subject");
            Mockito.when(jwt.getTokenValue()).thenReturn(jwtTokenValue);
            Mockito.when(restTemplate.exchange(userInfoUri, HttpMethod.GET, expectedRequestEntity, Map.class)).thenReturn(responseEntity);
            Mockito.when(responseEntity.getBody()).thenReturn(responseEntityBody);

            val expectedAuthorities = List.of(new SimpleGrantedAuthority(authority1), new SimpleGrantedAuthority(authority2),
                    new SimpleGrantedAuthority(authority3));

            val authorities = unitUnderTest.loadAuthorities(jwt);

            Assertions.assertThat(authorities).hasSize(claimAuthorityValues.size());
            Assertions.assertThat(authorities).containsAll(expectedAuthorities);
        }

        @Test
        void buildAuthoritiesFromTemplateResponseWithArray() {
            val jwtTokenValue = "myTokenValue";

            val expectedRequestHeaders = new HttpHeaders();
            expectedRequestHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwtTokenValue);
            val expectedRequestEntity = new HttpEntity<>(expectedRequestHeaders);

            val authority1 = "auth1";
            val authority2 = "auth2";
            val authority3 = "auth3";
            val claimAuthorityValues = new String[] { authority1, authority2, authority3 };

            val responseEntityBody = new HashMap<String, Object>();
            responseEntityBody.put(RESPONSEBODY_MAP_KEY_CLAIM_AUTHORITIES, claimAuthorityValues);

            Mockito.when(jwt.getSubject()).thenReturn("subject");
            Mockito.when(jwt.getTokenValue()).thenReturn(jwtTokenValue);
            Mockito.when(restTemplate.exchange(userInfoUri, HttpMethod.GET, expectedRequestEntity, Map.class)).thenReturn(responseEntity);
            Mockito.when(responseEntity.getBody()).thenReturn(responseEntityBody);

            val expctedAuthorities = List.of(new SimpleGrantedAuthority(authority1), new SimpleGrantedAuthority(authority2),
                    new SimpleGrantedAuthority(authority3));

            val authorities = unitUnderTest.loadAuthorities(jwt);

            Assertions.assertThat(authorities).hasSize(claimAuthorityValues.length);
            Assertions.assertThat(authorities).containsAll(expctedAuthorities);
        }

        @Test
        void buildAuthoritiesFromTemplateResponseWithUnhandledDataStructure() {
            val jwtTokenValue = "myTokenValue";

            val expectedRequestHeaders = new HttpHeaders();
            expectedRequestHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwtTokenValue);
            val expectedRequestEntity = new HttpEntity<>(expectedRequestHeaders);

            val claimAuthorityValues = "list;of;claims;as;CSV;that;is;not;supported";

            val responseEntityBody = new HashMap<String, Object>();
            responseEntityBody.put(RESPONSEBODY_MAP_KEY_CLAIM_AUTHORITIES, claimAuthorityValues);

            Mockito.when(jwt.getSubject()).thenReturn("subject");
            Mockito.when(jwt.getTokenValue()).thenReturn(jwtTokenValue);
            Mockito.when(restTemplate.exchange(userInfoUri, HttpMethod.GET, expectedRequestEntity, Map.class)).thenReturn(responseEntity);
            Mockito.when(responseEntity.getBody()).thenReturn(responseEntityBody);

            val authorities = unitUnderTest.loadAuthorities(jwt);

            Assertions.assertThat(authorities).isEmpty();
        }

        @Test
        void buildAuthoritiesFromTemplateResponseWithoutAuthoritiesClaim() {
            val jwtTokenValue = "myTokenValue";

            val expectedRequestHeaders = new HttpHeaders();
            expectedRequestHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwtTokenValue);
            val expectedRequestEntity = new HttpEntity<>(expectedRequestHeaders);

            val responseEntityBody = new HashMap<String, Object>();

            Mockito.when(jwt.getSubject()).thenReturn("subject");
            Mockito.when(jwt.getTokenValue()).thenReturn(jwtTokenValue);
            Mockito.when(restTemplate.exchange(userInfoUri, HttpMethod.GET, expectedRequestEntity, Map.class)).thenReturn(responseEntity);
            Mockito.when(responseEntity.getBody()).thenReturn(responseEntityBody);

            val authorities = unitUnderTest.loadAuthorities(jwt);

            Assertions.assertThat(authorities).isEmpty();
        }

        @Test
        void errorWhileLoadingViaTemplate() {
            val jwtTokenValue = "myTokenValue";

            val expectedRequestHeaders = new HttpHeaders();
            expectedRequestHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwtTokenValue);
            val expectedRequestEntity = new HttpEntity<>(expectedRequestHeaders);

            Mockito.when(jwt.getSubject()).thenReturn("subject");
            Mockito.when(jwt.getTokenValue()).thenReturn(jwtTokenValue);
            Mockito.when(restTemplate.exchange(userInfoUri, HttpMethod.GET, expectedRequestEntity, Map.class)).thenThrow(new RuntimeException("sth happend"));

            val authorities = unitUnderTest.loadAuthorities(jwt);

            Assertions.assertThat(authorities).isEmpty();
        }

        @Test
        void loadedAuthoritiesAsPlacedInCache() {
            val jwtSubject = "subject";
            val jwtTokenValue = "myTokenValue";
            val jwtForCachMethodCall = Mockito.mock(Jwt.class);

            val expectedRequestHeaders = new HttpHeaders();
            expectedRequestHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwtTokenValue);
            val expectedRequestEntity = new HttpEntity<>(expectedRequestHeaders);

            val authority1 = "auth1";
            val authority2 = "auth2";
            val authority3 = "auth3";
            val claimAuthorityValues = List.of(authority1, authority2, authority3);

            val responseEntityBody = new HashMap<String, Object>();
            responseEntityBody.put(RESPONSEBODY_MAP_KEY_CLAIM_AUTHORITIES, claimAuthorityValues);

            Mockito.when(jwt.getSubject()).thenReturn(jwtSubject);
            Mockito.when(jwtForCachMethodCall.getSubject()).thenReturn(jwtSubject);
            Mockito.when(jwt.getTokenValue()).thenReturn(jwtTokenValue);
            Mockito.when(restTemplate.exchange(userInfoUri, HttpMethod.GET, expectedRequestEntity, Map.class)).thenReturn(responseEntity);
            Mockito.when(responseEntity.getBody()).thenReturn(responseEntityBody);

            val expectedAuthorities = List.of(new SimpleGrantedAuthority(authority1), new SimpleGrantedAuthority(authority2),
                    new SimpleGrantedAuthority(authority3));

            val authorities = unitUnderTest.loadAuthorities(jwt);

            val authoritiesThatShouldComeFromCache = unitUnderTest.loadAuthorities(jwtForCachMethodCall);

            Assertions.assertThat(authorities).hasSize(claimAuthorityValues.size());
            Assertions.assertThat(authorities).containsAll(expectedAuthorities);
            Assertions.assertThat(authoritiesThatShouldComeFromCache).isSameAs(authorities);

            Mockito.verify(restTemplate, Mockito.times(1)).exchange(userInfoUri, HttpMethod.GET, expectedRequestEntity, Map.class);
        }

    }
}
