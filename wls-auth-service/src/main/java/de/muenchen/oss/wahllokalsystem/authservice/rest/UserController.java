package de.muenchen.oss.wahllokalsystem.authservice.rest;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public ResponseEntity<UserDTO> user(Principal user) {
        log.debug("user info for '{}' called.", user.getName());
        // TODO: use service
        //final User savedUser = userRepository.findFirstByUsername(user.getName());
        //UserDTO dto = userMapper.userToUserDTO(savedUser);
        //return new ResponseEntity<>(dto, HttpStatus.OK);
        return null;
    }

    @RequestMapping(value = "/user/{username}/unlock", method = POST)
    @PreAuthorize("hasAuthority('ROLE_ADMIN_ADMIN')")
    @Transactional
    public ResponseEntity<?> unlockUser(@PathVariable("username") String username) {
        log.info("unlockUser for '{}' called.", username);
        // TODO: use service
        //loginAttemptsRepository.resetFailAttempts(username);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
