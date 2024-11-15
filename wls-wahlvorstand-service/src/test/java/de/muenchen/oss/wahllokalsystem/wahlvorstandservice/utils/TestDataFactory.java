package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.utils;

import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.domain.wahlvorstand.Funktion;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.domain.wahlvorstand.Wahlvorstand;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.domain.wahlvorstand.Wahlvorstandsmitglied;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.eai.infomanagement.model.KonfigurierterWahltagDTO;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.rest.wahlvorstand.FunktionDTO;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.rest.wahlvorstand.WahlvorstandDTO;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.rest.wahlvorstand.WahlvorstandsmitgliedDTO;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.FunktionModel;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.KonfigurierterWahltagModel;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.WahlvorstandModel;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.WahlvorstandsmitgliedModel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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

        public static WahlvorstandModel fromDto(WahlvorstandDTO wahlvorstandDto) {
            List<WahlvorstandsmitgliedModel> wahlvorstandsmitgliedModelList = wahlvorstandDto.wahlvorstandsmitglieder().stream()
                    .map(mitglied -> new WahlvorstandsmitgliedModel(
                            mitglied.identifikator(), mitglied.familienname(), mitglied.vorname(), MapFunktion.funktionDtoToFunktionModel(mitglied.funktion()),
                            mitglied.funktionsname(), mitglied.anwesend()))
                    .toList();

            return new WahlvorstandModel(wahlvorstandDto.wahlbezirkID(), wahlvorstandDto.anwesenheitBeginn(), wahlvorstandsmitgliedModelList);
        }

        public static WahlvorstandModel fallback(String wahlbezirkID) {
            WahlvorstandModel fallbackWahlvorstand = WahlvorstandModel.builder().wahlbezirkID(wahlbezirkID).wahlvorstandsmitglieder(new ArrayList<>()).build();
            Arrays.stream(FunktionModel.values()).forEach(funktion -> {
                WahlvorstandsmitgliedModel mitglied = WahlvorstandsmitgliedModel.builder()
                        .identifikator("FALLBACK_" + funktion + wahlbezirkID)
                        .funktion(funktion)
                        .familienname("______________")
                        .vorname("______________")
                        .build();
                fallbackWahlvorstand.wahlvorstandsmitglieder().add(mitglied);
            });
            return fallbackWahlvorstand;
        }
    }

    public static class CreateWahlvorstandsmitgliedModel {

        public static WahlvorstandsmitgliedModel withData() {
            return new WahlvorstandsmitgliedModel("id", "familienname", "vorname", FunktionModel.B, "funktionsname", true);
        }
    }

    public static class CreateWahlvorstandDto {

        public static WahlvorstandDTO withData() {
            List<WahlvorstandsmitgliedDTO> wahlvorstandsmitgliedDtoList = new ArrayList<>();
            wahlvorstandsmitgliedDtoList.add(CreateWahlvorstandsmitgliedDto.withData());

            return new WahlvorstandDTO("wahlbezirkID", LocalDateTime.now().withNano(0), wahlvorstandsmitgliedDtoList);
        }

        public static WahlvorstandDTO withWahlbezirkID(String wahlbezirkID) {
            List<WahlvorstandsmitgliedDTO> wahlvorstandsmitgliedDtoList = new ArrayList<>();
            wahlvorstandsmitgliedDtoList.add(CreateWahlvorstandsmitgliedDto.withData());

            return new WahlvorstandDTO(wahlbezirkID, LocalDateTime.now().withNano(0), wahlvorstandsmitgliedDtoList);
        }

        public static WahlvorstandDTO fromModel(WahlvorstandModel model) {
            List<WahlvorstandsmitgliedDTO> wahlvorstandsmitgliedDtoList = model.wahlvorstandsmitglieder().stream().map(mitglied -> new WahlvorstandsmitgliedDTO(
                    mitglied.identifikator(), mitglied.familienname(), mitglied.vorname(), MapFunktion.funktionModelToFunktionDto(mitglied.funktion()),
                    mitglied.funktionsname(), mitglied.anwesend()))
                    .toList();
            return new WahlvorstandDTO(model.wahlbezirkID(), model.anwesenheitBeginn(), wahlvorstandsmitgliedDtoList);
        }

        public static WahlvorstandDTO fallback(String wahlbezirkID) {
            WahlvorstandDTO fallbackWahlvorstand = WahlvorstandDTO.builder().wahlbezirkID(wahlbezirkID).wahlvorstandsmitglieder(new ArrayList<>()).build();
            Arrays.stream(FunktionDTO.values()).forEach(funktion -> {
                WahlvorstandsmitgliedDTO mitglied = WahlvorstandsmitgliedDTO.builder()
                        .identifikator("FALLBACK_" + funktion + wahlbezirkID)
                        .funktion(funktion)
                        .familienname("______________")
                        .vorname("______________")
                        .build();
                fallbackWahlvorstand.wahlvorstandsmitglieder().add(mitglied);
            });
            return fallbackWahlvorstand;
        }
    }

    public static class CreateWahlvorstandsmitgliedDto {

        public static WahlvorstandsmitgliedDTO withData() {
            return new WahlvorstandsmitgliedDTO("id", "familienname", "vorname", FunktionDTO.SSB, "funktionsname", true);
        }
    }

    public static class CreateFromClient {

        public static KonfigurierterWahltagModel konfigurierterWahltagModel() {
            return new KonfigurierterWahltagModel(LocalDate.now(), "wahltagNummer");
        }

        public static KonfigurierterWahltagDTO konfigurierterWahltagDTO(LocalDate forDate, KonfigurierterWahltagDTO.WahltagStatusEnum status) {
            KonfigurierterWahltagDTO konfigurierterWahltagDTO = new KonfigurierterWahltagDTO();
            konfigurierterWahltagDTO.setWahltag(forDate);
            konfigurierterWahltagDTO.setWahltagID("wahltagID1");
            konfigurierterWahltagDTO.setWahltagStatus(status);
            konfigurierterWahltagDTO.setNummer("nummerWahltag");

            return konfigurierterWahltagDTO;
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

        public static FunktionDTO funktionModelToFunktionDto(FunktionModel funktionModel) {
            return switch (funktionModel) {
            case W -> FunktionDTO.W;
            case SB -> FunktionDTO.SB;
            case SWB -> FunktionDTO.SWB;
            case SSB -> FunktionDTO.SSB;
            case B -> FunktionDTO.B;
            };
        }

        public static FunktionModel funktionDtoToFunktionModel(FunktionDTO funktionDto) {
            return switch (funktionDto) {
            case W -> FunktionModel.W;
            case SB -> FunktionModel.SB;
            case SWB -> FunktionModel.SWB;
            case SSB -> FunktionModel.SSB;
            case B -> FunktionModel.B;
            };
        }
    }
}
