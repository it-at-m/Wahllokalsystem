package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.awerte;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte.AsyncProgress;
import java.time.LocalDateTime;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AsyncProgressControllerTest {

    @Mock
    AsyncProgressDTOMapper asyncProgressDTOMapper;

    @Mock
    AsyncProgress asyncProgress;

    @InjectMocks
    AsyncProgressController unitUnderTest;

    @Nested
    class GetAsyncProgress {

        @Test
        void should_returnData_when_dataIsFound() {
            val mockedAsyncProgressAsDTO = new AsyncProgressDTO(LocalDateTime.now(), LocalDateTime.now(), false, 0, 0, "");
            Mockito.when(asyncProgressDTOMapper.toDTO(asyncProgress)).thenReturn(mockedAsyncProgressAsDTO);

            val response = unitUnderTest.getAsyncProgress();

            Assertions.assertThat(response).isEqualTo(mockedAsyncProgressAsDTO);
        }
    }
}
