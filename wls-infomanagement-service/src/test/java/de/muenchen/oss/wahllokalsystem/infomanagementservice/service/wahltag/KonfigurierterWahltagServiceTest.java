package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.wahltag;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.wahltag.KonfigurierterWahltag;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.wahltag.KonfigurierterWahltagRepository;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ServiceIDFormatter;
import java.time.LocalDate;
import java.util.Arrays;
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
class KonfigurierterWahltagServiceTest {

    private static final String SERVICDE_ID = "serviceID";

    @Mock
    ServiceIDFormatter serviceIDFormatter;

    @Mock
    KonfigurierterWahltagRepository konfigurierterWahltagRepository;

    @Mock
    KonfigurierterWahltagModelMapper konfigurierterWahltagMapper;

    @Mock
    KonfigurierterWahltagValidator konfigurierterWahltagValidator;

    @InjectMocks
    KonfigurierterWahltagService unitUnderTest;

    @Nested
    class GetKonfigurierterWahltag {

        @Test
        void konfigurierterWahltagFound() {
            val mockedWahltagFromRepo = new KonfigurierterWahltag();
            val mockedEntityAsModel = KonfigurierterWahltagModel.builder().active(true).build();

            Mockito.when(konfigurierterWahltagRepository.findByActive(true)).thenReturn(mockedWahltagFromRepo);
            Mockito.when(konfigurierterWahltagMapper.toModel(mockedWahltagFromRepo)).thenReturn(mockedEntityAsModel);

            val result = unitUnderTest.getKonfigurierterWahltag();

            Assertions.assertThat(result).isSameAs(mockedEntityAsModel);

        }

        @Test
        void noKonfigurierterWahltagFound() {
            Mockito.when(konfigurierterWahltagRepository.findByActive(true)).thenReturn(null);

            val result = unitUnderTest.getKonfigurierterWahltag();

            Assertions.assertThat(result).isNull();
        }
    }

    @Nested
    class SetKonfigurierterWahltag {

        @Test
        void noSaveOnFailedValidation() {
            val konfigurierterWahltagToSave = KonfigurierterWahltagModel.builder().build();

            val mockedValidationException = new RuntimeException("failed validation");

            Mockito.doThrow(mockedValidationException).when(konfigurierterWahltagValidator).validPostModelOrThrow(konfigurierterWahltagToSave);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.setKonfigurierterWahltag(konfigurierterWahltagToSave))
                    .isSameAs(mockedValidationException);
        }

        @Test
        void newInaktivWahltagSaved() {
            val konfigurierterWahltagToSave = KonfigurierterWahltagModel.builder().active(false).build();

            val mockedModelAsEntity = new KonfigurierterWahltag();

            Mockito.doNothing().when(konfigurierterWahltagValidator).validPostModelOrThrow(konfigurierterWahltagToSave);
            Mockito.when(konfigurierterWahltagMapper.toEntity(konfigurierterWahltagToSave)).thenReturn(mockedModelAsEntity);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.setKonfigurierterWahltag(konfigurierterWahltagToSave));

            Mockito.verify(konfigurierterWahltagRepository).save(mockedModelAsEntity);

