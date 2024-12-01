package de.muenchen.oss.wahllokalsystem.wlsgraphqlservice.controller;

import de.muenchen.oss.wahllokalsystem.wlsgraphqlservice.controller.dto.BroadcastMessage;
import de.muenchen.oss.wahllokalsystem.wlsgraphqlservice.controller.dto.Wahlbezirk;
import de.muenchen.oss.wahllokalsystem.wlsgraphqlservice.controller.dto.WahlbezirkArt;
import de.muenchen.oss.wahllokalsystem.wlsgraphqlservice.eai.basisdaten.client.KopfdatenControllerApi;
import de.muenchen.oss.wahllokalsystem.wlsgraphqlservice.eai.basisdaten.client.WahlbezirkeControllerApi;
import de.muenchen.oss.wahllokalsystem.wlsgraphqlservice.eai.broadcast.client.BroadcastControllerApi;
import de.muenchen.oss.wahllokalsystem.wlsgraphqlservice.eai.broadcast.model.BroadcastMessageDTO;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class GraphqlController {

    private final WahlbezirkeControllerApi wahlbezirkeControllerApi;
    private final KopfdatenControllerApi kopfdatenControllerApi;
    private final BroadcastControllerApi broadcastControllerApi;

    @QueryMapping
    public Wahlbezirk wahlbezirk(@Argument final UUID id) {
        return new Wahlbezirk(id, LocalDate.now(), (int) (Math.random() * 1000), WahlbezirkArt.BWB);
    }

    @MutationMapping
    public int broadcastMessage(@Argument List<UUID> wahlbezirkIDs, @Argument List<String> messages) {
        val broadcasts = messages.stream()
                .map(message -> new BroadcastMessageDTO().nachricht(message).wahlbezirkIDs(wahlbezirkIDs.stream().map(UUID::toString).toList()))
                .toList();
        broadcasts.forEach(broadcastControllerApi::broadcast);

        return wahlbezirkIDs.size() * messages.size();
    }

    @SchemaMapping
    public BroadcastMessage latestBroadcastMessage(final Wahlbezirk wahlbezirk) {
        val message = broadcastControllerApi.getMessage(wahlbezirk.id().toString());
        return message != null ? new BroadcastMessage(message.getOid(), message.getNachricht()) : null;
    }
}
