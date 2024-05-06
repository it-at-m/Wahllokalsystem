package de.muenchen.oss.wahllokalsystem.briefwahlservice.service.beanstandetewahlbriefe;

import de.muenchen.oss.wahllokalsystem.briefwahlservice.domain.BeanstandeteWahlbriefe;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.domain.BeanstandeteWahlbriefeRepository;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
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
class BeanstandeteWahlbriefeServiceTest {

    @Mock
    BeanstandeteWahlbriefeRepository beanstandeteWahlbriefeRepository;

    @Mock
    BeanstandeteWahlbriefeModelMapper beanstandeteWahlbriefeModelMapper;

    @Mock
    BeanstandeteWahlbriefeValidator beanstandeteWahlbriefeValidator;

    @InjectMocks
    BeanstandeteWahlbriefeService service;

    @Nested
    class GetBeanstandeteWahlbriefe {

        @Test
        void nullFromRepo() {
            val reference = BeanstandeteWahlbriefeReference.builder().build();

            val mappedEntityId = new BezirkIDUndWaehlerverzeichnisNummer();

            Mockito.doNothing().when(beanstandeteWahlbriefeValidator).valideReferenceOrThrow(reference);
            Mockito.when(beanstandeteWahlbriefeModelMapper.toEmbeddedId(reference)).thenReturn(mappedEntityId);
            Mockito.when(beanstandeteWahlbriefeRepository.findById(mappedEntityId)).thenReturn(Optional.empty());

            val result = service.getBeanstandeteWahlbriefe(reference);

            Assertions.assertThat(result).isNull();
        }

        @Test
        void dataFromRepo() {
            val reference = BeanstandeteWahlbriefeReference.builder().build();

            val mappedEntityId = new BezirkIDUndWaehlerverzeichnisNummer();
            val entityFromRepo = new BeanstandeteWahlbriefe();
            val mappedEntity = BeanstandeteWahlbriefeModel.builder().build();

            Mockito.doNothing().when(beanstandeteWahlbriefeValidator).valideReferenceOrThrow(reference);
            Mockito.when(beanstandeteWahlbriefeModelMapper.toEmbeddedId(reference)).thenReturn(mappedEntityId);
            Mockito.when(beanstandeteWahlbriefeRepository.findById(mappedEntityId)).thenReturn(Optional.of(entityFromRepo));
            Mockito.when(beanstandeteWahlbriefeModelMapper.toModel(entityFromRepo)).thenReturn(mappedEntity);

            val result = service.getBeanstandeteWahlbriefe(reference);

            Assertions.assertThat(result).isSameAs(mappedEntity);
        }

        @Test
        void noRepoRequestWhenReferenceIsInvalid() {
            val reference = BeanstandeteWahlbriefeReference.builder().build();

            val exceptionToThrow = FachlicheWlsException.withCode("0815").buildWithMessage("upsi");

            Mockito.doThrow(exceptionToThrow).when(beanstandeteWahlbriefeValidator).valideReferenceOrThrow(reference);

            val exceptionThrown = Assertions.catchThrowable(() -> service.getBeanstandeteWahlbriefe(reference));

            Assertions.assertThat(exceptionThrown).isSameAs(exceptionToThrow);
            Mockito.verify(beanstandeteWahlbriefeRepository, Mockito.times(0)).findById(Mockito.any());
        }

    }

    @Nested
    class SetBeanstandeteWahlbriefe {
        @Test
        void noSaveWhenModelIsInvalid() {
            val invalidModel = BeanstandeteWahlbriefeModel.builder().build();

            val exceptionToThrow = FachlicheWlsException.withCode("0815").buildWithMessage("upsi");

            Mockito.doThrow(exceptionToThrow).when(beanstandeteWahlbriefeValidator).valideModelOrThrow(invalidModel);

            val exceptionThrown = Assertions.catchException(() -> service.setBeanstandeteWahlbriefe(invalidModel));

            Assertions.assertThat(exceptionThrown).isSameAs(exceptionToThrow);
            Mockito.verify(beanstandeteWahlbriefeRepository, Mockito.times(0)).save(Mockito.any());
        }

        @Test
        void modelIsSaved() {
            val model = BeanstandeteWahlbriefeModel.builder().build();

            val mappedEntityOfModel = new BeanstandeteWahlbriefe();

            Mockito.doNothing().when(beanstandeteWahlbriefeValidator).valideModelOrThrow(model);
            Mockito.when(beanstandeteWahlbriefeModelMapper.toEntity(model)).thenReturn(mappedEntityOfModel);

            service.setBeanstandeteWahlbriefe(model);

            Mockito.verify(beanstandeteWahlbriefeRepository).save(mappedEntityOfModel);
        }
    }
}
