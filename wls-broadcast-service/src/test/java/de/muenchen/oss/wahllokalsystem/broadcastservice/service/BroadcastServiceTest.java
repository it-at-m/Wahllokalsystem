package de.muenchen.oss.wahllokalsystem.broadcastservice.service;

import de.muenchen.oss.wahllokalsystem.broadcastservice.domain.Message;
import de.muenchen.oss.wahllokalsystem.broadcastservice.domain.MessageRepository;
import de.muenchen.oss.wahllokalsystem.broadcastservice.rest.BroadcastMessageDTO;
import de.muenchen.oss.wahllokalsystem.broadcastservice.rest.MessageDTO;
import de.muenchen.oss.wahllokalsystem.broadcastservice.util.BroadcastExceptionKonstanten;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionDataWrapper;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionKonstanten;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
class BroadcastServiceTest {

    @InjectMocks
    BroadcastService broadcastService;

    @Mock
    private MessageRepository messageRepo;

    @Mock
    private BroadcastMapper broadcastMapper;

    @Mock
    ExceptionFactory exceptionFactory;

    @Nested
    class Broadcast {

        @Test
        void broadcastPositive() {
            List<String> wahlbezirke = Arrays.asList("1", "2", "3", "4");
            String broadcastMessage = "Dies ist eine Broadcast-Nachricht";
            BroadcastMessageDTO m1 = new BroadcastMessageDTO(wahlbezirke, broadcastMessage);

            Assertions.assertThatNoException().isThrownBy(() -> broadcastService.broadcast(m1));
        }

        @Test
        void broadcastFailsIfWahlbezirkIDsNull() {
            String broadcastMessage = "Dies ist eine Broadcast-Nachricht";
            BroadcastMessageDTO m1 = new BroadcastMessageDTO(null, broadcastMessage);
            Mockito.when(exceptionFactory.createFachlicheWlsException(BroadcastExceptionKonstanten.BROADCAST_PARAMETER_UNVOLLSTAENDIG)).thenReturn(FachlicheWlsException.withCode("150").buildWithMessage("Das Object BroadcastMessage ist nicht vollständig."));

            Assertions.assertThatExceptionOfType(FachlicheWlsException.class)
                    .isThrownBy(() -> broadcastService.broadcast(m1))
                    .withMessageStartingWith("Das Object BroadcastMessage ist nicht vollständig.").extracting("code")
                    .isEqualTo(BroadcastExceptionKonstanten.CODE_NACHRICHTENABRUFEN_PARAMETER_UNVOLLSTAENDIG);
        }

        @Test
        void broadcastFailsIfNachrichtNull() {
            List<String> wahlbezirke = Arrays.asList("1", "2", "3", "4");
            BroadcastMessageDTO m1 = new BroadcastMessageDTO(wahlbezirke, null);
            Mockito.when(exceptionFactory.createFachlicheWlsException(BroadcastExceptionKonstanten.BROADCAST_PARAMETER_UNVOLLSTAENDIG)).thenReturn(FachlicheWlsException.withCode("150").buildWithMessage("Das Object BroadcastMessage ist nicht vollständig."));

            Assertions.assertThatExceptionOfType(FachlicheWlsException.class)
                    .isThrownBy(() -> broadcastService.broadcast(m1))
                    .withMessageStartingWith("Das Object BroadcastMessage ist nicht vollständig.").extracting("code")
                    .isEqualTo(BroadcastExceptionKonstanten.CODE_NACHRICHTENABRUFEN_PARAMETER_UNVOLLSTAENDIG);
        }

