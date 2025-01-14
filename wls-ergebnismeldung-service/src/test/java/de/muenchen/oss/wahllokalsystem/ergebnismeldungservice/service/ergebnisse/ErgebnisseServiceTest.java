package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.begruendung.BezirkUndWahlIDStapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse.Ergebnisse;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse.ErgebnisseRepository;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
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

    @Nested
    class GetErgebnisse {

        @Test
        void should_returnNull_when_givenNull() {
            val reference = ErgebnisseReference.builder().build();

            val mappedEntityId = new BezirkUndWahlIDStapelart();

            Mockito.doNothing().when(ergebnisseValidator).validReferenceOrThrow(reference);
            Mockito.when(ergebnisseModelMapper.toEmbeddedId(reference)).thenReturn(mappedEntityId);
            Mockito.when(ergebnisseRepository.findById(mappedEntityId)).thenReturn(Optional.empty());

            val result = unitUnderTest.getErgebnisse(reference);

            Assertions.assertThat(result).isNull();
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

        @Test
        void should_returnFachlicheWlsException_when_ErgebnisseIsInvalid() {
            val reference = ErgebnisseReference.builder().build();

            val exceptionToThrow = FachlicheWlsException.withCode("0815").buildWithMessage("upsi");

            Mockito.doThrow(exceptionToThrow).when(ergebnisseValidator).validReferenceOrThrow(reference);

            val exceptionThrown = Assertions.catchThrowable(() -> unitUnderTest.getErgebnisse(reference));

            Assertions.assertThat(exceptionThrown).isSameAs(exceptionToThrow);
            Mockito.verify(ergebnisseRepository, Mockito.times(0)).findById(Mockito.any());
        }
    }

    @Nested
    class PostErgebnisse {
        @Test
        void should_returnFachlicheWlsException_when_ErgebnisseModelIsInvalid() {
            val invalidModel = ErgebnisseModel.builder().build();
            val reference = ErgebnisseReference.builder().build();

            val exceptionToThrow = FachlicheWlsException.withCode("0815").buildWithMessage("upsi");

            Mockito.doThrow(exceptionToThrow).when(ergebnisseValidator).validModelOrThrow(invalidModel);

            val exceptionThrown = Assertions.catchException(() -> unitUnderTest.postErgebnisse(invalidModel, reference));

            Assertions.assertThat(exceptionThrown).isSameAs(exceptionToThrow);
            Mockito.verify(ergebnisseRepository, Mockito.times(0)).save(Mockito.any());
        }

        @Test
        void should_saveErgebnisse_when_called() {
            val model = ErgebnisseModel.builder().build();
            val reference = ErgebnisseReference.builder().build();

            val mappedEntityOfModel = new Ergebnisse();

            Mockito.doNothing().when(ergebnisseValidator).validModelOrThrow(model);
            Mockito.doNothing().when(ergebnisseValidator).validReferenceOrThrow(reference);
            Mockito.when(ergebnisseModelMapper.toEntity(model)).thenReturn(mappedEntityOfModel);

            unitUnderTest.postErgebnisse(model, reference);

            Mockito.verify(ergebnisseRepository).save(mappedEntityOfModel);
        }
    }
}
