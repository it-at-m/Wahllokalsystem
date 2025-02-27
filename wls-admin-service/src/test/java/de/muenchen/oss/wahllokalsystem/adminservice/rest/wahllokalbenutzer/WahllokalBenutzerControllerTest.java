package de.muenchen.oss.wahllokalsystem.adminservice.rest.wahllokalbenutzer;

import de.muenchen.oss.wahllokalsystem.adminservice.service.wahllokalbenutzer.CsvFileModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahllokalbenutzer.WahllokalBenutzerService;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WahllokalBenutzerControllerTest {

    @Mock
    WahllokalBenutzerService wahllokalBenutzerService;

    @Mock
    CsvFileDTOMapper csvFileDTOMapper;

    @InjectMocks
    WahllokalBenutzerController unitUnderTest;

    @Nested
    class GenerateWahllokalbenutzer {

        @Test
        void should_callServiceWithWahltagID_when_calledWithWahltagID() {
            val wahltagID = "wahltagID";

            unitUnderTest.generateWahllokalbenutzer(wahltagID);

            Mockito.verify(wahllokalBenutzerService).generateWahllokalbenutzer(wahltagID);
        }

        @Test
        void should_returnCsvFileDTO_when_serviceIsCalledWithValidWahltagID() {
            val wahltagID = "wahltagID";

            val mockedServiceResponse = new CsvFileModel("mockedUsernameList");
            val mockedMappedServiceResponse = new CsvFileDTO("mockedUsernameList");

            Mockito.when(wahllokalBenutzerService.generateWahllokalbenutzer(wahltagID)).thenReturn(mockedServiceResponse);
            Mockito.when(csvFileDTOMapper.toDTO(mockedServiceResponse)).thenReturn(mockedMappedServiceResponse);

            val result = unitUnderTest.generateWahllokalbenutzer(wahltagID);

            Assertions.assertThat(result).isSameAs(mockedMappedServiceResponse);
        }
    }

    @Nested
    class ExportWahllokalBenutzer {

        @Test
        void should_callServiceWithWahltagID_when_calledWithWahltagID() {
            val wahltagID = "wahltagID";

            unitUnderTest.exportWahllokalBenutzer(wahltagID);

            Mockito.verify(wahllokalBenutzerService).exportWahllokalBenutzer(wahltagID);
        }

        @Test
        void should_returnCsvFileDTO_when_serviceIsCalledWithValidWahltagID() {
            val wahltagID = "wahltagID";

            val mockedServiceResponse = new CsvFileModel("mockedUsernameList");
            val mockedMappedServiceResponse = new CsvFileDTO("mockedUsernameList");

            Mockito.when(wahllokalBenutzerService.exportWahllokalBenutzer(wahltagID)).thenReturn(mockedServiceResponse);
            Mockito.when(csvFileDTOMapper.toDTO(mockedServiceResponse)).thenReturn(mockedMappedServiceResponse);

            val result = unitUnderTest.exportWahllokalBenutzer(wahltagID);

            Assertions.assertThat(result).isSameAs(mockedMappedServiceResponse);
        }
    }

    @Nested
    class DeleteWahllokalBenutzer {

        @Test
        void should_callServiceWithWahltagID_when_givenWahltagID() {
            val wahltagID = "wahltagID";

            unitUnderTest.deleteWahllokalBenutzer(wahltagID);

            Mockito.verify(wahllokalBenutzerService).deleteWahllokalBenutzer(wahltagID);
        }
    }
}
