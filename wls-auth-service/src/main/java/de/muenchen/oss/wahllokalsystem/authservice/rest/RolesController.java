package de.muenchen.oss.wahllokalsystem.authservice.rest;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("roles")
public class RolesController {

  @GetMapping("mappings")
  public Map<RoleTypes, String> getRoleMappings() {
    return Map.of();
  }
}
