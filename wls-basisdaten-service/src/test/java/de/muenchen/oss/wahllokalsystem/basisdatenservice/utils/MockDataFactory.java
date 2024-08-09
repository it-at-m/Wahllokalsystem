package de.muenchen.oss.wahllokalsystem.basisdatenservice.utils;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Farbe;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Wahlart;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.BasisdatenDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.BasisstrukturdatenDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.StimmzettelgebietDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlbezirkDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.infomanagement.model.KonfigurierterWahltagDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.common.StimmzettelgebietsartModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.BasisdatenModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.BasisstrukturdatenModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.KonfigurierterWahltagModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.StimmzettelgebietModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.WahlbezirkModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlModel;
import java.time.LocalDate;
import java.util.Set;
import lombok.val;

public class MockDataFactory {

    public static BasisdatenModel createBasisdatenModel(LocalDate forDate) {
        return new BasisdatenModel(
                Set.of(new BasisstrukturdatenModel("wahlID1", "szgID", "wahlbezirkID1_1", forDate),
                        new BasisstrukturdatenModel("wahlID1", "szgID", "wahlbezirkID1_2", forDate),
                        new BasisstrukturdatenModel("wahlID1", "szgID", "wahlbezirkID2_1", forDate),
                        new BasisstrukturdatenModel("wahlID2", "szgID", "wahlbezirkID2_2", forDate),
                        new BasisstrukturdatenModel("wahlID2", "szgIDOther", "wahlbezirkID2_2", forDate)),
                Set.of(
                        new WahlModel("wahlID1", "Bundestagswahl", 1L, 1L, forDate, Wahlart.BTW, new Farbe(0, 1, 2), "0"),
                        new WahlModel("wahlID2", "Europawahl", 2L, 1L, forDate, Wahlart.EUW, new Farbe(3, 4, 5), "1")),
                Set.of(
                        new WahlbezirkModel("wahlbezirkID1_1", WahlbezirkArtModel.UWB, "1201", forDate, "0", "wahlID1"),
                        new WahlbezirkModel("wahlbezirkID1_2", WahlbezirkArtModel.BWB, "1251", forDate, "0", "wahlID1"),
                        new WahlbezirkModel("wahlbezirkID2_1", WahlbezirkArtModel.UWB, "1202", forDate, "0", "wahlID1"),
                        new WahlbezirkModel("wahlbezirkID2_2", WahlbezirkArtModel.BWB, "1252", forDate, "0", "wahlID1"),
                        new WahlbezirkModel("wahlbezirkID2_2", WahlbezirkArtModel.BWB, "1252", forDate, "1", "wahlID2")

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
        basisstrukturdaten5.setWahlbezirkID("wahlbezirkID2_2");
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
        wahlbezirk5.setIdentifikator("wahlbezirkID2_2");
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

    public static KonfigurierterWahltagDTO createClientKonfigurierterWahltagDTO(LocalDate forDate) {
        val konfigurierterWahltagDTO = new KonfigurierterWahltagDTO();
        konfigurierterWahltagDTO.setWahltag(forDate);
        konfigurierterWahltagDTO.setWahltagID("wahltagID1");
        konfigurierterWahltagDTO.setWahltagStatus(KonfigurierterWahltagDTO.WahltagStatusEnum.AKTIV);
        konfigurierterWahltagDTO.setNummer("1");

        return konfigurierterWahltagDTO;
    }

    public static KonfigurierterWahltagModel createClientKonfigurierterWahltagModel(LocalDate forDate) {
        return new KonfigurierterWahltagModel(forDate, "wahltagID1", true, "1");
    }
}