            Mockito.verifyNoMoreInteractions(konfigurierterWahltagRepository);
        }

        @Test
        void newAktivWahltagSaved() {
            val konfigurierterWahltagToSave = KonfigurierterWahltagModel.builder().active(true).build();

            val mockedModelAsEntity = new KonfigurierterWahltag();

            Mockito.doNothing().when(konfigurierterWahltagValidator).validPostModelOrThrow(konfigurierterWahltagToSave);
            Mockito.when(konfigurierterWahltagMapper.toEntity(konfigurierterWahltagToSave)).thenReturn(mockedModelAsEntity);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.setKonfigurierterWahltag(konfigurierterWahltagToSave));

            Mockito.verify(konfigurierterWahltagRepository).save(mockedModelAsEntity);

            Mockito.verify(konfigurierterWahltagRepository).setExistingKonfigurierteWahltageInaktiv();

        }
    }

    @Nested
    class DeleteKonfigurierterWahltag {

        @Test
        void validationOfModelFailed() {
            val wahltagID = "wahltagID";
            val konfigurierteWahltagDeleteModel = new KonfigurierterWahltagModel(null, wahltagID, false, null);

            val mockedValidationException = new RuntimeException("failed Validation");

            Mockito.doThrow(mockedValidationException).when(konfigurierterWahltagValidator).validDeleteModelOrThrow(konfigurierteWahltagDeleteModel);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.deleteKonfigurierterWahltag(konfigurierteWahltagDeleteModel))
                    .isSameAs(mockedValidationException);

            Mockito.verifyNoInteractions(konfigurierterWahltagRepository);
        }

        @Test
        void deletionSuccessful() {
            val wahltagID = "wahltagID";
            val konfigurierteWahltagDeleteModel = new KonfigurierterWahltagModel(null, wahltagID, false, null);

            val mockedModelAsEntity = new KonfigurierterWahltag();
            mockedModelAsEntity.setWahltagID(wahltagID);

            Mockito.doNothing().when(konfigurierterWahltagRepository).deleteById(wahltagID);
            Mockito.when(konfigurierterWahltagMapper.toEntity(konfigurierteWahltagDeleteModel)).thenReturn(mockedModelAsEntity);

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.deleteKonfigurierterWahltag(konfigurierteWahltagDeleteModel));

            Mockito.verify(konfigurierterWahltagRepository).deleteById(wahltagID);
            Mockito.verifyNoMoreInteractions(konfigurierterWahltagRepository);

            Mockito.verify(konfigurierterWahltagValidator).validDeleteModelOrThrow(konfigurierteWahltagDeleteModel);

        }

        @Test
        void onDeleteExceptionIsMappedAndThrown() {
            val wahltagID = "wahltagID";
            val konfigurierteWahltagDeleteModel = new KonfigurierterWahltagModel(null, wahltagID, false, null);

            val mockedThrownException = new RuntimeException("on delete exception");
            val mockedModelAsEntity = new KonfigurierterWahltag();
            mockedModelAsEntity.setWahltagID(wahltagID);

            Mockito.doThrow(mockedThrownException).when(konfigurierterWahltagRepository).deleteById(wahltagID);
            Mockito.when(konfigurierterWahltagMapper.toEntity(konfigurierteWahltagDeleteModel)).thenReturn(mockedModelAsEntity);
            Mockito.when(serviceIDFormatter.getId()).thenReturn(SERVICDE_ID);

            val expectedException = FachlicheWlsException.withCode("105").inService(SERVICDE_ID)
                    .buildWithMessage("deleteKonfigurierterWahltag: Der konfigurierte Wahltag konnte nicht geloescht werden.");

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.deleteKonfigurierterWahltag(konfigurierteWahltagDeleteModel))
                    .usingRecursiveComparison().isEqualTo(expectedException);
        }
    }

    @Nested
    class GetKonfigurierteWahltage {

        @Test
        void listOfKonfigurierteWahltageIsSend() {
            val mockedRepoResponse = Arrays.asList(new KonfigurierterWahltag(), new KonfigurierterWahltag(), new KonfigurierterWahltag());
            mockedRepoResponse.forEach(wahltag -> wahltag.setWahltag(LocalDate.now()));
            val mockedRepoResponseAsModelList = Arrays.asList(KonfigurierterWahltagModel.builder().build(), KonfigurierterWahltagModel.builder().build());

            Mockito.when(konfigurierterWahltagRepository.findAll()).thenReturn(mockedRepoResponse);
            Mockito.when(konfigurierterWahltagMapper.toModelList(mockedRepoResponse)).thenReturn(mockedRepoResponseAsModelList);

            Assertions.assertThat(unitUnderTest.getKonfigurierteWahltage()).isSameAs(mockedRepoResponseAsModelList);
        }
    }

    @Nested
    class IsWahltagActive {

        @Test
        void falseOnNoRepoHit() {
            val wahltagID = "wahltagID";
            val konfigurierterWahltagModel = new KonfigurierterWahltagModel(null, wahltagID, true, null);

            Mockito.when(konfigurierterWahltagRepository.findById(wahltagID)).thenReturn(Optional.empty());

            Assertions.assertThat(unitUnderTest.isWahltagActive(konfigurierterWahltagModel)).isFalse();
        }

        @Test
        void verifyInactiveRepoValueReturnsFalse() {
            val wahltagID = "wahltagID";
            val konfigurierterWahltagModel = new KonfigurierterWahltagModel(null, wahltagID, false, null);

            val mockedEntity = new KonfigurierterWahltag();
            mockedEntity.setActive(false);

            Mockito.when(konfigurierterWahltagRepository.findById(wahltagID)).thenReturn(Optional.of(mockedEntity));

            Assertions.assertThat(unitUnderTest.isWahltagActive(konfigurierterWahltagModel)).isFalse();
        }

        @Test
        void verifyActiveRepoValueReturnsTrue() {
            val wahltagID = "wahltagID";
            val konfigurierterWahltagModel = new KonfigurierterWahltagModel(null, wahltagID, true, null);

            val mockedEntity = new KonfigurierterWahltag();
            mockedEntity.setActive(true);

            Mockito.when(konfigurierterWahltagRepository.findById(wahltagID)).thenReturn(Optional.of(mockedEntity));

            Assertions.assertThat(unitUnderTest.isWahltagActive(konfigurierterWahltagModel)).isTrue();
        }
    }
}
