package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.client.eai;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.awerte.AWerte;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.Stapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse.Ergebnis;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse.Ergebnisse;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.ErgebnisDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.ErgebnismeldungDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.UngueltigeStimmzettelDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.WahlberechtigteDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.authentication.AuthenticationService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.WahlartModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Mapping {

    private final AuthenticationService authenticationService;
    private final ExceptionFactory exceptionFactory;

    private static final Logger LOGGER = LoggerFactory.getLogger(Mapping.class);
    private static Long wvsoz;

    public List<AWerte> toAWerteList(List<WahlberechtigteDTO> aoueai) {
        LOGGER.info("#toAWerteList List<AWerte>");
        if (aoueai == null) {
            LOGGER.error("Antwort von AOUEAI war leer.");
            return null;
        }
        List<AWerte> entity = new ArrayList<>();
        aoueai.forEach(wahlberechtigte -> entity.add(toEntity(wahlberechtigte)));
        return entity;
    }

    private AWerte toEntity(WahlberechtigteDTO aoueai) {
        if (aoueai == null || aoueai.getWahlID() == null || aoueai.getWahlbezirkID() == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.MAPPING_AOUEAI);
        }
        AWerte entity = new AWerte();
        entity.setBezirkUndWahlID(new BezirkUndWahlID(aoueai.getWahlbezirkID(), aoueai.getWahlID()));
        entity.setA1(aoueai.getA1());
        entity.setA2(aoueai.getA2());
        return entity;
    }

    public de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.AWerteDTO toEntity(AWerte aWerte) {
        de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.AWerteDTO aoueaiAWerte = new de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.AWerteDTO();
        if (aWerte != null) {
            aoueaiAWerte.setA1(aWerte.getA1());
            aoueaiAWerte.setA2(aWerte.getA2());
        }
        return aoueaiAWerte;
    }

    public Set<ErgebnisDTO> toAoueaiErgebnisseSet(List<Ergebnisse> ergebnisse) {
        Set<de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.ErgebnisDTO> ergebnisSet = new HashSet<>();

        ergebnisse.forEach(ergebnisList -> {
            Stapelart stapelart = ergebnisList.getBezirkUndWahlIDStapelart().getStapelart();
            ergebnisList.getErgebnisse().forEach(ergebnis -> {

                de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.ErgebnisDTO aoueaiErgebnis = new de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.ErgebnisDTO();
                aoueaiErgebnis.setErgebnis(ergebnis.getErgebnis());
                aoueaiErgebnis.setKandidatID(ergebnis.getKandidatID());
                aoueaiErgebnis.setWahlvorschlagID(ergebnis.getWahlvorschlagID());
                wvsoz = ergebnis.getWahlvorschlagsordnungszahl();

                try {
                    aoueaiErgebnis.setWahlvorschlagsordnungszahl((long) wvsoz);
                } catch (Exception e) {
                    LOGGER.warn("toAoueaiErgebnisseSet 4.1.1  fehler: {} e komplett: {}", e.getMessage(), e.toString());
                    LOGGER.warn("toAoueaiErgebnisseSet 4.1.1.1  ergebnisse: {} ", ergebnisse);
                    LOGGER.warn("toAoueaiErgebnisseSet 4.1.1.2  ergebnisList: {} ", ergebnisList);
                    LOGGER.warn("toAoueaiErgebnisseSet 4.1.1.3  ergebnis: {} ", ergebnis);
                }
                aoueaiErgebnis.setStimmenart(stapelart.name());
                ergebnisSet.add(aoueaiErgebnis);
            });
        });

        return ergebnisSet;
    }

    public ErgebnismeldungDTO.WahlartEnum convertWahlart(WahlartModel wahlart) {
        try {
            return ErgebnismeldungDTO.WahlartEnum.valueOf(wahlart.name());
        } catch (Exception e) {
            LOGGER.error("#convertWahlart: parsing Exception");
        }
        return null;
    }

    public ErgebnismeldungDTO.WahlartEnum toWahlart(WahlartModel wahlart) {
        try {
            return ErgebnismeldungDTO.WahlartEnum.valueOf(wahlart.name());
        } catch (Exception e) {
            LOGGER.error("#convertWahlart: parsing Exception");
        }
        return null;
    }

    public Set<UngueltigeStimmzettelDTO> toDtoSet(List<Ergebnisse> ungueltigeErgebnisse) {
        Set<UngueltigeStimmzettelDTO> ungueltigeStimmzettelSet = new HashSet<>();
        ungueltigeErgebnisse.forEach(ungueltigesErgebnis -> {
            Stapelart stapelart = ungueltigesErgebnis.getBezirkUndWahlIDStapelart().getStapelart();
            ungueltigesErgebnis.getErgebnisse().forEach(ergebnis -> ungueltigeStimmzettelSet.add(toDto(ergebnis, stapelart)));
        });
        return ungueltigeStimmzettelSet;
    }

    private UngueltigeStimmzettelDTO toDto(Ergebnis ergebnis, Stapelart stapelart) {
        UngueltigeStimmzettelDTO ungueltigeStimmzettel = new UngueltigeStimmzettelDTO();
        ungueltigeStimmzettel.setWahlvorschlagID(ergebnis.getWahlvorschlagID());
        ungueltigeStimmzettel.setAnzahl(ergebnis.getErgebnis());
        ungueltigeStimmzettel.setStimmenart(stapelart.name());
        return ungueltigeStimmzettel;
    }

    public WahlbezirkArtModel getWahlbezirkart() {
        return authenticationService.getWahlbezirkArtOfCurrentAuthentication();
    }
}
