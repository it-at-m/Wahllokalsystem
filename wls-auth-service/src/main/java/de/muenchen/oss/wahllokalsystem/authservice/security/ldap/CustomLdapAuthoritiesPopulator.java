package de.muenchen.oss.wahllokalsystem.authservice.security.ldap;

import de.muenchen.oss.wahllokalsystem.authservice.domain.User;
import de.muenchen.oss.wahllokalsystem.authservice.domain.UserRepository;
import java.util.Collection;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomLdapAuthoritiesPopulator implements LdapAuthoritiesPopulator {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Collection<? extends GrantedAuthority> getGrantedAuthorities(
            DirContextOperations userData, String username) {
        val userByUsername = userRepository.findByUsername(username);
        if (userByUsername.isPresent()) {
            log.info("Found authorities for user {}.", username);
        } else {
            log.info("Authorities for User {} not found.", username);
        }

        val userAuthorities = userByUsername.map(User::getAuthorities).orElse(Collections.emptySet());

        val result = userAuthorities.stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                .toList();
        log.info("User {} got Authorities: {}", username, result);
        return result;
    }
}
