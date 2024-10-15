package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahltermindaten;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltermindaten.AsyncProgress;
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
    AsyncProgress asyncProgress;

    @Mock
    AsyncProgressDTOMapper asyncProgressDTOMapper;

    @InjectMocks
    AsyncProgressController unitUnderTest;

    @Nested
    class GetAsyncProgress {

        @Test
        void should_returnCurrentProgressStateAsDTO_when_called() {
            val mockedProgressDTO = new AsyncProgressDTO(null, null, null, null, false, 0, 1, null, true, 3, 4, null);

            Mockito.when(asyncProgressDTOMapper.toDto(asyncProgress)).thenReturn(mockedProgressDTO);

            Assertions.assertThat(unitUnderTest.getAsyncProgress()).isEqualTo(mockedProgressDTO);
        }

    }
}
