import type { MeldungsartEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";
import type { Wahl } from "@/types/wahl/Wahl.ts";

import JsBarcode from "jsbarcode";

import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { useLogging } from "@/composables/common/logging.ts";
import { useMeldungValidierungsstatusEnumMapper } from "@/composables/ergebnismeldung/common/MeldungValidierungsstatusEnumMapper.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { MeldungsArtEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";
import { MeldungValidierungsstatusEnum } from "@/types/ergebnismeldung/common/MeldungValidierungsstatusEnum.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

export function useCommonPrintService() {
  const { toGermanDate, toHhMm } = useDateTimeFormatter();
  const { logError } = useLogging("commonPrintService");
  const { addNotification } = useUserNotificationService();
  const { enumToShortAlias } = useMeldungValidierungsstatusEnumMapper();

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
    wahlbezirkNummer: string
  ) {
    const date = new Date();
    const formattedDateWithTime = toGermanDate(date) + " " + toHhMm(date);
    const meldungValidierungsstatusShortAlias =
      enumToShortAlias[validierungsstatus];

    return `${crypto.randomUUID()}, ${formattedDateWithTime} ${meldungValidierungsstatusShortAlias} ${wahlbezirkNummer}`;
  }

  async function printInWindow(printContent: string) {
    const printWindow = window.open(
      "",
      "",
      "left=0,top=0,width=800,height=900,toolbar=0,scrollbars=0,status=0"
    );

    if (printWindow) {
      printWindow.document.writeln(printContent);
      printWindow.document.close();

      await _fetchImages(printWindow);
      printWindow.print();
      printWindow.close();
      return true;
    }

    return false;
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

  async function _fetchImages(printWindow: Window) {
    const images = printWindow.document.querySelectorAll("img");

    // Chrome/Chromium requires images to be fully loaded and ready in the DOM before printing.
    // Otherwise, they might be missing or blank in the generated PDF layout.
    const imagePromises = Array.from(images).map((img) =>
      img.decode
        ? img.decode().catch(() => {
            logError("Image konnte nicht dekodiert werden: " + img.src);
          })
        : Promise.resolve()
    );

    await Promise.all(imagePromises);
  }

  return {
    createBarcode,
    createFooter,
    printInWindow,
  };
}
