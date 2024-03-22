package de.muenchen.oss.wahllokalsystem.wls.common.exception.errorhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.InfrastrukturelleWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.SicherheitsWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionKonstanten;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.client.ClientHttpResponse;

@ExtendWith(MockitoExtension.class)
class WlsResponseErrorHandlerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ClientHttpResponse response;

    @InjectMocks
    private WlsResponseErrorHandler unitUnderTest;

    @Nested
    class HandleError {

        @Test
        void fachlicheExceptionCreatedFromResponse() throws Exception {
            val mockedResponseBody = new ByteArrayInputStream("mocked response body as stream".getBytes());
            Mockito.when(response.getBody()).thenReturn(mockedResponseBody);

            val fachlicheWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F, "code", "serviceName", "message");
            Mockito.when(objectMapper.readValue(mockedResponseBody, WlsExceptionDTO.class)).thenReturn(fachlicheWlsExceptionDTO);

            val exceptionThrown = Assertions.catchThrowableOfType(() -> unitUnderTest.handleError(response), FachlicheWlsException.class);

            assertAllWlsExceptionsAreSetExceptCategory(exceptionThrown, fachlicheWlsExceptionDTO);
        }

        @Test
        void technischeExceptionCreatedFromResponse() throws Exception {
            val mockedResponseBody = new ByteArrayInputStream("mocked response body as stream".getBytes());
            Mockito.when(response.getBody()).thenReturn(mockedResponseBody);

            val fachlicheWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.T, "code", "serviceName", "message");
            Mockito.when(objectMapper.readValue(mockedResponseBody, WlsExceptionDTO.class)).thenReturn(fachlicheWlsExceptionDTO);

            val exceptionThrown = Assertions.catchThrowableOfType(() -> unitUnderTest.handleError(response), TechnischeWlsException.class);

            assertAllWlsExceptionsAreSetExceptCategory(exceptionThrown, fachlicheWlsExceptionDTO);
        }

        @Test
        void securityExceptionCreatedFromResponse() throws Exception {
            val mockedResponseBody = new ByteArrayInputStream("mocked response body as stream".getBytes());
            Mockito.when(response.getBody()).thenReturn(mockedResponseBody);

            val fachlicheWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.S, "code", "serviceName", "message");
            Mockito.when(objectMapper.readValue(mockedResponseBody, WlsExceptionDTO.class)).thenReturn(fachlicheWlsExceptionDTO);

            val exceptionThrown = Assertions.catchThrowableOfType(() -> unitUnderTest.handleError(response), SicherheitsWlsException.class);

            assertAllWlsExceptionsAreSetExceptCategory(exceptionThrown, fachlicheWlsExceptionDTO);
        }

        @Test
        void infrastrukturExceptionCreatedFromResponse() throws Exception {
            val mockedResponseBody = new ByteArrayInputStream("mocked response body as stream".getBytes());
            Mockito.when(response.getBody()).thenReturn(mockedResponseBody);

            val fachlicheWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.I, "code", "serviceName", "message");
            Mockito.when(objectMapper.readValue(mockedResponseBody, WlsExceptionDTO.class)).thenReturn(fachlicheWlsExceptionDTO);

            val exceptionThrown = Assertions.catchThrowableOfType(() -> unitUnderTest.handleError(response), InfrastrukturelleWlsException.class);

            assertAllWlsExceptionsAreSetExceptCategory(exceptionThrown, fachlicheWlsExceptionDTO);
        }

        @Test
        void exceptionDuringCreationOfExceptionFromResponse() throws Exception {
            val exceptionThrownByMappingResponse = new IOException("something strange happened");
            Mockito.when(response.getBody()).thenThrow(exceptionThrownByMappingResponse);

            val exceptionThrown = Assertions.catchThrowableOfType(() -> unitUnderTest.handleError(response), TechnischeWlsException.class);

            val expectedExceptionMessage = ExceptionKonstanten.MESSAGE_UNBEKANNTER_FEHLER + "\nWegen: " + exceptionThrownByMappingResponse.getClass()
                    + " mit:\n" + exceptionThrownByMappingResponse.getMessage();
            val expectedException = TechnischeWlsException.withCode(ExceptionKonstanten.CODE_ALLGEMEIN_UNBEKANNT)
                    .inService(ExceptionKonstanten.SERVICE_UNBEKANNT).withCause(exceptionThrownByMappingResponse)
                    .buildWithMessage(expectedExceptionMessage);

            assertWlsExceptionPropertiesAreEqual(exceptionThrown, expectedException);
        }

        private void assertAllWlsExceptionsAreSetExceptCategory(final WlsException exceptionToCheck, final WlsExceptionDTO dataToBeSet) {
            //category muss nicht geprüft werden da diese durch die Exception-Klasse gesetzt wird (siehe deren Konstruktor)
            Assertions.assertThat(exceptionToCheck.getCode()).isEqualTo(dataToBeSet.code());
            Assertions.assertThat(exceptionToCheck.getMessage()).isEqualTo(dataToBeSet.message());
            Assertions.assertThat(exceptionToCheck.getServiceName()).isEqualTo(dataToBeSet.service());
            Assertions.assertThat(exceptionToCheck.getCause()).isNull();
        }

        private void assertWlsExceptionPropertiesAreEqual(final WlsException actual, final WlsException expected) {
            Assertions.assertThat(actual.getCause()).isEqualTo(expected.getCause());
            Assertions.assertThat(actual.getCode()).isEqualTo(expected.getCode());
            Assertions.assertThat(actual.getCategory()).isEqualTo(expected.getCategory());
            Assertions.assertThat(actual.getServiceName()).isEqualTo(expected.getServiceName());
            Assertions.assertThat(actual.getMessage()).isEqualTo(expected.getMessage());

        }
    }

}
