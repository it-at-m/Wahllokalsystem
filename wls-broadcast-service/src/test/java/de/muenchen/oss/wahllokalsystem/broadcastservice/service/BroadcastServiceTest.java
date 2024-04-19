package de.muenchen.oss.wahllokalsystem.broadcastservice.service;

import static de.muenchen.oss.wahllokalsystem.broadcastservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.broadcastservice.TestConstants.SPRING_TEST_PROFILE;
import de.muenchen.oss.wahllokalsystem.broadcastservice.domain.Message;
import de.muenchen.oss.wahllokalsystem.broadcastservice.domain.MessageRepository;
import de.muenchen.oss.wahllokalsystem.broadcastservice.rest.BroadcastMessageDTO;
import de.muenchen.oss.wahllokalsystem.broadcastservice.rest.MessageDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionKonstanten;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import de.muenchen.oss.wahllokalsystem.broadcastservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.broadcastservice.utils.BroadcastSecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
        classes = { MicroServiceApplication.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:wahllokalsystem;DB_CLOSE_ON_EXIT=FALSE",
                "refarch.gracefulshutdown.pre-wait-seconds=0"
        }
)
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
class BroadcastServiceTest {

    @Autowired
    BroadcastService broadcastService;

    @Autowired
    private MessageRepository messageRepo;

    @BeforeEach
    public void setup() {
        Assertions.assertThat(broadcastService).isNotNull();
        SecurityContextHolder.clearContext();
        BroadcastSecurityUtils.grantFullAccess();
        messageRepo.deleteAll();
    }

    @Test
    void broadcast() {
        List<String> wahlbezirke = Arrays.asList("1", "2", "3", "4");
        String broadcastMessage = "Dies ist eine Broadcast-Nachricht";

        BroadcastMessageDTO m1 = new BroadcastMessageDTO(wahlbezirke, broadcastMessage);
        broadcastService.broadcast(m1);

        List<Message> messages = messageRepo.findByWahlbezirkID("4");

        Assertions.assertThat(messages.size()).isEqualTo(1);
        Assertions.assertThat(broadcastMessage).isEqualTo(messages.get(0).getNachricht());
    }

    @Test
    public void getOldestMessage_No_Message_Test() {

        RuntimeException thrownException = null;
        try {
            broadcastService.getOldestMessage("123");
        } catch (Exception e) {
            thrownException = (RuntimeException) e;
        }
        Assertions.assertThat(thrownException)
                .isNotNull()
                .isInstanceOf(FachlicheWlsException.class)
                .hasMessageStartingWith("No message found")
                .extracting("code")
                .isEqualTo(ExceptionKonstanten.CODE_ENTITY_NOT_FOUND);

    }

    @Test
    public void getOldestMessageTest() {
        String wahlbezirkID = "4711";
        LocalDateTime time = LocalDateTime.of(2018, 5, 29, 12, 0);
        String messageToSave = "This is the test Message";

        Message msg1 = new Message();
        msg1.setWahlbezirkID(wahlbezirkID);
        msg1.setNachricht(messageToSave);
        msg1.setEmpfangsZeit(time);
        messageRepo.save(msg1);

        MessageDTO foundMessage = broadcastService.getOldestMessage(wahlbezirkID);
        Assertions.assertThat(msg1.getNachricht()).isEqualTo(foundMessage.nachricht());
    }

    @Test
    public void getOldestMessage_MultipleMessageTest() {
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

        messageRepo.save(msg1);
        messageRepo.save(msg2);

        String foundMessage = broadcastService.getOldestMessage(wahlbezirkID).nachricht();
        Assertions.assertThat(messageToSave2).isEqualTo(foundMessage);
    }

    @Test
    public void deleteMessage_messageRead_DoubleDelete_Test() {
        String wahlbezirkID_1 = "1", wahlbezirkID_2 = "2";
        LocalDateTime time = LocalDateTime.of(2018, 5, 29, 12, 0);

        String MESSAGE = "Message";
        Message msg1 = new Message();
        msg1.setWahlbezirkID(wahlbezirkID_1);
        msg1.setNachricht(MESSAGE + wahlbezirkID_1);
        msg1.setEmpfangsZeit(time);

        Message msg2 = new Message();
        msg2.setWahlbezirkID(wahlbezirkID_2);
        msg2.setNachricht(MESSAGE + wahlbezirkID_2);
        msg2.setEmpfangsZeit(time.minusYears(1L));

        messageRepo.save(msg1);
        messageRepo.save(msg2);

        List<Message> stillExistingMessages = messageRepo.findByWahlbezirkID(wahlbezirkID_2);

        Assertions.assertThat(stillExistingMessages)
                .hasSize(1)
                .hasAtLeastOneElementOfType(Message.class);

        Assertions.assertThat(MESSAGE + wahlbezirkID_2).isEqualTo(stillExistingMessages.get(0).getNachricht());
        Assertions.assertThat(MESSAGE + wahlbezirkID_2).isEqualTo(stillExistingMessages.get(0).getNachricht());

    }
}