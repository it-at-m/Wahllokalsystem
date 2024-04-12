package de.muenchen.oss.wahllokalsystem.domain;

import static de.muenchen.oss.wahllokalsystem.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.TestConstants.SPRING_TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.*;
import de.muenchen.oss.wahllokalsystem.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.rest.BroadcastMessageDTO;
import de.muenchen.oss.wahllokalsystem.service.BroadcastService;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
        classes = { MicroServiceApplication.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:wahllokalsystem;DB_CLOSE_ON_EXIT=FALSE",
                "refarch.gracefulshutdown.pre-wait-seconds=0"
        }
)
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
class MessageRepositoryTest {

    @Autowired
    BroadcastService broadcast_S;

    @Autowired
    private MessageRepository repository;

    @Test
    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = Exception.class)
    void testSave() {

        // Implement your logic here by replacing and/or extending the code

        // initialize
        Message original = new Message();
        original.setNachricht("test nachricht");

        // persist
        original = repository.save(original);

        // check
        Message persisted = repository.findById(original.getOid()).orElse(null);
        assertNotNull(persisted);
        assertEquals(original, persisted);

    }

    /**
     * Tests if searched saved Message from many is the first found
     */
    @Test
    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = Exception.class)
    void testFirst() {

        List<String> wahlbezirke = Arrays.asList("1", "2", "3", "4");

        BroadcastMessageDTO m1 = new BroadcastMessageDTO(wahlbezirke, "Ich bin Nachricht_1");
        broadcast_S.broadcast(m1);
        BroadcastMessageDTO m2 = new BroadcastMessageDTO( wahlbezirke, "Ich bin Nachricht_2");
        broadcast_S.broadcast(m2);
        BroadcastMessageDTO m3 = new BroadcastMessageDTO(wahlbezirke, "Ich bin Nachricht_3");
        broadcast_S.broadcast(m3);
        BroadcastMessageDTO m4 = new BroadcastMessageDTO(wahlbezirke, "Ich bin Nachricht_4");
        broadcast_S.broadcast(m4);
        BroadcastMessageDTO m5 = new BroadcastMessageDTO(wahlbezirke, "Ich bin Nachricht_5");
        broadcast_S.broadcast(m5);

        Optional<Message> optionalFoundMessage = repository.findFirstByWahlbezirkIDOrderByEmpfangsZeit("3");
        List<Message> allMessages = (List<Message>) repository.findAll();

        allMessages.sort(
                Comparator
                        .comparing((Message m) -> m.getEmpfangsZeit().toLocalDate())
                        .reversed()
                        .thenComparing(
                                Comparator
                                        .comparing((Message m) -> m.getEmpfangsZeit().toLocalTime()
                        )

                )
        );

        assertEquals(optionalFoundMessage.get(), allMessages.get(0));

    }

}