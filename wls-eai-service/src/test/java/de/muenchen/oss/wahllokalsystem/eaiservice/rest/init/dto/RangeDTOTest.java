package de.muenchen.oss.wahllokalsystem.eaiservice.rest.init.dto;

import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@Nested
class RangeDTOTest {

    @Nested
    class GetValueInRange {

        @Test
        void should_returnSameValue_when_minAndMaxAreEqual() {
            val range = new RangeDTO(5, 5);
            Assertions.assertThat(range.getValueInRange()).isEqualTo(5);
        }
    }

}
