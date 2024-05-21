package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.exception;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ServiceIDFormatter;
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
class ExceptionFactoryTest {

    @Mock
    ServiceIDFormatter serviceIDFormatter;

    @InjectMocks
    ExceptionFactory unitUnderTest;

    @Nested
    class CreateFachlicheWlsException {

        @Test
        void buildWithExceptionDataWrapperData() {
            val code = "0815";
            val message = "Everything Everywhere All at Once";
            val wrappedData = new ExceptionDataWrapper(code, message);

            val serviceID = "serviceID";
            Mockito.when(serviceIDFormatter.getId()).thenReturn(serviceID);

            val result = unitUnderTest.createFachlicheWlsException(wrappedData);

            val expectedResult = FachlicheWlsException.withCode(code).inService(serviceID).buildWithMessage(message);

            Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
        }

    }

}
