import type { AWerte } from "@/types/ergebnismeldung/common/AWerte.ts";
import type { BWerte } from "@/types/ergebnismeldung/common/BWerte.ts";
import type { MeldungsartEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";
import type { Status } from "@/types/ergebnismeldung/common/Status.ts";
import type { ErgebnismeldungDruckInput } from "@/types/ergebnismeldung/MBW/ErgebnismeldungDruckInput.ts";
import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnismeldung/MBW/MbwErgebnisseAndWahlvorschlag.ts";
import type { Wahl } from "@/types/wahl/Wahl.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import JsBarcode from "jsbarcode";
import { storeToRefs } from "pinia";
import { ref } from "vue";

import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useLogging } from "@/composables/common/logging.ts";
import { useNumberFormatter } from "@/composables/common/numberFormatter.ts";
import { useAWerteService } from "@/composables/ergebnismeldung/common/aWerteService.ts";
import { useErgebnisService } from "@/composables/ergebnismeldung/common/ergebnisService.ts";
import { useMbwErgebnisAndWahlvorschlagMapper } from "@/composables/ergebnismeldung/MBW/mbwErgebnisAndWahlvorschlagMapper.ts";
import { useStimmabgabevermerkeService } from "@/composables/stimmabgabevermerke/stimmabgabevermerkeService.ts";
import { useWahlvorschlaegeService } from "@/composables/wahlvorschlaege/wahlvorschlaegeService.ts";
import { useWahlvorschlagUtils } from "@/composables/wahlvorschlaege/wahlvorschlagUtils.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { MeldungsArtEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const {
  postErgebnisse,
  getErgebnisse,
  postSchnellmeldung,
  getStimmzettelumschlaege,
} = useErgebnisService();
const { getWahlvorschlaege } = useWahlvorschlaegeService();
const { sortWahlvorschlaegeByOrdnungszahl } = useWahlvorschlagUtils();
const { getAWerte } = useAWerteService();
const { getStimmabgabevermerke } = useStimmabgabevermerkeService();
const { logError } = useLogging("mbwUtils");
const { convertToSixDigitArray } = useNumberFormatter();
const { toGermanDate, toHhMm } = useDateTimeFormatter();

