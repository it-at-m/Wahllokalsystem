package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.unterbrechungsuhrzeit;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UnterbrechungsUhrzeitModelTest {

    @Nested
    class ConstructionTests {

        @Test
        void ParametersAreNullOnNullParameters() {
            val result = new UnterbrechungsUhrzeitModel(null, null);

            Assertions.assertThat(result.wahlbezirkID()).isNull();
            Assertions.assertThat(result.unterbrechungsUhrzeit()).isNull();
        }
    }

}
