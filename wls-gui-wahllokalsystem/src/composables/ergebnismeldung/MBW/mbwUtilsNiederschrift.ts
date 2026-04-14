import type { AWerte } from "@/types/ergebnismeldung/common/AWerte.ts";
import type { MeldungsartEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";
import type { Status } from "@/types/ergebnismeldung/common/Status.ts";
import type { NiederschriftBeanstandeteWahlbriefe } from "@/types/ergebnismeldung/MBW/niederschrift/NiederschriftBeanstandeteWahlbriefe";
import type { NiederschriftDruckInputBase } from "@/types/ergebnismeldung/MBW/niederschrift/NiederschriftDruckInputBase.ts";
import type {
  NiederschriftDruckInputBWB,
  NiederschriftUhrzeit,
} from "@/types/ergebnismeldung/MBW/niederschrift/NiederschriftDruckInputBWB.ts";
import type { NiederschriftDruckInputUWB } from "@/types/ergebnismeldung/MBW/niederschrift/NiederschriftDruckInputUWB.ts";
import type { NiederschriftEreignisse } from "@/types/ergebnismeldung/MBW/niederschrift/NiederschriftEreignisse.ts";
import type { NiederschriftGueltigeStimme } from "@/types/ergebnismeldung/MBW/niederschrift/NiederschriftGueltigeStimme";
import type { NiederschriftGueltigeStimmenErgebnisGesamt } from "@/types/ergebnismeldung/MBW/niederschrift/NiederschriftGueltigeStimmenErgebnisGesamt";
import type { NiederschriftWahlbriefdaten } from "@/types/ergebnismeldung/MBW/niederschrift/NiederschriftWahlbriefdaten";
import type { NiederschriftWahlvorstandsmitglied } from "@/types/ergebnismeldung/MBW/niederschrift/NiederschriftWahlvorstandsmitglied";
import type { Wahl } from "@/types/wahl/Wahl.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import JsBarcode from "jsbarcode";
import { storeToRefs } from "pinia";
import { ref } from "vue";

import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useLogging } from "@/composables/common/logging.ts";
import { useAWerteService } from "@/composables/ergebnismeldung/common/aWerteService.ts";
import { useErgebnisService } from "@/composables/ergebnismeldung/common/ergebnisService.ts";
import { useWahlscheineService } from "@/composables/ergebnismeldung/common/wahlscheineService.ts";
import { useMbwUtils } from "@/composables/ergebnismeldung/MBW/mbwUtils.ts";
import { useStimmabgabevermerkeService } from "@/composables/stimmabgabevermerke/stimmabgabevermerkeService.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { useWaehlerverzeichnisService } from "@/composables/wahlhandlung/waehlerverzeichnisService.ts";
import { useWahlvorbereitungService } from "@/composables/wahlhandlung/wahlvorbereitungService.ts";
import { useWahlvorstandService } from "@/composables/wahlvorstand/wahlvorstandService.ts";
import { useEreignisStore } from "@/stores/ereignisStore.ts";
import { useErgebnismeldungStore } from "@/stores/ergebnismeldungStore.ts";
import { useStimmabgabevermerkeStore } from "@/stores/stimmabgabevermerkeStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { useWahlvorschlaegeStore } from "@/stores/wahlvorschlaegeStore.ts";
import { ZurueckweisungsgrundEnum } from "@/types/briefwahl/ZurueckweisungsgrundEnum.ts";
import { MeldungsArtEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";
import { Parteei } from "@/types/ergebnismeldung/MBW/niederschrift/NiederschriftDruckInputBWB.ts";
import { EingenommenerWahlscheinStimmzettelartEnum } from "@/types/stimmabgabevermerke/EingenommenerWahlscheinStimmzettelartEnum.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const { logError } = useLogging("requestStrategies");
const { toGermanDate, toHhMm } = useDateTimeFormatter();
const { addNotification } = useUserNotificationService();
const { getErgebnisse } = useErgebnisService();

export function useMbtUtilsNiederschrift(wahlID: string, wahlbezirkID: string) {
  const { getAWerte } = useAWerteService();
  const { getUrnenwahlvorbereitung } = useWahlvorbereitungService();
  const { getStimmabgabevermerke } = useStimmabgabevermerkeService();
  const { getBegruendungStimmzettelumschlaege } = useErgebnisService();
  const { getWaehlerverzeichnis } = useWaehlerverzeichnisService();
  const { getWahlvorstand } = useWahlvorstandService();
  const { getWahlscheine } = useWahlscheineService();

  const { getErgebnisseByWahlIdAndStapelartOrUndefined } =
    useErgebnismeldungStore();
  const { getWahlvorschlaegeByWahlIDAndWahlbezirkID } =
    useWahlvorschlaegeStore();

  const { wahlenActions, waehlerverzeichnisActions } = useWahlenStore();
  const { currentUserWahlbezirkNummer, currentUserWahlbezirksArt } =
    storeToRefs(useUserStore());
  const {
    eroeffnungsuhrzeitState,
    schliessungsuhrzeitState,
    wahlbriefDatenState,
  } = storeToRefs(useWahlbezirkStore());
  const { wahlbezirkEreignisse } = storeToRefs(useEreignisStore());
  const { stimmabgabevermerke } = storeToRefs(useStimmabgabevermerkeStore());

  const wahlvorschlaegeByWahlIDAndWahlbezirkID =
    getWahlvorschlaegeByWahlIDAndWahlbezirkID(wahlID, wahlbezirkID);
  const { getBWerteForWahlbezirkAndWahl } = useMbwUtils();
  const gueltigeStimmenListe = ref<NiederschriftGueltigeStimme[]>([]);
  const gueltigeStimmenErgebnisGesamt =
    ref<NiederschriftGueltigeStimmenErgebnisGesamt>({
      gesamt: 0,
      stapelA: 0,
      stapelB: 0,
      stapelBC: 0,
    });

  async function prepareDataForNiederschriftDruck(
    status: Status,
    meldungsart: MeldungsartEnum,
    wahl: Wahl
  ): Promise<NiederschriftDruckInputBWB | NiederschriftDruckInputUWB> {
    const barcode = _createBarcode(wahl, meldungsart);
    const wahlbezirkNummer = currentUserWahlbezirkNummer.value;
    const wahlvorstaende = await _getWahlvorstand();
    const eroeffnungsuhrzeit: NiederschriftUhrzeit = _getEroeffnungsuhrzeit();
    const schliessungsuhrzeit: NiederschriftUhrzeit = _getSchliessungsuhrzeit();
    const wahltagFormatiert = toGermanDate(wahl.wahltag);
    const anzahlStimmzettel = wahl.stimmzettelumschlaege.anzahlWaehler;
    const anzahlWahlscheine = await _getAnzahlWahlscheine();
    const bWerte = await getBWerteForWahlbezirkAndWahl();
    const ereignisse = _getEreignisse();
    const parteienListe = _getParteienListe();
    const begruendung = await _getBegruendungStimmzettelumschlaege(wahl);
    const ungueltigeStimmen = await _getUngueltigeStimmen();
    _getStimmenListeUndErgebniseGesamt();
    const footer = _createFooter(status, meldungsart);
    console.log(gueltigeStimmenListe.value);
    const niederschriftDruckInputBaseData: NiederschriftDruckInputBase = {
      aktuelleWahl: wahl,
      wahltagFormatiert: wahltagFormatiert || "",
      barcode: barcode,
      wahlbezirkNummer: wahlbezirkNummer,
      wahlvorstaende: wahlvorstaende,
      eroeffnungsuhrzeit: eroeffnungsuhrzeit,
      schliessungsuhrzeit: schliessungsuhrzeit,
      anzahlStimmzettel: anzahlStimmzettel || 0,
      anzahlWahlscheine: anzahlWahlscheine,
      begruendungStimmzettelumschlaege: { grund: begruendung },
      bWerte: bWerte.b || 0,
      ungueltigeStimmen: ungueltigeStimmen || 0,
      gueltigeStimmenListe: gueltigeStimmenListe.value,
      gueltigeStimmenErgebnisGesamt: gueltigeStimmenErgebnisGesamt.value,
      parteienListe: parteienListe,
      ereignisse: ereignisse,
      footer: footer,
    };

    if (currentUserWahlbezirksArt.value == WahlbezirksArtEnum.UWB) {
      const anzahlWahltische = await _getAnzahlWahltische();
      const wvz = await _getWaehlerverzeichnisData();
      const aWerte = await getAWerteForWahlbezirkAndWahl();
      const aWerteGesamt = aWerte.a1 + (aWerte.a2 || 0);
      return {
        ...niederschriftDruckInputBaseData,
        anzahlStimmabgabevermerke: bWerte.b,
        aWerte: aWerteGesamt,
        a1: aWerte.a1,
        a2: aWerte.a2 || 0,
        wvz: wvz,
        anzahlWahltische: anzahlWahltische || 0,
        b1: bWerte.b1,
      };
    } else {
      const beanstandeteWahlbriefe = _getBeanstandteWahlbriefe();
      const wahlbriefdaten = _getWahlbriefdaten();
      return new Promise({
        ...niederschriftDruckInputBaseData,
        beanstandeteWahlbriefe: beanstandeteWahlbriefe,
        wahlbriefdaten: wahlbriefdaten,
      });
    }
  }

  function _getEroeffnungsuhrzeit() {
    return {
      stunde:
        eroeffnungsuhrzeitState.value.eroeffnungsuhrzeit?.getHours.toString() ??
        "",
      minute:
        eroeffnungsuhrzeitState.value.eroeffnungsuhrzeit
          ?.getMinutes()
          .toString() ?? "",
    };
  }

  function _getSchliessungsuhrzeit() {
    return {
      stunde:
        schliessungsuhrzeitState.value.schliessungsuhrzeit?.getHours.toString() ??
        "",
      minute:
        schliessungsuhrzeitState.value.schliessungsuhrzeit
          ?.getMinutes()
          .toString() ?? "",
    };
  }

  function _createBarcode(wahl: Wahl, meldungsart: MeldungsartEnum) {
    const canvas = document.createElement("canvas");
    const barcodeContent = _createBarcodeString(wahl, meldungsart);
    JsBarcode(canvas, barcodeContent, { displayValue: false });
    return canvas.toDataURL("image/jpeg");
  }

  function _createBarcodeString(wahl: Wahl, meldungsart: MeldungsartEnum) {
    const wahlartKurzbezeichnung = Array.from(wahl.wahlart)[0];
    const wahlbezirkNummer = parseInt(currentUserWahlbezirkNummer.value, 10);
    const wahlDatum = toGermanDate(wahl.wahltag);
    const meldungsartKurzbezeichnung =
      meldungsart == MeldungsArtEnum.Schnellmeldung ? "S" : "N";
    const wahlbezirkKurzbezeichnung =
      currentUserWahlbezirksArt.value == WahlbezirksArtEnum.UWB
        ? "SBZ" // Stimmbezirk (Urnenwahl)
        : "BWBZ"; // Briefwahlbezirk (Briefwahl)
    if (wahlartKurzbezeichnung && wahlbezirkNummer && wahlDatum) {
      return `${wahlartKurzbezeichnung}${wahlDatum}-${meldungsartKurzbezeichnung}-${wahlbezirkKurzbezeichnung}-${wahlbezirkNummer}`;
    } else {
      addNotification(
        "Fehler beim Erstellen des Barcodes",
        UserNotificationCategoryEnum.WARNING
      );
      return "";
    }
  }

  async function _getWahlvorstand(): Promise<
    NiederschriftWahlvorstandsmitglied[]
  > {
    try {
      const wahlvorstand = await getWahlvorstand(wahlbezirkID);
      return wahlvorstand.wahlvorstandsmitglieder.map((mitglied) => ({
        nachname: mitglied.familienname ?? "",
        vorname: mitglied.vorname ?? "",
        funktionsName: mitglied.funktionsname ?? "",
      }));
    } catch {
      throw new Error("Wahlvorstand konnte nicht geladen werden");
    }
  }

  function _getWahlbriefdaten(): NiederschriftWahlbriefdaten {
    const wb = wahlbriefDatenState.value.wahlbriefDaten;
    return {
      wahlbriefe: wb.wahlbriefe ?? "",
      verzeichnisseUngueltige: wb.verzeichnisseUngueltige ?? "",
      nachtraege: wb.nachtraege ?? "",
      nachtraeglichUeberbrachte: wb.nachtraeglichUeberbrachte ?? 0,
    } as NiederschriftWahlbriefdaten;
  }

  function _getBeanstandteWahlbriefe(): NiederschriftBeanstandeteWahlbriefe {
    const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);

    const beanstandeteWahlbriefe: NiederschriftBeanstandeteWahlbriefe = {
      gesamt: 0,
      keinGueltigerWahlschein: 0,
      keineUnterschrift: 0,
      keinStimmzettelumschlag: 0,
      nichtVerschlossen: 0,
      mehrereStimmzettelumschlaege: 0,
      keinAmtlicherStimmzettelumschlag: 0,
      loseStimmzettel: 0,
      gegenstandImUmschlag: 0,
      gefaehrdetWahlgeheimnis: 0,
      gesamtMinusZugelassen: 0,
      zugelassen: 0,
    } as NiederschriftBeanstandeteWahlbriefe;

    if (!wahl || !wahl.beanstandeteWahlbriefe) {
      return beanstandeteWahlbriefe;
    }

    wahl.beanstandeteWahlbriefe.forEach((grund) => {
      if (!grund) return;
      switch (grund) {
        case ZurueckweisungsgrundEnum.Zugelassen:
          beanstandeteWahlbriefe.gesamt++;
          beanstandeteWahlbriefe.zugelassen++;
          break;
        case ZurueckweisungsgrundEnum.ScheinUngueltig:
        case ZurueckweisungsgrundEnum.KeinOriginalSchein:
          beanstandeteWahlbriefe.gesamt++;
          beanstandeteWahlbriefe.gesamtMinusZugelassen++;
          beanstandeteWahlbriefe.keinGueltigerWahlschein++;
          break;
        case ZurueckweisungsgrundEnum.UnterschriftFehlt:
          beanstandeteWahlbriefe.gesamt++;
          beanstandeteWahlbriefe.gesamtMinusZugelassen++;
          beanstandeteWahlbriefe.keineUnterschrift++;
          break;
        case ZurueckweisungsgrundEnum.UmschlagFehlt:
          beanstandeteWahlbriefe.gesamt++;
          beanstandeteWahlbriefe.gesamtMinusZugelassen++;
          beanstandeteWahlbriefe.keinStimmzettelumschlag++;
          break;
        case ZurueckweisungsgrundEnum.LoseStimmzettel:
          beanstandeteWahlbriefe.gesamt++;
          beanstandeteWahlbriefe.gesamtMinusZugelassen++;
          beanstandeteWahlbriefe.loseStimmzettel++;
          break;
        case ZurueckweisungsgrundEnum.WahlbriefUndUmschlagOffen:
          beanstandeteWahlbriefe.gesamt++;
          beanstandeteWahlbriefe.gesamtMinusZugelassen++;
          beanstandeteWahlbriefe.nichtVerschlossen++;
          break;
        case ZurueckweisungsgrundEnum.ScheineUngleichUmschlaege:
          beanstandeteWahlbriefe.gesamt++;
          beanstandeteWahlbriefe.gesamtMinusZugelassen++;
          beanstandeteWahlbriefe.mehrereStimmzettelumschlaege++;
          break;
        case ZurueckweisungsgrundEnum.UmschlagNichtAmtlich:
          beanstandeteWahlbriefe.gesamt++;
          beanstandeteWahlbriefe.gesamtMinusZugelassen++;
          beanstandeteWahlbriefe.keinAmtlicherStimmzettelumschlag++;
          break;
        case ZurueckweisungsgrundEnum.UmschlagGefaehrdetWahlgeheimnis:
          beanstandeteWahlbriefe.gesamt++;
          beanstandeteWahlbriefe.gesamtMinusZugelassen++;
          beanstandeteWahlbriefe.gefaehrdetWahlgeheimnis++;
          break;
        case ZurueckweisungsgrundEnum.GegenstandImUmschlag:
          beanstandeteWahlbriefe.gesamt++;
          beanstandeteWahlbriefe.gesamtMinusZugelassen++;
          beanstandeteWahlbriefe.gegenstandImUmschlag++;
          break;
        case ZurueckweisungsgrundEnum.NichtWahlberechtigt:
          beanstandeteWahlbriefe.gesamt++;
          beanstandeteWahlbriefe.zugelassen++;
          break;
        default:
          beanstandeteWahlbriefe.gesamt++;
          beanstandeteWahlbriefe.gesamtMinusZugelassen++;
          break;
      }
    });

    return beanstandeteWahlbriefe;
  }

  async function _getUngueltigeStimmen() {
    if (wahlbezirkID) {
      try {
        const loadedErgebnisse = await getErgebnisse(
          wahlbezirkID,
          wahlID,
          "MBW_D_UNGUELTIG",
          false
        );
        return loadedErgebnisse?.ergebnisse.length;
      } catch (error) {
        logError("Fehler beim Laden der Ergebnisse: ", error);
      }
    }
  }

  function _getStimmenListeUndErgebniseGesamt() {
    let sumStapelA = 0;
    let sumStapelB = 0;
    let sumStapelBC = 0;
    let sumGesamt = 0;
    const ergebnisArray = [];
    const gueltigeStimmabgaben = _getGueltigeStimmabgabe();
    gueltigeStimmabgaben.forEach((erg) => {
      const listElement = {
        ordnungszahl: erg.ordnungszahl,
        bewerbername: erg.bewerbername,
        parteiname: erg.wahlvorschlag,
        stapelA: erg.stapelA,
        stapelB: erg.stapelB,
        stapelBC: erg.stapelBC,
        gesamt: erg.gesamt,
      };
      ergebnisArray.push(listElement);
      sumStapelA += erg.stapelA;
      sumStapelB += erg.stapelB;
      sumStapelBC += erg.stapelBC;
      sumGesamt += erg.gesamt;
    });

    gueltigeStimmenErgebnisGesamt.value = {
      stapelA: sumStapelA,
      stapelB: sumStapelB,
      stapelBC: sumStapelBC,
      gesamt: sumGesamt,
    };
    gueltigeStimmenListe.value = ergebnisArray;
  }

  function _getGueltigeStimmabgabe() {
    const stapelA = getErgebnisseByWahlIdAndStapelartOrUndefined(
      wahlID,
      StapelArtEnum.MbwA
    );
    const stapelB = getErgebnisseByWahlIdAndStapelartOrUndefined(
      wahlID,
      StapelArtEnum.MbwB
    );
    const stapelBC = getErgebnisseByWahlIdAndStapelartOrUndefined(
      wahlID,
      StapelArtEnum.MbwBC
    );

    if (wahlvorschlaegeByWahlIDAndWahlbezirkID) {
      let tmpGueltigeStimmabgaben = [];
      let pListe = [];

      wahlvorschlaegeByWahlIDAndWahlbezirkID.wahlvorschlaege.forEach((erg) => {
        const gueltigeStimmabgabe = {
          ordnungszahl: erg.ordnungszahl,
          wahlvorschlag: erg.kurzname,
          wahlvorschlagID: erg.identifikator,
          bewerbername:
            erg.kandidaten[0] && erg.kandidaten[0]["name"]
              ? erg.kandidaten[0]["name"]
              : "",
          stapelA: 0,
          stapelB: 0,
          stapelBC: 0,
        };

        if (stapelA) {
          stapelA.ergebnisse.forEach((ergebnis) => {
            if (erg.identifikator === ergebnis.wahlvorschlagID) {
              gueltigeStimmabgabe.stapelA = ergebnis.ergebnis || 0;
            }
          });
        }

        if (stapelB) {
          stapelB.ergebnisse.forEach((ergebnis) => {
            if (erg.identifikator === ergebnis.wahlvorschlagID) {
              gueltigeStimmabgabe.stapelB = ergebnis.ergebnis || 0;
            }
          });
        }

        if (stapelBC) {
          stapelBC.ergebnisse.forEach((ergebnis) => {
            if (erg.identifikator === ergebnis.wahlvorschlagID) {
              gueltigeStimmabgabe.stapelBC += ergebnis.ergebnis || 0;
            }
          });
        }
        gueltigeStimmabgabe.gesamt =
          gueltigeStimmabgabe.stapelA + gueltigeStimmabgabe.stapelB;
        tmpGueltigeStimmabgaben.push(gueltigeStimmabgabe);
      });
      tmpGueltigeStimmabgaben.sort((a, b) => a.ordnungszahl > b.ordnungszahl);
      return tmpGueltigeStimmabgaben;
    }
  }

  function _createParteeienListe(
    wahlvorschlaege: Wahlvorschlag[],
    stapelBC,
    forTemplate
  ) {
    let pListe = [];
    wahlvorschlaege.forEach((wv) => {
      let colSums = [];
      const bcKandidaten = stapelBC.ergebnisse
        .filter((k) => k.wahlvorschlagID === wv.identifikator)
        .sort((a, b) => (a.listenposition > b.listenposition ? 1 : -1));
      let partei = new Parteei(wv.identifikator, wv.kurzname, wv.ordnungszahl);
      wv.kandidaten
        .sort((a, b) => (a.listenposition > b.listenposition ? 1 : -1))
        .forEach((kand) => {
          let bckand = bcKandidaten.find(
            (bck) => bck.kandidatID === kand.identifikator
          );
          kand["ergebnis"] = bckand ? bckand.ergebnis : null;
          kand["wahlvorschlagID"] = bckand
            ? bckand.wahlvorschlagID
            : wv.identifikator;
          if (forTemplate && kand.listenposition === 0) {
            kand["laufendeNr"] =
              parseInt(partei.ordnungszahl) * 100 +
              parseInt(kand.listenposition);
            partei["direktKandMit00"] = kand;
          } else {
            partei.pushKandidat(kand);
          }
          colSums[kand.tabellenSpalteInNiederschrift] =
            parseInt(colSums[kand.tabellenSpalteInNiederschrift] || 0) +
            (parseInt(kand.ergebnis) || 0);
        });
      if (forTemplate) {
        partei = _bearbeiteForTemplate(partei, colSums);
      }
      pListe.push(partei);
    });
    return pListe;
  }

  function _bearbeiteForTemplate(partei, colSums) {
    let maxCols = 0;

    for (let row = 0; row < partei._tabledata.length; row++) {
      maxCols =
        maxCols > partei._tabledata[row].length
          ? maxCols
          : partei._tabledata[row].length;
    }
    partei["maxcols"] = [];

    let width1 = 0;
    let width2 = 0;
    if (maxCols) {
      width1 = (0.28 * 17) / maxCols;
      width2 = (0.72 * 17) / maxCols;
    }
    for (let k = 0; k < maxCols; k++) {
      partei.maxcols.push({
        width1: width1.toFixed(2),
        width2: width2.toFixed(2),
        colsum: colSums[k],
      });
    }
    return partei;
  }

  function _getEreignisse() {
    const ereignisData: NiederschriftEreignisse = {
      hasEreignisse: false,
      vorfaelle: [],
      vorkommnisse: [],
    };
    wahlbezirkEreignisse.value.ereigniseintraege.forEach((eintrag) => {
      if (eintrag.ereignisart === "VORFALL") {
        const uhrzeit = toHhMm(eintrag.uhrzeit);
        ereignisData.vorfaelle.push({
          uhrzeit: uhrzeit,
          beschreibung: eintrag.beschreibung || "",
        });
      } else {
        const uhrzeit = toHhMm(eintrag.uhrzeit);
        ereignisData.vorkommnisse.push({
          uhrzeit: uhrzeit,
          beschreibung: eintrag.beschreibung || "",
        });
      }
    });

    return ereignisData;
  }

  async function _getBegruendungStimmzettelumschlaege(wahl: Wahl) {
    try {
      const begruendung = await getBegruendungStimmzettelumschlaege(
        wahl,
        wahlbezirkID,
        "STIMMZETTEL_UMSCHLAEGE"
      );
      if (begruendung && begruendung.grund) {
        let begruendungsArray = begruendung.grund.split(" ");
        let begruendungsString = "";
        let zeileNr = 0;
        begruendungsArray.forEach((wort) => {
          begruendungsString += wort + " ";
          if (
            begruendungsString.length &&
            parseInt(begruendungsString.length / 80) !== zeileNr
          ) {
            begruendungsString += "<br/>";
            zeileNr++;
          }
        });
        return begruendungsString;
      }
    } catch {
      throw new Error(
        `Fehler beim Laden der Begruendungen der Stimmzettelumschlaege`
      );
    }
    return "";
  }

  function _createFooter(
    status: Status | undefined,
    meldungsArt: MeldungsartEnum
  ) {
    if (meldungsArt == MeldungsArtEnum.Niederschrift) {
      if (
        status &&
        status.niederschrift &&
        status.niederschrift.validierungsstatus
      ) {
        const date = new Date();
        const formattedDateWithTime = toGermanDate(date) + " " + toHhMm(date);

        if (status.niederschrift.validierungsstatus === "VALIDE") {
          return crypto.randomUUID() + ", " + formattedDateWithTime + " O";
        } else {
          return crypto.randomUUID() + ", " + formattedDateWithTime + " M";
        }
      }
    } else {
      // to be implemented - #1978
      return "";
    }
  }

  async function getAWerteForWahlbezirkAndWahl(): Promise<AWerte> {
    let aWerte;
    try {
      aWerte = await getAWerte(wahlbezirkID, false);
    } catch {
      throw new Error(`Fehler beim Laden der AWerte`);
    }

    const filteredAWert = aWerte.find(
      ({ bezirkUndWahlID }) => bezirkUndWahlID.wahlID === wahlID
    );

    if (!filteredAWert) {
      throw new Error(`Kein AWert gefunden für wahlID: ${wahlID}`);
    }
    return filteredAWert;
  }

  async function getB1() {
    const waehlerverzeichnisNummer =
      waehlerverzeichnisActions.getWaehlerverzeichnisNummerOrUndefinedById(
        wahlID
      );
    if (waehlerverzeichnisNummer) {
      try {
        const loadedStimmabgabevermerke = await getStimmabgabevermerke(
          wahlbezirkID,
          waehlerverzeichnisNummer
        );
        if (loadedStimmabgabevermerke) {
          // @ts-expect-error: noUncheckedIndexedAccess for wahldaten[0] | siehe #2008
          return loadedStimmabgabevermerke.wahldaten[0].vermerke
            .flatMap((vermerk) => vermerk.stimmzettel)
            .reduce(
              (summe, stimmzettel) => summe + (stimmzettel.anzahl || 0),
              0
            );
        }
      } catch {
        throw new Error(`Fehler beim Laden der BWerte`);
      }
    }
  }

  async function _getWaehlerverzeichnisData() {
    try {
      const waehlerverzeichnisNummer =
        waehlerverzeichnisActions.getWaehlerverzeichnisNummerOrUndefinedById(
          wahlID
        );
      if (waehlerverzeichnisNummer) {
        const waehlerverzeichnis = await getWaehlerverzeichnis(
          wahlbezirkID,
          waehlerverzeichnisNummer
        );
        return {
          nachtraeglicheBerichtigung:
            waehlerverzeichnis.nachtraeglicheBerichtigung,
          verzeichnisLagVor: waehlerverzeichnis.waehlerverzeichnisUnchanged,
          berichtigungVorBeginnDerAbstimmung:
            !waehlerverzeichnis.waehlerverzeichnisUnchanged,
        };
      }
    } catch {
      throw new Error(`Fehler beim Laden der BWerte`);
    }
  }

  async function _getAnzahlWahltische() {
    try {
      const urnenwahlvorbereitung =
        await getUrnenwahlvorbereitung(wahlbezirkID);
      return urnenwahlvorbereitung ? urnenwahlvorbereitung.anzahlWahltische : 0;
    } catch {
      throw new Error("Fehler beim Laden der Urnenwahlvorbereitung");
    }
  }

  function _getParteienListe() {
    let parteienListeForTemplate;
    const stapelBC = getErgebnisseByWahlIdAndStapelartOrUndefined(
      wahlID,
      StapelArtEnum.MbwBC
    );

    if (wahlvorschlaegeByWahlIDAndWahlbezirkID) {
      parteienListeForTemplate = _createParteeienListe(
        wahlvorschlaegeByWahlIDAndWahlbezirkID.wahlvorschlaege.sort((a, b) =>
          a.ordnungszahl > b.ordnungszahl ? 1 : -1
        ),
        stapelBC,
        true
      );
    }
    return parteienListeForTemplate;
  }

  async function _getAnzahlWahlscheine() {
    try {
      if (currentUserWahlbezirksArt.value === WahlbezirksArtEnum.UWB) {
        const waehlerverzeichnisNummer =
          waehlerverzeichnisActions.getWaehlerverzeichnisNummerOrUndefinedById(
            wahlID
          );
        if (waehlerverzeichnisNummer) {
          const loadedStimmabgabevermerke = await getStimmabgabevermerke(
            wahlbezirkID,
            waehlerverzeichnisNummer
          );
          if (loadedStimmabgabevermerke) {
            // @ts-expect-error: noUncheckedIndexedAccess for wahldaten[0] | siehe #2008
            const wahldatenForWahl = loadedStimmabgabevermerke.wahldaten.find(
              (wahldaten) => wahldaten.wahlID === wahlID
            );
            if (
              wahldatenForWahl?.eingenommeneWahlscheine &&
              Array.isArray(wahldatenForWahl.eingenommeneWahlscheine)
            ) {
              const kleineWahlscheine =
                wahldatenForWahl.eingenommeneWahlscheine.get(
                  EingenommenerWahlscheinStimmzettelartEnum.Klein
                );
              return kleineWahlscheine ?? 0;
            }
          }
        }
      } else {
        const loadedWahlscheine = await getWahlscheine(wahlID, wahlbezirkID);
        if (loadedWahlscheine) {
          return loadedWahlscheine.stimmabgabevermerke ?? 0;
        }
      }
    } catch {
      throw new Error(`Fehler beim Laden der BWerte`);
    }
  }

  return {
    prepareDataForNiederschriftDruck,
  };
}
