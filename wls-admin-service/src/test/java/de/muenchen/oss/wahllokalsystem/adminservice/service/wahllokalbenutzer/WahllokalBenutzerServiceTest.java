package de.muenchen.oss.wahllokalsystem.adminservice.service.wahllokalbenutzer;

import de.muenchen.oss.wahllokalsystem.adminservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.adminservice.service.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.common.WahlbezirkModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.common.WahlbezirkeClient;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.time.LocalDate;
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

@ExtendWith(MockitoExtension.class)
class WahllokalBenutzerServiceTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @Mock
    WahllokalBenutzerValidator wahllokalBenutzerValidator;

    @Mock
    WahllokalBenutzerClient wahllokalBenutzerClient;

    @Mock
    WahlbezirkeClient wahlbezirkeClient;

    @InjectMocks
    WahllokalBenutzerService unitUnderTest;

    @Nested
    class GenerateWahllokalbenutzer {

        @Test
        void should_throwException_when_wahltagIDIsInvalid() {
            val wahltagID = "wahltagID";

            val mockedValidationException = FachlicheWlsException.withCode("000").buildWithMessage("validation failed");

            Mockito.doThrow(mockedValidationException).when(wahllokalBenutzerValidator).validWahltagIDParamOrThrow(wahltagID);

            Assertions.assertThatThrownBy(() -> unitUnderTest.generateWahllokalbenutzer(wahltagID)).isSameAs(mockedValidationException);
        }

        @Test
        void should_throwException_when_noWahlbezirkWithWahltagIDExists() {
            val wahltagID = "wahltagID";

            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("message");

            Mockito.doNothing().when(wahllokalBenutzerValidator).validWahltagIDParamOrThrow(wahltagID);
            Mockito.when(wahlbezirkeClient.getWahlbezirke(wahltagID)).thenReturn(null);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.INVALID_ARGUMENT)).thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.generateWahllokalbenutzer(wahltagID)).isSameAs(mockedWlsException);
        }

        @Test
        void should_generateAndExportWahllokalBenutzer_when_givenWahltagIDAndWahlbezirke() {
            val wahltagID = "wahltagID";
            val mockedWahlbezirke = getMockWahlbezirke();
            val userModels = getListOfWahlLokalBenutzerModels();

            Mockito.doNothing().when(wahllokalBenutzerValidator).validWahltagIDParamOrThrow(wahltagID);
            Mockito.when(wahlbezirkeClient.getWahlbezirke(wahltagID)).thenReturn(mockedWahlbezirke);
            Mockito.when(wahllokalBenutzerClient.generateAndExportWahllokalBenutzer(wahltagID, userModels)).thenReturn(mockedWahllokalBenutzer);

            val expectedGeneratedWahllokalbenutzer = new CsvFileModel(mockedWahllokalBenutzer);
            val result = unitUnderTest.generateWahllokalbenutzer(wahltagID);

            Assertions.assertThat(result).isEqualTo(expectedGeneratedWahllokalbenutzer);
        }
    }

    @Nested
    class ExportWahllokalBenutzer {

        @Test
        void should_throwException_when_wahltagIDIsInvalid() {
            val wahltagID = "wahltagID";

            val mockedValidationException = FachlicheWlsException.withCode("000").buildWithMessage("validation failed");

            Mockito.doThrow(mockedValidationException).when(wahllokalBenutzerValidator).validWahltagIDParamOrThrow(wahltagID);

            Assertions.assertThatThrownBy(() -> unitUnderTest.exportWahllokalBenutzer(wahltagID)).isSameAs(mockedValidationException);
        }

        @Test
        void should_exportWahllokalBenutzer_when_givenWahltagID() {
            val wahltagID = "wahltagID";

            Mockito.doNothing().when(wahllokalBenutzerValidator).validWahltagIDParamOrThrow(wahltagID);
            Mockito.when(wahllokalBenutzerClient.exportWahllokalBenutzer(wahltagID)).thenReturn(mockedWahllokalBenutzer);

            val expectedWahllokalbenutzer = new CsvFileModel(mockedWahllokalBenutzer);
            val result = unitUnderTest.exportWahllokalBenutzer(wahltagID);

            Assertions.assertThat(result).isEqualTo(expectedWahllokalbenutzer);
        }
    }

    @Nested
    class DeleteWahllokalBenutzer {

        @Test
        void should_throwException_when_wahltagIDIsInvalid() {
            val wahltagID = "wahltagID";

            val mockedValidationException = FachlicheWlsException.withCode("000").buildWithMessage("validation failed");

            Mockito.doThrow(mockedValidationException).when(wahllokalBenutzerValidator).validWahltagIDParamOrThrow(wahltagID);

            Assertions.assertThatThrownBy(() -> unitUnderTest.deleteWahllokalBenutzer(wahltagID)).isSameAs(mockedValidationException);
        }

        @Test
        void should_deleteWahllokalBenutzer_when_givenWahltagID() {
            val wahltagID = "wahltagID";
            Mockito.doNothing().when(wahllokalBenutzerValidator).validWahltagIDParamOrThrow(wahltagID);
            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.deleteWahllokalBenutzer(wahltagID));
            Mockito.verify(wahllokalBenutzerClient).deleteWahllokalBenutzer(wahltagID);
        }
    }

    private final String mockedWahllokalBenutzer = "ftpprs-1503\r\n" + "c94m3c-0365\r\n" + "v7jnkr-2161\r\n";

    private List<WahlbezirkModel> getMockWahlbezirke(){
        return List.of(
                new WahlbezirkModel("wahlbezirkID1_0", WahlbezirkArtModel.UWB, "1503", LocalDate.now(), "0", "wahlID0"),
                new WahlbezirkModel("wahlbezirkID2_0", WahlbezirkArtModel.BWB, "0365", LocalDate.now(), "0", "wahlID0"),
                new WahlbezirkModel("wahlbezirkID3_0", WahlbezirkArtModel.UWB, "2161", LocalDate.now(), "0", "wahlID0"),

                new WahlbezirkModel("wahlbezirkID1_1", WahlbezirkArtModel.UWB, "1503", LocalDate.now(), "1", "wahlID1"),
                new WahlbezirkModel("wahlbezirkID1_2", WahlbezirkArtModel.UWB, "1503", LocalDate.now(), "2", "wahlID2"),

                new WahlbezirkModel("wahlbezirkID2_1", WahlbezirkArtModel.BWB, "0365", LocalDate.now(), "1", "wahlID1"),
                new WahlbezirkModel("wahlbezirkID2_2", WahlbezirkArtModel.BWB, "0365", LocalDate.now(), "2", "wahlID2"),

                new WahlbezirkModel("wahlbezirkID3_1", WahlbezirkArtModel.UWB, "2161", LocalDate.now(), "1", "wahlID1"),
                new WahlbezirkModel("wahlbezirkID3_2", WahlbezirkArtModel.UWB, "2161", LocalDate.now(), "2", "wahlID2")
        );
    }

    private List<WahllokalBenutzerModel> getListOfWahlLokalBenutzerModels() {
        val wahltag = LocalDate.now();
        val wahllokalBenutzerModel_1 = new WahllokalBenutzerModel(
                "wahlbezirkID1_0",
                "1503",
                wahltag,
                WahlbezirkArtModel.UWB,
                List.of(
                        new TripleOfWahlbezirkIDWahlnummerWahlIDModel("wahlbezirkID1_0", "0", "wahlID0"),
                        new TripleOfWahlbezirkIDWahlnummerWahlIDModel("wahlbezirkID1_1", "1", "wahlID1"),
                        new TripleOfWahlbezirkIDWahlnummerWahlIDModel("wahlbezirkID1_2", "2", "wahlID2")
                ));
        val wahllokalBenutzerModel_2 = new WahllokalBenutzerModel(
                "wahlbezirkID2_0",
                "0365",
                wahltag,
                WahlbezirkArtModel.BWB,
                List.of(
                        new TripleOfWahlbezirkIDWahlnummerWahlIDModel("wahlbezirkID2_0", "0", "wahlID0"),
                        new TripleOfWahlbezirkIDWahlnummerWahlIDModel("wahlbezirkID2_1", "1", "wahlID1"),
                        new TripleOfWahlbezirkIDWahlnummerWahlIDModel("wahlbezirkID2_2", "2", "wahlID2")
                ));
        val wahllokalBenutzerModel_3 = new WahllokalBenutzerModel(
                "wahlbezirkID3_0",
                "2161",
                wahltag,
                WahlbezirkArtModel.UWB,
                List.of(
                        new TripleOfWahlbezirkIDWahlnummerWahlIDModel("wahlbezirkID3_0", "0", "wahlID0"),
                        new TripleOfWahlbezirkIDWahlnummerWahlIDModel("wahlbezirkID3_1", "1", "wahlID1"),
                        new TripleOfWahlbezirkIDWahlnummerWahlIDModel("wahlbezirkID3_2", "2", "wahlID2")
                ));
        return List.of(wahllokalBenutzerModel_1, wahllokalBenutzerModel_2, wahllokalBenutzerModel_3);
    }
}