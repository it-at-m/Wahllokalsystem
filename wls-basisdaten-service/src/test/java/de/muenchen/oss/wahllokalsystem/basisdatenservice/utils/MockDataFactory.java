package de.muenchen.oss.wahllokalsystem.basisdatenservice.utils;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlbezirke.Wahlbezirk;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.common.WahlbezirkArt;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahltag.Wahltag;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.kopfdaten.Kopfdaten;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.kopfdaten.Stimmzettelgebietsart;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlen.Farbe;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlen.Wahl;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlen.Wahlart;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.BasisdatenDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.BasisstrukturdatenDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.StimmzettelgebietDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlbezirkDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.infomanagement.model.KonfigurierterWahltagDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.common.WahlbezirkArtDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.kopfdaten.KopfdatenDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.kopfdaten.StimmzettelgebietsartDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.common.StimmzettelgebietsartModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.BasisdatenModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.BasisstrukturdatenModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.KonfigurierterWahltagModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.KopfdatenModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.StimmzettelgebietModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlbezirke.WahlbezirkModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlModel;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.val;

public class MockDataFactory {

    public static KopfdatenDTO createControllerKopfdatenDTO(String wahlID, String wahlbezirkID, StimmzettelgebietsartDTO szgaDTO, String szGebietsNummer,
            String szGebietsName, String wahlname, String wahlbezirkNummer) {
        return new KopfdatenDTO(
                wahlID,
                wahlbezirkID,
                "LHM",
                szgaDTO,
                szGebietsNummer,
                szGebietsName,
                wahlname,
                wahlbezirkNummer);
    }

    public static KopfdatenModel createKopfdatenModelFor(String wahlID, String wahlbezirkID) {
        return createKopfdatenModelFor(wahlID, wahlbezirkID, null, null, null, null, null);
    }

    public static KopfdatenModel createKopfdatenModelFor(String wahlID, String wahlbezirkID, StimmzettelgebietsartModel szga, String szGebietsNummer,
            String szGebietsName, String wahlname, String wahlbezirkNummer) {
        return new KopfdatenModel(
                new BezirkUndWahlID(wahlID, wahlbezirkID),
                "LHM",
                szga,
                szGebietsNummer,
                szGebietsName,
                wahlname,
                wahlbezirkNummer);
    }

    public static Kopfdaten createKopfdatenEntityFor(String wahlID, String wahlbezirkID) {
        return createKopfdatenEntityFor(wahlID, wahlbezirkID, null, null, null, null, null);
    }

    public static Kopfdaten createKopfdatenEntityFor(String wahlID, String wahlbezirkID, Stimmzettelgebietsart szga, String szGebietsName,
            String szGebietsNummer, String wahlname, String wahlbezirkNummer) {
        Kopfdaten kopfdaten = new Kopfdaten();
        kopfdaten.setBezirkUndWahlID(new BezirkUndWahlID(wahlID, wahlbezirkID));
        kopfdaten.setGemeinde("LHM");
        kopfdaten.setStimmzettelgebietsart(szga);
        kopfdaten.setStimmzettelgebietsname(szGebietsName);
        kopfdaten.setStimmzettelgebietsnummer(szGebietsNummer);
        kopfdaten.setWahlbezirknummer(wahlbezirkNummer);
        kopfdaten.setWahlname(wahlname);
        return kopfdaten;
    }

    public static BasisdatenModel createBasisdatenModel(LocalDate forDate) {
        return new BasisdatenModel(
                Set.of(new BasisstrukturdatenModel("wahlID1", "szgID", "wahlbezirkID1_1", forDate),
                        new BasisstrukturdatenModel("wahlID1", "szgID", "wahlbezirkID1_2", forDate),
                        new BasisstrukturdatenModel("wahlID1", "szgID", "wahlbezirkID2_1", forDate),
                        new BasisstrukturdatenModel("wahlID2", "szgID", "wahlbezirkID2_2", forDate),
                        new BasisstrukturdatenModel("wahlID2", "szgIDOther", "wahlbezirkID2_1", forDate)),
                Set.of(
                        new WahlModel("wahlID1", "Bundestagswahl", 1L, 1L, forDate, Wahlart.BTW, new Farbe(0, 1, 2), "0"),
                        new WahlModel("wahlID2", "Europawahl", 2L, 1L, forDate, Wahlart.EUW, new Farbe(3, 4, 5), "1")),
                Set.of(
                        new WahlbezirkModel("wahlbezirkID1_1", WahlbezirkArtModel.UWB, "1201", forDate, "0", "wahlID1"),
                        new WahlbezirkModel("wahlbezirkID1_2", WahlbezirkArtModel.BWB, "1251", forDate, "0", "wahlID1"),
                        new WahlbezirkModel("wahlbezirkID2_1", WahlbezirkArtModel.UWB, "1202", forDate, "0", "wahlID1"),
                        new WahlbezirkModel("wahlbezirkID2_2", WahlbezirkArtModel.BWB, "1252", forDate, "0", "wahlID1"),
                        new WahlbezirkModel("wahlbezirkID2_1", WahlbezirkArtModel.BWB, "1252", forDate, "1", "wahlID2")

                ),
                Set.of(
                        new StimmzettelgebietModel("szgID", "120", "Munich", forDate, StimmzettelgebietsartModel.SG),
                        new StimmzettelgebietModel("szgIDOther", "920", "Munich Center", forDate, StimmzettelgebietsartModel.SB)));

    }

