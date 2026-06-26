package de.muenchen.oss.wahllokalsystem.authservice.rest.roles;

import de.muenchen.oss.wahllokalsystem.authservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("roles")
@RequiredArgsConstructor
public class RolesController {

  private final UserService userService;

  @Operation(description = "Lesen der Zuordnung technischer Rollenbezeichner zu fachlichen Rollen.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Eine Zuordnung ist vorhanden.",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = RoleMappingsDTO.class))
            })
      })
  @GetMapping("mappings")
  public RoleMappingsDTO getRoleMappings() {
    return new RoleMappingsDTO(
        userService.getSchriftfuehrerinAuthorityName(), userService.getAdminAuthorityName());
  }
}
