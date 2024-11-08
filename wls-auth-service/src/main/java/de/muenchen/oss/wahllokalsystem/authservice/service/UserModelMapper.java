package de.muenchen.oss.wahllokalsystem.authservice.service;

import de.muenchen.oss.wahllokalsystem.authservice.domain.Authority;
import de.muenchen.oss.wahllokalsystem.authservice.domain.User;
import org.mapstruct.Mapper;

@Mapper
public interface UserModelMapper {

    UserModel toModel(User user);

    default String toModel(Authority authority) {
        if (authority == null) {
            return null;
        }
        return authority.getAuthority();
    }

}
