package de.muenchen.oss.wahllokalsystem.rest;

import java.util.List;

public record BroadcastMessageDTO(List<String> wahlbezirkIDs, String nachricht) {

}
