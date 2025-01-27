package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.begruendung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.begruendung.Begruendung;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.begruendung.BegruendungRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.begruendung.BezirkUndWahlIDStapelart;
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
class BegruendungServiceTest {

    @Mock
    BegruendungRepository begruendungRepository;

    @Mock
    BegruendungModelMapper begruendungModelMapper;

    @Mock
    BegruendungValidator begruendungValidator;

    @InjectMocks
    BegruendungService unitUnderTest;

    @Nested
    class GetBegruendung {

        @Test
        void should_returnNull_when_givenNull() {
            val reference = BegruendungReference.builder().build();

            val mappedEntityId = new BezirkUndWahlIDStapelart();

            Mockito.doNothing().when(begruendungValidator).validReferenceOrThrow(reference);
            Mockito.when(begruendungModelMapper.toEmbeddedId(reference)).thenReturn(mappedEntityId);
            Mockito.when(begruendungRepository.findById(mappedEntityId)).thenReturn(Optional.empty());

            val result = unitUnderTest.getBegruendung(reference);

            Assertions.assertThat(result).isNull();
        }

        @Test
        void should_returnBegruendungModel_when_begruendungIsFoundFromRepo() {
            val reference = BegruendungReference.builder().build();

            val mappedEntityId = new BezirkUndWahlIDStapelart();
            val entityFromRepo = new Begruendung();
            val mappedEntity = BegruendungModel.builder().build();

            Mockito.doNothing().when(begruendungValidator).validReferenceOrThrow(reference);
            Mockito.when(begruendungModelMapper.toEmbeddedId(reference)).thenReturn(mappedEntityId);
            Mockito.when(begruendungRepository.findById(mappedEntityId)).thenReturn(Optional.of(entityFromRepo));
            Mockito.when(begruendungModelMapper.toModel(entityFromRepo)).thenReturn(mappedEntity);

            val result = unitUnderTest.getBegruendung(reference);

            Assertions.assertThat(result).isSameAs(mappedEntity);
        }

        @Test
        void should_returnFachlicheWlsException_when_begruendungIsInvalid() {
            val reference = BegruendungReference.builder().build();

            val exceptionToThrow = FachlicheWlsException.withCode("0815").buildWithMessage("upsi");

            Mockito.doThrow(exceptionToThrow).when(begruendungValidator).validReferenceOrThrow(reference);

            val exceptionThrown = Assertions.catchThrowable(() -> unitUnderTest.getBegruendung(reference));

            Assertions.assertThat(exceptionThrown).isSameAs(exceptionToThrow);
            Mockito.verify(begruendungRepository, Mockito.times(0)).findById(Mockito.any());
        }
    }

    @Nested
    class PostBegruendung {
        @Test
        void should_returnFachlicheWlsException_when_begruendungModelIsInvalid() {
            val invalidModel = BegruendungModel.builder().build();
            val reference = BegruendungReference.builder().build();

            val exceptionToThrow = FachlicheWlsException.withCode("0815").buildWithMessage("upsi");

            Mockito.doThrow(exceptionToThrow).when(begruendungValidator).validModelOrThrow(invalidModel);

            val exceptionThrown = Assertions.catchException(() -> unitUnderTest.postBegruendung(invalidModel, reference));

            Assertions.assertThat(exceptionThrown).isSameAs(exceptionToThrow);
            Mockito.verify(begruendungRepository, Mockito.times(0)).save(Mockito.any());
        }

        @Test
        void should_saveBegruendung_when_called() {
            val model = BegruendungModel.builder().build();
            val reference = BegruendungReference.builder().build();

            val mappedEntityOfModel = new Begruendung();

            Mockito.doNothing().when(begruendungValidator).validModelOrThrow(model);
            Mockito.doNothing().when(begruendungValidator).validReferenceOrThrow(reference);
            Mockito.when(begruendungModelMapper.toEntity(model)).thenReturn(mappedEntityOfModel);

            unitUnderTest.postBegruendung(model, reference);

            Mockito.verify(begruendungRepository).save(mappedEntityOfModel);
        }
    }
}
