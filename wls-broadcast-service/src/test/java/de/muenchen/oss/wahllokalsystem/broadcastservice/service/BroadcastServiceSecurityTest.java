package de.muenchen.oss.wahllokalsystem.broadcastservice.service;

import static de.muenchen.oss.wahllokalsystem.broadcastservice.TestConstants.SPRING_TEST_PROFILE;

import de.muenchen.oss.wahllokalsystem.broadcastservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.broadcastservice.domain.MessageRepository;
import de.muenchen.oss.wahllokalsystem.broadcastservice.rest.BroadcastMessageDTO;
import de.muenchen.oss.wahllokalsystem.broadcastservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.broadcastservice.utils.TestdataFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    classes = {MicroServiceApplication.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = {SPRING_TEST_PROFILE})
@Slf4j
public class BroadcastServiceSecurityTest {

  @Autowired MessageRepository messageRepository;

  @Autowired BroadcastService broadcastService;

  @BeforeEach
  void setup() {
    Assertions.assertThat(broadcastService).isNotNull();
    SecurityContextHolder.clearContext();
  }

  @AfterEach
  void teardown() {
    SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_MESSAGE);
    messageRepository.deleteAll();
    SecurityContextHolder.clearContext();
  }

  @Nested
  class Broadcast {

    @Test
    void should_throwAccessDeniedException_when_runWithDummyRole() {
      SecurityUtils.runWith("ROLE_DUMMY");
      Assertions.assertThatExceptionOfType(AccessDeniedException.class)
          .isThrownBy(() -> broadcastService.broadcast(null))
          .withMessageStartingWith("Access Denied");
    }

    @ParameterizedTest(name = "{index} - {1} missing")
    @MethodSource("getMissingAuthoritiesVariations")
    void should_throwAccessDeniedException_when_anyAuthorityMissing(
        final ArgumentsAccessor argumentsAccessor) {
      SecurityUtils.runWith(argumentsAccessor.get(0, String[].class));

      List<String> wahlbezirke = Arrays.asList("1", "2", "3", "4");
      BroadcastMessageDTO m1 = new BroadcastMessageDTO(wahlbezirke, "I should fail");
      Assertions.assertThatExceptionOfType(AccessDeniedException.class)
          .isThrownBy(() -> broadcastService.broadcast(m1))
          .withMessageStartingWith("Access Denied");
    }

    @Test
    void should_notThrowException_when_givenAllAuthorities() {
      SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_POST_BROADCAST);
      List<String> wahlbezirke = Arrays.asList("1", "2", "3", "4");
      BroadcastMessageDTO m1 = new BroadcastMessageDTO(wahlbezirke, "I should have access");

      Assertions.assertThatCode(() -> broadcastService.broadcast(m1)).doesNotThrowAnyException();
    }

    private static Stream<Arguments> getMissingAuthoritiesVariations() {
      return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(
          Authorities.ALL_AUTHORITIES_POST_BROADCAST);
    }
  }

  @Nested
  class GetOldestMessage {

    @Test
    void should_throwAccessDeniedException_when_runWithDummyRole() {
      SecurityUtils.runWith("ROLE_DUMMY");
      Assertions.assertThatExceptionOfType(AccessDeniedException.class)
          .isThrownBy(() -> broadcastService.getOldestMessage(null))
          .withMessageStartingWith("Access Denied");
    }

    @ParameterizedTest(name = "{index} - {1} missing")
    @MethodSource("getMissingAuthoritiesVariations")
    void should_throwAccessDeniedException_when_anyAuthorityMissing(
        final ArgumentsAccessor argumentsAccessor) {
      SecurityUtils.runWith(argumentsAccessor.get(0, String[].class));

      Assertions.assertThatExceptionOfType(AccessDeniedException.class)
          .isThrownBy(() -> broadcastService.getOldestMessage("wahlbezirkId"))
          .withMessageStartingWith("Access Denied");
    }

    private static Stream<Arguments> getMissingAuthoritiesVariations() {
      return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(
          Authorities.ALL_AUTHORITIES_GET_BROADCAST);
    }

    @Test
    void should_notThrowException_when_givenAllAuthorities() {
      SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_BROADCAST);
      Assertions.assertThatThrownBy(() -> broadcastService.getOldestMessage("wahlbezirkId"))
          .isNotInstanceOf(AccessDeniedException.class);
    }
  }

  @Nested
  class DeleteMessage {

    @Test
    void should_throwAccessDeniedException_when_runWithDummyRole() {
      SecurityUtils.runWith("ROLE_DUMMY");
      Assertions.assertThatExceptionOfType(AccessDeniedException.class)
          .isThrownBy(() -> broadcastService.deleteMessage(null))
          .withMessageStartingWith("Access Denied");
    }

    @ParameterizedTest(name = "{index} - {1} missing")
    @MethodSource("getMissingAuthoritiesVariations")
    void should_throwAccessDeniedException_when_anyAuthorityMissing(
        final ArgumentsAccessor argumentsAccessor) {
      SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_MESSAGE);
      val wahlbezirkID = "wahlbezirkId";
      val message =
          TestdataFactory.CreateMessageEntity.withCustomParams(
              wahlbezirkID, "nachricht", LocalDateTime.now());
      val savedMessage = messageRepository.save(message);
      SecurityContextHolder.clearContext();

      val userAuthorities =
          Arrays.stream(argumentsAccessor.get(0, String[].class))
              .map(SimpleGrantedAuthority::new)
              .toList();
      val userJwtAuthenticationToken =
          new JwtAuthenticationToken(
              new Jwt(
                  "tokenValue",
                  Instant.now(),
                  Instant.MAX,
                  Map.of("key", "value"),
                  Map.of("user_name", "USERNAME", "wahlbezirkID", wahlbezirkID)),
              userAuthorities);

      SecurityContextHolder.getContext().setAuthentication(userJwtAuthenticationToken);
      Assertions.assertThatExceptionOfType(AccessDeniedException.class)
          .isThrownBy(() -> broadcastService.deleteMessage(savedMessage.getOid().toString()))
          .withMessageStartingWith("Access Denied");
    }

    @Test
    void should_notThrowException_when_givenAllAuthorities() {
      SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_DELETE_BROADCAST);

      Assertions.assertThatCode(() -> broadcastService.deleteMessage("1-2-3-4-5"))
          .doesNotThrowAnyException();
    }

    private static Stream<Arguments> getMissingAuthoritiesVariations() {
      return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(
          Authorities.ALL_AUTHORITIES_DELETE_BROADCAST);
    }
  }
}
