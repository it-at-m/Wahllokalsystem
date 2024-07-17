package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltag;

import org.assertj.core.api.Assertions;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahltag;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahltagRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.val;
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

        private List<Wahltag> createWahltagList(String pIndex) {
            val wahltag1 = new Wahltag(pIndex + "_identifikatorWahltag1", LocalDate.now().minusMonths(2), "beschreibungWahltag1", "nummerWahltag1");
            val wahltag2 = new Wahltag(pIndex + "_identifikatorWahltag2", LocalDate.now().minusMonths(1), "beschreibungWahltag2", "nummerWahltag2");
            val wahltag3 = new Wahltag(pIndex + "_identifikatorWahltag3", LocalDate.now().plusMonths(1), "beschreibungWahltag3", "nummerWahltag3");

            return List.of(wahltag1, wahltag2, wahltag3);
        }

        private List<WahltagModel> createWahltagModelList(String pIndex) {
            val wahltag1 = new WahltagModel(pIndex + "_identifikatorWahltag1", LocalDate.now().minusMonths(2), "beschreibungWahltag1", "nummerWahltag1");
            val wahltag2 = new WahltagModel(pIndex + "_identifikatorWahltag2", LocalDate.now().minusMonths(1), "beschreibungWahltag2", "nummerWahltag2");
            val wahltag3 = new WahltagModel(pIndex + "_identifikatorWahltag3", LocalDate.now().plusMonths(1), "beschreibungWahltag3", "nummerWahltag3");

            return List.of(wahltag1, wahltag2, wahltag3);
        }
    }
}
