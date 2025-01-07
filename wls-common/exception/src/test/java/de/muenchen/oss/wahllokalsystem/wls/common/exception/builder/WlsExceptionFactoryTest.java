package de.muenchen.oss.wahllokalsystem.wls.common.exception.builder;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.model.WlsExceptionData;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WlsExceptionFactoryTest {

    @Mock
    private WlsExceptionCreator<WlsException> mockedCreator;

    @Nested
    class BuildWithMessage {

        @Test
        void should_returnWlsExceptionWithException_when_buildWithMessageIsCalled() {
            val unitUnderTest = new WlsExceptionFactory<>(mockedCreator);

            val dataForBuild = new WlsExceptionData("message", "serviceName", "code", new NullPointerException("npe"));
            val mockedWlsException = FachlicheWlsException.withCode(dataForBuild.getCode()).buildWithMessage(dataForBuild.getMessage());
            Mockito.when(mockedCreator.createWlsException(dataForBuild)).thenReturn(mockedWlsException);

            unitUnderTest.withCode(dataForBuild.getCode());
            unitUnderTest.inService(dataForBuild.getServiceName());
            unitUnderTest.withCause(dataForBuild.getCause());
            val createdWlsException = unitUnderTest.buildWithMessage(dataForBuild.getMessage());

            Assertions.assertThat(createdWlsException).isSameAs(mockedWlsException);
        }
    }

    @Nested
    class WithCode {

        @Test
        void should_returnFactoryItself_when_called() {
            val unitUnderTest = new WlsExceptionFactory<>(mockedCreator);

            Assertions.assertThat(unitUnderTest.withCode("code")).isSameAs(unitUnderTest);
        }
    }

    @Nested
    class InService {

        @Test
        void should_returnFactoryItself_when_called() {
            val unitUnderTest = new WlsExceptionFactory<>(mockedCreator);

            Assertions.assertThat(unitUnderTest.inService("service")).isSameAs(unitUnderTest);
        }
    }

    @Nested
    class WithCause {

        @Test
        void should_returnFactoryItself_when_called() {
            val unitUnderTest = new WlsExceptionFactory<>(mockedCreator);

            Assertions.assertThat(unitUnderTest.withCause(new NullPointerException("npe"))).isSameAs(unitUnderTest);
        }
    }

}
