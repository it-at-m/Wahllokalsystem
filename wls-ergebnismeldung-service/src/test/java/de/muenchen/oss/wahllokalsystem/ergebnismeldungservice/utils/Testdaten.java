package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.BezirkUndWahlIDUndWaehlerverzeichnisnummer;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.EingenommenerWahlschein;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Stimmzettelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke.EingenommenerWahlscheinDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke.StimmzettelDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke.StimmzettelartDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke.VermerkDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke.WahldatenDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.EingenommenerWahlscheinModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.StimmzettelModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.StimmzettelartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.VermerkModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.WahldatenModel;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;
import lombok.val;

public class Testdaten {

    public static class Wahldaten {

        public static WahldatenModel createModel(@NotNull String wahlbezirkID, @NotNull String wahlID, @NotNull Long waehlerverzeichnisnummer) {
            val vermerke = Set.of(
                    Vermerk.createModel(waehlerverzeichnisnummer * 10 + 1),
                    Vermerk.createModel(waehlerverzeichnisnummer * 10 + 2));
            val eingenommeneWahlscheine = Set.of(
                    EigenommenerWahlschein.createModel(waehlerverzeichnisnummer * 10 + 1),
                    EigenommenerWahlschein.createModel(waehlerverzeichnisnummer * 10 + 2));

            return new WahldatenModel(wahlbezirkID, wahlID, waehlerverzeichnisnummer, vermerke, eingenommeneWahlscheine);
        }

        public static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Wahldaten createEntity(@NotNull String wahlbezirkID,
                @NotNull String wahlID, @NotNull Long waehlerverzeichnisnummer) {
            val vermerke = Set.of(
                    Vermerk.createEntity(waehlerverzeichnisnummer * 10 + 1),
                    Vermerk.createEntity(waehlerverzeichnisnummer * 10 + 2));
            val eingenommeneWahlscheine = Set.of(
                    EigenommenerWahlschein.createEntity(waehlerverzeichnisnummer * 10 + 1),
                    EigenommenerWahlschein.createEntity(waehlerverzeichnisnummer * 10 + 2));

            return new de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Wahldaten(
                    UUID.randomUUID(), new BezirkUndWahlIDUndWaehlerverzeichnisnummer(wahlbezirkID, wahlID, waehlerverzeichnisnummer), vermerke,
                    eingenommeneWahlscheine);
        }

        public static WahldatenDTO createDTO(@NotNull String wahlbezirkID, @NotNull String wahlID, @NotNull Long waehlerverzeichnisnummer) {
            val vermerke = Set.of(
                    Vermerk.createDTO(waehlerverzeichnisnummer * 10 + 1),
                    Vermerk.createDTO(waehlerverzeichnisnummer * 10 + 2));
            val eingenommeneWahlscheine = Set.of(
                    EigenommenerWahlschein.createDTO(waehlerverzeichnisnummer * 10 + 1),
                    EigenommenerWahlschein.createDTO(waehlerverzeichnisnummer * 10 + 2));

            return new WahldatenDTO(wahlbezirkID, wahlID, waehlerverzeichnisnummer, vermerke, eingenommeneWahlscheine);
        }
    }

    public static class Vermerk {

        public static VermerkModel createModel(@NotNull long blattnummer) {
            val stimmzettel = Set.of(
                    Stimmzettel.createModel(blattnummer * 10 + 1),
                    Stimmzettel.createModel(blattnummer * 10 + 2));

            return new VermerkModel(blattnummer, stimmzettel);
        }

        public static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Vermerk createEntity(@NotNull long blattnummer) {
            val stimmzettel = Set.of(
                    Stimmzettel.createEntity(blattnummer * 10 + 1),
                    Stimmzettel.createEntity(blattnummer * 10 + 2));

            return new de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Vermerk(UUID.randomUUID(), null, blattnummer,
                    stimmzettel);
        }

        public static VermerkDTO createDTO(@NotNull long blattnummer) {
            val stimmzettel = Set.of(
                    Stimmzettel.createDTO(blattnummer * 10 + 1),
                    Stimmzettel.createDTO(blattnummer * 10 + 2));

            return new VermerkDTO(blattnummer, stimmzettel);
        }
    }

    public static class Stimmzettel {

        public static StimmzettelModel createModel(@NotNull long anzahl) {
            return new StimmzettelModel(anzahl, StimmzettelartModel.KLEIN);
        }

        public static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Stimmzettel createEntity(@NotNull long anzahl) {
            return new de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Stimmzettel(anzahl, Stimmzettelart.KLEIN);
        }

        public static StimmzettelDTO createDTO(@NotNull long anzahl) {
            return new StimmzettelDTO(anzahl, StimmzettelartDTO.KLEIN);
        }
    }

    public static class EigenommenerWahlschein {

        public static EingenommenerWahlscheinModel createModel(@NotNull long anzahl) {
            return new EingenommenerWahlscheinModel(anzahl, StimmzettelartModel.KLEIN);
        }

        public static EingenommenerWahlschein createEntity(@NotNull long anzahl) {
            return new EingenommenerWahlschein(anzahl, Stimmzettelart.KLEIN);
        }

        public static EingenommenerWahlscheinDTO createDTO(@NotNull long anzahl) {
            return new EingenommenerWahlscheinDTO(anzahl, StimmzettelartDTO.KLEIN);
        }
    }
}
