package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.awerte;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte.AsyncProgress;
import java.time.LocalDateTime;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class AsyncProgressDTOMapperTest {

    private final AsyncProgressDTOMapper mapper = Mappers.getMapper(AsyncProgressDTOMapper.class);

    @Nested
    class ToDTO {

        @Test
        void should_mapCorrectly_when_validAsyncProgressGiven() {
            val lastStartTime = LocalDateTime.now().minusHours(1);
            val lastFinishTime = LocalDateTime.now();
            val asyncProgress = new AsyncProgress(lastStartTime, lastFinishTime, true, 10, 5, "NextValue");

            val result = mapper.toDTO(asyncProgress);

            val expected = new AsyncProgressDTO(
                    lastStartTime,
                    lastFinishTime,
                    true,
                    10,
                    5,
                    "NextValue");

            Assertions.assertThat(result).isEqualTo(expected);
        }

        @Test
        void should_returnNull_when_nullAsyncProgressGiven() {
            val dto = mapper.toDTO(null);

            Assertions.assertThat(dto).isNull();
        }

        @Test
        void should_HandleNullFieldsCorrectly_when_AsyncProgressWithNullFieldsGiven() {
            val asyncProgress = new AsyncProgress(null, null, false, 0, 0, null);

            val result = mapper.toDTO(asyncProgress);

            val expected = new AsyncProgressDTO(
                    null,
                    null,
                    false,
                    0,
                    0,
                    null);

            Assertions.assertThat(result).isEqualTo(expected);
        }
    }
}
