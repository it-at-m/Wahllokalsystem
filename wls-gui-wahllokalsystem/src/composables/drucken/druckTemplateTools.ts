import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { MeldungValidierungsstatusEnum } from "@/types/ergebnismeldung/common/MeldungValidierungsstatusEnum.ts";

const { toGermanDate, toHhMm } = useDateTimeFormatter();

export function useDruckTemplateTools() {
  function createFooter(
    validierungsstatus: MeldungValidierungsstatusEnum,
    wahlbezirkNummer: string
  ): string {
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

  return {
    createFooter,
  };
}
