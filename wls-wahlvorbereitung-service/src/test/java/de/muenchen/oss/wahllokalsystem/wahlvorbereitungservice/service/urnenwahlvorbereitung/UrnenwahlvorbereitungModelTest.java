package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.urnenwahlvorbereitung;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UrnenwahlvorbereitungModelTest {

    @Nested
    class ConstructionTests {

        @Test
        void listOfWahlurneIsNotNullOnNullParameter() {
            val result = new UrnenwahlvorbereitungModel(null, 0, 0, 0, null);

            Assertions.assertThat(result.urnenAnzahl()).isNotNull();
        }
    }

}
