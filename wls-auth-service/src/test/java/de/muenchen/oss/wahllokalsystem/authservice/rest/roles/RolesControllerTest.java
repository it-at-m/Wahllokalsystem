package de.muenchen.oss.wahllokalsystem.authservice.rest.roles;

import de.muenchen.oss.wahllokalsystem.authservice.service.UserService;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RolesControllerTest {

  @Mock UserService userService;

  @InjectMocks RolesController unitUnderTest;

  @Nested
  class GetRoleMappings {

    @Test
    void should_returnRoleMappings_when_called() {
      val schriftfueherungRole = "schriftfuehrung";
      val adminRole = "admin";
      val erfassungsteamRole = "erfassungsteam";

      Mockito.when(userService.getSchriftfuehrungAuthorityName()).thenReturn(schriftfueherungRole);
      Mockito.when(userService.getAdminAuthorityName()).thenReturn(adminRole);
      Mockito.when(userService.getErfassungsteamAuthorityName()).thenReturn(erfassungsteamRole);

      val result = unitUnderTest.getRoleMappings();

      val expectedResult = new RoleMappingsDTO(schriftfueherungRole, adminRole, erfassungsteamRole);
      Assertions.assertThat(result).isEqualTo(expectedResult);
    }
  }
}
