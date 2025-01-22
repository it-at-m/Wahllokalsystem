package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck;

import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.Ausdruck;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.AusdruckRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.Meldungsart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.WahlUndBezirkIDUndMeldungsart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validator;
import jakarta.validation.metadata.ConstraintDescriptor;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
class AusdruckServiceTest {

    @Mock
    AusdruckRepository ausdruckRepository;

    @Mock
    AusdruckModelMapper ausdruckModelMapper;

    @Mock
    WahlUndBezirkIDUndMeldungsartValidator wahlUndBezirkIDUndMeldungsartValidator;

    @Mock
    Validator validator;

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    AusdruckService unitUnderTest;

    @Nested
    class GetAusdruck {

        @Test
        void should_throwFachlicheWlsExceptionForParameter_when_callingValidator() {
            val id = new WahlUndBezirkIDUndMeldungsart();
            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("validation of parameters failed");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GET_AUSDRUCK_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);

            unitUnderTest.getAusdruck(id);

            Mockito.verify(wahlUndBezirkIDUndMeldungsartValidator).validWahlUndBezirkIDUndMeldungsartOrThrow(eq(id), eq(mockedWlsException));
        }

        @Test
        void should_returnAusdruckModel_when_ausdruckIsFoundInRepo() {
            val id = new WahlUndBezirkIDUndMeldungsart();
            val mockedMappedEntityAsModel = new AusdruckModel(id, null, null);
            val mockedEntity = new Ausdruck();

            Mockito.when(ausdruckRepository.findById(id)).thenReturn(Optional.of(mockedEntity));
            Mockito.when(ausdruckModelMapper.toModel(mockedEntity)).thenReturn(mockedMappedEntityAsModel);

            val result = unitUnderTest.getAusdruck(id);

            Assertions.assertThat(result.get()).isEqualTo(mockedMappedEntityAsModel);
        }

