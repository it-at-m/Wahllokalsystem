package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.utils;

import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.domain.wahlvorstand.Funktion;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.domain.wahlvorstand.Wahlvorstand;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.domain.wahlvorstand.Wahlvorstandsmitglied;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.FunktionModel;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.WahlvorstandsmitgliedModel;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.aoueaiClient.WahlvorstandModel;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.infomanagementClient.KonfigurierterWahltagModel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestDataFactory {

    public static class CreateWahlvorstandEntity {

        public static Wahlvorstand withData() {
            List<Wahlvorstandsmitglied> wahlvorstandsmitgliedList = new ArrayList<>();
            wahlvorstandsmitgliedList.add(CreateWahlvorstandsmitgliedEntity.withData());
            return new Wahlvorstand("wahlbezirkID", LocalDateTime.now().withNano(0), wahlvorstandsmitgliedList);
        }

        public static Wahlvorstand fromModel(WahlvorstandModel model) {
            return new Wahlvorstand(model.wahlbezirkID(), model.anwesenheitBeginn(),
                    MapWahlvorstandsmitglied.wahlvorstandsmitgliedModelToWahlvorstandsmitglied(model.wahlvorstandsmitglieder()));
        }
    }

    public static class CreateWahlvorstandsmitgliedEntity {

        public static Wahlvorstandsmitglied withData() {
            return new Wahlvorstandsmitglied("id", "familienname", "vorname", Funktion.SB, "funktionsname", true);
        }

        public static Wahlvorstandsmitglied fromModel(WahlvorstandsmitgliedModel model) {
            return new Wahlvorstandsmitglied(model.identifikator(), model.familienname(), model.vorname(),
                    MapFunktion.funktionModelToFunktion(model.funktion()), model.funktionsname(), model.anwesend());
        }
    }

    public static class CreateWahlvorstandModel {

        public static WahlvorstandModel fromEntity(Wahlvorstand wahlvorstand) {
            return new WahlvorstandModel(wahlvorstand.getWahlbezirkID(), wahlvorstand.getAnwesenheitBeginn(),
                    MapWahlvorstandsmitglied.wahlvorstandsmitgliedToWahlvorstandsmitgliedModel(wahlvorstand.getWahlvorstandsmitglieder()));
        }

        public static WahlvorstandModel withData() {
            List<WahlvorstandsmitgliedModel> wahlvorstandsmitgliedModelList = new ArrayList<>();
            wahlvorstandsmitgliedModelList.add(CreateWahlvorstandsmitgliedModel.withData());

            return new WahlvorstandModel("wahlbezirkID", LocalDateTime.now().withNano(0), wahlvorstandsmitgliedModelList);
        }
    }

    public static class CreateWahlvorstandsmitgliedModel {

        public static WahlvorstandsmitgliedModel withData() {
            return new WahlvorstandsmitgliedModel("id", "familienname", "vorname", FunktionModel.B, "funktionsname", true);
        }
    }

    public static class CreateFromClient {

        public static KonfigurierterWahltagModel konfigurierterWahltagModel() {
            return new KonfigurierterWahltagModel(LocalDate.now(), "wahltagID", true, "wahltagNummer");
        }

        public static WahlvorstandModel wahlvorstandModel(String wahlbezirkID) {
            List<WahlvorstandsmitgliedModel> wahlvorstandsmitgliedModelList = new ArrayList<>();
            wahlvorstandsmitgliedModelList.add(CreateWahlvorstandsmitgliedModel.withData());

            return new WahlvorstandModel(wahlbezirkID, LocalDateTime.now().withNano(0), wahlvorstandsmitgliedModelList);
        }
    }

    public static class MapWahlvorstandsmitglied {
        public static List<WahlvorstandsmitgliedModel> wahlvorstandsmitgliedToWahlvorstandsmitgliedModel(List<Wahlvorstandsmitglied> mitglieder) {
            return mitglieder.stream()
                    .map(m -> new WahlvorstandsmitgliedModel(m.getIdentifikator(), m.getFamilienname(), m.getVorname(),
                            MapFunktion.funktionToFunktionModel(m.getFunktion()), m.getFunktionsname(), m.isAnwesend()))
                    .toList();
        }

        public static List<Wahlvorstandsmitglied> wahlvorstandsmitgliedModelToWahlvorstandsmitglied(List<WahlvorstandsmitgliedModel> mitglieder) {
            return mitglieder.stream()
                    .map(m -> new Wahlvorstandsmitglied(m.identifikator(), m.familienname(), m.vorname(), MapFunktion.funktionModelToFunktion(m.funktion()),
                            m.funktionsname(), m.anwesend()))
                    .toList();
        }
    }

    public static class MapFunktion {
        public static Funktion funktionModelToFunktion(FunktionModel funktionModel) {
            return switch (funktionModel) {
            case W -> Funktion.W;
            case SB -> Funktion.SB;
            case SWB -> Funktion.SWB;
            case SSB -> Funktion.SSB;
            case B -> Funktion.B;

            };
        }

        public static FunktionModel funktionToFunktionModel(Funktion funktion) {
            return switch (funktion) {
            case W -> FunktionModel.W;
            case SB -> FunktionModel.SB;
            case SWB -> FunktionModel.SWB;
            case SSB -> FunktionModel.SSB;
            case B -> FunktionModel.B;
            };
        }
    }
}
