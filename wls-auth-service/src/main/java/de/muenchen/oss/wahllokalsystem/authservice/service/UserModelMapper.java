package de.muenchen.oss.wahllokalsystem.authservice.service;

import de.muenchen.oss.wahllokalsystem.authservice.domain.Authority;
import de.muenchen.oss.wahllokalsystem.authservice.domain.Permission;
import de.muenchen.oss.wahllokalsystem.authservice.domain.User;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Mapper
public interface UserModelMapper {

    UserModel toModel(User user);

    default Set<String> permissionsOfAuthoritiesToAuthorities(Set<Authority> authorities) {
        if (authorities == null) {
            return null;
        }

        return authorities.stream()
                .flatMap(authority -> authority.getPermissions().stream())
                .map(Permission::getPermission)
                .collect(Collectors.toSet());
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", constant = "dummy")
    @Mapping(target = "email", constant = "dummy@dummy.local")
    @Mapping(target = "accountNonLocked", constant = "true")
    @Mapping(target = "userEnabled", ignore = true)
    @Mapping(target = "wahlbezirkNummer", source = "wahllokalUserInfoModel.wahlbezirknummer")
    @Mapping(target = "wahlbezirksArt", source = "wahllokalUserInfoModel.wahlbezirksart")
    User toUser(String wahltagID, WahllokalUserInfoModel wahllokalUserInfoModel, Set<Authority> authorities, String pin, String username);

    default org.springframework.security.core.userdetails.User toSpringSecurityUser(User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                Optional.ofNullable(user.getPassword()).orElse(StringUtils.EMPTY),
                true,
                true,
                true,
                user.isAccountNonLocked(),
                authoritiesToGrantedAuthorities(user.getAuthorities()));
    }

    default Set<GrantedAuthority> authoritiesToGrantedAuthorities(final Set<Authority> authorities) {
        if (authorities == null) {
            return Collections.emptySet();
        }
        return authorities.stream().map(authority -> new SimpleGrantedAuthority(authority.getAuthority())).collect(Collectors.toSet());
    }
}
