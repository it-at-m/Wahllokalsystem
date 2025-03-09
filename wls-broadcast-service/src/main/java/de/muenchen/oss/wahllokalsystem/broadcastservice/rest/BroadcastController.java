package de.muenchen.oss.wahllokalsystem.broadcastservice.rest;

import de.muenchen.oss.wahllokalsystem.broadcastservice.service.BroadcastService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/businessActions")
@RequiredArgsConstructor
public class BroadcastController {

    private final String BROADCAST_PATH = "/broadcast";
    private final String MESSAGE_PATH = "/getMessage/{wahlbezirkID}";
    private final String MESSAGE_READ_PATH = "/messageRead/{nachrichtID}";

    @Autowired
    private final BroadcastService broadcastService;

    @Operation(
            summary = "Nachricht an alle senden",
            description = "Bietet einen Endpunkt an, der eine Nachricht für alle Wahlbezirke(Wahllokale) bereitstellt.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Nachricht erfolgreich gesendet."
                    )
            }
    )
    @PostMapping(value = BROADCAST_PATH)
    public void broadcast(@RequestBody BroadcastMessageDTO body) {
        broadcastService.broadcast(body);
    }

    @Operation(
            summary = "Letzte Nachricht lesen", description = "Sucht nach der ältesten Nachricht für die gegebene Wahlbezirk-Id und gibt diese zurück.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Nachricht erfolgreich zurückgegeben.")
            }
    )
    @GetMapping(value = MESSAGE_PATH)
    public MessageDTO getMessage(@PathVariable("wahlbezirkID") String wahlbezirkID) {
        return broadcastService.getOldestMessage(wahlbezirkID);
    }

    @Operation(
            summary = "Nachricht löschen",
            description = "Löscht die Nachricht mit der gegebenen ID, nachdem sie gelesen wurde. Es wird nur der dem entsprechenden Wahllokal zugewiesene Datenbankeintrag.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Nachricht erfolgreich gelöscht.")
            }
    )
    @PostMapping(value = MESSAGE_READ_PATH)
    public void deleteMessage(@PathVariable("nachrichtID") String nachrichtID) { //TODO Besser wäre 204
        broadcastService.deleteMessage(nachrichtID);
    }

}