    public static BasisdatenDTO createClientBasisdatenDTO(LocalDate aDate) {
        BasisdatenDTO basisdatenDTO = new BasisdatenDTO();

        val basisstrukturdaten1 = new BasisstrukturdatenDTO();
        basisstrukturdaten1.setWahlID("wahlID1");
        basisstrukturdaten1.setStimmzettelgebietID("szgID");
        basisstrukturdaten1.setWahlbezirkID("wahlbezirkID1_1");
        basisstrukturdaten1.setWahltag(aDate);

        val basisstrukturdaten2 = new BasisstrukturdatenDTO();
        basisstrukturdaten2.setWahlID("wahlID1");
        basisstrukturdaten2.setStimmzettelgebietID("szgID");
        basisstrukturdaten2.setWahlbezirkID("wahlbezirkID1_2");
        basisstrukturdaten2.setWahltag(aDate);

        val basisstrukturdaten3 = new BasisstrukturdatenDTO();
        basisstrukturdaten3.setWahlID("wahlID1");
        basisstrukturdaten3.setStimmzettelgebietID("szgID");
        basisstrukturdaten3.setWahlbezirkID("wahlbezirkID2_1");
        basisstrukturdaten3.setWahltag(aDate);

        val basisstrukturdaten4 = new BasisstrukturdatenDTO();
        basisstrukturdaten4.setWahlID("wahlID2");
        basisstrukturdaten4.setStimmzettelgebietID("szgID");
        basisstrukturdaten4.setWahlbezirkID("wahlbezirkID2_2");
        basisstrukturdaten4.setWahltag(aDate);

        val basisstrukturdaten5 = new BasisstrukturdatenDTO();
        basisstrukturdaten5.setWahlID("wahlID2");
        basisstrukturdaten5.setStimmzettelgebietID("szgIDOther");
        basisstrukturdaten5.setWahlbezirkID("wahlbezirkID2_1");
        basisstrukturdaten5.setWahltag(aDate);

        val wahl1 = new WahlDTO();
        wahl1.setIdentifikator("wahlID1");
        wahl1.setNummer("0");
        wahl1.setName("Bundestagswahl");
        wahl1.setWahlart(WahlDTO.WahlartEnum.BTW);
        wahl1.setWahltag(aDate);

        val wahl2 = new WahlDTO();
        wahl2.setIdentifikator("wahlID2");
        wahl2.setNummer("1");
        wahl2.setName("Europawahl");
        wahl2.setWahlart(WahlDTO.WahlartEnum.EUW);
        wahl2.setWahltag(aDate);

        val wahlbezirk1 = new WahlbezirkDTO();
        wahlbezirk1.setIdentifikator("wahlbezirkID1_1");
        wahlbezirk1.setWahlbezirkArt(WahlbezirkDTO.WahlbezirkArtEnum.UWB);
        wahlbezirk1.setNummer("1201");
        wahlbezirk1.setWahltag(aDate);
        wahlbezirk1.setWahlnummer("0");
        wahlbezirk1.setWahlID("wahlID1");

        val wahlbezirk2 = new WahlbezirkDTO();
        wahlbezirk2.setIdentifikator("wahlbezirkID1_2");
        wahlbezirk2.setWahlbezirkArt(WahlbezirkDTO.WahlbezirkArtEnum.BWB);
        wahlbezirk2.setNummer("1251");
        wahlbezirk2.setWahltag(aDate);
        wahlbezirk2.setWahlnummer("0");
        wahlbezirk2.setWahlID("wahlID1");

        val wahlbezirk3 = new WahlbezirkDTO();
        wahlbezirk3.setIdentifikator("wahlbezirkID2_1");
        wahlbezirk3.setWahlbezirkArt(WahlbezirkDTO.WahlbezirkArtEnum.UWB);
        wahlbezirk3.setNummer("1202");
        wahlbezirk3.setWahltag(aDate);
        wahlbezirk3.setWahlnummer("0");
        wahlbezirk3.setWahlID("wahlID1");

        val wahlbezirk4 = new WahlbezirkDTO();
        wahlbezirk4.setIdentifikator("wahlbezirkID2_2");
        wahlbezirk4.setWahlbezirkArt(WahlbezirkDTO.WahlbezirkArtEnum.BWB);
        wahlbezirk4.setNummer("1252");
        wahlbezirk4.setWahltag(aDate);
        wahlbezirk4.setWahlnummer("0");
        wahlbezirk4.setWahlID("wahlID1");

        val wahlbezirk5 = new WahlbezirkDTO();
        wahlbezirk5.setIdentifikator("wahlbezirkID2_1");
        wahlbezirk5.setWahlbezirkArt(WahlbezirkDTO.WahlbezirkArtEnum.BWB);
        wahlbezirk5.setNummer("1252");
        wahlbezirk5.setWahltag(aDate);
        wahlbezirk5.setWahlnummer("1");
        wahlbezirk5.setWahlID("wahlID2");

        val stimmzettelgebiet1 = new StimmzettelgebietDTO();
        stimmzettelgebiet1.setIdentifikator("szgID");
        stimmzettelgebiet1.setNummer("120");
        stimmzettelgebiet1.setName("Munich");
        stimmzettelgebiet1.setWahltag(aDate);
        stimmzettelgebiet1.setStimmzettelgebietsart(StimmzettelgebietDTO.StimmzettelgebietsartEnum.SG);

        val stimmzettelgebiet2 = new StimmzettelgebietDTO();
        stimmzettelgebiet2.setIdentifikator("szgIDOther");
        stimmzettelgebiet2.setNummer("920");
        stimmzettelgebiet2.setName("Munich Center");
        stimmzettelgebiet2.setWahltag(aDate);
        stimmzettelgebiet2.setStimmzettelgebietsart(StimmzettelgebietDTO.StimmzettelgebietsartEnum.SB);

        basisdatenDTO.setBasisstrukturdaten(Set.of(basisstrukturdaten1, basisstrukturdaten2, basisstrukturdaten3, basisstrukturdaten4, basisstrukturdaten5));
        basisdatenDTO.setWahlen(Set.of(wahl1, wahl2));
        basisdatenDTO.setWahlbezirke(Set.of(wahlbezirk1, wahlbezirk2, wahlbezirk3, wahlbezirk4, wahlbezirk5));
        basisdatenDTO.setStimmzettelgebiete(Set.of(stimmzettelgebiet1, stimmzettelgebiet2));

        return basisdatenDTO;
    }

