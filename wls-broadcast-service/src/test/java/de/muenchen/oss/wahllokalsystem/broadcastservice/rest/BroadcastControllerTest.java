package de.muenchen.oss.wahllokalsystem.broadcastservice.rest;

import de.muenchen.oss.wahllokalsystem.broadcastservice.service.BroadcastService;
import de.muenchen.oss.wahllokalsystem.broadcastservice.utils.TestdataFactory;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;
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
public class BroadcastControllerTest {

    @Mock
    BroadcastService broadcastService;

    @InjectMocks
    BroadcastController unitUnderTest;

    @Nested
    class Broadcast {

        @Test
        void should_notThrowException_when_newBroadcastSent() {
            val wahlbezirkIds = Arrays.asList("1", "2", "3", "4");
            val mockedBroadcastMessageDTO = TestdataFactory.CreateBroadcastMessageDto.withCustomParams(wahlbezirkIds, "nachricht");

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.broadcast(mockedBroadcastMessageDTO));
            Mockito.verify(broadcastService).broadcast(mockedBroadcastMessageDTO);
        }
    }

    @Nested
    class GetMessage {

        @Test
        void should_returnMessageDTO_when_givenValidWahlbezirkId() {
            val wahlbezirkId = "wahlbezirkID";
            val empfangsZeit = LocalDateTime.now().withNano(0);
            val mockedMessageDTO = TestdataFactory.CreateMessageDto.withCustomParams(UUID.randomUUID(), wahlbezirkId, "nachricht", empfangsZeit);

            Mockito.when(broadcastService.getOldestMessage(wahlbezirkId)).thenReturn(mockedMessageDTO);

            val result = unitUnderTest.getMessage(wahlbezirkId);
            Assertions.assertThat(result).isEqualTo(mockedMessageDTO);
        }
    }

    @Nested
    class DeleteMessage {

        @Test
        void should_notThrowException_when_messageDeleted() {
            val messageId = "id";

            Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.deleteMessage(messageId));
            Mockito.verify(broadcastService).deleteMessage(messageId);
        }
    }
}
