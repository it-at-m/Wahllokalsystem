import type { UngueltigerWahlschein } from "@/types/wahlbezirk/UngueltigerWahlschein.ts";

const NEW_LINE = "\n";
const VALUE_DELIMITER = ";";
const REGEX_PART_LEADING_WHITESPACES = /^([\s"]+)/g;
const REGEX_PART_TRAILING_WHITESPACES = /([\s"]+)$/g;

export function useUngueltigeWahlscheineMapper() {
  const removeSpacesAndQuotationsCallback = (entry: string) =>
    entry
      .replace(REGEX_PART_LEADING_WHITESPACES, "")
      .replace(REGEX_PART_TRAILING_WHITESPACES, "");

  function toModel(
    ungueltigeWahlscheineCSVString: string
  ): UngueltigerWahlschein[] {
    return ungueltigeWahlscheineCSVString
      .split(NEW_LINE)
      .map((line) => _csvLineToModel(line))
      .filter((ungueltigerWahlschein) => ungueltigerWahlschein !== undefined);
  }

  function _csvLineToModel(
    ungueltigerWahlscheinLine: string
  ): UngueltigerWahlschein | undefined {
    const values = ungueltigerWahlscheinLine.split(VALUE_DELIMITER);
    const cleanedValues = values.map(removeSpacesAndQuotationsCallback);
    return cleanedValues.length > 2
      ? {
          familienname: cleanedValues[0] ?? "",
          vorname: cleanedValues[1] ?? "",
          wahlscheinnummer: cleanedValues[2] ?? "",
        }
      : undefined;
  }

  return {
    toModel,
  };
}
