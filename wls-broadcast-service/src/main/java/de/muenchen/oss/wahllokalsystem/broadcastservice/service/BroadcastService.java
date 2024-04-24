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
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * Provides a service to execute business-actions.
 * If used as generated by GAIA this service will be autowired and called by
 * BusinessActionController.
 */
@Service
@PreAuthorize("hasAuthority('Broadcast_BUSINESSACTION_Broadcast')")
@Slf4j
@Component
public class BroadcastService {

    @Autowired
    MessageRepository messageRepo;

    @Autowired
    BroadcastMapper broadcastMapper;

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

        LocalDateTime now = LocalDateTime.now();
        List<Message> messagesToSave = messageToBroadcast.wahlbezirkIDs().stream().map(s -> {
            Message message = new Message();
            message.setWahlbezirkID(s);
            message.setEmpfangsZeit(now);
            message.setNachricht(messageToBroadcast.nachricht());
            return message;
        }).toList();

        messageRepo.saveAll(messagesToSave);
    }

    @PreAuthorize("hasAuthority('Broadcast_BUSINESSACTION_GetMessage')")
    public MessageDTO getOldestMessage(String wahlbezirkID) throws FachlicheWlsException {
        log.debug("#nachrichtenAbrufen");
        //ToDo:     Wird später aus neuem wls-common exception ExceptionKonstanten etwa gebaut

        /*
         * if (Strings.isNullOrEmpty(wahlbezirkID)) {
         * throw
         * WlsExceptionFactory.build(ExceptionKonstanten.CODE_NACHRICHTENABRUFEN_PARAMETER_UNVOLLSTAENDIG);
         * }
         */

        val message = messageRepo.findFirstByWahlbezirkIDOrderByEmpfangsZeit(wahlbezirkID);

        if (message.isEmpty()) {
            throw FachlicheWlsException.withCode(ExceptionKonstanten.CODE_ENTITY_NOT_FOUND).buildWithMessage("No message found");
        }

        return broadcastMapper.toDto(message.get());
    }

    @PreAuthorize("hasAuthority('Broadcast_BUSINESSACTION_MessageRead')")
    public void deleteMessage(String nachrichtID) { //TODO UUID als Parameter

        //         if (Strings.isNullOrEmpty(nachrichtID)) {
        //             throw WlsExceptionFactory.build(ExceptionKonstanten.CODE_NACHRICHTENABRUFEN_PARAMETER_UNVOLLSTAENDIG);
        //         }

        val nachrichtUUID = java.util.UUID.fromString(nachrichtID);
        //TODO hat das Altsystem einen Fehler geworfen wenn er nichts gefunden hat?
        // Das Altsystem hat so getan alswürde es werfen:
        // catch (Exception e) {
        //    LOG.info("Message with OID:" + nachrichtUUID + "already deleted");
        // }
        // In Wirklichkeit wirft in diesem Fall CrudRepository keine Exception
        // https://github.com/spring-projects/spring-data-commons/issues/2651
        try {
            messageRepo.deleteById(nachrichtUUID);
        } catch (Exception e) {
            //ToDo aus neuen ExceptionKonstanten etwas finden
            log.error("Message with OID:" + nachrichtUUID + " could not be deleted");
        }
    }

}
