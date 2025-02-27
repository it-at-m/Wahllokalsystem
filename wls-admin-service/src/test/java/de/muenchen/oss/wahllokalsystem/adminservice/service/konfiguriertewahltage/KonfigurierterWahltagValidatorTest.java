package de.muenchen.oss.wahllokalsystem.adminservice.service.konfiguriertewahltage;

import de.muenchen.oss.wahllokalsystem.adminservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.adminservice.service.common.KonfigurierterWahltagModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.konfigurierterwahltag.KonfigurierterWahltagValidator;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.time.LocalDate;
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
class KonfigurierterWahltagValidatorTest {

    @Mock
    ExceptionFactory exceptionFactory;

    @InjectMocks
    KonfigurierterWahltagValidator unitUnderTest;

    @Nested
    class ValidModel {

        @Test
        void should_notThrowException_when_modelIDIsValid() {
            val modelToValidate = new KonfigurierterWahltagModel(LocalDate.now(), "wahltagID", true, "0");

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.validateModel(modelToValidate));
        }

        @Test
        void should_throwException_when_modelIsNull() {
            val mockedWlsException = FachlicheWlsException.withCode("0").buildWithMessage("fail");
            Mockito.when(exceptionFactory.createFachlicheWlsException(ExceptionConstants.MISSING_ARGUMENT)).thenReturn(mockedWlsException);

            Assertions.assertThatException().isThrownBy(() -> unitUnderTest.validateModel(null)).isSameAs(mockedWlsException);
        }
    }
}
