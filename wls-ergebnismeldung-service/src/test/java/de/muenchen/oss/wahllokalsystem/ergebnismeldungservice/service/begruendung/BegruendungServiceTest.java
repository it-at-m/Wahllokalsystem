package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.begruendung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.begruendung.Begruendung;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.begruendung.BegruendungRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.BezirkUndWahlIDStapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
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
class BegruendungServiceTest {

    @Mock
    BegruendungRepository begruendungRepository;

    @Mock
    BegruendungModelMapper begruendungModelMapper;

    @Mock
    BegruendungValidator begruendungValidator;

    @InjectMocks
    BegruendungService unitUnderTest;

    @Mock
    ExceptionFactory exceptionFactory;

    @Nested
    class GetBegruendung {

        @Test
        void should_returnNull_when_repoIsEmpty() {
            val reference = BegruendungReference.builder().build();

            val mappedEntityId = new BezirkUndWahlIDStapelart();

            Mockito.when(begruendungModelMapper.toEmbeddedId(reference)).thenReturn(mappedEntityId);
            Mockito.when(begruendungRepository.findById(mappedEntityId)).thenReturn(Optional.empty());

            val result = unitUnderTest.getBegruendung(reference);

            Assertions.assertThat(result).isNull();
            Mockito.verify(begruendungValidator).validReferenceOrThrow(reference);
        }

        @Test
        void should_returnBegruendungModel_when_begruendungIsFoundFromRepo() {
            val reference = BegruendungReference.builder().build();

            val mappedEntityId = new BezirkUndWahlIDStapelart();
            val entityFromRepo = new Begruendung();
            val mappedEntity = BegruendungModel.builder().build();

            Mockito.when(begruendungModelMapper.toEmbeddedId(reference)).thenReturn(mappedEntityId);
            Mockito.when(begruendungRepository.findById(mappedEntityId)).thenReturn(Optional.of(entityFromRepo));
            Mockito.when(begruendungModelMapper.toModel(entityFromRepo)).thenReturn(mappedEntity);

            val result = unitUnderTest.getBegruendung(reference);

            Assertions.assertThat(result).isSameAs(mappedEntity);
            Mockito.verify(begruendungValidator).validReferenceOrThrow(reference);
        }
    }

    @Nested
    class PostBegruendung {

        @Test
        void should_callValidator_when_posting() {

            val reference = BegruendungReference.builder().build();
            val invalidModel = BegruendungModel.builder().build();

            unitUnderTest.postBegruendung(reference, invalidModel);

            Mockito.verify(begruendungValidator).validModelOrThrow(invalidModel);
        }

        @Test
        void should_saveBegruendung_when_called() {
            val model = BegruendungModel.builder().build();
            val reference = BegruendungReference.builder().build();

            val mappedEntityOfModel = new Begruendung();

            Mockito.when(begruendungModelMapper.toEntity(model)).thenReturn(mappedEntityOfModel);

            unitUnderTest.postBegruendung(reference, model);

            Mockito.verify(begruendungRepository).save(mappedEntityOfModel);
            Mockito.verify(begruendungValidator).validModelOrThrow(model);
            Mockito.verify(begruendungValidator).validReferenceOrThrow(reference);
        }

        @Test
        void should_throwTechnischeException_when_called() {
            val model = BegruendungModel.builder().build();
            val reference = BegruendungReference.builder().build();

            val mockedModelAsEntity = Mockito.mock(Begruendung.class);
            val mockedRepositorySaveException = new RuntimeException("saving failed");
            val mockedExceptionFactoryWlsException = TechnischeWlsException.withCode("").buildWithMessage("save exception");

            Mockito.when(begruendungModelMapper.toEntity(model)).thenReturn(mockedModelAsEntity);
            Mockito.doThrow(mockedRepositorySaveException).when(begruendungRepository).save(mockedModelAsEntity);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.BEGRUENDUNG_UNSAVEABLE))
                    .thenReturn(mockedExceptionFactoryWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.postBegruendung(reference, model)).isSameAs(mockedExceptionFactoryWlsException);
        }
    }
}
