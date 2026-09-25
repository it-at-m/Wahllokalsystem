import type { MeldungsartEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";
import type { Wahl } from "@/types/wahl/Wahl.ts";

import JsBarcode from "jsbarcode";

import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { MeldungsArtEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";
import { MeldungValidierungsstatusEnum } from "@/types/ergebnismeldung/common/MeldungValidierungsstatusEnum.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

export function useCommonPrintService() {
  const { toGermanDate, toHhMm } = useDateTimeFormatter();
  const { addNotification } = useUserNotificationService();

  function createBarcode(
    wahl: Wahl,
    meldungsart: MeldungsartEnum,
    wahlbezirkArt: WahlbezirksArtEnum,
    wahlbezirkNummer: string
  ) {
    const barcodeContent = _createBarcodeString(
      wahl,
      meldungsart,
      wahlbezirkArt,
      wahlbezirkNummer
    );
    if (!barcodeContent) {
      return "";
    }

    const canvas = document.createElement("canvas");
    JsBarcode(canvas, barcodeContent, { displayValue: false });
    return canvas.toDataURL("image/jpeg");
  }

  function createFooter(
    validierungsstatus: MeldungValidierungsstatusEnum,
    meldungsArt: MeldungsartEnum,
    wahlbezirkNummer: string
  ) {
    if (meldungsArt == MeldungsArtEnum.Schnellmeldung) {
      if (validierungsstatus) {
        const date = new Date();
        const formattedDateWithTime = toGermanDate(date) + " " + toHhMm(date);

        if (validierungsstatus === "VALIDE") {
          return crypto.randomUUID() + ", " + formattedDateWithTime + " O";
        } else {
          return crypto.randomUUID() + ", " + formattedDateWithTime + " M";
        }
      }
    } else if (meldungsArt == MeldungsArtEnum.Beschlussentscheidungen) {
      if (validierungsstatus) {
        const date = new Date();
        const formattedDateWithTime = toGermanDate(date) + " " + toHhMm(date);

        if (validierungsstatus === "VALIDE") {
          return (
            crypto.randomUUID() +
            ", " +
            formattedDateWithTime +
            " O " +
            wahlbezirkNummer
          );
        } else {
          return (
            crypto.randomUUID() +
            ", " +
            formattedDateWithTime +
            " M " +
            wahlbezirkNummer
          );
        }
      }
    } else {
      // to be implemented - #1978
      return "";
    }
  }

  function _createBarcodeString(
    wahl: Wahl,
    meldungsart: MeldungsartEnum,
    wahlbezirkArt: WahlbezirksArtEnum,
    wahlbezirkNummer: string
  ) {
    const wahlbezirkKurzbezeichnung =
      wahlbezirkArt == WahlbezirksArtEnum.UWB
        ? "SBZ" // Stimmbezirk (Urnenwahl)
        : "BWBZ"; // Briefwahlbezirk (Briefwahl)
    const meldungsartKurzbezeichnung =
      meldungsart == MeldungsArtEnum.Schnellmeldung ? "S" : "N";
    const wahlbezirkNummerAsInt = parseInt(wahlbezirkNummer, 10);
    const wahlDatum = toGermanDate(wahl.wahltag);
    if (wahl.kennzeichen && wahlbezirkNummerAsInt && wahlDatum) {
      return `${wahl.kennzeichen}${wahlDatum}-${meldungsartKurzbezeichnung}-${wahlbezirkKurzbezeichnung}-${wahlbezirkNummer}`;
    } else {
      addNotification(
        "Fehler beim Erstellen des Barcodes",
        UserNotificationCategoryEnum.WARNING
      );
      return "";
    }
  }

  return {
    createBarcode,
    createFooter,
  };
}
