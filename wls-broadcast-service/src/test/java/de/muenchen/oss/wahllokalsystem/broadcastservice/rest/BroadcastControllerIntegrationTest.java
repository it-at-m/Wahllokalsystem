package de.muenchen.oss.wahllokalsystem.broadcastservice.rest;

import static de.muenchen.oss.wahllokalsystem.broadcastservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.broadcastservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.broadcastservice.domain.Message;
import de.muenchen.oss.wahllokalsystem.broadcastservice.domain.MessageRepository;
import de.muenchen.oss.wahllokalsystem.broadcastservice.util.BroadcastExceptionKonstanten;
import de.muenchen.oss.wahllokalsystem.broadcastservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.broadcastservice.utils.TestdataFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@Slf4j
@SpringBootTest(
    classes = {MicroServiceApplication.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = {SPRING_TEST_PROFILE})
public class BroadcastControllerIntegrationTest {

  @Autowired MockMvc mvc;

  @Autowired MessageRepository messageRepository;

  @Autowired ObjectMapper objectMapper;

  @Value("${local.server.port}")
  private int port;

  @Value("${service.info.oid}")
  private String serviceOid;

  private static final String BROADCAST_URL = "/businessActions/broadcast";
  private static final String GETMESSAGE_URL = "/businessActions/getMessage/";
  private static final String DELETE_URL = "/businessActions/messageRead/";

  private static final List<String> WAHLBEZIRK_I_DS = Arrays.asList("1", "2", "3");
  private static final BroadcastMessageDTO BROADCAST_MESSAGE_DTO =
      new BroadcastMessageDTO(WAHLBEZIRK_I_DS, "Das ist ein Test");

  @BeforeEach
  void setup() {
    log.debug("Setting up test ...");
    log.debug("Port > {}", port);

    SecurityUtils.runWith(Authorities.ALL_BROADCAST_AUTHORITIES);
    messageRepository.deleteAll();
    SecurityContextHolder.clearContext();
  }

  @AfterEach
  void teardown() {
    SecurityContextHolder.clearContext();
  }

  @Nested
  class Broadcast {

    @Test
    void should_sendBroadcastMessage_when_messageSuccessfullySaved() throws Exception {
      log.debug("#BroadcastControllerIntegrationTest");
      MockHttpServletResponse result;
      result = mvc.perform(createPostRequest(BROADCAST_MESSAGE_DTO)).andReturn().getResponse();

      int status = result.getStatus();
      Assertions.assertThat(status).isEqualTo(200);
      log.debug("Result > Status: {} ", status);
    }

    @Test
    void should_throwFachlicheWlsException_when_givenWahlbezirkIdIsNull() throws Exception {
      final BroadcastMessageDTO bmDTOIncomplete1 =
          new BroadcastMessageDTO(null, "Das ist ein Test");
      mvc.perform(createPostRequest(bmDTOIncomplete1))
          .andExpect(status().isBadRequest())
          .andExpect(
              result -> {
                Exception resolvedException = result.getResolvedException();
                Assertions.assertThat(resolvedException)
                    .isInstanceOf(FachlicheWlsException.class)
                    .extracting("code", "serviceName", "message")
                    .contains(
                        BroadcastExceptionKonstanten
                            .CODE_NACHRICHTENABRUFEN_PARAMETER_UNVOLLSTAENDIG,
                        serviceOid,
                        "Das Object BroadcastMessage ist nicht vollständig.");
              });
    }

    @Test
    void should_throwFachlicheWlsException_when_givenMessageIsNull() throws Exception {
      final BroadcastMessageDTO bmDTOIncomplete2 =
          new BroadcastMessageDTO(Arrays.asList("1", "2", "3", "4"), null);
      mvc.perform(createPostRequest(bmDTOIncomplete2))
          .andExpect(status().isBadRequest())
          .andExpect(
              result -> {
                Exception resolvedException = result.getResolvedException();
                Assertions.assertThat(resolvedException)
                    .isInstanceOf(FachlicheWlsException.class)
                    .extracting("code", "serviceName", "message")
                    .contains(
                        BroadcastExceptionKonstanten
                            .CODE_NACHRICHTENABRUFEN_PARAMETER_UNVOLLSTAENDIG,
                        serviceOid,
                        "Das Object BroadcastMessage ist nicht vollständig.");
              });
    }

    private MockHttpServletRequestBuilder createPostRequest(final BroadcastMessageDTO requestBody) {
      return post(BROADCAST_URL)
          .with(
              jwt()
                  .authorities(
                      new SimpleGrantedAuthority(Authorities.SERVICE_POST_MESSAGE),
                      new SimpleGrantedAuthority(Authorities.REPOSITORY_WRITE_MESSAGE)))
          .with(csrf())
          .content(TestdataFactory.asJsonString(requestBody, objectMapper))
          .contentType(MediaType.APPLICATION_JSON_UTF8)
          .accept(MediaType.APPLICATION_JSON);
    }
  }

  @Nested
  class GetOldestMessage {

    @Test
    void should_returnBroadcastMessage_when_givenWahlbezirkId() throws Exception {
      log.debug("#GetMessageIntegrationTest");
      SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_MESSAGE);
      messageRepository.save(
          TestdataFactory.CreateMessageEntity.withCustomParams(
              "123", "Das ist ein Test", LocalDateTime.now()));
      SecurityContextHolder.clearContext();

      MockHttpServletResponse result =
          mvc.perform(createGetRequest("123")).andReturn().getResponse();
      String content = result.getContentAsString();
      Message message = objectMapper.readValue(content, Message.class);
      Assertions.assertThat(message.getNachricht()).isEqualTo("Das ist ein Test");
    }

    @Test
    void should_throwFachlicheWlsException_when_wahlbezirkIdIsBlank() throws Exception {
      log.debug("#GetMessageIntegrationTestGetParamBlank");
      mvc.perform(createGetRequest("   "))
          .andExpect(status().isBadRequest())
          .andExpect(
              result -> {
                Exception resolvedException = result.getResolvedException();
                Assertions.assertThat(resolvedException)
                    .isInstanceOf(FachlicheWlsException.class)
                    .extracting("code", "serviceName", "message")
                    .contains(
                        BroadcastExceptionKonstanten
                            .CODE_NACHRICHTENABRUFEN_PARAMETER_UNVOLLSTAENDIG,
                        serviceOid,
                        "wahlbezirkID is blank or empty");
              });
    }

    @Test
    void should_throwFachlicheWlsException_when_wahlbezirkIdIsEmpty() throws Exception {
      log.debug("#GetMessageIntegrationTestGetParamEmpty");
      String wahlbezirkID = "";

      mvc.perform(createGetRequest(wahlbezirkID))
          .andExpect(status().isInternalServerError())
          .andExpect(
              result -> {
                String actualStringResponse = result.getResponse().getContentAsString();
                String expectedStringResponse =
                    "{\"category\":\"T\",\"code\":\"999\",\"service\":\"WLS-BROADCAST\",\"message\":\"Ursache: class org.springframework.web.servlet.resource.NoResourceFoundException, Nachricht: No static resource businessActions/getMessage.\"}";
                Assertions.assertThat(actualStringResponse).isEqualTo(expectedStringResponse);
              });
    }

    @Test
    void should_throwFachlicheWlsException_when_noMessageFound() throws Exception {
      log.debug("#GetMessageNoContentIntegrationTest");
      mvc.perform(createGetRequest("123"))
          .andExpect(status().isNoContent())
          .andExpect(
              result -> {
                Exception resolvedException = result.getResolvedException();
                Assertions.assertThat(resolvedException)
                    .isInstanceOf(FachlicheWlsException.class)
                    .extracting("code", "serviceName", "message")
                    .contains("204", serviceOid, "No message found");
              });
    }

    private MockHttpServletRequestBuilder createGetRequest(final String wahlbezirkId) {
      return get(GETMESSAGE_URL + wahlbezirkId)
          .with(
              jwt()
                  .authorities(
                      new SimpleGrantedAuthority(Authorities.SERVICE_GET_MESSAGE),
                      new SimpleGrantedAuthority(Authorities.REPOSITORY_READ_MESSAGE)))
          .contentType(MediaType.APPLICATION_JSON_UTF8)
          .accept(MediaType.APPLICATION_JSON);
    }
  }

  @Nested
  class DeleteMessage {

    @Test
    void should_notThrowException_when_givenValidWahlbezirkID() throws Exception {
      log.debug("#deleteIntegrationTest");

      SecurityUtils.runWith(
          Authorities.REPOSITORY_WRITE_MESSAGE, Authorities.REPOSITORY_READ_MESSAGE);
      val wahlbezirkID = "123";
      Message message =
          TestdataFactory.CreateMessageEntity.withCustomParams(
              wahlbezirkID, "Das ist ein Test", LocalDateTime.now());
      messageRepository.save(message);

      List<Message> foundMessages =
          ((List<Message>) messageRepository.findAll())
              .stream().filter((m) -> m.getWahlbezirkID().equals(wahlbezirkID)).toList();
      Message foundMessage = foundMessages.stream().findFirst().get();
      Assertions.assertThat(foundMessage).isNotNull();
      SecurityContextHolder.clearContext();

      MockHttpServletResponse result =
          mvc.perform(createPostRequest(foundMessage.getOid().toString(), wahlbezirkID))
              .andReturn()
              .getResponse();

      int status = result.getStatus();
      Assertions.assertThat(status).isEqualTo(200);
      log.info("Result > Status: {} ", status);

      SecurityUtils.runWith(Authorities.REPOSITORY_READ_MESSAGE);
      foundMessages =
          ((List<Message>) messageRepository.findAll())
              .stream().filter((m) -> m.getWahlbezirkID().equals(wahlbezirkID)).toList();
      SecurityContextHolder.clearContext();
      Assertions.assertThat(foundMessages).isEmpty();
    }

    @Test
    void should_throwFachlicheWlsException_when_givenBadFormatUUID() throws Exception {
      log.debug("#deleteIntegrationTestBadFormatUUID");
      mvc.perform(createPostRequest("badformatparam-u-u-i-d", ""))
          .andExpect(status().isBadRequest())
          .andExpect(
              result -> {
                Exception resolvedException = result.getResolvedException();
                Assertions.assertThat(resolvedException)
                    .isInstanceOf(FachlicheWlsException.class)
                    .extracting("code", "serviceName", "message")
                    .contains("150", serviceOid, "Nachricht-UUID bad format");
              });
    }

    @Test
    void should_throw403_when_messageToDeleteIsNotOwnedByUser() throws Exception {

      SecurityUtils.runWith(
          Authorities.REPOSITORY_WRITE_MESSAGE, Authorities.REPOSITORY_READ_MESSAGE);
      val wahlbezirkID = "123";
      Message message =
          TestdataFactory.CreateMessageEntity.withCustomParams(
              wahlbezirkID, "Das ist ein Test", LocalDateTime.now());
      messageRepository.save(message);

      List<Message> foundMessages =
          ((List<Message>) messageRepository.findAll())
              .stream().filter((m) -> m.getWahlbezirkID().equals("123")).toList();
      Message foundMessage = foundMessages.stream().findFirst().get();
      Assertions.assertThat(foundMessage).isNotNull();
      SecurityContextHolder.clearContext();

      mvc.perform(createPostRequest(foundMessage.getOid().toString(), wahlbezirkID + "sth"))
          .andExpect(status().isForbidden());

      SecurityUtils.runWith(Authorities.REPOSITORY_READ_MESSAGE);
      foundMessages =
          ((List<Message>) messageRepository.findAll())
              .stream().filter((m) -> m.getWahlbezirkID().equals(wahlbezirkID)).toList();
      SecurityContextHolder.clearContext();
      Assertions.assertThat(foundMessages).isNotEmpty();
    }

    private MockHttpServletRequestBuilder createPostRequest(
        final String messageUUId, final String claimWahlbezirkId) {
      return post(DELETE_URL + messageUUId)
          .with(
              jwt()
                  .authorities(
                      new SimpleGrantedAuthority(Authorities.SERVICE_READ_MESSAGE),
                      new SimpleGrantedAuthority(Authorities.REPOSITORY_READ_MESSAGE),
                      new SimpleGrantedAuthority(Authorities.REPOSITORY_DELETE_MESSAGE))
                  .jwt(jwt -> jwt.claim("wahlbezirkID", claimWahlbezirkId)))
          .with(csrf())
          .contentType(MediaType.APPLICATION_JSON_UTF8)
          .accept(MediaType.APPLICATION_JSON);
    }
  }
}
