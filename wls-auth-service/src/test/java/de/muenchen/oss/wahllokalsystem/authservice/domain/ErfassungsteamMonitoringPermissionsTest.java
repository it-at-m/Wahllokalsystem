package de.muenchen.oss.wahllokalsystem.authservice.domain;

import static org.assertj.core.api.Assertions.assertThat;

import de.muenchen.oss.wahllokalsystem.authservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.authservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.authservice.configuration.Profiles;
import java.util.Set;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles({TestConstants.SPRING_TEST_PROFILE, Profiles.DUMMY_CLIENTS})
class ErfassungsteamMonitoringPermissionsTest {

  private static final String ERFASSUNGSTEAM = "ERFASSUNGSTEAM";
  private static final String PERM_LASTSEEN = "Monitoring_BUSINESSACTION_PostLastSeen";
  private static final String PERM_LETZTE_ABMELDUNG =
      "Monitoring_BUSINESSACTION_PostLetzteAbmeldung";
  private static final String PERM_SAVE_WAHLLOKALZUSTAND =
      "aoueai_BUSINESSACTION_SaveWahllokalZustand";

  @Autowired private AuthorityRepository authorityRepository;

  @Test
  void should_have_lastSeen_and_letzte_abmeldung_permissions_when_erfassungsteam() {
    val authority = authorityRepository.findByAuthority(ERFASSUNGSTEAM);
    assertThat(authority)
        .withFailMessage("Authority '%s' should exist", ERFASSUNGSTEAM)
        .isPresent();

    val permissions = authority.get().getPermissions();
    val permissionNames =
        permissions.stream()
            .map(Permission::getPermission)
            .collect(java.util.stream.Collectors.toSet());

    assertThat(permissionNames)
        .withFailMessage("'%s' should contain both monitoring permissions", ERFASSUNGSTEAM)
        .containsAll(Set.of(PERM_LASTSEEN, PERM_LETZTE_ABMELDUNG, PERM_SAVE_WAHLLOKALZUSTAND));
  }
}
