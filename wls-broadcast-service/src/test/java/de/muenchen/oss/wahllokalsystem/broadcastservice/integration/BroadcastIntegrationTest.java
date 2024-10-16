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
import de.muenchen.oss.wahllokalsystem.broadcastservice.util.BroadcastExceptionKonstanten;
import de.muenchen.oss.wahllokalsystem.broadcastservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import de.muenchen.oss.wahllokalsystem.broadcastservice.utils.Testdaten;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
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
    void setup() {
        log.debug("Setting up test ...");
        log.debug("Port > {}", port);
        closeable = MockitoAnnotations.openMocks(this);
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        SecurityUtils.runWith(Authorities.getAllAuthorities());
        messageRepository.deleteAll();
    }

    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }

    @Nested
    class BroadcastTest {

        @Test
        void broadcast() throws Exception {
            log.debug("#BroadcastIntegrationTest");
            MockHttpServletResponse result;
            result = mvc.perform(
                    post(BROADCAST_URL)
                            .content(Testdaten.asJsonString(BROADCAST_MESSAGE_DTO, objectMapper))
                            .contentType(MediaType.APPLICATION_JSON_UTF8)
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

            int status = result.getStatus();
            Assertions.assertThat(status).isEqualTo(200);
            log.debug("Result > Status: {} ", status);
        }

        @Test
        void messageIncomplete_WahlbezirkID_Null() throws Exception {
            final BroadcastMessageDTO bmDTOIncomplete1 = new BroadcastMessageDTO(null, "Das ist ein Test");
            mvc.perform(post(BROADCAST_URL)
                    .content(Testdaten.asJsonString(bmDTOIncomplete1, objectMapper))
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> {
                        Exception resolvedException = result.getResolvedException();
                        Assertions
                                .assertThat(resolvedException)
                                .isInstanceOf(FachlicheWlsException.class)
                                .extracting("code", "serviceName", "message")
                                .contains(BroadcastExceptionKonstanten.CODE_NACHRICHTENABRUFEN_PARAMETER_UNVOLLSTAENDIG, serviceOid,
                                        "Das Object BroadcastMessage ist nicht vollständig.");
                    });
        }

        @Test
        void messageIncomplete_Nachricht_Null() throws Exception {
            final BroadcastMessageDTO bmDTOIncomplete2 = new BroadcastMessageDTO(Arrays.asList("1", "2", "3", "4"), null);
            mvc.perform(post(BROADCAST_URL)
                    .content(Testdaten.asJsonString(bmDTOIncomplete2, objectMapper))
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> {
                        Exception resolvedException = result.getResolvedException();
                        Assertions
                                .assertThat(resolvedException)
                                .isInstanceOf(FachlicheWlsException.class)
                                .extracting("code", "serviceName", "message")
                                .contains(BroadcastExceptionKonstanten.CODE_NACHRICHTENABRUFEN_PARAMETER_UNVOLLSTAENDIG, serviceOid,
                                        "Das Object BroadcastMessage ist nicht vollständig.");
                    });
        }

    }

    @Test
    void getMessageIntegrationTest() throws Exception {
        log.debug("#GetMessageIntegrationTest");
        messageRepository.save(Testdaten.createMessage("123", "Das ist ein Test", LocalDateTime.now()));
        MockHttpServletResponse result = mvc.perform(
                get(GETMESSAGE_URL + "123")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        String content = result.getContentAsString();
        Message message = objectMapper.readValue(content, Message.class);
        Assertions.assertThat(message.getNachricht()).isEqualTo("Das ist ein Test");
    }

    @Test
    void getMessageIntegrationTestGetParamBlank() throws Exception {
        log.debug("#GetMessageIntegrationTestGetParamBlank");
        mvc.perform(get(GETMESSAGE_URL + "   ")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    Exception resolvedException = result.getResolvedException();
                    Assertions
                            .assertThat(resolvedException)
                            .isInstanceOf(FachlicheWlsException.class)
                            .extracting("code", "serviceName", "message")
                            .contains(BroadcastExceptionKonstanten.CODE_NACHRICHTENABRUFEN_PARAMETER_UNVOLLSTAENDIG, serviceOid,
                                    "wahlbezirkID is blank or empty");
                });

    }

    @Test
    void getMessageIntegrationTestGetParamEmpty() throws Exception {
        log.debug("#GetMessageIntegrationTestGetParamEmpty");
        String wahlbezirkID = "";

        mvc.perform(get(GETMESSAGE_URL + wahlbezirkID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> {
                    String actualStringResponse = result.getResponse().getContentAsString();
                    String expectedStringResponse = "{\"category\":\"T\",\"code\":\"999\",\"service\":\"WLS-BROADCAST\",\"message\":\"Ursache: class org.springframework.web.servlet.resource.NoResourceFoundException, Nachricht: No static resource businessActions/getMessage.\"}";
                    Assertions.assertThat(actualStringResponse).isEqualTo(expectedStringResponse);
                });
    }

    @Test
    void getMessageNoContentIntegrationTest() throws Exception {
        log.debug("#GetMessageNoContentIntegrationTest");
        mvc.perform(get(GETMESSAGE_URL + "123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(result -> {
                    Exception resolvedException = result.getResolvedException();
                    Assertions
                            .assertThat(resolvedException)
                            .isInstanceOf(FachlicheWlsException.class)
                            .extracting("code", "serviceName", "message")
                            .contains("204", serviceOid, "No message found");
                });
    }

    @Test
    void deleteIntegrationTest() throws Exception {
        log.debug("#deleteIntegrationTest");

        Message message = Testdaten.createMessage("123", "Das ist ein Test", LocalDateTime.now());
        messageRepository.save(message);

        List<Message> foundMessages = ((List<Message>) messageRepository
                .findAll()).stream().filter((m) -> m.getWahlbezirkID().equals("123")).toList();
        Message foundMessage = foundMessages.stream().findFirst().get();
        Assertions.assertThat(foundMessage).isNotNull();

        MockHttpServletResponse result = mvc.perform(
                post(DELETE_URL + foundMessage.getOid())
                        .contentType(MediaType.APPLICATION_JSON_UTF8).accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        int status = result.getStatus();
        Assertions.assertThat(status).isEqualTo(200);
        log.info("Result > Status: {} ", status);

        foundMessages = ((List<Message>) messageRepository
                .findAll()).stream().filter((m) -> m.getWahlbezirkID().equals("123")).toList();
        Assertions.assertThat(foundMessages).isEmpty();
    }

    @Test
    void deleteIntegrationTestBadFormatUUID() throws Exception {
        log.debug("#deleteIntegrationTestBadFormatUUID");
        mvc.perform(post(DELETE_URL + "badformatparam-u-u-i-d")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    Exception resolvedException = result.getResolvedException();
                    Assertions
                            .assertThat(resolvedException)
                            .isInstanceOf(FachlicheWlsException.class)
                            .extracting("code", "serviceName", "message")
                            .contains("150", serviceOid, "Nachricht-UUID bad format");
                });
    }
}
