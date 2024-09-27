package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltag;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.common.Wahltag;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.common.WahltagRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.time.LocalDate;
import java.util.List;
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
class WahltageServiceTest {

    @Mock
    WahltagRepository wahltagRepository;
    @Mock
    WahltagModelMapper wahltagModelMapper;
    @Mock
    WahltageClient wahltageClient;

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    WahltageService unitUnderTest;

    @Nested
    class GetWahltage {

        @Test
        void dataIsLoadedFromRemoteEvenIfExistingInRepoAndRepoIsUpdated() {
            val mockedListOfEntities = createWahltagList("2");
            val mockedClientResponse = createWahltagModelList("2");
            val mockedMappedSavedEntities = createWahltagModelList("2");

            Mockito.when(wahltageClient.getWahltage(LocalDate.now().minusMonths(3))).thenReturn(mockedClientResponse);
            Mockito.when(wahltagModelMapper.fromWahltagModelToWahltagEntityList(mockedClientResponse)).thenReturn(mockedListOfEntities);
            Mockito.when(wahltagRepository.findAllByOrderByWahltagAsc()).thenReturn(mockedListOfEntities);
            Mockito.when(wahltagModelMapper.fromWahltagEntityToWahltagModelList(mockedListOfEntities)).thenReturn(mockedMappedSavedEntities);

            val result = unitUnderTest.getWahltage();
            Assertions.assertThat(result).isSameAs(mockedMappedSavedEntities);
            Mockito.verify(wahltagRepository).saveAll(mockedListOfEntities);

        }

        private List<Wahltag> createWahltagList(String wahltagIDPrefix) {
            val wahltag1 = new Wahltag(wahltagIDPrefix + "_identifikatorWahltag1", LocalDate.now().minusMonths(2), "beschreibungWahltag1", "nummerWahltag1");
            val wahltag2 = new Wahltag(wahltagIDPrefix + "_identifikatorWahltag2", LocalDate.now().minusMonths(1), "beschreibungWahltag2", "nummerWahltag2");
            val wahltag3 = new Wahltag(wahltagIDPrefix + "_identifikatorWahltag3", LocalDate.now().plusMonths(1), "beschreibungWahltag3", "nummerWahltag3");

            return List.of(wahltag1, wahltag2, wahltag3);
        }

        private List<WahltagModel> createWahltagModelList(String wahltagIDPrefix) {
            val wahltag1 = new WahltagModel(wahltagIDPrefix + "_identifikatorWahltag1", LocalDate.now().minusMonths(2), "beschreibungWahltag1",
                    "nummerWahltag1");
            val wahltag2 = new WahltagModel(wahltagIDPrefix + "_identifikatorWahltag2", LocalDate.now().minusMonths(1), "beschreibungWahltag2",
                    "nummerWahltag2");
            val wahltag3 = new WahltagModel(wahltagIDPrefix + "_identifikatorWahltag3", LocalDate.now().plusMonths(1), "beschreibungWahltag3",
                    "nummerWahltag3");

            return List.of(wahltag1, wahltag2, wahltag3);
        }
    }

    @Nested
    class GetWahltagById {

        @Test
        void wahltagFound() {
            val wahltagID = "wahltagID";

            val mockedRepoResponse = new Wahltag();
            val mockedMappedRepoResponse = new WahltagModel(wahltagID, LocalDate.now(), null, null);

            Mockito.when(wahltagRepository.findById(wahltagID)).thenReturn(Optional.of(mockedRepoResponse));
            Mockito.when(wahltagModelMapper.toModel(mockedRepoResponse)).thenReturn(mockedMappedRepoResponse);

            Assertions.assertThat(unitUnderTest.getWahltagByID(wahltagID)).isSameAs(mockedMappedRepoResponse);
        }

        @Test
        void exceptionWhenNotFound() {
            val wahltagID = "wahltagID";

            val mockedRepoResponse = new Wahltag();
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("");

            Mockito.when(wahltagRepository.findById(wahltagID)).thenReturn(Optional.empty());
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETWAHLBEZIRKE_NO_WAHLTAG)).thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.getWahltagByID(wahltagID)).isSameAs(mockedWlsException);
        }
    }
}