    public static KonfigurierterWahltagDTO createClientKonfigurierterWahltagDTO(LocalDate forDate, KonfigurierterWahltagDTO.WahltagStatusEnum status) {
        val konfigurierterWahltagDTO = new KonfigurierterWahltagDTO();
        konfigurierterWahltagDTO.setWahltag(forDate);
        konfigurierterWahltagDTO.setWahltagID("wahltagID1");
        konfigurierterWahltagDTO.setWahltagStatus(status);
        konfigurierterWahltagDTO.setNummer("nummerWahltag");

        return konfigurierterWahltagDTO;
    }

    public static KonfigurierterWahltagModel createClientKonfigurierterWahltagModel(LocalDate forDate, Boolean isWahltagActive) {
        return new KonfigurierterWahltagModel(forDate, "wahltagID1", isWahltagActive, "nummerWahltag");
    }

    public static List<Wahltag> createWahltagList(String wahltagIDPrefix) {
        val wahltag1 = new Wahltag(wahltagIDPrefix + "_identifikatorWahltag1", LocalDate.now().minusMonths(2), "beschreibungWahltag1", "nummerWahltag1");
        val wahltag2 = new Wahltag(wahltagIDPrefix + "_identifikatorWahltag2", LocalDate.now().minusMonths(1), "beschreibungWahltag2", "nummerWahltag2");
        val wahltag3 = new Wahltag(wahltagIDPrefix + "_identifikatorWahltag3", LocalDate.now().plusMonths(1), "beschreibungWahltag3", "nummerWahltag3");

        return List.of(wahltag1, wahltag2, wahltag3);
    }

