package de.muenchen.oss.wahllokalsystem.authservice.service;

import de.muenchen.oss.wahllokalsystem.authservice.domain.User;
import org.mapstruct.Mapper;

@Mapper
public interface UserModelMapper {

    // TODO: custom mapping
    // @Mapping(target = "authorities", source = "authorities.authority")
    UserModel toModel(User user);

}
