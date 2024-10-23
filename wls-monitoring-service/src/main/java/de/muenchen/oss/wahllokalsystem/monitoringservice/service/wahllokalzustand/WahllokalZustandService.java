package de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WahllokalZustandService {

    private final ExceptionFactory exceptionFactory;
    private final WahllokalZustandClient wahllokalZustandClient;
    private final WahllokalZustandValidator wahllokalZustandValidator;

    @PreAuthorize("hasAuthority('Monitoring_BUSINESSACTION_PostLastSeen')")
    public void postLastSeen(final String wahlbezirkID) {
        wahllokalZustandValidator.validWahlbezirkIDOrThrow(wahlbezirkID, WahllokalZustandOperation.POST_LASTSEEN);
        val wahllokalZustand = WahllokalZustandModel.builder().zuletztGesehen(LocalDateTime.now()).wahlbezirkID(wahlbezirkID).build();
        wahllokalZustandClient.postWahllokalZustand(wahllokalZustand);
    }

    @PreAuthorize("hasAuthority('Monitoring_BUSINESSACTION_PostLetzteAbmeldung')")
    public void postLetzteAbmeldung(final String wahlbezirkID) {
        wahllokalZustandValidator.validWahlbezirkIDOrThrow(wahlbezirkID, WahllokalZustandOperation.POST_LETZTEABMELDUNG);
        val wahllokalZustand = WahllokalZustandModel.builder().letzteAbmeldung(LocalDateTime.now()).wahlbezirkID(wahlbezirkID).build();
        wahllokalZustandClient.postWahllokalZustand(wahllokalZustand);
    }

    @PreAuthorize("hasAuthority('Monitoring_BUSINESSACTION_PostSchnellmeldungSendungsuhrzeit')")
    public void postSchnellmeldungSendungsuhrzeit(SendungsdatenModel sendungsdatenModel) {
        wahllokalZustandValidator.validSendungsdatenModel(sendungsdatenModel, WahllokalZustandOperation.POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT);
        wahllokalZustandClient.postWahllokalZustand(
                WahllokalZustandModel.builder()
                        .wahlID(sendungsdatenModel.bezirkUndWahlID().getWahlID())
                        .wahlbezirkID(sendungsdatenModel.bezirkUndWahlID().getWahlbezirkID())
                        .druckzustaende(
                                Set.of(
                                        DruckzustandModel.builder()
                                                .wahlID(sendungsdatenModel.bezirkUndWahlID().getWahlID())
                                                .schnellmeldungSendenUhrzeit(sendungsdatenModel.sendungsuhrzeit())
                                                .build()))
                        .build());
    }

    @PreAuthorize("hasAuthority('Monitoring_BUSINESSACTION_PostSchnellmeldungDruckuhrzeit')")
    public void postSchnellmeldungDruckuhrzeit(DruckdatenModel druckdatenModel) {
        wahllokalZustandValidator.validDruckdatenModel(druckdatenModel, WahllokalZustandOperation.POST_SCHNELLMELDUNG_DRUCKUHRZEIT);
        wahllokalZustandClient.postWahllokalZustand(
                WahllokalZustandModel.builder()
                        .wahlID(druckdatenModel.bezirkUndWahlID().getWahlID())
                        .wahlbezirkID(druckdatenModel.bezirkUndWahlID().getWahlbezirkID())
                        .druckzustaende(
                                Set.of(
                                        DruckzustandModel.builder()
                                                .wahlID(druckdatenModel.bezirkUndWahlID().getWahlID())
                                                .schnellmeldungDruckUhrzeit(druckdatenModel.druckuhrzeit())
                                                .build()))
                        .build());
    }

    @PreAuthorize("hasAuthority('Monitoring_BUSINESSACTION_PostNiederschriftSendungsuhrzeit')")
    public void postNiederschriftSendungsuhrzeit(SendungsdatenModel sendungsdatenModel) {
        wahllokalZustandValidator.validSendungsdatenModel(sendungsdatenModel, WahllokalZustandOperation.POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT);
        wahllokalZustandClient.postWahllokalZustand(
                WahllokalZustandModel.builder()
                        .wahlID(sendungsdatenModel.bezirkUndWahlID().getWahlID())
                        .wahlbezirkID(sendungsdatenModel.bezirkUndWahlID().getWahlbezirkID())
                        .druckzustaende(
                                Set.of(
                                        DruckzustandModel.builder()
                                                .wahlID(sendungsdatenModel.bezirkUndWahlID().getWahlID())
                                                .niederschriftSendenUhrzeit(sendungsdatenModel.sendungsuhrzeit())
                                                .build()))
                        .build());
    }

    @PreAuthorize("hasAuthority('Monitoring_BUSINESSACTION_PostNiederschriftDruckuhrzeit')")
    public void postNiederschriftDruckuhrzeit(DruckdatenModel druckdatenModel) {
        wahllokalZustandValidator.validDruckdatenModel(druckdatenModel, WahllokalZustandOperation.POST_NIEDERSCHRIFT_DRUCKUHRZEIT);
        wahllokalZustandClient.postWahllokalZustand(
                WahllokalZustandModel.builder()
                        .wahlID(druckdatenModel.bezirkUndWahlID().getWahlID())
                        .wahlbezirkID(druckdatenModel.bezirkUndWahlID().getWahlbezirkID())
                        .druckzustaende(
                                Set.of(
                                        DruckzustandModel.builder()
                                                .wahlID(druckdatenModel.bezirkUndWahlID().getWahlID())
                                                .niederschriftDruckUhrzeit(druckdatenModel.druckuhrzeit())
                                                .build()))
                        .build());
    }

}
