package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.urnenwahlvorbereitung;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class UrnenwahlvorbereitungModelTest {

    @Test
    void should_notReturnNullForUrnenanzahl_when_noDataProvided() {
        val result = new UrnenwahlvorbereitungModel(null, 0, 0, 0, null);

        Assertions.assertThat(result.urnenAnzahl()).isNotNull();
    }

}
