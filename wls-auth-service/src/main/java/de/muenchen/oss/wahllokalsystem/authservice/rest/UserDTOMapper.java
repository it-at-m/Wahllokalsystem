package de.muenchen.oss.wahllokalsystem.authservice.rest;

import de.muenchen.oss.wahllokalsystem.authservice.service.UsersOfWahltagModel;
import java.util.Collection;
import de.muenchen.oss.wahllokalsystem.authservice.service.UserModel;
import org.mapstruct.Mapper;

@Mapper
public interface UserDTOMapper {

    UserDTO toDTO(UserModel userModel);
    UsersOfWahltagModel toModel(String wahltagID, Collection<WahllokalUserInfoDTO> users);
}
