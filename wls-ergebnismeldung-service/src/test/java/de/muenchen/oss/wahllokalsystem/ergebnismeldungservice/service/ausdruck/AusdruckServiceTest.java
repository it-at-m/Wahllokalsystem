package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck;

import static org.mockito.ArgumentMatchers.eq;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.Ausdruck;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.AusdruckRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.WahlUndBezirkIDUndMeldungsart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validator;
import jakarta.validation.metadata.ConstraintDescriptor;
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
        void should_returnAusdruckModel_when_ausdruckIsfoundInRepo() {
            val id = new WahlUndBezirkIDUndMeldungsart();
            val mockedMappedEntityAsModel = new AusdruckModel(id, null, null);
            val mockedEntity = new Ausdruck();

            Mockito.when(ausdruckRepository.findOneByWahlUndBezirkIDUndMeldungsart(id)).thenReturn(mockedEntity);
            Mockito.when(ausdruckModelMapper.toModel(mockedEntity)).thenReturn(mockedMappedEntityAsModel);

            val result = unitUnderTest.getAusdruck(id);

            Assertions.assertThat(result).isEqualTo(mockedMappedEntityAsModel);
        }

        @Test
        void should_returnNull_when_ausdruckIsNotFoundInRepo() {
            val id = new WahlUndBezirkIDUndMeldungsart();
            Mockito.when(ausdruckRepository.findOneByWahlUndBezirkIDUndMeldungsart(id)).thenReturn(null);

            val result = unitUnderTest.getAusdruck(id);

            Assertions.assertThat(result).isNull();
        }
    }

    @Nested
    class SaveAusdruck {

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
