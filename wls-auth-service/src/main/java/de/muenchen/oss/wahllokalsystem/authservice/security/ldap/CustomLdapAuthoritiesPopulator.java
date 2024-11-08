package de.muenchen.oss.wahllokalsystem.authservice.security.ldap;

import de.muenchen.oss.wahllokalsystem.authservice.domain.Authority;
import de.muenchen.oss.wahllokalsystem.authservice.domain.UserRepository;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
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
        final Set<Authority> userAuthorities;

        val userByUsername = userRepository.findByUsername(username);
        if (userByUsername.isPresent()) {
            log.info("Found authorities for user {}.", username);
            userAuthorities = userByUsername.get().getAuthorities();
        } else {
            log.info("Authorities for User {} not found.", username);
            userAuthorities = Collections.emptySet();
        }
        log.info("User {} got Authorities: {}", username, authorityCollectionToString(userAuthorities));

        return userAuthorities.stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                .toList();
    }

    private String authorityCollectionToString(final Collection<Authority> authorities) {
        return authorities.stream()
                .map(Authority::getAuthority)
                .collect(Collectors.joining(","));
    }
}
