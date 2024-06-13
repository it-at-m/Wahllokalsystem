package de.muenchen.oss.wahllokalsystem.wls.common.exception.util;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.InfrastrukturelleWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.SicherheitsWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.val;
import org.assertj.core.api.Assertions;
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

    @Test
    void createFachlicheWlsException() {
        val code = "0815";
        val message = "Everything Everywhere All at Once";
        val wrappedData = new ExceptionDataWrapper(code, message);

        val serviceID = "serviceID";
        Mockito.when(serviceIDFormatter.getId()).thenReturn(serviceID);

        val result = unitUnderTest.createFachlicheWlsException(wrappedData);

        val expectedResult = FachlicheWlsException.withCode(code).inService(serviceID).buildWithMessage(message);

        Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @Test
    void createTechnischeWlsException() {
        val code = "0815";
        val message = "Everything Everywhere All at Once";
        val wrappedData = new ExceptionDataWrapper(code, message);

        val serviceID = "serviceID";
        Mockito.when(serviceIDFormatter.getId()).thenReturn(serviceID);

        val result = unitUnderTest.createTechnischeWlsException(wrappedData);

        val expectedResult = TechnischeWlsException.withCode(code).inService(serviceID).buildWithMessage(message);

        Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @Test
    void createInfrastrukturelleWlsException() {
        val code = "0815";
        val message = "Everything Everywhere All at Once";
        val wrappedData = new ExceptionDataWrapper(code, message);

        val serviceID = "serviceID";
        Mockito.when(serviceIDFormatter.getId()).thenReturn(serviceID);

        val result = unitUnderTest.createInfrastrukturelleWlsException(wrappedData);

        val expectedResult = InfrastrukturelleWlsException.withCode(code).inService(serviceID).buildWithMessage(message);

        Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @Test
    void createSicherheitsWlsException() {
        val code = "0815";
        val message = "Everything Everywhere All at Once";
        val wrappedData = new ExceptionDataWrapper(code, message);

        val serviceID = "serviceID";
        Mockito.when(serviceIDFormatter.getId()).thenReturn(serviceID);

        val result = unitUnderTest.createSicherheitsWlsException(wrappedData);

        val expectedResult = SicherheitsWlsException.withCode(code).inService(serviceID).buildWithMessage(message);

        Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @Test
    void verifyAllWlsExceptionsAreSupportedAndMethodSignaturesFollowsCommonDesign() {
        val createMethodNamePrefix = "create";
        val permittedSubclassed = WlsException.class.getPermittedSubclasses();
        val createMethods = Arrays.stream(unitUnderTest.getClass().getMethods())
                .filter(method -> Arrays.equals(method.getParameterTypes(), new Class<?>[] { ExceptionDataWrapper.class }))
                .filter(method -> method.getName().startsWith(createMethodNamePrefix))
                .collect(Collectors.toMap(Method::getReturnType, Method::getName));

        Assertions.assertThat(permittedSubclassed).allSatisfy(permittedSubclass -> Assertions.assertThat(createMethods.get(permittedSubclass))
                .isEqualTo(createMethodNamePrefix + permittedSubclass.getSimpleName()));
    }

}