    public static List<Wahl> createWahlEntityList() {
        val wahl1 = new Wahl();
        wahl1.setWahlID("wahlID1");
        wahl1.setName("wahl1");
        wahl1.setNummer("0");
        wahl1.setFarbe(new Farbe(1, 1, 1));
        wahl1.setWahlart(Wahlart.BAW);
        wahl1.setReihenfolge(1);
        wahl1.setWaehlerverzeichnisnummer(1);
        wahl1.setWahltag(LocalDate.now().plusMonths(1));

        val wahl2 = new Wahl();
        wahl2.setWahlID("wahlID2");
        wahl2.setName("wahl2");
        wahl2.setNummer("1");
        wahl2.setFarbe(new Farbe(2, 2, 2));
        wahl2.setWahlart(Wahlart.LTW);
        wahl2.setReihenfolge(2);
        wahl2.setWaehlerverzeichnisnummer(2);
        wahl2.setWahltag(LocalDate.now().plusMonths(1));

        val wahl3 = new Wahl();
        wahl3.setWahlID("wahlID3");
        wahl3.setName("wahl3");
        wahl3.setNummer("2");
        wahl3.setFarbe(new Farbe(3, 3, 3));
        wahl3.setWahlart(Wahlart.EUW);
        wahl3.setReihenfolge(3);
        wahl3.setWaehlerverzeichnisnummer(3);
        wahl3.setWahltag(LocalDate.now().plusMonths(3));

        return List.of(wahl1, wahl2, wahl3);
    }

    public static List<WahlModel> createWahlModelList(final String praefix, final LocalDate forDate) {
        return List.of(
                new WahlModel(praefix + "wahlID1", "Bundestagswahl", 1L, 1L, forDate, Wahlart.BTW, new Farbe(0, 1, 2), "0"),
                new WahlModel(praefix + "wahlID2", "Europawahl", 2L, 1L, forDate, Wahlart.EUW, new Farbe(3, 4, 5), "1"));
    }

    public static Set<WahlbezirkDTO> createSetOfClientWahlbezirkDTO(LocalDate aDate) {
        val wahlbezirk1 = new WahlbezirkDTO();
        wahlbezirk1.setIdentifikator("wahlbezirkID1_1");
        wahlbezirk1.setWahlbezirkArt(WahlbezirkDTO.WahlbezirkArtEnum.UWB);
        wahlbezirk1.setNummer("1201");
        wahlbezirk1.setWahltag(aDate);
        wahlbezirk1.setWahlnummer("0");
        wahlbezirk1.setWahlID("wahlID1");

        val wahlbezirk2 = new WahlbezirkDTO();
        wahlbezirk2.setIdentifikator("wahlbezirkID1_2");
        wahlbezirk2.setWahlbezirkArt(WahlbezirkDTO.WahlbezirkArtEnum.BWB);
        wahlbezirk2.setNummer("1251");
        wahlbezirk2.setWahltag(aDate);
        wahlbezirk2.setWahlnummer("0");
        wahlbezirk2.setWahlID("wahlID1");

        val wahlbezirk3 = new WahlbezirkDTO();
        wahlbezirk3.setIdentifikator("wahlbezirkID2_2");
        wahlbezirk3.setWahlbezirkArt(WahlbezirkDTO.WahlbezirkArtEnum.BWB);
        wahlbezirk3.setNummer("1252");
        wahlbezirk3.setWahltag(aDate);
        wahlbezirk3.setWahlnummer("0");
        wahlbezirk3.setWahlID("wahlID1");

        val wahlbezirk4 = new WahlbezirkDTO();
        wahlbezirk4.setIdentifikator("wahlbezirkID2_1");
        wahlbezirk4.setWahlbezirkArt(WahlbezirkDTO.WahlbezirkArtEnum.BWB);
        wahlbezirk4.setNummer("1252");
        wahlbezirk4.setWahltag(aDate);
        wahlbezirk4.setWahlnummer("1");
        wahlbezirk4.setWahlID("wahlID2");

        return Set.of(wahlbezirk1, wahlbezirk2, wahlbezirk3, wahlbezirk4);
    }

    public static Wahlbezirk createWahlbezirkEntity(final LocalDate wahltagDate) {
        return new Wahlbezirk(UUID.randomUUID().toString(), wahltagDate, "nummer", WahlbezirkArt.UWB, "nummer", "wahlID");
    }

