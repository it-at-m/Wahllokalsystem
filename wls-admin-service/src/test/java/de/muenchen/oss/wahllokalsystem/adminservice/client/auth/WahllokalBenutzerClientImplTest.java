package de.muenchen.oss.wahllokalsystem.adminservice.client.auth;

import de.muenchen.oss.wahllokalsystem.adminservice.eai.auth.client.WahllokalBenutzerControllerApi;
import de.muenchen.oss.wahllokalsystem.adminservice.eai.auth.model.WahllokalUserInfoDTO;
import de.muenchen.oss.wahllokalsystem.adminservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.adminservice.service.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahllokalbenutzer.TripleOfWahlbezirkIDWahlnummerWahlIDModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahllokalbenutzer.WahllokalBenutzerModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Nested;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WahllokalBenutzerClientImplTest {

    @Mock
    WahllokalBenutzerControllerApi wahllokalBenutzerControllerApi;

    @Mock
    ExceptionFactory exceptionFactory;

    @Mock
    WahllokalBenutzerClientMapper wahllokalBenutzerClientMapper;

    @InjectMocks
    WahllokalBenutzerClientImpl unitUnderTest;

    @Nested
    class GenerateAndExportWahllokalBenutzer {

        @Test
        void should_verifyCreateAndExportWahllokalBenutzer_when_wahllokalBenutzerModelsIsGiven() {
            val listOfWahllokalbenutzerModels = getListOfWahlLokalBenutzerModels();
            val mockedListOfWahllokalUserInfoDTOs = getListOfWahlLokalUserInfoDTO();
            Mockito.when(wahllokalBenutzerClientMapper.toListOfWahllokalUserInfoDTO(listOfWahllokalbenutzerModels))
                    .thenReturn(mockedListOfWahllokalUserInfoDTOs);
            unitUnderTest.generateAndExportWahllokalBenutzer("wahltagID", listOfWahllokalbenutzerModels);
            Mockito.verify(wahllokalBenutzerControllerApi).createAndExportWahllokalBenutzer("wahltagID", mockedListOfWahllokalUserInfoDTOs);
        }

        @Test
        void should_rethrowWlsException_when_wlsExceptionIsThrownFromWahllokalBenutzerControllerApi() {
            val listOfWahllokalbenutzerModels = getListOfWahlLokalBenutzerModels();
            val mockedListOfWahllokalUserInfoDTOs = getListOfWahlLokalUserInfoDTO();
            Mockito.when(wahllokalBenutzerClientMapper.toListOfWahllokalUserInfoDTO(listOfWahllokalbenutzerModels))
                    .thenReturn(mockedListOfWahllokalUserInfoDTOs);

            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("communication with WahllokalBenutzerControllerApi api failed");

            Mockito.doThrow(mockedWlsException).when(wahllokalBenutzerControllerApi).createAndExportWahllokalBenutzer("wahltagID",
                    mockedListOfWahllokalUserInfoDTOs);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.generateAndExportWahllokalBenutzer("wahltagID", listOfWahllokalbenutzerModels))
                    .isSameAs(mockedWlsException);
        }

        @Test
        void should_throwTechnischeWlsException_when_nonWlsExceptionIsThrownFromWahllokalBenutzerControllerApi() {
            val listOfWahllokalbenutzerModels = getListOfWahlLokalBenutzerModels();
            val mockedListOfWahllokalUserInfoDTOs = getListOfWahlLokalUserInfoDTO();
            Mockito.when(wahllokalBenutzerClientMapper.toListOfWahllokalUserInfoDTO(listOfWahllokalbenutzerModels))
                    .thenReturn(mockedListOfWahllokalUserInfoDTOs);

            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("communication with WahllokalBenutzerControllerApi api failed");

            Mockito.doThrow(new RuntimeException("api call failed")).when(wahllokalBenutzerControllerApi).createAndExportWahllokalBenutzer("wahltagID",
                    mockedListOfWahllokalUserInfoDTOs);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_AUTH))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.generateAndExportWahllokalBenutzer("wahltagID", listOfWahllokalbenutzerModels))
                    .isSameAs(mockedWlsException);
        }
    }

    @Nested
    class ExportWahllokalBenutzer {

        @Test
        void should_rethrowWlsException_when_wlsExceptionIsThrownFromWahllokalBenutzerControllerApi() {
            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("communication with WahllokalBenutzerControllerApi api failed");

            Mockito.doThrow(mockedWlsException).when(wahllokalBenutzerControllerApi).exportWahllokalBenutzer("wahltagID");

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.exportWahllokalBenutzer("wahltagID")).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwTechnischeWlsException_when_nonWlsExceptionIsThrownFromWahllokalBenutzerControllerApi() {
            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("communication with WahllokalBenutzerControllerApi api failed");

            Mockito.doThrow(new RuntimeException("api call failed")).when(wahllokalBenutzerControllerApi).exportWahllokalBenutzer("wahltagID");
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_AUTH))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.exportWahllokalBenutzer("wahltagID")).isSameAs(mockedWlsException);
        }
    }

    @Nested
    class DeleteWahllokalBenutzer {

        @Test
        void should_rethrowWlsException_when_wlsExceptionIsThrownFromWahllokalBenutzerControllerApi() {
            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("communication with WahllokalBenutzerControllerApi api failed");

            Mockito.doThrow(mockedWlsException).when(wahllokalBenutzerControllerApi).deleteWahllokalBenutzer("wahltagID");

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.deleteWahllokalBenutzer("wahltagID")).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwTechnischeWlsException_when_nonWlsExceptionIsThrownFromWahllokalBenutzerControllerApi() {
            val mockedWlsException = TechnischeWlsException.withCode("000").buildWithMessage("communication with WahllokalBenutzerControllerApi api failed");

            Mockito.doThrow(new RuntimeException("api call failed")).when(wahllokalBenutzerControllerApi).deleteWahllokalBenutzer("wahltagID");
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_AUTH))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.deleteWahllokalBenutzer("wahltagID")).isSameAs(mockedWlsException);
        }
    }

    private List<WahllokalBenutzerModel> getListOfWahlLokalBenutzerModels() {
        val wahltag = LocalDate.now();
        val wahllokalBenutzerModel_1 = new WahllokalBenutzerModel(
                "wahlbezirkID1_0",
                "0001",
                wahltag,
                WahlbezirkArtModel.UWB,
                List.of(
                        new TripleOfWahlbezirkIDWahlnummerWahlIDModel("wahlbezirkID1_0", "0", "wahlID0"),
                        new TripleOfWahlbezirkIDWahlnummerWahlIDModel("wahlbezirkID1_1", "1", "wahlID1"),
                        new TripleOfWahlbezirkIDWahlnummerWahlIDModel("wahlbezirkID1_2", "2", "wahlID2")));
        val wahllokalBenutzerModel_2 = new WahllokalBenutzerModel(
                "wahlbezirkID2_0",
                "0002",
                wahltag,
                WahlbezirkArtModel.BWB,
                List.of(
                        new TripleOfWahlbezirkIDWahlnummerWahlIDModel("wahlbezirkID2_0", "0", "wahlID0"),
                        new TripleOfWahlbezirkIDWahlnummerWahlIDModel("wahlbezirkID2_1", "1", "wahlID1"),
                        new TripleOfWahlbezirkIDWahlnummerWahlIDModel("wahlbezirkID2_2", "2", "wahlID2")));
        val wahllokalBenutzerModel_3 = new WahllokalBenutzerModel(
                "wahlbezirkID3_0",
                "0003",
                wahltag,
                WahlbezirkArtModel.UWB,
                List.of(
                        new TripleOfWahlbezirkIDWahlnummerWahlIDModel("wahlbezirkID3_0", "0", "wahlID0"),
                        new TripleOfWahlbezirkIDWahlnummerWahlIDModel("wahlbezirkID3_1", "1", "wahlID1"),
                        new TripleOfWahlbezirkIDWahlnummerWahlIDModel("wahlbezirkID3_2", "2", "wahlID2")));
        return List.of(wahllokalBenutzerModel_1, wahllokalBenutzerModel_2, wahllokalBenutzerModel_3);
    }

    private List<WahllokalUserInfoDTO> getListOfWahlLokalUserInfoDTO() {
        val wahltag = LocalDate.now();
        val wahllokalUserInfoDTO_1 = new WahllokalUserInfoDTO();
        wahllokalUserInfoDTO_1.setWahlbezirkID("wahlbezirkID1_0");
        wahllokalUserInfoDTO_1.setWahlbezirknummer("0001");
        wahllokalUserInfoDTO_1.setWahltag(wahltag);
        wahllokalUserInfoDTO_1.setWahlbezirksart(WahllokalUserInfoDTO.WahlbezirksartEnum.UWB);
        wahllokalUserInfoDTO_1.setWbidWahlnummer(
                "[" +
                        "{\"wahlbezirkID\":\"wahlbezirkID1_0\",\"wahlnummer\":\"0\",\"wahlID\":\"wahlID0\"}," +
                        "{\"wahlbezirkID\":\"wahlbezirkID1_1\",\"wahlnummer\":\"1\",\"wahlID\":\"wahlID1\"}," +
                        "{\"wahlbezirkID\":\"wahlbezirkID1_2\",\"wahlnummer\":\"2\",\"wahlID\":\"wahlID2\"}" +
                        "]");
        val wahllokalUserInfoDTO_2 = new WahllokalUserInfoDTO();
        wahllokalUserInfoDTO_2.setWahlbezirkID("wahlbezirkID2_0");
        wahllokalUserInfoDTO_2.setWahlbezirknummer("0002");
        wahllokalUserInfoDTO_2.setWahltag(wahltag);
        wahllokalUserInfoDTO_2.setWahlbezirksart(WahllokalUserInfoDTO.WahlbezirksartEnum.BWB);
        wahllokalUserInfoDTO_2.setWbidWahlnummer(
                "[" +
                        "{\"wahlbezirkID\":\"wahlbezirkID2_0\",\"wahlnummer\":\"0\",\"wahlID\":\"wahlID0\"}," +
                        "{\"wahlbezirkID\":\"wahlbezirkID2_1\",\"wahlnummer\":\"1\",\"wahlID\":\"wahlID1\"}," +
                        "{\"wahlbezirkID\":\"wahlbezirkID2_2\",\"wahlnummer\":\"2\",\"wahlID\":\"wahlID2\"}" +
                        "]");
        val wahllokalUserInfoDTO_3 = new WahllokalUserInfoDTO();
        wahllokalUserInfoDTO_3.setWahlbezirkID("wahlbezirkID3_0");
        wahllokalUserInfoDTO_3.setWahlbezirknummer("0003");
        wahllokalUserInfoDTO_3.setWahltag(wahltag);
        wahllokalUserInfoDTO_3.setWahlbezirksart(WahllokalUserInfoDTO.WahlbezirksartEnum.UWB);
        wahllokalUserInfoDTO_3.setWbidWahlnummer(
                "[" +
                        "{\"wahlbezirkID\":\"wahlbezirkID3_0\",\"wahlnummer\":\"0\",\"wahlID\":\"wahlID0\"}," +
                        "{\"wahlbezirkID\":\"wahlbezirkID3_1\",\"wahlnummer\":\"1\",\"wahlID\":\"wahlID1\"}," +
                        "{\"wahlbezirkID\":\"wahlbezirkID3_2\",\"wahlnummer\":\"2\",\"wahlID\":\"wahlID2\"}" +
                        "]");
        return List.of(wahllokalUserInfoDTO_1, wahllokalUserInfoDTO_2, wahllokalUserInfoDTO_3);
    }
}
