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
      val schriftfueherinRole = "schriftfueherin";
      val adminRole = "admin";

      Mockito.when(userService.getSchriftfuehrerinAuthorityName()).thenReturn(schriftfueherinRole);
      Mockito.when(userService.getAdminAuthorityName()).thenReturn(adminRole);

      val result = unitUnderTest.getRoleMappings();

      val expectedResult = new RoleMappingsDTO(schriftfueherinRole, adminRole);
      Assertions.assertThat(result).isEqualTo(expectedResult);
    }
  }
}
