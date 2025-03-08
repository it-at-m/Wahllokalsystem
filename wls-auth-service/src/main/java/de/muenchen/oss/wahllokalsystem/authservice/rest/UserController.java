package de.muenchen.oss.wahllokalsystem.authservice.rest;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import de.muenchen.oss.wahllokalsystem.authservice.configuration.CacheConfig;
import de.muenchen.oss.wahllokalsystem.authservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    private final UserDTOMapper userDTOMapper;

    @Operation(
            description = "Liefert einen User.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Enthält den angegebenen user oder null falls keiner vorhanden ist"
                    )
            }
    )
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    @Cacheable(value = CacheConfig.USER_CACHE, key = "#a0.getName()")
    public UserDTO user(Principal user) {
        log.debug("user info for '{}' called.", user.getName());
        val userServiceModel = userService.getUser(user.getName());
        return userServiceModel.map(userDTOMapper::toDTO).orElse(null);
    }

    @Operation(
            description = "Freischalten eines users.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "User erfolgreich freigeschaltet."
                    )
            }
    )
    @RequestMapping(value = "/user/{username}/unlock", method = POST)
    @PreAuthorize("hasAuthority('ROLE_ADMIN_ADMIN')")
    @Transactional
    public void unlockUser(@PathVariable("username") String username) {
        log.info("unlockUser for '{}' called.", username);
        userService.resetFailAttempts(username);
    }
}
