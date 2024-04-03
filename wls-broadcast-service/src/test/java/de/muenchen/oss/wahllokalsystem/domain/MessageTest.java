package de.muenchen.oss.wahllokalsystem.domain;

import jakarta.validation.ValidatorFactory;
import jakarta.validation.Validator;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

class MessageTest {

    @Nested
    class Validation {

        private final ValidatorFactory factory = ValidatorFactory.buildDefaultValidatorFactory();
        private final Validator validator = factory.getValidator();


        @Test
        void allRequiredFieldsAreSet() {
            val message = createMessageWithAllRequiredData();

            val validationResult = validator.validate(message);

            Assertions.assertThat(validationResult.isEmpty()).isTrue();
        }

        @Test
        void failurOnWahlbezirkIdIsMissing() {
            val message = createMessageWithAllRequiredData();
            message.setWahlbezirkID(null);

            val validationResult = validator.validate(message);

            Assertions.assertThat(validationResult.size()).isEqualTo(1);
        }

        @Test
        void failurOnWahlbezirkIdIsMoreThan1024Chars() {
            val message = createMessageWithAllRequiredData();
            message.setWahlbezirkID(StringUtils.left("", 1025));

            val validationResult = validator.validate(message);

            Assertions.assertThat(validationResult.size()).isEqualTo(1);
        }

        @Test
        void failurOnNachrichtIsNull() {
            val message = createMessageWithAllRequiredData();
            message.setNachricht(null);

            val validationResult = validator.validate(message);

            Assertions.assertThat(validationResult.size()).isEqualTo(1);
        }

        @Test
        void failurOnNachrichtIsMoreThan1024Chars() {
            val message = createMessageWithAllRequiredData();
            message.setNachricht(StringUtils.left("", 1025));

            val validationResult = validator.validate(message);

            Assertions.assertThat(validationResult.size()).isEqualTo(1);
        }

        @Test
        void failurOnEmpfangszeitIsNull() {
            val message = createMessageWithAllRequiredData();
            message.setEmpfangsZeit(null);

            val validationResult = validator.validate(message);

            Assertions.assertThat(validationResult.size()).isEqualTo(1);
        }

        @Test
        void successOn1024CharsOnWahlbezirkId() {
            val message = createMessageWithAllRequiredData();
            message.setWahlbezirkID(StringUtils.left("", 1024));

            val validationResult = validator.validate(message);

            Assertions.assertThat(validationResult.isEmpty()).isTrue();
        }

        @Test
        void successOn1024CharsOnNachricht() {
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
