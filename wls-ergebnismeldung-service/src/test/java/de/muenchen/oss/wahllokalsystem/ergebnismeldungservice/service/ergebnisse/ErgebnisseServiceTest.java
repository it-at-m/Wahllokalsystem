package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.BezirkUndWahlIDStapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse.Ergebnisse;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse.ErgebnisseRepository;
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
class ErgebnisseServiceTest {

    @Mock
    ErgebnisseRepository ergebnisseRepository;

    @Mock
    ErgebnisseModelMapper ergebnisseModelMapper;

    @Mock
    ErgebnisseValidator ergebnisseValidator;

    @InjectMocks
    ErgebnisseService unitUnderTest;

    @Mock
    ExceptionFactory exceptionFactory;

    @Nested
    class GetErgebnisse {

        @Test
        void should_returnNull_when_RepoIsEmpty() {
            val reference = ErgebnisseReference.builder().build();
            val mappedEntityId = new BezirkUndWahlIDStapelart();

            Mockito.when(ergebnisseModelMapper.toEmbeddedId(reference)).thenReturn(mappedEntityId);
            Mockito.when(ergebnisseRepository.findById(mappedEntityId)).thenReturn(Optional.empty());

            val result = unitUnderTest.getErgebnisse(reference);

            Assertions.assertThat(result).isNull();

            Mockito.verify(ergebnisseValidator).validReferenceOrThrow(reference);
        }

        @Test
        void should_returnErgebnisseModel_when_ErgebnisseIsFoundFromRepo() {
            val reference = ErgebnisseReference.builder().build();

            val mappedEntityId = new BezirkUndWahlIDStapelart();
            val entityFromRepo = new Ergebnisse();
            val mappedEntity = ErgebnisseModel.builder().build();

            Mockito.doNothing().when(ergebnisseValidator).validReferenceOrThrow(reference);
            Mockito.when(ergebnisseModelMapper.toEmbeddedId(reference)).thenReturn(mappedEntityId);
            Mockito.when(ergebnisseRepository.findById(mappedEntityId)).thenReturn(Optional.of(entityFromRepo));
            Mockito.when(ergebnisseModelMapper.toModel(entityFromRepo)).thenReturn(mappedEntity);

            val result = unitUnderTest.getErgebnisse(reference);

            Assertions.assertThat(result).isSameAs(mappedEntity);
        }
    }

    @Nested
    class PostErgebnisse {
        @Test
        void should_callValidator_when_posting() {

            val reference = ErgebnisseReference.builder().build();
            val invalidModel = ErgebnisseModel.builder().build();

            unitUnderTest.postErgebnisse(reference, invalidModel);

            Mockito.verify(ergebnisseValidator).validModelOrThrow(invalidModel);
        }

        @Test
        void should_saveErgebnisse_when_called() {
            val model = ErgebnisseModel.builder().build();
            val reference = ErgebnisseReference.builder().build();
            val mappedEntityOfModel = new Ergebnisse();
            
            Mockito.when(ergebnisseModelMapper.toEntity(model)).thenReturn(mappedEntityOfModel);

            unitUnderTest.postErgebnisse(reference, model);

            Mockito.verify(ergebnisseRepository).save(mappedEntityOfModel);
            Mockito.verify(ergebnisseValidator).validModelOrThrow(model);
            Mockito.verify(ergebnisseValidator).validReferenceOrThrow(reference);
        }

        @Test
        void should_throwTechnischeException_when_called() {
            val model = ErgebnisseModel.builder().build();
            val reference = ErgebnisseReference.builder().build();

            val mockedModelAsEntity = Mockito.mock(Ergebnisse.class);
            val mockedRepositorySaveException = new RuntimeException("saving failed");
            val mockedExceptionFactoryWlsException = TechnischeWlsException.withCode("").buildWithMessage("save exception");

            Mockito.when(ergebnisseModelMapper.toEntity(model)).thenReturn(mockedModelAsEntity);
            Mockito.doThrow(mockedRepositorySaveException).when(ergebnisseRepository).save(mockedModelAsEntity);
            Mockito.when(exceptionFactory.createTechnischeWlsException(ExceptionConstants.ERGEBNISSE_UNSAVEABLE))
                    .thenReturn(mockedExceptionFactoryWlsException);

            Assertions.assertThatThrownBy(() -> unitUnderTest.postErgebnisse(reference, model)).isSameAs(mockedExceptionFactoryWlsException);
        }
    }
}
