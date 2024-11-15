package de.muenchen.oss.wahllokalsystem.authservice.rest;

import de.muenchen.oss.wahllokalsystem.authservice.service.UserModel;
import de.muenchen.oss.wahllokalsystem.authservice.service.UserService;
import java.security.Principal;
import java.util.Optional;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    UserDTOMapper userDTOMapper;

    @Mock
    UserService userService;

    @InjectMocks
    UserController unitUnderTest;

    @Nested
    class User {

        @Test
        void should_returnOKAndUserDTO_when_serviceWasSuccessful() {
            val mockedPrincipalParameter = Mockito.mock(Principal.class);
            val mockedMappedUserModel = UserModel.builder().build();
            Mockito.when(userService.getUser(mockedPrincipalParameter.getName())).thenReturn(Optional.of(mockedMappedUserModel));

            val mockedMapperUserToDTO = UserDTO.builder().build();
            Mockito.when(userDTOMapper.toDTO(mockedMappedUserModel)).thenReturn(mockedMapperUserToDTO);

            val result = unitUnderTest.user(mockedPrincipalParameter);

            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            Assertions.assertThat(result.getBody()).isEqualTo(mockedMapperUserToDTO);
        }

        @Test
        void should_returnOKAndNullBody_when_exceptionInServiceOccurred() {
            val mockedPrincipalParameter = Mockito.mock(Principal.class);
            Mockito.when(userService.getUser(mockedPrincipalParameter.getName())).thenReturn(Optional.empty());

            val result = unitUnderTest.user(mockedPrincipalParameter);

            Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            Assertions.assertThat(result.getBody()).isNull();
        }
    }

    @Nested
    class UnlockUser {

        @Test
        void should_returnOK_when_serviceWasSuccessful() {
            val username = "Hansi";
            Mockito.doNothing().when(userService).resetFailAttempts(username);

            unitUnderTest.unlockUser(username);

            Mockito.verify(userService).resetFailAttempts(username);
        }

        @Test
        void should_failWithIllegalArgumentException_when_serviceWasNotSuccessful() {
            val username = "Hansi";
            Mockito.doThrow(new IllegalArgumentException("User with username " + username + " not found.")).when(userService).resetFailAttempts(username);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.unlockUser(username));
        }

    }
}
