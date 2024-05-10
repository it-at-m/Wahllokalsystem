package de.muenchen.oss.wahllokalsystem.broadcastservice.service;

import de.muenchen.oss.wahllokalsystem.broadcastservice.domain.Message;
import de.muenchen.oss.wahllokalsystem.broadcastservice.domain.MessageRepository;
import de.muenchen.oss.wahllokalsystem.broadcastservice.rest.BroadcastMessageDTO;
import de.muenchen.oss.wahllokalsystem.broadcastservice.rest.MessageDTO;
import de.muenchen.oss.wahllokalsystem.broadcastservice.util.BroadcastExceptionKonstanten;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionKonstanten;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BroadcastService {

    @Autowired
    private final MessageRepository messageRepo;

    @Autowired
    private final BroadcastMapper broadcastMapper;

    public BroadcastService(MessageRepository messageRepo, BroadcastMapper broadcastMapper) {
        this.messageRepo = messageRepo;
        this.broadcastMapper = broadcastMapper;
    }

    @Value("${service.info.oid}")
    private String serviceOid;

    @PreAuthorize("hasAuthority('Broadcast_BUSINESSACTION_Broadcast')")
    public void broadcast(final BroadcastMessageDTO messageToBroadcast) {
        log.debug("#broadcast");

        if (null == messageToBroadcast || null == messageToBroadcast.wahlbezirkIDs() || messageToBroadcast.wahlbezirkIDs().size() <= 0
                || StringUtils.isEmpty(messageToBroadcast.nachricht()) || StringUtils.isBlank(messageToBroadcast.nachricht())) {
            throw FachlicheWlsException.withCode(BroadcastExceptionKonstanten.CODE_NACHRICHTENABRUFEN_PARAMETER_UNVOLLSTAENDIG).inService(serviceOid)
                    .buildWithMessage("Das Object BroadcastMessage ist nicht vollständig.");
        }

        List<Message> messagesToSave = broadcastMapper.fromBroadcastMessageDTOtoListOfMessages(messageToBroadcast, LocalDateTime.now());

        messageRepo.saveAll(messagesToSave);
    }

    @PreAuthorize("hasAuthority('Broadcast_BUSINESSACTION_GetMessage')")
    public MessageDTO getOldestMessage(String wahlbezirkID) throws FachlicheWlsException {
        log.debug("#nachrichtenAbrufen wahlbezirkID {} length {}", wahlbezirkID, wahlbezirkID.length());

        if (StringUtils.isEmpty(wahlbezirkID) || StringUtils.isBlank(wahlbezirkID)) {
            throw FachlicheWlsException.withCode(BroadcastExceptionKonstanten.CODE_NACHRICHTENABRUFEN_PARAMETER_UNVOLLSTAENDIG).inService(serviceOid)
                    .buildWithMessage("wahlbezirkID is blank or empty");
        }

        val message = messageRepo.findFirstByWahlbezirkIDOrderByEmpfangsZeit(wahlbezirkID);

        if (message.isEmpty()) {
            throw FachlicheWlsException.withCode(ExceptionKonstanten.CODE_ENTITY_NOT_FOUND).inService(serviceOid).buildWithMessage("No message found");
        }

        return broadcastMapper.toDto(message.get());
    }

    @PreAuthorize("hasAuthority('Broadcast_BUSINESSACTION_MessageRead')")
    public void deleteMessage(String nachrichtID) { //TODO UUID als Parameter
        if (StringUtils.isEmpty(nachrichtID) || StringUtils.isBlank(nachrichtID)) {
            throw FachlicheWlsException.withCode(BroadcastExceptionKonstanten.CODE_NACHRICHTENABRUFEN_PARAMETER_UNVOLLSTAENDIG)
                    .buildWithMessage("nachrichtID is blank or empty");
        }

        try {
            UUID nachrichtUUID = java.util.UUID.fromString(nachrichtID);
            messageRepo.deleteById(nachrichtUUID);
        } catch (IllegalArgumentException e) {
            throw FachlicheWlsException.withCode(BroadcastExceptionKonstanten.CODE_NACHRICHTENABRUFEN_PARAMETER_UNVOLLSTAENDIG).inService(serviceOid)
                        .buildWithMessage("Nachricht-UUID bad format");
        }
    }

}
