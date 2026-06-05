import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useStimmabgabevermerkeUtils } from "@/composables/stimmabgabevermerke/stimmabgabevermerkeUtils.ts";

const { generateRandomString, generateRandomNumber } =
  useCommonTestDataFactory();

describe("stimmabgabevermerkeUtils.ts", () => {
  const { createEmptyStimmabgabevermerke } = useStimmabgabevermerkeUtils();

  describe("createEmptyStimmabgabevermerke", () => {
    it("should_createAnObjectWithData_when_called", () => {
      const wahlID = generateRandomString(10);
      const wahlbezirkID = generateRandomString(10);
      const waehlerverzeichnisNummer = generateRandomNumber(3);

      const result = createEmptyStimmabgabevermerke(
        wahlID,
        wahlbezirkID,
        waehlerverzeichnisNummer
      );

      const expectedResult = {
        wahlbezirkID: wahlbezirkID,
        waehlerverzeichnisNummer: waehlerverzeichnisNummer,
        vermerke: [],
        wahlID: wahlID,
        eingenommeneWahlscheine: new Map(),
      };
      expect(result).toStrictEqual(expectedResult);
    });
  });
});
