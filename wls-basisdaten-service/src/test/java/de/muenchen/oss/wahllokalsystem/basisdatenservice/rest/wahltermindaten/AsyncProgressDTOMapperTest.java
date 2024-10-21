package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahltermindaten;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltermindaten.AsyncProgress;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class AsyncProgressDTOMapperTest {

    AsyncProgressDTOMapper unitUnderTest = Mappers.getMapper(AsyncProgressDTOMapper.class);

    @Nested
    class ToDTO {

        @Test
        void should_returnDTO_when_asyncProgressIsDelivered() {
            val wahltag = LocalDate.now();
            val wahlnummer = "wahlnummer";
            val lastStartTime = LocalDateTime.now().minusDays(2);
            val lastFinishTime = LocalDateTime.now().minusDays(1);
            val wahlvorschlaegeIsLoading = true;
            val wahlvorschlaegeTotal = 23;
            val wahlvorschlaegeFinished = 12;
            val wahlvorschlaegeNext = "wahlvorschlagID13";
            val referendumvorlagenIsLoading = false;
            val referendumvorlagenTotal = 21;
            val referendumvorlagenFinished = 20;
            val referendumvorlagenNext = "referendumvorlagenID13";

            val asyncProgress = new AsyncProgress(wahltag, wahlnummer, lastStartTime, lastFinishTime, wahlvorschlaegeIsLoading, wahlvorschlaegeTotal,
                    wahlvorschlaegeFinished, wahlvorschlaegeNext, referendumvorlagenIsLoading, referendumvorlagenTotal, referendumvorlagenFinished,
                    referendumvorlagenNext);

            val result = unitUnderTest.toDto(asyncProgress);

            val expectedResult = new AsyncProgressDTO(wahltag, wahlnummer, lastStartTime, lastFinishTime, wahlvorschlaegeIsLoading, wahlvorschlaegeTotal,
                    wahlvorschlaegeFinished, wahlvorschlaegeNext, referendumvorlagenIsLoading, referendumvorlagenTotal, referendumvorlagenFinished,
                    referendumvorlagenNext);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        void should_returnNull_when_parameterIsNull() {
            Assertions.assertThat(unitUnderTest.toDto(null)).isNull();
        }
    }

}
