import type { AWerte } from "@/types/ergebnismeldung/common/AWerte.ts";
import type { BWerte } from "@/types/ergebnismeldung/common/BWerte.ts";
import type { MeldungsartEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";
import type { SchnellmeldungDruckInput } from "@/types/ergebnismeldung/common/SchnellmeldungDruckInput.ts";
import type { Status } from "@/types/ergebnismeldung/common/Status.ts";
import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnismeldung/MBW/MbwErgebnisseAndWahlvorschlag.ts";
import type { Wahl } from "@/types/wahl/Wahl.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import JsBarcode from "jsbarcode";
import { storeToRefs } from "pinia";
import { ref } from "vue";

import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useLogging } from "@/composables/common/logging.ts";
import { useNumberFormatter } from "@/composables/common/numberFormatter.ts";
import { useAusdruckService } from "@/composables/ergebnismeldung/common/ausdruckService.ts";
import { useAWerteService } from "@/composables/ergebnismeldung/common/aWerteService.ts";
import { useErgebnisService } from "@/composables/ergebnismeldung/common/ergebnisService.ts";
import { useStatusService } from "@/composables/ergebnismeldung/common/statusService.ts";
import { useStatusUtils } from "@/composables/ergebnismeldung/common/statusUtils.ts";
import { useMbwErgebnisAndWahlvorschlagMapper } from "@/composables/ergebnismeldung/MBW/mbwErgebnisAndWahlvorschlagMapper.ts";
import { useStimmabgabevermerkeService } from "@/composables/stimmabgabevermerke/stimmabgabevermerkeService.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { useWahlvorschlaegeService } from "@/composables/wahlvorschlaege/wahlvorschlaegeService.ts";
import { useWahlvorschlagUtils } from "@/composables/wahlvorschlaege/wahlvorschlagUtils.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { MeldungsArtEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";
import { MeldungValidierungsstatusEnum } from "@/types/ergebnismeldung/common/MeldungValidierungsstatusEnum.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const {
  postErgebnisse,
  getErgebnisse,
  postSchnellmeldung,
  getStimmzettelumschlaege,
  postNiederschrift,
} = useErgebnisService();
const { getWahlvorschlaege } = useWahlvorschlaegeService();
const { sortWahlvorschlaegeByOrdnungszahl } = useWahlvorschlagUtils();
const { getAWerte } = useAWerteService();
const { getStimmabgabevermerke } = useStimmabgabevermerkeService();
const { logError } = useLogging("mbwUtils");
const { convertToSixDigitArray } = useNumberFormatter();
const { toGermanDate, toHhMm } = useDateTimeFormatter();
const { addNotification } = useUserNotificationService();
const { postStatus } = useStatusService();
const { loadStatusByWahlIdAndWahlbezirkId } = useStatusUtils();
const { toYyyyMmDdWithTimeWithoutTimezoneOffset } = useDateTimeFormatter();

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

  const { postAusdruck } = useAusdruckService();

  const isErgebnisseSaving = ref<boolean>(false);
  const isSendingSchnellmeldung = ref<boolean>(false);
  const isSendingNiederschrift = ref<boolean>(false);

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
          if (loadedStimmabgabevermerke) {
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
    const status = await loadStatusByWahlIdAndWahlbezirkId(
      wahlID,
      wahlbezirkID
    );

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
        )
          .then(() => {
            status.schnellmeldung.uebermittelt = true;
          })
          .catch(() => {
            status.schnellmeldung.uebermittelt = false;
          })
          .finally(async () => {
            status.schnellmeldung.validierungsstatus =
              MeldungValidierungsstatusEnum.Valide;
            status.schnellmeldung.sendeuhrzeit =
              toYyyyMmDdWithTimeWithoutTimezoneOffset(new Date());
            await postStatus(wahlID, wahlbezirkID, status, false);
          });
      }
    } finally {
      isSendingSchnellmeldung.value = false;
    }
  }

  async function prepareDataForSchnellmeldungDruck(
    wahl: Wahl,
    status: Status,
    meldungsart: MeldungsartEnum
  ): Promise<SchnellmeldungDruckInput> {
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
    const ungueltigeStimmen = ungueltige?.ergebnisse[0]?.ergebnis ?? 0;

    const stimmenGesamt = gueltigeStimmenGesamt + ungueltigeStimmen;

    const footer = _createFooter(status, meldungsart);

    const jpegUrl = _createBarcode(wahl, meldungsart);

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

  async function updateStatusAfterSchnellmeldungDrucken() {
    const status = await loadStatusByWahlIdAndWahlbezirkId(
      wahlID,
      wahlbezirkID
    );
    status.schnellmeldung.gedruckt = true;
    await postStatus(wahlID, wahlbezirkID, status, false);
  }

  async function sendNiederschrift() {
    isSendingNiederschrift.value = true;
    const status = await loadStatusByWahlIdAndWahlbezirkId(
      wahlID,
      wahlbezirkID
    );

    try {
      const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);
      if (!wahl) {
        logError(`zur wahlID ${wahlID} existiert keine Wahl`);
      } else {
        await postNiederschrift(
          wahlID,
          wahlbezirkID,
          wahl.waehlerverzeichnisNummer,
          currentUserWahlbezirkID.value
        )
          .then(() => {
            status.niederschrift.uebermittelt = true;
          })
          .catch(() => {
            status.niederschrift.uebermittelt = false;
          })
          .finally(() => {
            status.niederschrift.validierungsstatus =
              MeldungValidierungsstatusEnum.Valide;
            status.niederschrift.sendeuhrzeit =
              toYyyyMmDdWithTimeWithoutTimezoneOffset(new Date());
            postStatus(wahlID, wahlbezirkID, status, false);
          });
      }
    } finally {
      isSendingNiederschrift.value = false;
    }
  }

  async function sendAusdruckNiederschrift(
    meldungsart: MeldungsartEnum,
    ausdruck: string
  ) {
    const status = await loadStatusByWahlIdAndWahlbezirkId(
      wahlID,
      wahlbezirkID
    );
    try {
      await postAusdruck(wahlbezirkID, wahlID, meldungsart, ausdruck).then(
        async () => {
          status.niederschrift.gedruckt = true;
          await postStatus(wahlID, wahlbezirkID, status, false);
        }
      );
    } catch {
      logError("Fehler beim Speichern des Ausdrucks");
    }
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

  function _createBarcode(wahl: Wahl, meldungsart: MeldungsartEnum) {
    const canvas = document.createElement("canvas");
    const barcodeContent = _createBarcodeString(wahl, meldungsart);
    JsBarcode(canvas, barcodeContent, { displayValue: false });
    return canvas.toDataURL("image/jpeg");
  }

  function _createBarcodeString(wahl: Wahl, meldungsart: MeldungsartEnum) {
    const wahlartKurzbezeichnung = Array.from(wahl.wahlart)[0];
    const wahlbezirkKurzbezeichnung =
      currentUserWahlbezirksArt.value == WahlbezirksArtEnum.UWB
        ? "SBZ" // Stimmbezirk (Urnenwahl)
        : "BWBZ"; // Briefwahlbezirk (Briefwahl)
    const meldungsartKurzbezeichnung =
      meldungsart == MeldungsArtEnum.Schnellmeldung ? "S" : "N";
    const wahlbezirkNummer = parseInt(currentUserWahlbezirkNummer.value, 10);
    const wahlDatum = toGermanDate(wahl.wahltag);
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

  return {
    isErgebnisseSaving,
    isSendingSchnellmeldung,
    isSendingNiederschrift,
    saveGueltigeErgebnisse,
    loadAndCombineErgebnisseAndWahlvorschlaege,
    getAWerteForWahlbezirkAndWahl,
    getBWerteForWahlbezirkAndWahl,
    sendSchnellmeldung,
    prepareDataForSchnellmeldungDruck,
    sendAusdruckNiederschrift,
    _createBarcode,
    _createFooter,
    updateStatusAfterSchnellmeldungDrucken,
    sendNiederschrift,
  };
}
