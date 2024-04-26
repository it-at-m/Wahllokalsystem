package de.muenchen.oss.wahllokalsystem.broadcastservice.integration;

import static de.muenchen.oss.wahllokalsystem.broadcastservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.broadcastservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.broadcastservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.broadcastservice.domain.Message;
import de.muenchen.oss.wahllokalsystem.broadcastservice.domain.MessageRepository;
import de.muenchen.oss.wahllokalsystem.broadcastservice.rest.BroadcastMessageDTO;
import de.muenchen.oss.wahllokalsystem.broadcastservice.service.BroadcastMapper;
import de.muenchen.oss.wahllokalsystem.broadcastservice.utils.BroadcastSecurityUtils;
import de.muenchen.oss.wahllokalsystem.broadcastservice.utils.Testdaten;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import jakarta.servlet.ServletException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.servlet.resource.NoResourceFoundException;

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

    @Value("${service.info.oid}")
    private String serviceOid;

    private static final String BROADCAST_URL = "/businessActions/broadcast";
    private static final String GETMESSAGE_URL = "/businessActions/getMessage/";
    private static final String DELETE_URL = "/businessActions/messageRead/";

    private static final List<String> WAHLBEZIRK_I_DS = Arrays.asList("1", "2", "3");
    private static final BroadcastMessageDTO BROADCAST_MESSAGE_DTO = new BroadcastMessageDTO(WAHLBEZIRK_I_DS, "Das ist ein Test");

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
                        post(BROADCAST_URL)
                                .content(Testdaten.asJsonString(BROADCAST_MESSAGE_DTO, objectMapper))
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                                .accept(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse();
        // @formatter:on
        int status = result.getStatus();
        Assertions.assertThat(status).isEqualTo(200);
        log.debug("Result > Status: {} ", status);
    }

    @Test
    public void broadcastIntegrationTestMessageIncomplete() {
        log.debug("#BroadcastIntegrationTestMessageIncomplete");

        final BroadcastMessageDTO bmDTOIncomplete1 = new BroadcastMessageDTO(null, "Das ist ein Test");
        Exception catchedException1 = null;
        try {
            mvc.perform(
                    post(BROADCAST_URL)
                            .content(Testdaten.asJsonString(bmDTOIncomplete1, objectMapper))
                            .contentType(MediaType.APPLICATION_JSON_UTF8)
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

        } catch (Exception e) {
            catchedException1 = e;
        }

        Assertions.assertThat(catchedException1)
                .isNotNull()
                .isInstanceOf(ServletException.class)
                .hasMessageContaining("Das Object BroadcastMessage ist nicht vollständig.");

        final BroadcastMessageDTO bmDTOIncomplete2 = new BroadcastMessageDTO(Arrays.asList("1", "2", "3", "4"), null);

        Exception catchedException2 = null;
        try {
            mvc.perform(
                    post(BROADCAST_URL)
                            .content(Testdaten.asJsonString(bmDTOIncomplete2, objectMapper))
                            .contentType(MediaType.APPLICATION_JSON_UTF8)
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

        } catch (Exception e) {
            catchedException2 = e;
        }

        Assertions.assertThat(catchedException2)
                .isNotNull()
                .isInstanceOf(ServletException.class)
                .hasMessageContaining("Das Object BroadcastMessage ist nicht vollständig.");
    }

    @Test
    public void getMessageIntegrationTest() throws Exception {
        log.debug("#GetMessageIntegrationTest");
        messageRepository.save(Testdaten.createMessage("123", "Das ist ein Test", LocalDateTime.now()));
        // @formatter:off
        MockHttpServletResponse result =
                mvc.perform(
                        get(GETMESSAGE_URL + "123")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse();
        // @formatter:on
        String content = result.getContentAsString();
        Message message = objectMapper.readValue(content, Message.class);
        Assertions.assertThat(message.getNachricht()).isEqualTo("Das ist ein Test");
    }

    @Test
    public void getMessageIntegrationTestGetParamBlank() {
        log.debug("#GetMessageIntegrationTestGetParamBlan");
        Exception catchedException1 = null;
        try {
            mvc.perform(
                    get(GETMESSAGE_URL + "    ")
                            .contentType(MediaType.APPLICATION_JSON_UTF8)
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

        } catch (Exception e) {
            catchedException1 = e;
        }

        Assertions.assertThat(catchedException1)
                .isNotNull()
                .isInstanceOf(ServletException.class)
                .hasMessageContaining("wahlbezirkID is blank or empty");
    }

    @Test
    public void getMessageIntegrationTestGetParamEmpty() throws Exception {
        log.debug("#GetMessageIntegrationTestGetParamEmpty");
        String wahlbezirkID = "";
        mvc.perform(get(GETMESSAGE_URL + wahlbezirkID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    Map<String, Object> mmodel = result.getModelAndView().getModel();
                    Assertions
                            .assertThat(mmodel)
                            .containsKey("wlsException")
                            .extracting("wlsException")
                            .isInstanceOf(TechnischeWlsException.class)
                            .extracting("code", "serviceName")
                            .contains("999", serviceOid);

                    Assertions
                            .assertThat(mmodel)
                            .containsKey("wlsException")
                            .extracting("wlsException.cause")
                            .isInstanceOf(NoResourceFoundException.class);
                });
    }

    @Test
    public void getMessageNoContentIntegrationTest() {
        log.debug("#GetMessageNoContentIntegrationTest");

        ServletException thrownException = null;
        try {
            mvc.perform(
                    get(GETMESSAGE_URL + "123")
                            .contentType(MediaType.APPLICATION_JSON_UTF8)
                            .accept(MediaType.APPLICATION_JSON));
        } catch (Exception e) {
            thrownException = (ServletException) e;
        }

        Assertions.assertThat(thrownException.getCause())
                .isNotNull()
                .isInstanceOf(FachlicheWlsException.class)
                .hasMessageContaining("No message found")
                .extracting("code", "serviceName")
                .contains("204", serviceOid);
    }

    @Test
    public void deleteIntegrationTest() throws Exception {
        log.debug("#deleteIntegrationTest");

        Message message = Testdaten.createMessage("123", "Das ist ein Test", LocalDateTime.now());
        messageRepository.save(message);

        List<Message> foundMessages = messageRepository.findByWahlbezirkID("123");
        Message foundMessage = foundMessages.stream().findFirst().get();
        Assertions.assertThat(foundMessage).isNotNull();

        // @formatter:off
        MockHttpServletResponse result =
                mvc.perform(
                        post(DELETE_URL + foundMessage.getOid())
                                .contentType(MediaType.APPLICATION_JSON_UTF8).accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();
        // @formatter:on

        int status = result.getStatus();
        Assertions.assertThat(status).isEqualTo(200);
        log.info("Result > Status: {} ", status);

        foundMessages = messageRepository.findByWahlbezirkID("123");
        Assertions.assertThat(foundMessages).size().isEqualTo(0);
    }

    @Test
    public void deleteIntegrationTestBadFormatUUID() {
        log.debug("#deleteIntegrationTestBadFormatUUID");

        Exception catchedException1 = null;
        try {
            mvc.perform(post(DELETE_URL + "badformatparam-u-u-i-d")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .accept(MediaType.APPLICATION_JSON));
        } catch (Exception e) {
            catchedException1 = e;
        }

        Assertions.assertThat(catchedException1.getCause())
                .isNotNull()
                .isInstanceOf(FachlicheWlsException.class)
                .hasMessageContaining("Nachricht-UUID bad format")
                .extracting("code", "serviceName")
                .contains("150", serviceOid);
    }
}
