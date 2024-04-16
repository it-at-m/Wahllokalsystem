package de.muenchen.oss.wahllokalsystem.integration;

import static de.muenchen.oss.wahllokalsystem.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.domain.Message;
import de.muenchen.oss.wahllokalsystem.domain.MessageRepository;
import de.muenchen.oss.wahllokalsystem.rest.BroadcastMessageDTO;
import de.muenchen.oss.wahllokalsystem.service.BroadcastMapper;
import de.muenchen.oss.wahllokalsystem.utils.BroadcastSecurityUtils;
import de.muenchen.oss.wahllokalsystem.utils.Testdaten;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionKonstanten;
import jakarta.servlet.ServletException;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Slf4j
@SpringBootTest(
        classes = { MicroServiceApplication.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:wahllokalsystem;DB_CLOSE_ON_EXIT=FALSE",
                "refarch.gracefulshutdown.pre-wait-seconds=0"
        }
)
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
public class BroadcastIntegrationTest {

    @Autowired
    WebApplicationContext context;

    @Autowired
    FilterChainProxy springSecurityFilterChain;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    BroadcastMapper bcMapper;

    @Autowired
    ObjectMapper objectMapper;

    private AutoCloseable closeable;

    MockMvc mvc;

    @Value("${local.server.port}")
    private int port;

    private static final String broadcast_url = "/businessActions/broadcast";
    private static final String getmessage_url = "/businessActions/getMessage/" + Testdaten.WAHLBEZIRK_ID;
    private static final String delete_url = "/businessActions/messageRead/";

    private static final List<String> wahlbezirkIDs = Arrays.asList("1", "2", "3");
    private static final BroadcastMessageDTO bmDTO = new BroadcastMessageDTO(wahlbezirkIDs, Testdaten.MESSAGE);

    @BeforeEach
    public void setup() {
        log.debug("Setting up test ...");
        log.debug("Port > {}", port);
        closeable = MockitoAnnotations.openMocks(this);
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        BroadcastSecurityUtils.grantFullAccess();
        messageRepository.deleteAll();
    }

    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }



    @Test
    public void broadcastIntegrationTest() throws Exception {
        log.debug("#BroadcastIntegrationTest");
        // @formatter:off
        MockHttpServletResponse result =
                mvc.perform(
                        post(broadcast_url)
                                .content(Testdaten.asJsonString(bmDTO))
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                                .accept(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse();
        // @formatter:on
        int status = result.getStatus();
        Assertions.assertThat(status).isEqualTo(200);
        log.debug("Result > Status: {} ", status);
    }


    @Test
    public void getMessageIntegrationTest() throws Exception {
        log.debug("#GetMessageIntegrationTest");
        messageRepository.save(Testdaten.createMessage(Testdaten.WAHLBEZIRK_ID, Testdaten.MESSAGE, Testdaten.UHRZEIT));
        // @formatter:off
        MockHttpServletResponse result =
                mvc.perform(
                        get(getmessage_url)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse();
        // @formatter:on
        String content = result.getContentAsString();
        log.debug("string_obj {}", content);
        Message message = objectMapper.readValue(content, Message.class);
        Assertions.assertThat(message.getNachricht()).isEqualTo(Testdaten.MESSAGE);
    }

    @Test
    public void getMessageNoContentIntegrationTest() {
        log.debug("#GetMessageNoContentIntegrationTest");

        ServletException thrownException = null;
        try{
            // @formatter:off
            MockHttpServletResponse result =
                    mvc.perform(
                                    get(getmessage_url)
                                            .contentType(MediaType.APPLICATION_JSON_UTF8)
                                            .accept(MediaType.APPLICATION_JSON))
                            .andReturn().getResponse();
            // @formatter:on
        } catch (Exception e){
            thrownException = (ServletException) e;
        }

        log.debug("thrown exception ist:{}", thrownException.toString());
        //ToDo: warum überschreibt der MockServer als ServletException die im BroadcastService, für de Fall isEmpty(), richtig geworfene FachlicheWlsException
        // Wie sollten wir das angehen?
        Assertions.assertThat(thrownException)
                .isNotNull()
                //.isInstanceOf(FachlicheWlsException.class)
                .isInstanceOf(ServletException.class)
                //.hasMessageStartingWith("No message found")
                .hasMessageContaining("No message found");
                //.extracting("code")
                //.isEqualTo(ExceptionKonstanten.CODE_ENTITY_NOT_FOUND);
    }

    @Test
    public void deleteIntegrationTest() throws Exception {
        log.debug("#deleteIntegrationTest");

        Message message = Testdaten.createMessage(Testdaten.WAHLBEZIRK_ID, Testdaten.MESSAGE, Testdaten.UHRZEIT);
        messageRepository.save(message);

        Message foundMessage = messageRepository.findByNachricht(Testdaten.MESSAGE);
        Assertions.assertThat(foundMessage).isNotNull();

        // @formatter:off
        MockHttpServletResponse result =
                mvc.perform(
                        post(delete_url + foundMessage.getOid())
                                .contentType(MediaType.APPLICATION_JSON_UTF8).accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();
        // @formatter:on

        int status = result.getStatus();
        Assertions.assertThat(status).isEqualTo(200);
        log.info("Result > Status: {} ", status);

        foundMessage = messageRepository.findByNachricht(Testdaten.MESSAGE);
        Assertions.assertThat(foundMessage).isNull();
    }
}
