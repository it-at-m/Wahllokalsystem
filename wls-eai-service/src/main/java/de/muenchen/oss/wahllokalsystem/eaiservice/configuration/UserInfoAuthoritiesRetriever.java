/*
 * Copyright (c): it@M - Dienstleister für Informations- und Telekommunikationstechnik
 * der Landeshauptstadt München, 2024
 */
package de.muenchen.oss.wahllokalsystem.eaiservice.configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestClient;

/**
 * Retriever, der einen OIDC /userinfo Endpoint aufruft (mit JWT Bearer Auth) und dort die
 * enthaltenen "Authorities" extrahiert.
 */
@Slf4j
public class UserInfoAuthoritiesRetriever {

  private static final int AUTHENTICATION_CACHE_ENTRY_SECONDS_TO_EXPIRE = 60;

  private static final String CLAIM_AUTHORITIES = "authorities";

  private final String userInfoUri;
  private final RestClient restClient;
  private final Cache<String, Collection<SimpleGrantedAuthority>> cache;

  /**
   * Erzeugt eine neue Instanz.
   *
   * @param userInfoUri userinfo Endpoint URI
   * @param restClient der HTTP-Client für den Userinfo-Endpunkt
   */
  public UserInfoAuthoritiesRetriever(String userInfoUri, RestClient restClient) {
    this.userInfoUri = userInfoUri;
    this.restClient = restClient;
    this.cache =
        Caffeine.newBuilder()
            .expireAfterWrite(AUTHENTICATION_CACHE_ENTRY_SECONDS_TO_EXPIRE, TimeUnit.SECONDS)
            .ticker(Ticker.systemTicker())
            .build();
  }

  /**
   * Ruft den /userinfo Endpoint und extrahiert {@link GrantedAuthority}s aus dem "authorities"
   * Claim.
   *
   * @param jwt der JWT
   * @return die {@link GrantedAuthority}s gem. Claim "authorities" des /userinfo Endpoints
   */
  public Collection<SimpleGrantedAuthority> loadAuthorities(Jwt jwt) {
    Collection<SimpleGrantedAuthority> cachedAuthorities =
        this.cache.getIfPresent(Objects.requireNonNull(jwt.getSubject()));
    if (cachedAuthorities != null) {
      // value present in cache
      log.debug("Resolved authorities (from cache): {}", cachedAuthorities);
      return cachedAuthorities;
    }

    log.debug("Fetching user-info for token subject: {}", jwt.getSubject());
    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
    try {
      Map<?, ?> map =
          restClient
              .get()
              .uri(this.userInfoUri)
              .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt.getTokenValue())
              .retrieve()
              .body(Map.class);

      log.debug("Response from user-info Endpoint: {}", map);
      Objects.requireNonNull(map, "Empty user-info response");
      if (map.containsKey(CLAIM_AUTHORITIES)) {
        authorities = asAuthorities(map.get(CLAIM_AUTHORITIES));
      }
      log.debug("Resolved Authorities (from /userinfo Endpoint): {}", authorities);
      // store
      this.cache.put(jwt.getSubject(), authorities);
    } catch (Exception e) {
      log.error(
          String.format(
              "Could not fetch user details from %s - user is granted NO authorities",
              this.userInfoUri),
          e);
    }

    return authorities;
  }

  private static List<SimpleGrantedAuthority> asAuthorities(Object object) {
    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
    if (object instanceof Collection<?> collectionWithAuthorities) {
      object = collectionWithAuthorities.toArray(new Object[0]);
    }
    if (ObjectUtils.isArray(object)) {
      authorities.addAll(
          Stream.of(((Object[]) object))
              .map(Object::toString)
              .map(SimpleGrantedAuthority::new)
              .toList());
    }
    return authorities;
  }
}
