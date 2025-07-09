import type { WaehlerverzeichnisWriteDTO } from "@/api/wls-clients/generated-wahlvorbereitung-api";
import type { PflegeWaehlerverzeichnis } from "@/types/wahlbezirk/PflegeWaehlerverzeichnis.ts";

import { usePflegeWaehlerverzeichnisTestDataFactory } from "@tests/utils/wahlvorbereitung/PflegeWaehlerverzeichnisTestDataFactory.ts";
import { beforeEach, describe, expect, it } from "vitest";

import { useWaehlerverzeichnisMapper } from "@/composables/wahlvorbereitung/waehlerverzeichnisMapper.ts";

const { createPflegeWaehlerverzeichnis, createWaehlverzeichnisDTO } =
  usePflegeWaehlerverzeichnisTestDataFactory();

describe("waehlverzeichnisMapper.ts", () => {
  let unitUnderTest: ReturnType<typeof useWaehlerverzeichnisMapper>;

  beforeEach(() => {
    unitUnderTest = useWaehlerverzeichnisMapper();
  });

  describe("toWaehlerverzeichnisWriteDTO", () => {
    it("should_createWaehlerverzeichnisWriteDTO_when_pflegeWaehlerverzeichnisIsGiven", () => {
      const objectToMap = createPflegeWaehlerverzeichnis();

      const result = unitUnderTest.toWaehlerverzeichnisWriteDTO(objectToMap);

      const expectedResult: WaehlerverzeichnisWriteDTO = {
        mitteilungUeberUngueltigeWahlscheineErhalten:
          objectToMap.mitteilungUeberUngueltigeWahlscheineErhalten,
        nachtraeglicheBerichtigung: objectToMap.nachtraeglicheBerichtigung,
        berichtigungVorBeginnDerAbstimmung:
          !objectToMap.waehlerverzeichnisUnchanged,
        verzeichnisLagVor: objectToMap.waehlerverzeichnisUnchanged,
      };
      expect(result).toStrictEqual(expectedResult);
    });
  });

  describe("toPflegeWaehlerverzeichnis", () => {
    it("should_createPflegeWaehlerverzeichnis_when_waehlverzeichnisDTOIsGiven", () => {
      const dtoToMap = createWaehlverzeichnisDTO();

      const result = unitUnderTest.toPflegeWaehlerverzeichnis(dtoToMap);

      const expectedResult: PflegeWaehlerverzeichnis = {
        waehlerverzeichnisUnchanged: dtoToMap.verzeichnisLagVor === true,
        nachtraeglicheBerichtigung:
          dtoToMap.nachtraeglicheBerichtigung === true,
        mitteilungUeberUngueltigeWahlscheineErhalten:
          dtoToMap.mitteilungUeberUngueltigeWahlscheineErhalten === true,
      };
      expect(result).toStrictEqual(expectedResult);
    });
  });
});
