package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.kopfdaten.KopfdatenRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.kopfdaten.Stimmzettelgebietsart;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.common.StimmzettelgebietsartModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahltagWithNummer;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.MockDataFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDate;
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

@ExtendWith(MockitoExtension.class)
class KopfdatenServiceTest {

    @Mock
    KopfdatenRepository kopfdatenRepository;

    @Mock
    KopfdatenModelMapper kopfdatenModelMapper;

    @Mock
    KopfdatenValidator kopfdatenValidator;

    @Mock
    ExceptionFactory exceptionFactory;

    @Mock
    WahldatenClient wahldatenClient;

    @Mock
    KonfigurierterWahltagClient konfigurierterWahltagClient;

    @InjectMocks
    KopfdatenService unitUnderTest;

    @Nested
    class GetKopfdaten {

        @Test
        void dataIsLoadedFromRemoteIfNotExistingInRepo() {
            val bezirkUndWahlId = new BezirkUndWahlID("wahlID1", "wahlbezirkID1_1");
            val forDate = LocalDate.now().plusMonths(1);
            val mockedKonfigurierterWahltagFromClient = MockDataFactory.createClientKonfigurierterWahltagModel(forDate, true);
            val mockedBasisdatenModelFromClient = MockDataFactory.createBasisdatenModel(mockedKonfigurierterWahltagFromClient.wahltag());

            val expectedKopfdatenFromClient = MockDataFactory.createKopfdatenModelFor("wahlID1", "wahlbezirkID1_1",
                    StimmzettelgebietsartModel.SG, "120", "Munich",
                    "Bundestagswahl", "1201");

            Mockito.when(kopfdatenRepository.findById(bezirkUndWahlId)).thenReturn(Optional.ofNullable(null));
            Mockito.lenient().when(kopfdatenModelMapper.toModel(null)).thenReturn(null);
            Mockito.when(konfigurierterWahltagClient.getKonfigurierterWahltag()).thenReturn(mockedKonfigurierterWahltagFromClient);
            Mockito.when(wahldatenClient.loadBasisdaten(
                    new WahltagWithNummer(mockedKonfigurierterWahltagFromClient.wahltag(), mockedKonfigurierterWahltagFromClient.nummer())))
                    .thenReturn(mockedBasisdatenModelFromClient);

            val result = unitUnderTest.getKopfdaten(bezirkUndWahlId);
            Assertions.assertThat(result).isEqualTo(expectedKopfdatenFromClient);
            Mockito.verify(kopfdatenValidator).validWahlIdUndWahlbezirkIDOrThrow(bezirkUndWahlId);
        }

        @Test
        void dataIsLoadedFromRepoIfPresentAndNotFromRemoteClient() {
            val bezirkUndWahlId = new BezirkUndWahlID("wahlID1", "wahlbezirkID1_1");
            val forDate = LocalDate.now().plusMonths(1);

            val kopfdatenEntityInRepo = MockDataFactory.createKopfdatenEntityFor("wahlID1", "wahlbezirkID1_1",
                    Stimmzettelgebietsart.SG, "Munich-Repo", "120",
                    "Bundestagswahl", "1201");

            val mockedKonfigurierterWahltagFromClient = MockDataFactory.createClientKonfigurierterWahltagModel(forDate, true);
            val mockedBasisdatenModelFromClient = MockDataFactory.createBasisdatenModel(mockedKonfigurierterWahltagFromClient.wahltag());

            val expectedKopfdaten = MockDataFactory.createKopfdatenModelFor("wahlID1", "wahlbezirkID1_1",
                    StimmzettelgebietsartModel.SG, "120", "Munich-Repo",
                    "Bundestagswahl", "1201");

            val notExpectedRemoteKopfdaten = MockDataFactory.createKopfdatenModelFor("wahlID1", "wahlbezirkID1_1",
                    StimmzettelgebietsartModel.SG, "120", "Munich",
                    "Bundestagswahl", "1201");

            Mockito.when(kopfdatenRepository.findById(bezirkUndWahlId)).thenReturn(Optional.of(kopfdatenEntityInRepo));
            Mockito.when(kopfdatenModelMapper.toModel(kopfdatenEntityInRepo)).thenReturn(expectedKopfdaten);
            Mockito.lenient().when(konfigurierterWahltagClient.getKonfigurierterWahltag()).thenReturn(mockedKonfigurierterWahltagFromClient);
            Mockito.lenient()
                    .when(wahldatenClient.loadBasisdaten(
                            new WahltagWithNummer(mockedKonfigurierterWahltagFromClient.wahltag(), mockedKonfigurierterWahltagFromClient.nummer())))
                    .thenReturn(mockedBasisdatenModelFromClient);

            val result = unitUnderTest.getKopfdaten(bezirkUndWahlId);
            Assertions.assertThat(result).isEqualTo(expectedKopfdaten);
            Assertions.assertThat(result).isNotEqualTo(notExpectedRemoteKopfdaten);
            Mockito.verify(kopfdatenValidator).validWahlIdUndWahlbezirkIDOrThrow(bezirkUndWahlId);
        }

        @Test
        void throwsFachlicheWlsExceptionIfBasisdataPartialEmptyOrNotConsistent() {
            val bezirkUndWahlId = new BezirkUndWahlID("wahlID99", "wahlbezirkID1_99");
            val forDate = LocalDate.now().plusMonths(1);
            val mockedKonfigurierterWahltagFromClient = MockDataFactory.createClientKonfigurierterWahltagModel(forDate, true);
            val mockedBasisdatenModelFromClient = MockDataFactory.createBasisdatenModel(mockedKonfigurierterWahltagFromClient.wahltag());

            Mockito.when(kopfdatenRepository.findById(bezirkUndWahlId)).thenReturn(Optional.ofNullable(null));
            Mockito.lenient().when(kopfdatenModelMapper.toModel(null)).thenReturn(null);
            Mockito.when(konfigurierterWahltagClient.getKonfigurierterWahltag()).thenReturn(mockedKonfigurierterWahltagFromClient);
            Mockito.when(wahldatenClient.loadBasisdaten(
                    new WahltagWithNummer(mockedKonfigurierterWahltagFromClient.wahltag(), mockedKonfigurierterWahltagFromClient.nummer())))
                    .thenReturn(mockedBasisdatenModelFromClient);
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.INITIALIZE_KOPFDATEN_NO_BASISSTRUKTURDATEN))
                    .thenReturn(mockedWlsException);
            Mockito.lenient().when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.INITIALIZE_KOPFDATEN_NO_WAHL_WAHLBEZIRK_STIMMZETTELGEBIET))
                    .thenReturn(mockedWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.getKopfdaten(bezirkUndWahlId)).isInstanceOf(FachlicheWlsException.class);
        }
    }
}
