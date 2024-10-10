package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten;

import static org.mockito.ArgumentMatchers.any;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.kopfdaten.KopfdatenRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.kopfdaten.Stimmzettelgebietsart;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.common.StimmzettelgebietsartModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahltagWithNummer;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.MockDataFactory;
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
    KopfdatenMapper kopfdatenMapper;

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
            val wahlID = "wahlID1";
            val wahlbezrkID = "wahlbezirkID1_1";
            val bezirkUndWahlId = new BezirkUndWahlID(wahlID, wahlbezrkID);
            val forDate = LocalDate.now().plusMonths(1);
            val mockedKonfigurierterWahltagFromClient = MockDataFactory.createClientKonfigurierterWahltagModel(forDate, true);
            val mockedBasisdatenModelFromClient = MockDataFactory.createBasisdatenModel(mockedKonfigurierterWahltagFromClient.wahltag());
            val mockedKopfdatenModelByInitializer = MockDataFactory.createKopfdatenModelFor(wahlID, wahlbezrkID);
            val mockedKopfdatenModelMappedToEntity = MockDataFactory.createKopfdatenEntityFor(wahlID, wahlbezrkID);

            Mockito.when(kopfdatenRepository.findById(bezirkUndWahlId)).thenReturn(Optional.empty());
            Mockito.when(kopfdatenModelMapper.toEntity(mockedKopfdatenModelByInitializer)).thenReturn(mockedKopfdatenModelMappedToEntity);
            Mockito.when(konfigurierterWahltagClient.getKonfigurierterWahltag()).thenReturn(mockedKonfigurierterWahltagFromClient);
            Mockito.when(wahldatenClient.loadBasisdaten(
                            new WahltagWithNummer(mockedKonfigurierterWahltagFromClient.wahltag(), mockedKonfigurierterWahltagFromClient.nummer())))
                    .thenReturn(mockedBasisdatenModelFromClient);
            Mockito.when(kopfdatenMapper.initKopfdata(wahlID, wahlbezrkID, mockedBasisdatenModelFromClient)).thenReturn(mockedKopfdatenModelByInitializer);

            val result = unitUnderTest.getKopfdaten(bezirkUndWahlId);
            Assertions.assertThat(result).isEqualTo(mockedKopfdatenModelByInitializer);

            Mockito.verify(kopfdatenValidator).validWahlIdUndWahlbezirkIDOrThrow(bezirkUndWahlId);
            Mockito.verify(kopfdatenRepository).save(mockedKopfdatenModelMappedToEntity);
        }

        @Test
        void dataIsLoadedFromRepoIfPresentAndNotFromRemoteClient() {
            val bezirkUndWahlId = new BezirkUndWahlID("wahlID1", "wahlbezirkID1_1");

            val kopfdatenEntityInRepo = MockDataFactory.createKopfdatenEntityFor("wahlID1", "wahlbezirkID1_1",
                    Stimmzettelgebietsart.SG, "Munich-Repo", "120",
                    "Bundestagswahl", "1201");

            val expectedKopfdaten = MockDataFactory.createKopfdatenModelFor("wahlID1", "wahlbezirkID1_1",
                    StimmzettelgebietsartModel.SG, "120", "Munich-Repo",
                    "Bundestagswahl", "1201");

            Mockito.when(kopfdatenRepository.findById(bezirkUndWahlId)).thenReturn(Optional.of(kopfdatenEntityInRepo));
            Mockito.when(kopfdatenModelMapper.toModel(kopfdatenEntityInRepo)).thenReturn(expectedKopfdaten);

            val result = unitUnderTest.getKopfdaten(bezirkUndWahlId);
            Assertions.assertThat(result).isEqualTo(expectedKopfdaten);
            Mockito.verify(kopfdatenValidator).validWahlIdUndWahlbezirkIDOrThrow(bezirkUndWahlId);
            Mockito.verify(kopfdatenRepository, Mockito.times(0)).save(any());
        }
    }
}
