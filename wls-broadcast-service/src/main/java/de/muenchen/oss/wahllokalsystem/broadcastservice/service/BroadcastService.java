package de.muenchen.oss.wahllokalsystem.broadcastservice.service;

import de.muenchen.oss.wahllokalsystem.broadcastservice.domain.Message;
import de.muenchen.oss.wahllokalsystem.broadcastservice.domain.MessageRepository;
import de.muenchen.oss.wahllokalsystem.broadcastservice.rest.BroadcastDTOMapper;
import de.muenchen.oss.wahllokalsystem.broadcastservice.rest.BroadcastMessageDTO;
import de.muenchen.oss.wahllokalsystem.broadcastservice.rest.MessageDTO;
import de.muenchen.oss.wahllokalsystem.broadcastservice.util.BroadcastExceptionKonstanten;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.BezirkIDPermissionEvaluator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class BroadcastService {

  private final MessageRepository messageRepo;

  private final BroadcastDTOMapper broadcastMapper;

  private final ExceptionFactory exceptionFactory;

  private final BezirkIDPermissionEvaluator bezirkIdPermissionEvaluator;

  @PreAuthorize("hasAuthority('Broadcast_BUSINESSACTION_Broadcast')")
  public void broadcast(final BroadcastMessageDTO messageToBroadcast) {
    log.debug("#broadcast");

    if (null == messageToBroadcast
        || null == messageToBroadcast.wahlbezirkIDs()
        || messageToBroadcast.wahlbezirkIDs().size() <= 0
        || StringUtils.isEmpty(messageToBroadcast.nachricht())
        || StringUtils.isBlank(messageToBroadcast.nachricht())) {
      throw exceptionFactory.createFachlicheWlsException(
          BroadcastExceptionKonstanten.BROADCAST_PARAMETER_UNVOLLSTAENDIG);
    }

    List<Message> messagesToSave =
        broadcastMapper.toListOfMessageEntity(messageToBroadcast, LocalDateTime.now());

    messageRepo.saveAll(messagesToSave);
  }

  @PreAuthorize(
      "hasAuthority('Broadcast_BUSINESSACTION_GetMessage')"
          + " and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#wahlbezirkID, authentication)")
  public MessageDTO getOldestMessage(@P("wahlbezirkID") String wahlbezirkID) {
    log.debug("#nachrichtenAbrufen wahlbezirkID {} length {}", wahlbezirkID, wahlbezirkID.length());

    if (StringUtils.isEmpty(wahlbezirkID) || StringUtils.isBlank(wahlbezirkID)) {
      throw exceptionFactory.createFachlicheWlsException(
          BroadcastExceptionKonstanten.BROADCAST_PARAMETER_UNVOLLSTAENDIG_EMPTY_WAHLBEZIRKID);
    }

    val message = messageRepo.findFirstByWahlbezirkIDOrderByEmpfangsZeit(wahlbezirkID);

    if (message.isEmpty()) {
      throw exceptionFactory.createFachlicheWlsException(
          BroadcastExceptionKonstanten.BROADCAST_ENTITY_NOT_FOUND);
    }

    return broadcastMapper.toDto(message.get());
  }

  @PreAuthorize("hasAuthority('Broadcast_BUSINESSACTION_MessageRead')")
  public void deleteMessage(String nachrichtID) { // TODO UUID als Parameter
    if (StringUtils.isEmpty(nachrichtID) || StringUtils.isBlank(nachrichtID)) {
      throw exceptionFactory.createFachlicheWlsException(
          BroadcastExceptionKonstanten.BROADCAST_PARAMETER_UNVOLLSTAENDIG_EMPTY_NACHRICHTID);
    }

    val nachrichtUUID = toUUID(nachrichtID);
    val messageFromRepo = messageRepo.findById(nachrichtUUID);
    if (messageFromRepo.isEmpty()) {
      return;
    }

    throwSecurityExceptionWhenCurrentUserIsNotOwnerOfMessage(messageFromRepo.get());
    messageRepo.deleteById(nachrichtUUID);
  }

  private void throwSecurityExceptionWhenCurrentUserIsNotOwnerOfMessage(final Message message) {
    if (!bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(
        message.getWahlbezirkID(), SecurityContextHolder.getContext().getAuthentication())) {
      throw exceptionFactory.createSicherheitsWlsException(
          BroadcastExceptionKonstanten.USER_NOT_OWNER_OF_MESSAGE);
    }
  }

  private UUID toUUID(String uuidAsString) {
    try {
      return java.util.UUID.fromString(uuidAsString);
    } catch (IllegalArgumentException e) {
      throw exceptionFactory.createFachlicheWlsException(
          BroadcastExceptionKonstanten.BROADCAST_PARAMETER_UNVOLLSTAENDIG_BAD_FORMAT_UUID);
    }
  }
}
