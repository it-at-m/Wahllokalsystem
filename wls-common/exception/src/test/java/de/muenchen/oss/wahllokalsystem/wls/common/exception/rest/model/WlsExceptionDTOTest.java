package de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionKonstanten;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.LoggerExtension;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

class WlsExceptionDTOTest {

    @Nested
    class Constructor {

        @Test
        void should_onlyHaveDefinedConstructorWithNullHandling_when_checkingAvailableConstructors() {
            Assertions.assertThat(WlsExceptionDTO.class.getConstructors().length).withFailMessage("es sollte nur den überschrieben Konstruktur geben")
                    .isEqualTo(1);
        }

        @Test
        void should_useDefaultValues_when_isCalledWithNullArguments() {
            val result = new WlsExceptionDTO(null, null, null, null);

            val expectedResult = new WlsExceptionDTO(WlsExceptionCategory.T, ExceptionKonstanten.CODE_ALLGEMEIN_UNBEKANNT,
                    ExceptionKonstanten.SERVICE_UNBEKANNT,
                    ExceptionKonstanten.MESSAGE_UNBEKANNTER_FEHLER);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        void should_assignParametersCorrectly_when_calledWithParameters() {
            val category = WlsExceptionCategory.I;
            val code = "089233";
            val service = "WLS3.0 TestService";
            val message = "Lorem ipsum";

            val result = new WlsExceptionDTO(category, code, service, message);

            Assertions.assertThat(result.category()).isEqualTo(category);
            Assertions.assertThat(result.code()).isEqualTo(code);
            Assertions.assertThat(result.message()).isEqualTo(message);
            Assertions.assertThat(result.service()).isEqualTo(service);
        }

        @Nested
        class TestLoggingEvents {

            @RegisterExtension
            public LoggerExtension loggerExtension = new LoggerExtension();

            @Test
            void should_creteNoLoggEvents_when_allParametersArNonNull() {
                new WlsExceptionDTO(WlsExceptionCategory.T, "code", "service", "message");

                Assertions.assertThat(loggerExtension.getFormattedMessages().isEmpty()).isTrue();
            }

            @Test
            void should_createWarn_when_categoryIsNull() {
                new WlsExceptionDTO(null, "code", "service", "message");

                Assertions.assertThat(loggerExtension.getFormattedMessages().size()).isEqualTo(1);
            }

            @Test
            void should_createWarn_when_codeIsNull() {
                new WlsExceptionDTO(WlsExceptionCategory.T, null, "service", "message");

                Assertions.assertThat(loggerExtension.getFormattedMessages().size()).isEqualTo(1);
            }

            @Test
            void should_createWarn_when_serviceIsNull() {
                new WlsExceptionDTO(WlsExceptionCategory.T, "code", null, "message");

                Assertions.assertThat(loggerExtension.getFormattedMessages().size()).isEqualTo(1);
            }

            @Test
            void should_createWarn_when_messageIsNull() {
                new WlsExceptionDTO(WlsExceptionCategory.T, "code", "service", null);

                Assertions.assertThat(loggerExtension.getFormattedMessages().size()).isEqualTo(1);
            }
        }
    }

}
