package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.urnenwahlvorbereitung;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UrnenwahlvorbereitungDTOTest {

    @Nested
    class ConstructionTests {

        @Test
        void listOfWahlurneIsNotNullOnNullParameter() {
            val result = new UrnenwahlvorbereitungDTO(null, 0, 0, 0, null);

            Assertions.assertThat(result.urnenAnzahl()).isNotNull();
        }
    }

}
