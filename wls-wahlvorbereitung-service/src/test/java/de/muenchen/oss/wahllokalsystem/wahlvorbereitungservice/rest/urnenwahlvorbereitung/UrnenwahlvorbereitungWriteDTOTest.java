package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.urnenwahlvorbereitung;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UrnenwahlvorbereitungWriteDTOTest {

    @Nested
    class ConstructionTests {

        @Test
        void listOfWahlurneIsNotNullOnNullParameter() {
            val result = new UrnenwahlvorbereitungWriteDTO(0, 0, 0, null);

            Assertions.assertThat(result.urnenAnzahl()).isNotNull();
        }
    }

}