        @Test
        void broadcastFailsIfNachrichtEmpty() {
            List<String> wahlbezirke = Arrays.asList("1", "2", "3", "4");
            String broadcastMessage = "";
            BroadcastMessageDTO m1 = new BroadcastMessageDTO(wahlbezirke, broadcastMessage);
            Mockito.when(exceptionFactory.createFachlicheWlsException(BroadcastExceptionKonstanten.BROADCAST_PARAMETER_UNVOLLSTAENDIG)).thenReturn(FachlicheWlsException.withCode("150").buildWithMessage("Das Object BroadcastMessage ist nicht vollständig."));

            Assertions.assertThatExceptionOfType(FachlicheWlsException.class)
                    .isThrownBy(() -> broadcastService.broadcast(m1))
                    .withMessageStartingWith("Das Object BroadcastMessage ist nicht vollständig.")
                    .extracting("code")
                    .isEqualTo(BroadcastExceptionKonstanten.CODE_NACHRICHTENABRUFEN_PARAMETER_UNVOLLSTAENDIG);
        }

        @Test
        void broadcastFailsIfNachrichtBlank() {
            List<String> wahlbezirke = Arrays.asList("1", "2", "3", "4");
            String broadcastMessage = "   ";
            BroadcastMessageDTO m1 = new BroadcastMessageDTO(wahlbezirke, broadcastMessage);
            Mockito.when(exceptionFactory.createFachlicheWlsException(BroadcastExceptionKonstanten.BROADCAST_PARAMETER_UNVOLLSTAENDIG)).thenReturn(FachlicheWlsException.withCode("150").buildWithMessage("Das Object BroadcastMessage ist nicht vollständig."));

            Assertions.assertThatExceptionOfType(FachlicheWlsException.class)
                    .isThrownBy(() -> broadcastService.broadcast(m1))
                    .withMessageStartingWith("Das Object BroadcastMessage ist nicht vollständig.")
                    .extracting("code")
                    .isEqualTo(BroadcastExceptionKonstanten.CODE_NACHRICHTENABRUFEN_PARAMETER_UNVOLLSTAENDIG);
        }
    }

    @Nested
    class GetMessage {

        @Test
        void nullFromRepository() {
            String wahlbezirkID = "987";
            Mockito.when(messageRepo.findFirstByWahlbezirkIDOrderByEmpfangsZeit(wahlbezirkID)).thenReturn(Optional.empty());
            val mockedExceptionFactoryWlsException = FachlicheWlsException.withCode("204").buildWithMessage("No message found");
            Mockito.when(exceptionFactory.createFachlicheWlsException(new ExceptionDataWrapper(ExceptionKonstanten.CODE_ENTITY_NOT_FOUND, "No message found"))).thenReturn(mockedExceptionFactoryWlsException);

            Assertions.assertThatExceptionOfType(FachlicheWlsException.class)
                    .isThrownBy(() -> broadcastService.getOldestMessage(wahlbezirkID))
                    .withMessageStartingWith("No message found").extracting("code")
                    .isEqualTo(ExceptionKonstanten.CODE_ENTITY_NOT_FOUND);
        }

        @Test
        void isRightMessage() {
            String wahlbezirkID = "4711";
            LocalDateTime time = LocalDateTime.of(2018, 5, 29, 12, 0);
            String messageToSave = "This is the test Message";

            Message msg1 = new Message();
            msg1.setWahlbezirkID(wahlbezirkID);
            msg1.setNachricht(messageToSave);
            msg1.setEmpfangsZeit(time);
            msg1.setOid(UUID.fromString("1-2-3-4-5"));

            Mockito.when(broadcastMapper.toDto(msg1))
                    .thenReturn(new MessageDTO(msg1.getOid(), msg1.getWahlbezirkID(), msg1.getNachricht(), msg1.getEmpfangsZeit()));
            Mockito.when(messageRepo.findFirstByWahlbezirkIDOrderByEmpfangsZeit(wahlbezirkID)).thenReturn(Optional.of(msg1));
            MessageDTO foundMessage = broadcastService.getOldestMessage(wahlbezirkID);

            Assertions.assertThat(msg1.getNachricht()).isEqualTo(foundMessage.nachricht());
        }

