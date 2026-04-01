import type { MeldungsartEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";
import type {
  NiederschriftBeanstandeteWahlbriefe,
  NiederschriftDruckInput,
  NiederschriftGueltigeStimme,
  NiederschriftGueltigeStimmenErgebnisGesamt,
  NiederschriftUhrzeit,
  NiederschriftWahlbriefdaten,
  NiederschriftWahlvorstandsmitglied,
} from "@/types/ergebnismeldung/common/NiederschriftDruckInput.ts";
import type { Status } from "@/types/ergebnismeldung/common/Status.ts";
import type { Wahl } from "@/types/wahl/Wahl.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import JsBarcode from "jsbarcode";
import { storeToRefs } from "pinia";
import { ref } from "vue";

import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useLogging } from "@/composables/common/logging.ts";
import { useErgebnisService } from "@/composables/ergebnismeldung/common/ergebnisService.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { useEreignisStore } from "@/stores/ereignisStore.ts";
import { useErgebnismeldungStore } from "@/stores/ergebnismeldungStore.ts";
import { useStimmabgabevermerkeStore } from "@/stores/stimmabgabevermerkeStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlbezirkStore } from "@/stores/wahlbezirkStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { useWahlvorschlaegeStore } from "@/stores/wahlvorschlaegeStore.ts";
import { useWahlvorstandStore } from "@/stores/wahlvorstandStore.ts";
import { ZurueckweisungsgrundEnum } from "@/types/briefwahl/ZurueckweisungsgrundEnum.ts";
import { MeldungsArtEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";
import { Parteei } from "@/types/ergebnismeldung/common/NiederschriftDruckInput.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const { logError } = useLogging("requestStrategies");
const { toGermanDate, toHhMm } = useDateTimeFormatter();
const { addNotification } = useUserNotificationService();
const { getErgebnisse } = useErgebnisService();

export function useMbtUtilsNiederschrift(wahlID: string, wahlbezirkID: string) {
  const { getErgebnisseByWahlIdAndStapelartOrUndefined } =
    useErgebnismeldungStore();

  const { wahlenActions } = useWahlenStore();
  const { currentUserWahlbezirkNummer, currentUserWahlbezirksArt } =
    storeToRefs(useUserStore());
  const { wahlvorstand } = storeToRefs(useWahlvorstandStore());

  const { getWahlvorschlaegeByWahlIDAndWahlbezirkID } =
    useWahlvorschlaegeStore();
  const {
    eroeffnungsuhrzeitState,
    schliessungsuhrzeitState,
    wahlbriefDatenState,
  } = storeToRefs(useWahlbezirkStore());
  const { wahlbezirkEreignisse } = storeToRefs(useEreignisStore());
  const { stimmabgabevermerke } = storeToRefs(useStimmabgabevermerkeStore());
  const wahlvorschlaegeByWahlIDAndWahlbezirkID =
    getWahlvorschlaegeByWahlIDAndWahlbezirkID(wahlID, wahlbezirkID);
  const gueltigeStimmenListe = ref<NiederschriftGueltigeStimme[]>([]);
  const gueltigeStimmenErgebnisGesamt =
    ref<NiederschriftGueltigeStimmenErgebnisGesamt>({});
  const vorfallArray = [];
  const vorkommnisArray = [];
  const { getBegruendungStimmzettelumschlaege } = useErgebnisService();

  async function gatherDataForTemplate(
    status: Status,
    meldungsart: MeldungsartEnum
  ) {
    const aktulleWahl = wahlenActions.getWahlOrUndefinedById(wahlID);
    const barcode = _createBarcode(aktulleWahl, meldungsart);
    const wahlbezirkNummer = currentUserWahlbezirkNummer.value;
    const wahlvorstand = _getWahlvorstand();
    const eroeffnungsuhrzeit: NiederschriftUhrzeit = {
      stunde:
        eroeffnungsuhrzeitState.value.eroeffnungsuhrzeit?.getHours.toString() ??
        "",
      minute:
        eroeffnungsuhrzeitState.value.eroeffnungsuhrzeit
          ?.getMinutes()
          .toString() ?? "",
    };

    const schliessungsuhrzeit: NiederschriftUhrzeit = {
      stunde:
        schliessungsuhrzeitState.value.schliessungsuhrzeit?.getHours.toString() ??
        "",
      minute:
        schliessungsuhrzeitState.value.schliessungsuhrzeit
          ?.getMinutes()
          .toString() ?? "",
    };

    const wahlbriefdaten = _getWahlbriefdaten();
    const beanstandteWahlbriefe = _getBeanstandteWahlbriefe();
    const anzahlStimmzettell = aktulleWahl.stimmzettelumschlaege.anzahlWaehler;
    const anzahlWahlscheine = stimmabgabevermerke.value.length;
    const bWerte = aktulleWahl.stimmzettelumschlaege.anzahlWaehler;
    const ungueltigeStimmen = _getUngueltigeStimmen();
    getEreignisse();
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
    const begruendung = await _getBegruendungStimmzettelumschlaege(aktulleWahl);

    _getStimmenListeUndErgebniseGesamt();

    const templateData: NiederschriftDruckInput = {
      aktuelleWahl: aktulleWahl,
      wahltagFormatiert: toGermanDate(aktulleWahl.wahltag),
      barcode: barcode,
      wahlbezirkNummer: wahlbezirkNummer,
      wahlvorstaende: wahlvorstand,
      eroeffnungsuhrzeit: eroeffnungsuhrzeit,
      schliessungsuhrzeit: schliessungsuhrzeit,
      wahlbriefdaten: wahlbriefdaten,
      beanstandeteWahlbriefe: beanstandteWahlbriefe,
      anzahlStimmzettel: anzahlStimmzettell,
      anzahlWahlscheine: anzahlWahlscheine,
      begruendungStimmzettelumschlaege: {
        grund: begruendung,
      }, // TODO noch nicht eingetragen
      bWerte: bWerte,
      ungueltigeStimmen: ungueltigeStimmen,
      gueltigeStimmenListe: gueltigeStimmenListe.value,
      gueltigeStimmenErgebnisGesamt: gueltigeStimmenErgebnisGesamt,
      parteienListe: parteienListeForTemplate,
      ereignisse: vorkommnisArray.concat(vorfallArray),
      footer: _createFooter(status, meldungsart),
    };
    console.log(JSON.stringify(templateData));
    return templateData;
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

  function _getWahlvorstand(): NiederschriftWahlvorstandsmitglied[] {
    return wahlvorstand.value.wahlvorstandsmitglieder.map((mitglied) => {
      return {
        nachname: mitglied.familienname ?? "",
        vorname: mitglied.vorname ?? "",
        funktionsName: mitglied.funktionsname ?? "",
      } as NiederschriftWahlvorstandsmitglied;
    });
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
          // In the legacy implementation this was counted as 'zugelassen' as well as a separate counter.
          beanstandeteWahlbriefe.gesamt++;
          beanstandeteWahlbriefe.zugelassen++;
          break;
        default:
          // unknown / unhandled reasons - count towards total if present
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

  function getEreignisse() {
    wahlbezirkEreignisse.value.ereigniseintraege.forEach((eintrag) => {
      if (eintrag.ereignisart === "VORFALL") {
        eintrag.uhrzeit = new Date(toHhMm(eintrag.uhrzeit));
        vorfallArray.push(eintrag);
      } else {
        eintrag.uhrzeit = new Date(toHhMm(eintrag.uhrzeit));
        vorkommnisArray.push(eintrag);
      }
    });
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

  return {
    gatherDataForTemplate,
  };
}
