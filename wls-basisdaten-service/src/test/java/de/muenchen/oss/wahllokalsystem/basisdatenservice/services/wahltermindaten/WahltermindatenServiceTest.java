package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltermindaten;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.kopfdaten.KopfdatenRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen.ReferendumvorlagenRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlbezirke.Wahlbezirk;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlbezirke.WahlbezirkRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlen.Wahl;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlen.WahlRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlen.Wahlart;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlvorschlag.WahlvorschlaegeRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.BasisdatenModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.BasisstrukturdatenModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.InitializeKopfdaten;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.WahldatenClient;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlbezirke.WahlbezirkModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlbezirke.WahlbezirkModelMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlModelMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahltagWithNummer;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltag.WahltagModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltag.WahltageService;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WahltermindatenServiceTest {

    @Mock
    WahltermindatenValidator wahltermindatenValidator;

    @Mock
    WahltageService wahltageService;

    @Mock
    WahldatenClient wahldatenClient;

    @Mock
    WahlModelMapper wahlModelMapper;

    @Mock
    WahlbezirkModelMapper wahlbezirkModelMapper;

    @Mock
    WahlRepository wahlRepository;

    @Mock
    WahlbezirkRepository wahlbezirkRepository;

    @Mock
    KopfdatenRepository kopfdatenRepository;

    @Mock
    WahlvorschlaegeRepository wahlvorschlaegeRepository;

    @Mock
    ReferendumvorlagenRepository referendumvorlagenRepository;

    @Mock
    InitializeKopfdaten kopfDataInitializer;

    @Mock
    ExceptionFactory exceptionFactory;

    @Mock
    AsyncWahltermindatenService asyncWahltermindatenService;

    @InjectMocks
    WahltermindatenService unitUnderTest;

    @Nested
    class PutWahltermindaten {

        @Captor
        ArgumentCaptor<Collection<Wahlbezirk>> wahlbezirkEntitiesCaptor;

        @Test
        void should_throwException_when_wahltagIDIsInvalid() {
            val wahltagID = "wahltagID";

            val mockedValidationException = FachlicheWlsException.withCode("000").buildWithMessage("validation failed");

            Mockito.doThrow(mockedValidationException).when(wahltermindatenValidator).validateParameterToInitWahltermindaten(wahltagID);

            Assertions.assertThatThrownBy(() -> unitUnderTest.putWahltermindaten(wahltagID)).isSameAs(mockedValidationException);
        }

        @Test
        void should_throwException_when_wahltagWithIDDoesNotExist() {
            val wahltagID = "wahltagID";

            val mockedWlsException = FachlicheWlsException.withCode("000").buildWithMessage("message");
            val mockedWahltageServiceResponse = List.of(new WahltagModel("otherWahltagID", LocalDate.now(), "", ""));

            Mockito.when(wahltageService.getWahltage()).thenReturn(mockedWahltageServiceResponse);
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.CODE_PUTWAHLTERMINDATEN_NO_WAHLTAG)).thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.putWahltermindaten(wahltagID)).isSameAs(mockedWlsException);
        }

        @Test
        void should_throwException_when_noBasisdatenWhereFound() {
            val wahltagID = "wahltagID";

            val mockedMatchingWahltag = new WahltagModel(wahltagID, LocalDate.now(), "", "nummer");
            val mockedWahltageServiceResponse = List.of(mockedMatchingWahltag);
            val mockedWlsException = FachlicheWlsException.withCode("000").buildWithMessage("message");

            Mockito.when(wahltageService.getWahltage()).thenReturn(mockedWahltageServiceResponse);
            Mockito.when(wahldatenClient.loadBasisdaten(new WahltagWithNummer(mockedMatchingWahltag.wahltag(), mockedMatchingWahltag.nummer())))
                    .thenReturn(null);
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GET_BASISDATEN_NO_DATA)).thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.putWahltermindaten(wahltagID)).isSameAs(mockedWlsException);

        }

        @Test
        void should_persistData_when_gettingBasisdaten() {
            val wahltagID = "wahltagID";

            val mockedDataWahltag = LocalDate.now();

            val mockedMatchingWahltag = new WahltagModel(wahltagID, mockedDataWahltag, "", "nummer");
            val mockedWahltageServiceResponse = List.of(mockedMatchingWahltag);
            val mockedWahldatenClientResponse = createMockedBasisdatenModel(mockedDataWahltag);
            val mockedWahlenMappedAsEntity = List.of(createWahlWithNummer("1"), createWahlWithNummer("2"));
            val mockedWahlbezirkeMappedAsEntity = Set.of(createWahlbezirkWithWahlnummer("1"), createWahlbezirkWithWahlnummer("2"));

            Mockito.when(wahltageService.getWahltage()).thenReturn(mockedWahltageServiceResponse);
            Mockito.when(wahldatenClient.loadBasisdaten(new WahltagWithNummer(mockedMatchingWahltag.wahltag(), mockedMatchingWahltag.nummer())))
                    .thenReturn(mockedWahldatenClientResponse);
            Mockito.when(wahlModelMapper.fromListOfWahlModeltoListOfWahlEntities(any())).thenReturn(mockedWahlenMappedAsEntity);
            Mockito.when(wahlbezirkModelMapper.fromListOfWahlbezirkModeltoListOfWahlbezirkEntities(any())).thenReturn(mockedWahlbezirkeMappedAsEntity);

            unitUnderTest.putWahltermindaten(wahltagID);

            Mockito.verify(wahlRepository).saveAll(mockedWahlenMappedAsEntity);
            Mockito.verify(wahlbezirkRepository).saveAll(wahlbezirkEntitiesCaptor.capture());
            Mockito.verify(kopfDataInitializer).initKopfdaten(mockedWahldatenClientResponse);
            Mockito.verify(asyncWahltermindatenService)
                    .initVorlagenAndVorschlaege(eq(mockedMatchingWahltag.wahltag()), eq(mockedMatchingWahltag.nummer()), eq(mockedWahldatenClientResponse));

            Assertions.assertThat(wahlbezirkEntitiesCaptor.getAllValues().size()).isEqualTo(1);
            Assertions.assertThat(wahlbezirkEntitiesCaptor.getValue()).containsOnly(
                    new Wahlbezirk(null, null, null, null, "1", "wahlID1"),
                    new Wahlbezirk(null, null, null, null, "2", "wahlID2"));
        }

        private static BasisdatenModel createMockedBasisdatenModel(LocalDate wahltagDate) {
            val mockedWahldatenWahlen = Set.of(new WahlModel("wahlID1", "wahl 1", 1L, 1L, wahltagDate, Wahlart.BTW, null, "1"),
                    new WahlModel("wahlID2", "wahl 2", 1L, 1L, wahltagDate, Wahlart.BTW, null, "2"));
            val mockedWahldatenWahlbezirke = Set.of(new WahlbezirkModel("wbz1", WahlbezirkArtModel.UWB, "1", wahltagDate, "1", "1"),
                    new WahlbezirkModel("wbz2", WahlbezirkArtModel.UWB, "1", wahltagDate, "2", "2"));
            return new BasisdatenModel(Collections.emptySet(), mockedWahldatenWahlen, mockedWahldatenWahlbezirke,
                    Collections.emptySet());
        }

        private Wahl createWahlWithNummer(final String nummer) {
            val wahl = new Wahl();
            wahl.setNummer(nummer);

            return wahl;
        }

        private Wahlbezirk createWahlbezirkWithWahlnummer(final String wahlnummer) {
            val wahlbezirk = new Wahlbezirk();
            wahlbezirk.setWahlnummer(wahlnummer);

            return wahlbezirk;
        }
    }

    @Nested
    class DeleteWahltermindaten {

        @Test
        void should_throwException_when_wahltagIDIsNotValid() {
            val wahltagID = "wahltagID";

            val mockedValidationException = FachlicheWlsException.withCode("000").buildWithMessage("validation failed");

            Mockito.doThrow(mockedValidationException).when(wahltermindatenValidator).validateParameterToDeleteWahltermindaten(wahltagID);

            Assertions.assertThatThrownBy(() -> unitUnderTest.deleteWahltermindaten(wahltagID)).isSameAs(mockedValidationException);
        }

        @Test
        void should_throwException_when_wahltagOfIDDoesNotExist() {
            val wahltagID = "wahltagID";

            val mockedFachlicheWlsException = FachlicheWlsException.withCode("000").buildWithMessage("fachlicheWlsException");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.CODE_DELETEWAHLTERMINDATEN_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedFachlicheWlsException);
            Mockito.when(wahltageService.getWahltage()).thenReturn(Collections.emptyList());

            Assertions.assertThatThrownBy(() -> unitUnderTest.deleteWahltermindaten(wahltagID)).isSameAs(mockedFachlicheWlsException);
        }

        @Test
        void should_deleteDataInReposOfWahlen_when_wahltagIdHasWahltagWithBasisdaten() {
            val wahltagID = "wahltagID";

            val mockedDataWahltagDate = LocalDate.now();
            val mockedMatchingWahltag = new WahltagModel(wahltagID, mockedDataWahltagDate, "", "nummer");
            val mockedWahltageServiceResponse = List.of(mockedMatchingWahltag);
            val mockedBasisstrukturdatenModels = Set.of(createBasisstrukturdatenModelWithWahlID("wahlID1"), createBasisstrukturdatenModelWithWahlID("wahlID2"));
            val mockedWahldatenClientResponse = new BasisdatenModel(mockedBasisstrukturdatenModels, null, null, null);

            Mockito.when(wahltageService.getWahltage()).thenReturn(mockedWahltageServiceResponse);
            Mockito.when(wahldatenClient.loadBasisdaten(new WahltagWithNummer(mockedDataWahltagDate, mockedMatchingWahltag.nummer())))
                    .thenReturn(mockedWahldatenClientResponse);

            unitUnderTest.deleteWahltermindaten(wahltagID);

            Mockito.verify(wahlbezirkRepository).deleteByWahltag(mockedDataWahltagDate);
            Mockito.verify(kopfdatenRepository).deleteAllByBezirkUndWahlID_WahlID("wahlID1");
            Mockito.verify(kopfdatenRepository).deleteAllByBezirkUndWahlID_WahlID("wahlID2");
            Mockito.verify(wahlRepository).deleteById("wahlID1");
            Mockito.verify(wahlRepository).deleteById("wahlID2");
            Mockito.verify(wahlvorschlaegeRepository).deleteAllByBezirkUndWahlID_WahlID("wahlID1");
            Mockito.verify(wahlvorschlaegeRepository).deleteAllByBezirkUndWahlID_WahlID("wahlID2");
            Mockito.verify(referendumvorlagenRepository).deleteAllByBezirkUndWahlID_WahlID("wahlID1");
            Mockito.verify(referendumvorlagenRepository).deleteAllByBezirkUndWahlID_WahlID("wahlID2");
        }

        private BasisstrukturdatenModel createBasisstrukturdatenModelWithWahlID(String wahlID) {
            return new BasisstrukturdatenModel(wahlID, null, null, null);
        }
    }

}
