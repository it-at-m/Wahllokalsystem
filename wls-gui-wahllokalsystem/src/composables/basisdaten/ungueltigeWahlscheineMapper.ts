import type { UngueltigerWahlschein } from "@/types/wahlbezirk/UngueltigerWahlschein.ts";

const NEW_LINE = "\n";
const VALUE_DELIMITER = ";";

export function useUngueltigeWahlscheineMapper() {
  const removeSpacesAndQuotationsCallback = (entry: string) =>
    entry.replace(/[\s"]+/g, "");

  function toModel(
    ungueltigeWahlscheineCSVString: string
  ): UngueltigerWahlschein[] {
    return ungueltigeWahlscheineCSVString
      .split(NEW_LINE)
      .map(removeSpacesAndQuotationsCallback)
      .map((line) => _toModel(line))
      .filter((ungueltigerWahlschein) => ungueltigerWahlschein !== undefined);
  }

  function _toModel(
    ungueltigerWahlscheinLine: string
  ): UngueltigerWahlschein | undefined {
    const values = ungueltigerWahlscheinLine.split(VALUE_DELIMITER);
    return values.length > 2
      ? {
          familienname: values[0],
          vorname: values[1],
          wahlscheinnummer: values[2],
        }
      : undefined;
  }

  return {
    toModel,
  };
}