    public static List<Wahlbezirk> createListOfWahlbezirkEntity(String praefix, LocalDate aDate) {
        val wahlbezirk1 = new Wahlbezirk();
        wahlbezirk1.setWahlbezirkID(praefix + "wahlbezirkID1_1");
        wahlbezirk1.setWahlbezirkart(WahlbezirkArt.UWB);
        wahlbezirk1.setNummer("1201");
        wahlbezirk1.setWahltag(aDate);
        wahlbezirk1.setWahlnummer("0");
        wahlbezirk1.setWahlID("wahlID1");

        val wahlbezirk2 = new Wahlbezirk();
        wahlbezirk2.setWahlbezirkID(praefix + "wahlbezirkID1_2");
        wahlbezirk2.setWahlbezirkart(WahlbezirkArt.BWB);
        wahlbezirk2.setNummer("1251");
        wahlbezirk2.setWahltag(aDate);
        wahlbezirk2.setWahlnummer("0");
        wahlbezirk2.setWahlID("wahlID1");

        val wahlbezirk3 = new Wahlbezirk();
        wahlbezirk3.setWahlbezirkID(praefix + "wahlbezirkID2_1");
        wahlbezirk3.setWahlbezirkart(WahlbezirkArt.UWB);
        wahlbezirk3.setNummer("1202");
        wahlbezirk3.setWahltag(aDate);
        wahlbezirk3.setWahlnummer("0");
        wahlbezirk3.setWahlID("wahlID1");

        val wahlbezirk4 = new Wahlbezirk();
        wahlbezirk4.setWahlbezirkID(praefix + "wahlbezirkID2_2");
        wahlbezirk4.setWahlbezirkart(WahlbezirkArt.BWB);
        wahlbezirk4.setNummer("1252");
        wahlbezirk4.setWahltag(aDate);
        wahlbezirk4.setWahlnummer("0");
        wahlbezirk4.setWahlID("wahlID1");

        return List.of(wahlbezirk1, wahlbezirk2, wahlbezirk3, wahlbezirk4);
    }

    public static List<WahlbezirkModel> createListOfWahlbezirkModel(String praefix, LocalDate forDate) {
        return List.of(
                new WahlbezirkModel(praefix + "wahlbezirkID1_1", WahlbezirkArtModel.UWB, "1201", forDate, "0", "wahlID1"),
                new WahlbezirkModel(praefix + "wahlbezirkID1_2", WahlbezirkArtModel.BWB, "1251", forDate, "0", "wahlID1"),
                new WahlbezirkModel(praefix + "wahlbezirkID2_1", WahlbezirkArtModel.UWB, "1202", forDate, "0", "wahlID1"),
                new WahlbezirkModel(praefix + "wahlbezirkID2_2", WahlbezirkArtModel.BWB, "1252", forDate, "0", "wahlID1"));
    }

    public static Set<WahlbezirkModel> createSetOfWahlbezirkModel(String praefix, LocalDate forDate) {
        return Set.of(
                new WahlbezirkModel(praefix + "wahlbezirkID1_1", WahlbezirkArtModel.UWB, "1201", forDate, "0", "wahlID1"),
                new WahlbezirkModel(praefix + "wahlbezirkID1_2", WahlbezirkArtModel.BWB, "1251", forDate, "0", "wahlID1"),
                new WahlbezirkModel(praefix + "wahlbezirkID2_1", WahlbezirkArtModel.UWB, "1202", forDate, "0", "wahlID1"),
                new WahlbezirkModel(praefix + "wahlbezirkID2_2", WahlbezirkArtModel.BWB, "1252", forDate, "0", "wahlID1"));
    }

    public static List<de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlbezirke.WahlbezirkDTO> createWlsWahlbezirkDTOs(final String praefix,
            final LocalDate forDate) {
        val wahlbezirk1 = new de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlbezirke.WahlbezirkDTO(
                praefix + "wahlbezirkID1_1",
                forDate,
                "1201",
                WahlbezirkArtDTO.UWB,
                "0",
                "wahlID1");
        val wahlbezirk2 = new de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlbezirke.WahlbezirkDTO(
                praefix + "wahlbezirkID1_2",
                forDate,
                "1251",
                WahlbezirkArtDTO.BWB,
                "0",
                "wahlID1");
        val wahlbezirk3 = new de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlbezirke.WahlbezirkDTO(
                praefix + "wahlbezirkID2_1",
                forDate,
                "1202",
                WahlbezirkArtDTO.UWB,
                "0",
                "wahlID1");
        val wahlbezirk4 = new de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlbezirke.WahlbezirkDTO(
                praefix + "wahlbezirkID2_2",
                forDate,
                "1252",
                WahlbezirkArtDTO.BWB,
                "0",
                "wahlID1");

        return List.of(wahlbezirk1, wahlbezirk2, wahlbezirk3, wahlbezirk4);
    }
}
