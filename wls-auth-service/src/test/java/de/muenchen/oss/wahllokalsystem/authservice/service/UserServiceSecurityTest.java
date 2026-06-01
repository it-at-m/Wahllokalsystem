package de.muenchen.oss.wahllokalsystem.authservice.service;

import de.muenchen.oss.wahllokalsystem.authservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.authservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.authservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.authservice.domain.Authority;
import de.muenchen.oss.wahllokalsystem.authservice.domain.AuthorityRepository;
import de.muenchen.oss.wahllokalsystem.authservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.authservice.utils.TestCrudUserRepository;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.util.Collections;
import java.util.Optional;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles({TestConstants.SPRING_TEST_PROFILE, Profiles.DUMMY_CLIENTS})
class UserServiceSecurityTest {

  @Autowired UserService unitUnderTest;

  @Autowired TestCrudUserRepository userRepository;

  @MockitoBean AuthorityRepository authorityRepository;

  @AfterEach
  void teardown() {
    userRepository.deleteAll();
  }

  @Nested
  class GenerateWahllokalBenutzer {

    @Test
    void should_permitAccess_when_authorityIsGiven() {
      val users = new UsersOfWahltagModel("wahltagID", Collections.emptyList());
      Mockito.when(authorityRepository.findByAuthority(Mockito.any()))
          .thenReturn(
              Optional.of(
                  new Authority("authority", Collections.emptySet(), Collections.emptySet())));

      SecurityUtils.runWith(Authorities.ROLE_ADMIN);
      Assertions.assertThatNoException()
          .isThrownBy(() -> unitUnderTest.generateWahllokalBenutzer(users));
    }

    @Test
    void should_throwAccessDeniedException_when_authorityIsMissing() {
      val users = new UsersOfWahltagModel("wahltagID", Collections.emptyList());

      SecurityUtils.runWith();
      Assertions.assertThatException()
          .isThrownBy(() -> unitUnderTest.generateWahllokalBenutzer(users))
          .isInstanceOf(AccessDeniedException.class);
    }
  }

  @Nested
  class ExportWahllokalBenutzer {

    @Test
    void should_permitAccess_when_authorityIsGiven() {
      SecurityUtils.runWith(Authorities.ROLE_ADMIN);
      Assertions.assertThatNoException()
          .isThrownBy(() -> unitUnderTest.exportWahllokalBenutzer("wahltagID"));
    }

    @Test
    void should_throwAccessDeniedException_when_authorityIsMissing() {
      SecurityUtils.runWith();
      Assertions.assertThatException()
          .isThrownBy(() -> unitUnderTest.exportWahllokalBenutzer("wahltagID"))
          .isInstanceOf(AccessDeniedException.class);
    }
  }

  @Nested
  class DeleteWahllokalBenutzer {

    @Test
    void should_permitAccess_when_authorityIsGiven() {
      SecurityUtils.runWith(Authorities.ROLE_ADMIN);
      Assertions.assertThatNoException()
          .isThrownBy(() -> unitUnderTest.deleteWahllokalBenutzer("wahltagID"));
    }

    @Test
    void should_throwAccessDeniedException_when_authorityIsMissing() {
      SecurityUtils.runWith();
      Assertions.assertThatException()
          .isThrownBy(() -> unitUnderTest.deleteWahllokalBenutzer("wahltagID"))
          .isInstanceOf(AccessDeniedException.class);
    }
  }
}
