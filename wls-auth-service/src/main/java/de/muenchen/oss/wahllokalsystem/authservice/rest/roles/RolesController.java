package de.muenchen.oss.wahllokalsystem.authservice.rest.roles;

import de.muenchen.oss.wahllokalsystem.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("roles")
@RequiredArgsConstructor
public class RolesController {

  private final UserService userService;

  @GetMapping("mappings")
  public RoleMappingsDTO getRoleMappings() {
    return new RoleMappingsDTO(
        userService.getSchriftfuehrerinAuthorityName(), userService.getAdminAuthorityName());
  }
}
