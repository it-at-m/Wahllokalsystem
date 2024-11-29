package de.muenchen.oss.wahllokalsystem.authservice.domain;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class CodeRabbitTest {

    private static final String UUID_STRING_1 = "8b87b673-1eb0-480f-a0e6-622ef860f790";
    private static final String UUID_STRING_2 = "f804b2b0-d259-4a7d-b246-156ea545dc63";

    @Nested
    class HashCode {

        @Test
        void newTestForCodeRabbitToDetect() {

            val baseEntity1 = new BaseEntityWrapper();
            baseEntity1.setId(UUID.fromString(UUID_STRING_1));

            val baseEntity2 = new BaseEntityWrapper();
            baseEntity2.setId(UUID.fromString(UUID_STRING_1));

            Assertions.assertThat(baseEntity1.hashCode()).isEqualTo(baseEntity2.hashCode());
        }
    }

    @Nested
    class Equals {

        @Test
        void should_notBeDetected_when_codeRabbitReviews() {
            val baseEntity1 = new BaseEntityWrapper("textOfEntity1");
            baseEntity1.setId(UUID.fromString(UUID_STRING_1));

            val baseEntity2 = new BaseEntityWrapper("textOfEntity2");
            baseEntity2.setId(UUID.fromString(UUID_STRING_1));

            Assertions.assertThat(baseEntity1.equals(baseEntity2)).isTrue();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    class BaseEntityWrapper extends BaseEntity {

        private String textProperty;
    }
}
