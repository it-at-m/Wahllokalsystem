package de.muenchen.oss.wahllokalsystem.authservice.rest;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import de.muenchen.oss.wahllokalsystem.authservice.configuration.CacheConfig;
import de.muenchen.oss.wahllokalsystem.authservice.service.UserService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    private final UserDTOMapper userDTOMapper;

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    @Cacheable(value = CacheConfig.USER_CACHE, key = "#user.getName()")
    public ResponseEntity<UserDTO> user(Principal user) {
        log.debug("user info for '{}' called.", user.getName());
        val userServiceModel = userService.getUser(user.getName());
        return userServiceModel.map(userModel -> ResponseEntity.ok(userDTOMapper.toDTO(userModel))).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @RequestMapping(value = "/user/{username}/unlock", method = POST)
    @PreAuthorize("hasAuthority('ROLE_ADMIN_ADMIN')")
    @Transactional
    public ResponseEntity<?> unlockUser(@PathVariable("username") String username) {
        log.info("unlockUser for '{}' called.", username);
        userService.resetFailAttempts(username);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