export function useMbwUtils(wahlID: string, wahlbezirkID: string) {
  const { mapErgebnisseFromErgebnisseAndWahlvorschlagListToErgebnisse } =
    useMbwErgebnisAndWahlvorschlagMapper(wahlID, wahlbezirkID);

  const { wahlenActions, waehlerverzeichnisActions } = useWahlenStore();
  const {
    isUWB,
    isBWB,
    currentUserWahlbezirkNummer,
    currentUserWahlbezirkID,
    currentUserWahlbezirksArt,
  } = storeToRefs(useUserStore());

  const isErgebnisseSaving = ref<boolean>(false);
  const isSendingSchnellmeldung = ref<boolean>(false);

  async function saveGueltigeErgebnisse(
    ergebnisse: MbwErgebnisseAndWahlvorschlag[]
  ) {
    isErgebnisseSaving.value = true;

    try {
      await postErgebnisse(
        wahlbezirkID,
        wahlID,
        StapelArtEnum.MbwA,
        mapErgebnisseFromErgebnisseAndWahlvorschlagListToErgebnisse(
          StapelArtEnum.MbwA,
          ergebnisse
        ),
        true
      );

      await postErgebnisse(
        wahlbezirkID,
        wahlID,
        StapelArtEnum.MbwB,
        mapErgebnisseFromErgebnisseAndWahlvorschlagListToErgebnisse(
          StapelArtEnum.MbwB,
          ergebnisse
        ),
        true
      );
    } catch {
      throw new Error("Fehler beim Speichern der Ergebnisse");
    } finally {
      isErgebnisseSaving.value = false;
    }
  }

  async function loadAndCombineErgebnisseAndWahlvorschlaege() {
    const ergebnisse: MbwErgebnisseAndWahlvorschlag[] = [];

    const wahlvorschlaege = await _loadWahlvorschlaege();
    const ergebnisseStapelA = await _loadGueltigeErgebnisseByStapelArt(
      StapelArtEnum.MbwA
    );
    const ergebnisseStapelB = await _loadGueltigeErgebnisseByStapelArt(
      StapelArtEnum.MbwB
    );

    for (const wahlvorschlag of wahlvorschlaege.wahlvorschlaege) {
      const ergebnisStapelAForWahlvorschlag =
        ergebnisseStapelA?.ergebnisse.find(
          (ergebnis) => ergebnis.wahlvorschlagID === wahlvorschlag.identifikator
        );
      const ergebnisStapelBForWahlvorschlag =
        ergebnisseStapelB?.ergebnisse.find(
          (ergebnis) => ergebnis.wahlvorschlagID === wahlvorschlag.identifikator
        );

      ergebnisse.push({
        wahlvorschlag: wahlvorschlag,
        ergebnisStapelA:
          ergebnisStapelAForWahlvorschlag ??
          _createEmptyErgebnisForWahlvorschlag(wahlvorschlag),
        ergebnisStapelB:
          ergebnisStapelBForWahlvorschlag ??
          _createEmptyErgebnisForWahlvorschlag(wahlvorschlag),
      });
    }
    return ergebnisse;
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

  async function getBWerteForWahlbezirkAndWahl(): Promise<BWerte> {
    const bWerte: BWerte = {
      bezirkUndWahlID: {
        wahlbezirkID: wahlbezirkID,
        wahlID: wahlID,
      },
      b: 0,
      b1: 0,
      b2: 0,
    };

    try {
      if (isUWB.value) {
        const waehlerverzeichnisNummer =
          waehlerverzeichnisActions.getWaehlerverzeichnisNummerOrUndefinedById(
            wahlID
          );
        if (waehlerverzeichnisNummer) {
          const loadedStimmabgabevermerke = await getStimmabgabevermerke(
            wahlbezirkID,
            waehlerverzeichnisNummer
          );
          // @ts-expect-error: noUncheckedIndexedAccess for wahldaten[0] | siehe #2008
          bWerte.b1 = loadedStimmabgabevermerke.wahldaten[0].vermerke
            .flatMap((vermerk) => vermerk.stimmzettel)
            .reduce(
              (summe, stimmzettel) => summe + (stimmzettel.anzahl || 0),
              0
            );
          bWerte.b2 = Array.from(
            // @ts-expect-error: noUncheckedIndexedAccess for wahldaten[0] | siehe #2008
            loadedStimmabgabevermerke.wahldaten[0].eingenommeneWahlscheine.values()
          ).reduce((sum, value) => sum + value, 0);

          bWerte.b = bWerte.b1 + bWerte.b2;
        }
      }
      if (isBWB.value) {
        const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);
        if (wahl) {
          const loadedStimmzettelumschlaege = await getStimmzettelumschlaege(
            wahl,
            wahlbezirkID,
            "",
            false
          );
          bWerte.b = loadedStimmzettelumschlaege?.anzahlWaehler || 0;
        }
      }
    } catch {
      throw new Error(`Fehler beim Laden der BWerte`);
    }

    return bWerte;
  }

  async function sendSchnellmeldung() {
    isSendingSchnellmeldung.value = true;

    try {
      const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);
      if (!wahl) {
        logError(`zur wahlID ${wahlID} existiert keine Wahl`);
      } else {
        await postSchnellmeldung(
          wahlID,
          wahlbezirkID,
          currentUserWahlbezirkID.value,
          wahl.waehlerverzeichnisNummer
        );
      }
    } finally {
      isSendingSchnellmeldung.value = false;
    }
  }

  async function prepareDataForErgebnismeldungDruck(
    wahl: Wahl,
    status: Status,
    meldungsart: MeldungsartEnum
  ): Promise<ErgebnismeldungDruckInput> {
    let aWerte = undefined;
    if (currentUserWahlbezirksArt.value == WahlbezirksArtEnum.UWB) {
      aWerte = await getAWerteForWahlbezirkAndWahl();
    }

    const bWerte = await getBWerteForWahlbezirkAndWahl();

    const ergebnisseAndWahlvorschlaege =
      await loadAndCombineErgebnisseAndWahlvorschlaege();

    let gueltigeStimmenGesamt = 0;
    for (const vorschlag of ergebnisseAndWahlvorschlaege) {
      gueltigeStimmenGesamt +=
        (vorschlag.ergebnisStapelA.ergebnis ?? 0) +
        (vorschlag.ergebnisStapelB.ergebnis ?? 0);
    }

    const ungueltige = await getErgebnisse(
      wahlbezirkID,
      wahlID,
      StapelArtEnum.MbwDUngueltig,
      false
    );
    const ungueltigeStimmen = ungueltige?.ergebnisse[0]?.ergebnis || 0;

    const stimmenGesamt = gueltigeStimmenGesamt + ungueltigeStimmen;

    const footer = _createFooter(status, meldungsart);

    const canvas = document.createElement("canvas");
    const barcodeContent = _createBarcodeString(wahl, meldungsart);
    JsBarcode(canvas, barcodeContent, { displayValue: false });
    const jpegUrl = canvas.toDataURL("image/jpeg");

    return {
      meldungsArt: meldungsart,
      wahlbezirksArt: currentUserWahlbezirksArt.value,
      aktuelleWahl: wahl,
      footer: footer,
      alleStimmen: convertToSixDigitArray(stimmenGesamt),
      gueltigeStimmenListe: ergebnisseAndWahlvorschlaege,
      gueltigeStimmenGesamt: convertToSixDigitArray(gueltigeStimmenGesamt),
      ungueltigeStimmen: convertToSixDigitArray(ungueltigeStimmen),
      bWerte: bWerte,
      aWerte: aWerte,
      wahlbezirkNummer: currentUserWahlbezirkNummer.value || "",
      barcode: jpegUrl,
      sendOk: status.schnellmeldung.uebermittelt || false,
    };
  }

  async function _loadGueltigeErgebnisseByStapelArt(stapelArt: StapelArtEnum) {
    try {
      return await getErgebnisse(wahlbezirkID, wahlID, stapelArt, false);
    } catch {
      throw new Error("Fehler beim Laden der Ergebnisse");
    }
  }

  async function _loadWahlvorschlaege() {
    try {
      const loadedWahlvorschlaege = await getWahlvorschlaege(
        wahlID,
        wahlbezirkID
      );
      return sortWahlvorschlaegeByOrdnungszahl(loadedWahlvorschlaege);
    } catch {
      throw new Error("Fehler beim Laden der Wahlvorschläge");
    }
  }

  function _createEmptyErgebnisForWahlvorschlag(wahlvorschlag: Wahlvorschlag) {
    return {
      wahlvorschlagID: wahlvorschlag.identifikator,
      kandidatID: null,
      wahlvorschlagsOrdnungszahl: wahlvorschlag.ordnungszahl,
      ergebnis: null,
      numIndex: null,
    };
  }

  function _createFooter(
    status: Status | undefined,
    meldungsArt: MeldungsartEnum
  ) {
    if (meldungsArt == MeldungsArtEnum.Schnellmeldung) {
      if (
        status &&
        status.schnellmeldung &&
        status.schnellmeldung.validierungsstatus
      ) {
        const date = new Date();
        const formattedDateWithTime = toGermanDate(date) + " " + toHhMm(date);

        if (status.schnellmeldung.validierungsstatus === "VALIDE") {
          return "" + _uuidv4() + ", " + formattedDateWithTime + " O";
        } else {
          return "" + _uuidv4() + ", " + formattedDateWithTime + " M";
        }
      }
    } else {
      return "to be implemented - #1978";
    }
  }

  function _uuidv4() {
    return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(
      /[xy]/g,
      function (c) {
        const r = (Math.random() * 16) | 0;
        const v = c == "x" ? r : (r & 0x3) | 0x8;
        return v.toString(16);
      }
    );
  }

  function _createBarcodeString(wahl: Wahl, meldungsart: MeldungsartEnum) {
    const wahlartKurzbezeichnung = Array.from(wahl.wahlart)[0];
    const wahlbezirkKurzbezeichnung =
      currentUserWahlbezirksArt.value == WahlbezirksArtEnum.UWB
        ? "SBZ"
        : "BWBZ";
    const meldungsartKurzbezeichnung =
      meldungsart == MeldungsArtEnum.Schnellmeldung ? "S" : "N";
    const wahlDatum = toGermanDate(wahl.wahltag);
    return `${wahlartKurzbezeichnung}${wahlDatum}-${meldungsartKurzbezeichnung}-${wahlbezirkKurzbezeichnung}-${parseInt(currentUserWahlbezirkNummer.value, 10)}`;
  }

  return {
    isErgebnisseSaving,
    isSendingSchnellmeldung,
    saveGueltigeErgebnisse,
    loadAndCombineErgebnisseAndWahlvorschlaege,
    getAWerteForWahlbezirkAndWahl,
    getBWerteForWahlbezirkAndWahl,
    sendSchnellmeldung,
    prepareDataForErgebnismeldungDruck,
  };
}
