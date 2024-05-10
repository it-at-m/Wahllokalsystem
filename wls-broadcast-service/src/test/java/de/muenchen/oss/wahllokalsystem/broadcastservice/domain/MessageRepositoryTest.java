package de.muenchen.oss.wahllokalsystem.broadcastservice.domain;

import static de.muenchen.oss.wahllokalsystem.broadcastservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.broadcastservice.TestConstants.SPRING_TEST_PROFILE;

import java.time.LocalDateTime;
import org.assertj.core.api.Assertions;
import de.muenchen.oss.wahllokalsystem.broadcastservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.broadcastservice.rest.BroadcastMessageDTO;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
        classes = { MicroServiceApplication.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
@Slf4j
class MessageRepositoryTest {

    @Autowired
    private MessageRepository repository;

    /**
     * Tests if searched saved Message from many is the first found
     */
    @Test
    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = Exception.class)
    void findFirstByWahlbezirkIDOrderByEmpfangsZeit_FirstMessageIsFound() {

        List<String> wahlbezirke = Arrays.asList("1", "2", "3", "4");
        String searchedWahlbezirkID = "3";

        BroadcastMessageDTO m1 = new BroadcastMessageDTO(wahlbezirke, "Ich bin Nachricht_1");
        BroadcastMessageDTO m2 = new BroadcastMessageDTO(wahlbezirke, "Ich bin Nachricht_2");
        BroadcastMessageDTO m3 = new BroadcastMessageDTO(wahlbezirke, "Ich bin Nachricht_3");
        BroadcastMessageDTO m4 = new BroadcastMessageDTO(wahlbezirke, "Ich bin Nachricht_4");
        BroadcastMessageDTO m5 = new BroadcastMessageDTO(wahlbezirke, "Ich bin Nachricht_5");

        List<Message> messagesToSend1 = mapForTestsFromBroadcastMessageDTO_toListOfMessages(m1, LocalDateTime.now());
        List<Message> messagesToSend2 = mapForTestsFromBroadcastMessageDTO_toListOfMessages(m2, LocalDateTime.now());
        List<Message> messagesToSend3 = mapForTestsFromBroadcastMessageDTO_toListOfMessages(m3, LocalDateTime.now());
        List<Message> messagesToSend4 = mapForTestsFromBroadcastMessageDTO_toListOfMessages(m4, LocalDateTime.now());
        List<Message> messagesToSend5 = mapForTestsFromBroadcastMessageDTO_toListOfMessages(m5, LocalDateTime.now());

        repository.saveAll(messagesToSend1);
        repository.saveAll(messagesToSend2);
        repository.saveAll(messagesToSend3);
        repository.saveAll(messagesToSend4);
        repository.saveAll(messagesToSend5);

        //Expected -sent Message
        List<Message> allMessages = (List<Message>) repository.findAll();
        allMessages.sort(
                Comparator
                        .comparing((Message m) -> m.getEmpfangsZeit().toLocalDate())
                        .reversed()
                        .thenComparing((Message m) -> m.getEmpfangsZeit().toLocalTime()));

        Message sentMessage = allMessages.stream()
                .filter(mes -> mes.getWahlbezirkID().equals(searchedWahlbezirkID))
                .findFirst()
                .get();

        //Actual - Found
        Optional<Message> optionalFoundMessage = repository.findFirstByWahlbezirkIDOrderByEmpfangsZeit(searchedWahlbezirkID);
        Assertions.assertThat(optionalFoundMessage.get()).isEqualTo(sentMessage);
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = Exception.class)
    void testDeleteById() {

        String originalOid = "1-2-3-4-5";
        String originalNachricht = "Test Nachricht";
        // initialize
        Message original = new Message();
        original.setOid(UUID.fromString(originalOid));
        original.setNachricht(originalNachricht);

        // persist
        original = repository.save(original);
        // check
        Message persisted = repository.findById(original.getOid()).orElse(null);

        Assertions.assertThat(persisted).isNotNull();
        Assertions.assertThat(persisted).isEqualTo(original);

        repository.deleteById(original.getOid());

        Message foundMessage = null;
        Exception thrownException = null;

        try {
            foundMessage = repository.findById(original.getOid()).orElse(null);
        } catch (Exception e) {
            thrownException = e;
        }

        Assertions.assertThat(foundMessage).isNull();
    }

    private List<Message> mapForTestsFromBroadcastMessageDTO_toListOfMessages(BroadcastMessageDTO messageToBroadcast, LocalDateTime now) {
        return messageToBroadcast.wahlbezirkIDs().stream().map(wahlbezirkId -> {
            Message message = new Message();
            message.setWahlbezirkID(wahlbezirkId);
            message.setEmpfangsZeit(now);
            message.setNachricht(messageToBroadcast.nachricht());
            return message;
        }).toList();
    }

}
