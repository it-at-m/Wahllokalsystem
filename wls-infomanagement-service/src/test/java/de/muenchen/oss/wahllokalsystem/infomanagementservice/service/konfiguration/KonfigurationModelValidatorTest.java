package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KonfigurationKonfigKey;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KonfigurationSetModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
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
class KonfigurationModelValidatorTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    KonfigurationModelValidator unitUnderTest;

    @Nested
    class ValidOrThrowGetKonfigurationByKey {

        @Test
        void noExceptionOnNonNull() {
            Assertions.assertThatNoException()
                    .isThrownBy(() -> unitUnderTest.validOrThrowGetKonfigurationByKey(KonfigurationKonfigKey.WILLKOMMENSTEXT));
        }

        @Test
        void exceptionOnNullKey() {
            val expectedException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETKONFIGURATION_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(expectedException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validOrThrowGetKonfigurationByKey(null)).isSameAs(expectedException);
        }
    }

    @Nested
    class ValidOrThrowSetKonfiguration {

        @Test
        void noExceptionWhenValid() {
            val validModel = initValidModel().build();

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validOrThrowSetKonfiguration(validModel));
        }

        @Test
        void exceptionWhenModelIsNull() {
            val expectedException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTKONFIGURATION_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(expectedException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validOrThrowSetKonfiguration(null)).isSameAs(expectedException);
        }

        @Test
        void exceptionWhenSchluesselIsNull() {
            val invalidModel = initValidModel().schluessel(null).build();

            val expectedException = FachlicheWlsException.withCode("").buildWithMessage("");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTKONFIGURATION_PARAMETER_UNVOLLSTAENDIG))
                    .thenReturn(expectedException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validOrThrowSetKonfiguration(invalidModel)).isSameAs(expectedException);
        }

        private KonfigurationSetModel.KonfigurationSetModelBuilder initValidModel() {
            return KonfigurationSetModel.builder().schluessel("schluessel");
        }
    }

}
