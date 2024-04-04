package de.muenchen.oss.wahllokalsystem.wls.common.exception.errorhandler;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.SicherheitsWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.DTOMapper;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionKonstanten;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class AbstractExceptionHandlerTest {

    private static final String GET_SERVICE_RESULT = "theService";

    private final DTOMapper dtoMapper = Mockito.mock(DTOMapper.class);

    final AbstractExceptionHandler unitUnderTest = new AbstractExceptionHandler(dtoMapper) {
        @Override
        protected String getService() {
            return GET_SERVICE_RESULT;
        }
    };

    @Nested
    class GetWahlExceptionDTO {

        @Test
        void handleWlsException() {
            val code = "089";
            val serviceName = "service name";
            val cause = new NullPointerException("sth null");
            val message = "message for exception";
            val exceptionToHandle = SicherheitsWlsException.withCode(code).inService(serviceName).withCause(cause).buildWithMessage(message);

            val mappedDTO = new WlsExceptionDTO(WlsExceptionCategory.S, code, serviceName, message);
            Mockito.when(dtoMapper.toDTO(exceptionToHandle)).thenReturn(mappedDTO);

            val result = unitUnderTest.getWahlExceptionDTO(exceptionToHandle);

            Assertions.assertThat(result).isSameAs(mappedDTO);
        }

        @Test
        void handleAccessDeniedException() {
            val causingException = new NullPointerException("some was null");
            val accessDeniedException = new AccessDeniedException("you shall not pass", causingException);

            val result = unitUnderTest.getWahlExceptionDTO(accessDeniedException);

            val expectedResult = new WlsExceptionDTO(WlsExceptionCategory.S, ExceptionKonstanten.CODE_SECURITY_ACCESS_DENIED, GET_SERVICE_RESULT,
                    accessDeniedException.getMessage());

            Assertions.assertThat(result).isEqualTo(expectedResult);

        }

        @Test
        void handleHttpMessageNotReadableException() {
            val cause = new IllegalArgumentException("text is not a fax");
            final HttpInputMessage inputMessage = (null);
            val messageNotReadableException = new HttpMessageNotReadableException("wrong type of message", cause, inputMessage);

            val result = unitUnderTest.getWahlExceptionDTO(messageNotReadableException);

            val expectedResult = new WlsExceptionDTO(WlsExceptionCategory.F, ExceptionKonstanten.CODE_HTTP_MESSAGE_NOT_READABLE, GET_SERVICE_RESULT,
                    "HTTP-Nachricht nicht lesbar: " + messageNotReadableException.getMessage());

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        void handleTransientDataAccessException() {
            val causingException = new NullPointerException("some was null");
            val transientException = new TransientDataAccessException("my TransientDataAccessException message", causingException) {

            };

            val result = unitUnderTest.getWahlExceptionDTO(transientException);

            val expectedResult = new WlsExceptionDTO(WlsExceptionCategory.T, ExceptionKonstanten.CODE_TRANSIENT, GET_SERVICE_RESULT,
                    "Temporäres Problem, Ursache: " + transientException.getClass() + ", Nachricht: " + transientException.getMessage());

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        void handleAnythingElse() {
            val nullPointerException = new NullPointerException("object was null");

            val result = unitUnderTest.getWahlExceptionDTO(nullPointerException);

            val expectedResult = new WlsExceptionDTO(WlsExceptionCategory.T, ExceptionKonstanten.CODE_ALLGEMEIN_UNBEKANNT, GET_SERVICE_RESULT,
                    "Ursache: " + nullPointerException.getClass() + ", Nachricht: " + nullPointerException.getMessage());

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Test
    void createForTransientException() {
        val causingException = new NullPointerException("some was null");
        val transientException = new TransientDataAccessException("my TransientDataAccessException message", causingException) {

        };

        val result = unitUnderTest.createForTransientException(transientException);

        val expectedResult = new WlsExceptionDTO(WlsExceptionCategory.T, ExceptionKonstanten.CODE_TRANSIENT, GET_SERVICE_RESULT,
                "Temporäres Problem, Ursache: " + transientException.getClass() + ", Nachricht: " + transientException.getMessage());

        Assertions.assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void createForAccessDeniedException() {
        val causingException = new NullPointerException("some was null");
        val accessDeniedException = new AccessDeniedException("you shall not pass", causingException);

        val result = unitUnderTest.createForAccessDeniedException(accessDeniedException);

        val expectedResult = new WlsExceptionDTO(WlsExceptionCategory.S, ExceptionKonstanten.CODE_SECURITY_ACCESS_DENIED, GET_SERVICE_RESULT,
                accessDeniedException.getMessage());

        Assertions.assertThat(result).isEqualTo(expectedResult);
    }

    @Nested
    class CreateResponse {

        @Test
        void categoryIsNull() {
            val wlsExceptionDTO = createWlsExceptionDTOWithCategory(null);

            val result = unitUnderTest.createResponse(wlsExceptionDTO);

            val expectedResult = ResponseEntity.internalServerError().body(wlsExceptionDTO);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        void categoryIsFachlichNotFound() {
            val wlsExceptionDTO = createWlsExceptionDTOWithCategoryAndCode(WlsExceptionCategory.F, ExceptionKonstanten.CODE_ENTITY_NOT_FOUND);

            val result = unitUnderTest.createResponse(wlsExceptionDTO);

            val expectedResult = ResponseEntity.noContent().build();

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        void categoryIsFachlichNotNotFound() {
            val wlsExceptionDTO = createWlsExceptionDTOWithCategoryAndCode(WlsExceptionCategory.F, ExceptionKonstanten.CODE_ENTITY_NOT_FOUND + "ABC");

            val result = unitUnderTest.createResponse(wlsExceptionDTO);

            val expectedResult = ResponseEntity.badRequest().body(wlsExceptionDTO);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        void categoryIsTechnischNotTransient() {
            val wlsExceptionDTO = createWlsExceptionDTOWithCategoryAndCode(WlsExceptionCategory.T, ExceptionKonstanten.CODE_TRANSIENT + "ABC");

            val result = unitUnderTest.createResponse(wlsExceptionDTO);

            val expectedResult = ResponseEntity.internalServerError().body(wlsExceptionDTO);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        void categoryIsTechnischTransient() {
            val wlsExceptionDTO = createWlsExceptionDTOWithCategoryAndCode(WlsExceptionCategory.T, ExceptionKonstanten.CODE_TRANSIENT);

            val result = unitUnderTest.createResponse(wlsExceptionDTO);

            val expectedResult = ResponseEntity.status(HttpStatus.CONFLICT).body(wlsExceptionDTO);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        void categoryIsSicherheit() {
            val wlsExceptionDTO = createWlsExceptionDTOWithCategory(WlsExceptionCategory.S);

            val result = unitUnderTest.createResponse(wlsExceptionDTO);

            val expectedResult = ResponseEntity.status(HttpStatus.FORBIDDEN).body(wlsExceptionDTO);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        void categoryIsInfrastruktur() {
            val wlsExceptionDTO = createWlsExceptionDTOWithCategory(WlsExceptionCategory.I);

            val result = unitUnderTest.createResponse(wlsExceptionDTO);

            val expectedResult = ResponseEntity.internalServerError().body(wlsExceptionDTO);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        private WlsExceptionDTO createWlsExceptionDTOWithCategory(final WlsExceptionCategory category) {
            return createWlsExceptionDTOWithCategoryAndCode(category, "code");
        }

        private WlsExceptionDTO createWlsExceptionDTOWithCategoryAndCode(final WlsExceptionCategory category, final String code) {
            return new WlsExceptionDTO(category, code, "service", "message");
        }
    }

}
