package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.utils.testdaten;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.UrnenwahlVorbereitung;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.urnenwahlvorbereitung.UrnenwahlvorbereitungWriteDTO;
import java.util.List;

public class UrnenwahlVorbereitungTestdatenfactory {

    public static UrnenwahlVorbereitung.UrnenwahlVorbereitungBuilder initValid(final String wahlbezirkID, String wahlID) {
        return UrnenwahlVorbereitung.builder().wahlbezirkID(wahlbezirkID).urnenAnzahl(List.of(WahlurneTestdatenfactory.initValid(wahlID).build()))
                .anzahlWahlkabinen(10).anzahlWahltische(12).anzahlNebenraeume(0);
    }

    public static UrnenwahlvorbereitungWriteDTO.UrnenwahlvorbereitungWriteDTOBuilder initValidDTO(final String wahlID) {
        return UrnenwahlvorbereitungWriteDTO.builder().anzahlWahlkabinen(10).anzahlWahltische(12).anzahlNebenraeume(0)
                .urnenAnzahl(List.of(WahlurneTestdatenfactory.initValidDTO(wahlID).build()));
    }
}
