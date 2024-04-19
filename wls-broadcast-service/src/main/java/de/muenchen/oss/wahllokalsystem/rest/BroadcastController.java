package de.muenchen.oss.wahllokalsystem.rest;

import de.muenchen.oss.wahllokalsystem.service.BroadcastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/businessActions")
public class BroadcastController {

    private final String BROADCAST_PATH = "/broadcast";
    private final String MESSAGE_PATH = "/getMessage/{wahlbezirkID}";
    private final String MESSAGE_READ_PATH = "/messageRead/{nachrichtID}";

    @Autowired
    BroadcastService broadcastService;

    @PostMapping(value = BROADCAST_PATH)
    public void broadcast(@RequestBody BroadcastMessageDTO body) {
        broadcastService.broadcast(body);
    }

    @GetMapping(value = MESSAGE_PATH)
    public MessageDTO getMessage(@PathVariable("wahlbezirkID") String wahlbezirkID) {
        return broadcastService.getOldestMessage(wahlbezirkID);
    }

    /**
     * This BusinessAction's purpose is: Markiert die Nachricht mit der gegebenen ID als gelesen
     */
    @PostMapping(value = MESSAGE_READ_PATH) //TODO DeleteMapping wäre besser
    public void deleteMessage(@PathVariable("nachrichtID") String nachrichtID) { //TODO Besser wäre 204
        broadcastService.deleteMessage(nachrichtID);
    }

}
