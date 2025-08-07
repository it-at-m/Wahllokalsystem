import type { UngueltigerWahlschein } from "@/types/wahlbezirk/UngueltigerWahlschein.ts";

import { describe, expect, it } from "vitest";

import { useUngueltigeWahlscheineMapper } from "@/composables/basisdaten/ungueltigeWahlscheineMapper.ts";

describe("ungueltigerWahlscheinMapper.ts", () => {
  const { toModel } = useUngueltigeWahlscheineMapper();

  describe("toModel", () => {
    it("should_returnListOfUngueltigeWahlscheine_when_multipleLinesAreGiven", () => {
      const csvString =
        "fam name1;vor name1;1234\n" +
        "fam nam e2;vor nam e2;23\n" +
        "fam name3;vor name3;42";

      const result = toModel(csvString);

      const expectedResult: UngueltigerWahlschein[] = [
        {
          familienname: "fam name1",
          vorname: "vor name1",
          wahlscheinnummer: "1234",
        },
        {
          familienname: "fam nam e2",
          vorname: "vor nam e2",
          wahlscheinnummer: "23",
        },
        {
          familienname: "fam name3",
          vorname: "vor name3",
          wahlscheinnummer: "42",
        },
      ];
      expect(result).toStrictEqual(expectedResult);
    });

    it("should_removeSpacesAndQuotations_when_linesAreGiven", () => {
      const csvString =
        '"famname1";"vorname1";"1234"\n' +
        "     famname2;     vorname2;     23\n" +
        "    famname3;    vorname3;    42";

      const result = toModel(csvString);

      const expectedResult: UngueltigerWahlschein[] = [
        {
          familienname: "famname1",
          vorname: "vorname1",
          wahlscheinnummer: "1234",
        },
        {
          familienname: "famname2",
          vorname: "vorname2",
          wahlscheinnummer: "23",
        },
        {
          familienname: "famname3",
          vorname: "vorname3",
          wahlscheinnummer: "42",
        },
      ];
      expect(result).toStrictEqual(expectedResult);
    });

    it("should_returnEmptyList_when_stringIsBlank", () => {
      const csvString = "";

      const result = toModel(csvString);

      expect(result).toHaveLength(0);
    });

    it("should_returnEmptyList_when_linesDontHaveEnoughValues", () => {
      const csvString = "onlyOneValue";

      const result = toModel(csvString);

      expect(result).toHaveLength(0);
    });
  });
});