        @Test
        void should_returnEmptyOptional_when_notFound() {
            val id = new WahlUndBezirkIDUndMeldungsart();
            Mockito.when(ausdruckRepository.findById(id)).thenReturn(Optional.empty());

            val result = unitUnderTest.getAusdruck(id);

            Assertions.assertThat(result.isEmpty());
        }
    }

    @Nested
    class GetAllAusdrucke {

        @Test
        void should_retrieveAllAusdruckeFromRepo_when_dataFound() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val erstelltAm = Instant.now();
            val ausdruckEntityList = createListOfAusdruckEntities(wahlID, wahlbezirkID, erstelltAm);
            val ausdruckModelList = createListOfAusdruckModels(wahlID, wahlbezirkID, erstelltAm);

            Mockito.when(ausdruckRepository.findAllByWahlUndBezirkIDUndMeldungsart_WahlIDAndWahlUndBezirkIDUndMeldungsart_WahlbezirkID(wahlID, wahlbezirkID))
                    .thenReturn(
                            ausdruckEntityList);
            Mockito.when(ausdruckModelMapper.toModelList(ausdruckEntityList)).thenReturn(ausdruckModelList);

            val result = unitUnderTest.getAllAusdrucke(wahlID, wahlbezirkID);

            Mockito.verify(ausdruckRepository).findAllByWahlUndBezirkIDUndMeldungsart_WahlIDAndWahlUndBezirkIDUndMeldungsart_WahlbezirkID(wahlID, wahlbezirkID);
            Assertions.assertThat(result).isEqualTo(ausdruckModelList);
        }

        private List<AusdruckModel> createListOfAusdruckModels(String wahlbezirkID, String wahlID, Instant erstelltAm) {
            val content = "Testausdruck";

            val ausdruckModel1 = new AusdruckModel(new WahlUndBezirkIDUndMeldungsart(wahlbezirkID, wahlID, Meldungsart.V1), content, erstelltAm);
            val ausdruckModel2 = new AusdruckModel(new WahlUndBezirkIDUndMeldungsart(wahlbezirkID, wahlID, Meldungsart.V3), content, erstelltAm);

            return List.of(ausdruckModel1, ausdruckModel2);

        }

        private List<Ausdruck> createListOfAusdruckEntities(String wahlbezirkID, String wahlID, Instant erstelltAm) {
            val content = "Testausdruck";

            val ausdruckEntity1 = new Ausdruck(new WahlUndBezirkIDUndMeldungsart(wahlbezirkID, wahlID, Meldungsart.V1), content, erstelltAm);
            val ausdruckEntity2 = new Ausdruck(new WahlUndBezirkIDUndMeldungsart(wahlbezirkID, wahlID, Meldungsart.V3), content, erstelltAm);

            return List.of(ausdruckEntity1, ausdruckEntity2);
        }
    }

    @Nested
    class SaveAusdruck {

        @Test
        void should_saveMappedAusdruckModel_when_called() {
            val id = new WahlUndBezirkIDUndMeldungsart();
            val ausdruckModelToSave = new AusdruckModel(id, null, null);
            val mockedModelAsEntity = Mockito.mock(Ausdruck.class);

            Mockito.when(ausdruckModelMapper.toEntity(ausdruckModelToSave)).thenReturn(mockedModelAsEntity);

            unitUnderTest.saveAusdruck(ausdruckModelToSave);

            Mockito.verify(ausdruckRepository).save(mockedModelAsEntity);
        }

        @Test
        void should_throwRepositoryException_when_savingFailed() {
            val id = new WahlUndBezirkIDUndMeldungsart();
            val ausdruckModelToSave = new AusdruckModel(id, null, null);
            val mockedModelAsEntity = Mockito.mock(Ausdruck.class);

            val mockedRepositorySaveException = new RuntimeException("saving failed");

            Mockito.when(ausdruckModelMapper.toEntity(ausdruckModelToSave)).thenReturn(mockedModelAsEntity);
            Mockito.doThrow(mockedRepositorySaveException).when(ausdruckRepository).save(mockedModelAsEntity);

            Assertions.assertThatThrownBy(() -> unitUnderTest.saveAusdruck(ausdruckModelToSave)).isSameAs(mockedRepositorySaveException);
        }

        @Test
        void should_throwFachlicheWlsExceptionForParameter_when_callingValidator() {
            val id = new WahlUndBezirkIDUndMeldungsart();
            val ausdruckModelToSave = new AusdruckModel(id, null, null);

            val mockedWlsException = FachlicheWlsException.withCode("").buildWithMessage("postAusdruck: Parameter unvollstaendig");

            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_AUSDRUCK_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(mockedWlsException);
            Mockito.when(validator.validate(ausdruckModelToSave)).thenReturn(Set.of(new TestConstraintViolation()));

            Assertions.assertThatThrownBy(() -> unitUnderTest.saveAusdruck(ausdruckModelToSave)).isSameAs(mockedWlsException);
        }

        static class TestConstraintViolation implements ConstraintViolation<AusdruckModel> {

            @Override
            public String getMessage() {
                return "";
            }

            @Override
            public String getMessageTemplate() {
                return "";
            }

            @Override
            public AusdruckModel getRootBean() {
                return null;
            }

            @Override
            public Class<AusdruckModel> getRootBeanClass() {
                return null;
            }

            @Override
            public Object getLeafBean() {
                return null;
            }

            @Override
            public Object[] getExecutableParameters() {
                return new Object[0];
            }

            @Override
            public Object getExecutableReturnValue() {
                return null;
            }

            @Override
            public Path getPropertyPath() {
                return null;
            }

            @Override
            public Object getInvalidValue() {
                return null;
            }

            @Override
            public ConstraintDescriptor<?> getConstraintDescriptor() {
                return null;
            }

            @Override
            public <U> U unwrap(Class<U> aClass) {
                return null;
            }
        }
    }
}
