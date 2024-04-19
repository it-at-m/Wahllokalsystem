package de.muenchen.oss.wahllokalsystem.broadcastservice.rest;

import java.util.List;

public record BroadcastMessageDTO(List<String> wahlbezirkIDs, String nachricht) {

}
