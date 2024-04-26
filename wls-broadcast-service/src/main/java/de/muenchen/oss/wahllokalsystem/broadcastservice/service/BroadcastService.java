package de.muenchen.oss.wahllokalsystem.broadcastservice.service;

import de.muenchen.oss.wahllokalsystem.broadcastservice.domain.Message;
import de.muenchen.oss.wahllokalsystem.broadcastservice.domain.MessageRepository;
import de.muenchen.oss.wahllokalsystem.broadcastservice.rest.BroadcastMessageDTO;
import de.muenchen.oss.wahllokalsystem.broadcastservice.rest.MessageDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionKonstanten;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * Provides a service to execute business-actions.
 * If used as generated by GAIA this service will be autowired and called by
 * BusinessActionController.
 */
@Service
@Slf4j
@Component
public class BroadcastService {

    @Autowired
    MessageRepository messageRepo;

    @Autowired
    BroadcastMapper broadcastMapper;

    @Value("${service.info.oid}")
    private String serviceOid;

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @PreAuthorize("hasAuthority('Broadcast_BUSINESSACTION_Broadcast')")
    public void broadcast(final BroadcastMessageDTO messageToBroadcast) {
        log.debug("#broadcast");

        val validationResult = validator.validate(messageToBroadcast);

        if (!validationResult.isEmpty()) {
            throw FachlicheWlsException.withCode(ExceptionKonstanten.CODE_NACHRICHTENABRUFEN_PARAMETER_UNVOLLSTAENDIG)
                    .buildWithMessage("Das Object BroadcastMessage ist nicht vollständig.");
        }

        List<Message> messagesToSave = broadcastMapper.fromBroadcastMessageDTOtoListOfMessages(messageToBroadcast);

        messageRepo.saveAll(messagesToSave);
    }

    @PreAuthorize("hasAuthority('Broadcast_BUSINESSACTION_GetMessage')")
    public MessageDTO getOldestMessage(String wahlbezirkID) throws FachlicheWlsException {
        log.debug("#nachrichtenAbrufen wahlbezirkID {} length {}", wahlbezirkID, wahlbezirkID.length());

        if (StringUtils.isEmpty(wahlbezirkID) || StringUtils.isBlank(wahlbezirkID)) {
            throw FachlicheWlsException.withCode(ExceptionKonstanten.CODE_NACHRICHTENABRUFEN_PARAMETER_UNVOLLSTAENDIG)
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
        UUID nachrichtUUID;
        if (StringUtils.isEmpty(nachrichtID) || StringUtils.isBlank(nachrichtID)) {
            throw FachlicheWlsException.withCode(ExceptionKonstanten.CODE_NACHRICHTENABRUFEN_PARAMETER_UNVOLLSTAENDIG)
                    .buildWithMessage("nachrichtID is blank or empty");
        }

        try {
            nachrichtUUID = java.util.UUID.fromString(nachrichtID);
            messageRepo.deleteById(nachrichtUUID);
        } catch (Exception e) {
            if (e instanceof NumberFormatException) {
                throw FachlicheWlsException.withCode(ExceptionKonstanten.CODE_NACHRICHTENABRUFEN_PARAMETER_UNVOLLSTAENDIG).inService(serviceOid)
                        .buildWithMessage("Nachricht-UUID bad format");
            } else {
                throw e;
            }
        }
    }

}