        @Test
        void isLastFromMultipleMessages() {
            String wahlbezirkID = "4711";
            LocalDateTime time = LocalDateTime.of(2018, 5, 29, 12, 0);
            String messageToSave1 = "This is the test Message1";
            String messageToSave2 = "This is the test Message2";

            Message msg1 = new Message();
            msg1.setWahlbezirkID(wahlbezirkID);
            msg1.setNachricht(messageToSave1);
            msg1.setEmpfangsZeit(time);

            Message msg2 = new Message();
            msg2.setWahlbezirkID(wahlbezirkID);
            msg2.setNachricht(messageToSave2);
            msg2.setEmpfangsZeit(time.minusYears(1L));

            Mockito.when(broadcastMapper.toDto(msg2))
                    .thenReturn(new MessageDTO(msg2.getOid(), msg2.getWahlbezirkID(), msg2.getNachricht(), msg2.getEmpfangsZeit()));
            Mockito.when(messageRepo.findFirstByWahlbezirkIDOrderByEmpfangsZeit(wahlbezirkID)).thenReturn(Optional.of(msg2));

            String foundMessage = broadcastService.getOldestMessage(wahlbezirkID).nachricht();
            Assertions.assertThat(messageToSave2).isEqualTo(foundMessage);
        }
    }

    @Nested
    class DeleteMessage {

        @Test
        void deletePositive() {
            Mockito.doNothing().when(messageRepo).deleteById(UUID.fromString("1-2-3-4-5"));
            Assertions.assertThatNoException().isThrownBy(() -> broadcastService.deleteMessage("1-2-3-4-5"));

        }

        @Test
        void deleteParamBlank() {
            val mockedExceptionFactoryWlsException = FachlicheWlsException.withCode("105").buildWithMessage("nachrichtID is blank or empty");
            Mockito.when(exceptionFactory.createFachlicheWlsException(new ExceptionDataWrapper(BroadcastExceptionKonstanten.CODE_NACHRICHTENABRUFEN_PARAMETER_UNVOLLSTAENDIG, "nachrichtID is blank or empty"))).thenReturn(mockedExceptionFactoryWlsException);

            String nachrichtID = "   ";
            Assertions.assertThatExceptionOfType(FachlicheWlsException.class)
                    .isThrownBy(() -> broadcastService.deleteMessage(nachrichtID))
                    .withMessageStartingWith("nachrichtID is blank or empty");
        }

        @Test
        void deleteParamEmpty() {
            val mockedExceptionFactoryWlsException = FachlicheWlsException.withCode("105").buildWithMessage("nachrichtID is blank or empty");
            Mockito.when(exceptionFactory.createFachlicheWlsException(new ExceptionDataWrapper(BroadcastExceptionKonstanten.CODE_NACHRICHTENABRUFEN_PARAMETER_UNVOLLSTAENDIG, "nachrichtID is blank or empty"))).thenReturn(mockedExceptionFactoryWlsException);

            String nachrichtID = "";
            Assertions.assertThatExceptionOfType(FachlicheWlsException.class)
                    .isThrownBy(() -> broadcastService.deleteMessage(nachrichtID))
                    .withMessageStartingWith("nachrichtID is blank or empty");
        }

        @Test
        void deleteParamNull() {
            val mockedExceptionFactoryWlsException = FachlicheWlsException.withCode("105").buildWithMessage("nachrichtID is blank or empty");
            Mockito.when(exceptionFactory.createFachlicheWlsException(new ExceptionDataWrapper(BroadcastExceptionKonstanten.CODE_NACHRICHTENABRUFEN_PARAMETER_UNVOLLSTAENDIG, "nachrichtID is blank or empty"))).thenReturn(mockedExceptionFactoryWlsException);

            Assertions.assertThatExceptionOfType(FachlicheWlsException.class)
                    .isThrownBy(() -> broadcastService.deleteMessage(null))
                    .withMessageStartingWith("nachrichtID is blank or empty");
        }
    }
}
