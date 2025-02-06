package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.client.eai;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.awerte.AWerte;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.Stapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse.Ergebnis;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse.Ergebnisse;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.AWerteDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.ErgebnisDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.ErgebnismeldungDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.UngueltigeStimmzettelDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.MeldungsartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.authentication.AuthenticationService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.WahlartModel;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class Mapping {

    private final AuthenticationService authenticationService;

    public AWerteDTO toEntity(final AWerte aWerte) {
        AWerteDTO aoueaiAWerte = new AWerteDTO();
        if (aWerte != null) {
            aoueaiAWerte.setA1(aWerte.getA1());
            aoueaiAWerte.setA2(aWerte.getA2());
        }
        return aoueaiAWerte;
    }

    public Set<ErgebnisDTO> toAoueaiErgebnisseSet(final List<Ergebnisse> ergebnisse) {
        Set<ErgebnisDTO> ergebnisSet = new HashSet<>();

        ergebnisse.forEach(ergebnisList -> {
            Stapelart stapelart = ergebnisList.getBezirkUndWahlIDStapelart().getStapelart();
            ergebnisList.getErgebnisse().forEach(ergebnis -> {

                ErgebnisDTO aoueaiErgebnis = new ErgebnisDTO();
                aoueaiErgebnis.setErgebnis(ergebnis.getErgebnis());
                aoueaiErgebnis.setKandidatID(ergebnis.getKandidatID());
                aoueaiErgebnis.setWahlvorschlagID(ergebnis.getWahlvorschlagID());
                val wvsoz = ergebnis.getWahlvorschlagsordnungszahl();

                try {
                    aoueaiErgebnis.setWahlvorschlagsordnungszahl(wvsoz);
                } catch (Exception e) {
                    log.warn("toAoueaiErgebnisseSet 4.1.1  fehler: {} e komplett: {}", e.getMessage(), e.toString());
                    log.warn("toAoueaiErgebnisseSet 4.1.1.1  ergebnisse: {} ", ergebnisse);
                    log.warn("toAoueaiErgebnisseSet 4.1.1.2  ergebnisList: {} ", ergebnisList);
                    log.warn("toAoueaiErgebnisseSet 4.1.1.3  ergebnis: {} ", ergebnis);
                }
                aoueaiErgebnis.setStimmenart(stapelart.name());
                ergebnisSet.add(aoueaiErgebnis);
            });
        });

        return ergebnisSet;
    }

    public ErgebnismeldungDTO.WahlartEnum toWahlart(final WahlartModel wahlart) {
        try {
            return ErgebnismeldungDTO.WahlartEnum.valueOf(wahlart.name());
        } catch (Exception e) {
            log.error("#convertWahlart: parsing Exception", e);
        }
        return null;
    }

    public Set<UngueltigeStimmzettelDTO> toDtoSet(final List<Ergebnisse> ungueltigeErgebnisse) {
        Set<UngueltigeStimmzettelDTO> ungueltigeStimmzettelSet = new HashSet<>();
        ungueltigeErgebnisse.forEach(ungueltigesErgebnis -> {
            Stapelart stapelart = ungueltigesErgebnis.getBezirkUndWahlIDStapelart().getStapelart();
            ungueltigesErgebnis.getErgebnisse().forEach(ergebnis -> ungueltigeStimmzettelSet.add(toDto(ergebnis, stapelart)));
        });
        return ungueltigeStimmzettelSet;
    }

    public ErgebnismeldungDTO.MeldungsartEnum toDTO(final MeldungsartModel meldungsart) {
        return switch (meldungsart) {
        case V1 -> ErgebnismeldungDTO.MeldungsartEnum.NIEDERSCHRIFT;
        case V3 -> ErgebnismeldungDTO.MeldungsartEnum.SCHNELLMELDUNG;
        };
    }

    private UngueltigeStimmzettelDTO toDto(final Ergebnis ergebnis, final Stapelart stapelart) {
        UngueltigeStimmzettelDTO ungueltigeStimmzettel = new UngueltigeStimmzettelDTO();
        ungueltigeStimmzettel.setWahlvorschlagID(ergebnis.getWahlvorschlagID());
        ungueltigeStimmzettel.setAnzahl(ergebnis.getErgebnis());
        ungueltigeStimmzettel.setStimmenart(stapelart.name());
        return ungueltigeStimmzettel;
    }
}
