package de.muenchen.oss.wahllokalsystem.authservice.service;

import de.muenchen.oss.wahllokalsystem.authservice.domain.LoginAttempt;
import org.mapstruct.Mapper;

@Mapper
public interface LoginAttemptModelMapper {

    LoginAttemptModel toModel(LoginAttempt loginAttempt);
}
