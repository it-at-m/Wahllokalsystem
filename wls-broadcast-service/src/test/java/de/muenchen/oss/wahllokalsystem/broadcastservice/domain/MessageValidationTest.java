package de.muenchen.oss.wahllokalsystem.broadcastservice.domain;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MessageValidationTest {

    @Nested
    class ValidationTest {

        private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        private final Validator validator = factory.getValidator();

        @Test
        void should_succeedValidation_when_allRequiredFieldsAreSet() {
            val message = createMessageWithAllRequiredData();

            val validationResult = validator.validate(message);

            Assertions.assertThat(validationResult.isEmpty()).isTrue();
        }

        @Test
        void should_failValidation_when_wahlbezirkIdMissing() {
            val message = createMessageWithAllRequiredData();
            message.setWahlbezirkID(null);

            val validationResult = validator.validate(message);

            Assertions.assertThat(validationResult.size()).isEqualTo(1);
        }

        @Test
        void should_failValidation_when_wahlbezirkIdLargerThan1024Chars() {
            val message = createMessageWithAllRequiredData();
            String myString = " ".repeat(1025);
            message.setWahlbezirkID(myString);

            val validationResult = validator.validate(message);

            Assertions.assertThat(validationResult.size()).isEqualTo(1);
        }

        @Test
        void should_failValidation_when_messageIsNull() {
            val message = createMessageWithAllRequiredData();
            message.setNachricht(null);

            val validationResult = validator.validate(message);

            Assertions.assertThat(validationResult.size()).isEqualTo(1);
        }

        @Test
        void should_failValidation_when_messageLargerThan1024Chars() {
            val message = createMessageWithAllRequiredData();
            String myString = " ".repeat(1025);
            message.setNachricht(myString);

            val validationResult = validator.validate(message);

            Assertions.assertThat(validationResult.size()).isEqualTo(1);
        }

        @Test
        void should_failValidation_when_messageEmpfangsZeitIsNull() {
            val message = createMessageWithAllRequiredData();
            message.setEmpfangsZeit(null);

            val validationResult = validator.validate(message);

            Assertions.assertThat(validationResult.size()).isEqualTo(1);
        }

        @Test
        void should_succeedValidation_when_wahlbezirkIdHas1024Chars() {
            val message = createMessageWithAllRequiredData();
            message.setWahlbezirkID(StringUtils.left("", 1024));

            val validationResult = validator.validate(message);

            Assertions.assertThat(validationResult.isEmpty()).isTrue();
        }

        @Test
        void should_succeedValidation_when_messageHas1024Chars() {
            val message = createMessageWithAllRequiredData();
            message.setNachricht(StringUtils.left("", 1024));

            val validationResult = validator.validate(message);

            Assertions.assertThat(validationResult.isEmpty()).isTrue();
        }

        private Message createMessageWithAllRequiredData() {
            return new Message(UUID.randomUUID(), "", "", LocalDateTime.now());
        }
    }
}
