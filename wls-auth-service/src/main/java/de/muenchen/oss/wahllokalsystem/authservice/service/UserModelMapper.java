package de.muenchen.oss.wahllokalsystem.authservice.service;

import de.muenchen.oss.wahllokalsystem.authservice.domain.Authority;
import de.muenchen.oss.wahllokalsystem.authservice.domain.User;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserModelMapper {

    UserModel toModel(User user);

    default String toModel(Authority authority) {
        if (authority == null) {
            return null;
        }
        return authority.getAuthority();
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", constant = "dummy")
    @Mapping(target = "email", constant = "dummy@dummy.local")
    @Mapping(target = "accountNonLocked", constant = "true")
    @Mapping(target = "userEnabled", ignore = true)
    @Mapping(target = "wahlbezirkNummer", source = "wahllokalUserInfoModel.wahlbezirknummer")
    @Mapping(target = "wahlbezirksArt", source = "wahllokalUserInfoModel.wahlbezirksart")
    User toUser(String wahltagID, WahllokalUserInfoModel wahllokalUserInfoModel, Set<Authority> authorities, String pin, String username);
}
