package de.muenchen.oss.wahllokalsystem.authservice.rest;

import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.authservice.service.UserService;
import de.muenchen.oss.wahllokalsystem.authservice.service.UsersOfWahltagModel;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
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
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class WahllokalBenutzerRestControllerTest {

    @Mock
    UserDTOMapper userDTOMapper;

    @Mock
    UserService userService;

    @InjectMocks
    WahllokalBenutzerRestController unitUnderTest;

    @Nested
    class CreateAndExportWahllokalBenutzer {

        @Test
        void should_returnCreatedResponseWithUsersAsString_when_serviceWorkedSuccessfully() {
            val wahltagID = "wahltagID";
            val wahllokalUser = new WahllokalUserInfoDTO("wahlbezirknummer", LocalDate.now(), "wbzID", WahlbezirksartDTO.UWB, "nummer");

            val mockedWahllokalUserAsModel = new UsersOfWahltagModel(wahltagID, Collections.emptyList());
            val mockedServiceResponse = "users as string";

            Mockito.when(userDTOMapper.toModel(eq(wahltagID), eq(List.of(wahllokalUser)))).thenReturn(mockedWahllokalUserAsModel);
            Mockito.when(userService.generateWahllokalBenutzer(mockedWahllokalUserAsModel)).thenReturn(mockedServiceResponse);

            val result = unitUnderTest.createAndExportWahllokalBenutzer(wahltagID, List.of(wahllokalUser));

            val expectedResult = ResponseEntity.status(HttpStatus.CREATED).body(mockedServiceResponse);
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        void should_returnInternalServerErrorResponse_when_exceptionInServiceOccurred() {
            val wahltagID = "wahltagID";
            val wahllokalUser = new WahllokalUserInfoDTO("wahlbezirknummer", LocalDate.now(), "wbzID", WahlbezirksartDTO.UWB, "nummer");

            val mockedWahllokalUserAsModel = new UsersOfWahltagModel(wahltagID, Collections.emptyList());
            val mockedServiceException = new RuntimeException("mocked exception");

            Mockito.when(userDTOMapper.toModel(eq(wahltagID), eq(List.of(wahllokalUser)))).thenReturn(mockedWahllokalUserAsModel);
            Mockito.doThrow(mockedServiceException).when(userService).generateWahllokalBenutzer(mockedWahllokalUserAsModel);

            val result = unitUnderTest.createAndExportWahllokalBenutzer(wahltagID, List.of(wahllokalUser));

            val expectedResult = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("createAndExportWahllokalBenutzer error");
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    class ExportWahllokalBenutzer {

        @Test
        void should_returnCreatedResponseWithExistingUsersAsString_when_serviceWorkedSuccessfully() {
            val wahltagID = "wahltagID";

            val mockedServiceResponse = "exported users";
            Mockito.when(userService.exportWahllokalBenutzer(wahltagID)).thenReturn(mockedServiceResponse);

            val result = unitUnderTest.exportWahllokalBenutzer(wahltagID);

            val expectedResult = ResponseEntity.status(HttpStatus.CREATED).body(mockedServiceResponse);
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        void should_returnInternalServerErrorResponse_when_exceptionInServiceOccurred() {
            val wahltagID = "wahltagID";

            val mockedServiceException = new RuntimeException("mocked exception");
            Mockito.doThrow(mockedServiceException).when(userService).exportWahllokalBenutzer(wahltagID);

            val result = unitUnderTest.exportWahllokalBenutzer(wahltagID);

            val expectedResult = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("exportWahllokalBenutzer error");
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

    }

    @Nested
    class DeleteWahllokalBenutzer {

        @Test
        void should_callServiceWithWahltagID_when_controllerIsCalled() {
            val wahltagID = "wahltagID";

            unitUnderTest.deleteWahllokalBenutzer(wahltagID);

            Mockito.verify(userService).deleteWahllokalBenutzer(wahltagID);
        }

    }

}
