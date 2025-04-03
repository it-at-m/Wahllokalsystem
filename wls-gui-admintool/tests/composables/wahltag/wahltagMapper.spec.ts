/* eslint-disable  @typescript-eslint/no-non-null-assertion */
/* when we access a null field the test will fail */
import type { WahltagDTO } from "@/api/wls-clients/generated-admin-api";
import type { Wahltag } from "@/types/wahltag/Wahltag.ts";

import { useWahltagTestDataFactory } from "@tests/types/wahltag/WahltagTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useWahltagMapper } from "@/composables/wahltag/wahltagMapper.ts";

const { createWahltagDtoComplete } = useWahltagTestDataFactory();

const unitUnderTest = useWahltagMapper();

describe("wahltagMapper.ts", () => {
  describe("mapGroupedWahltagDtosToWahltage", () => {
    it("should_returnTyp_when_mapWithDtosAreGiven", () => {
      const wahltag1Key = "wahltag1";
      const wahltag2Key = "wahltag2";

      const mapWithDtosToMap = new Map<string, WahltagDTO[]>([
        [
          wahltag1Key,
          [
            createWahltagDtoComplete().build(),
            createWahltagDtoComplete().build(),
          ],
        ],
        [
          wahltag2Key,
          [
            createWahltagDtoComplete().build(),
            createWahltagDtoComplete().build(),
            createWahltagDtoComplete().build(),
          ],
        ],
      ]);

      const result =
        unitUnderTest.mapGroupedWahltagDtosToWahltage(mapWithDtosToMap);

      const expectedWahltag1: Wahltag = {
        wahltag: wahltag1Key,
        events: [
          {
            wahltagID: mapWithDtosToMap.get(wahltag1Key)![0].wahltagID,
            beschreibung: mapWithDtosToMap.get(wahltag1Key)![0].beschreibung,
            nummer: mapWithDtosToMap.get(wahltag1Key)![0].nummer,
          },
          {
            wahltagID: mapWithDtosToMap.get(wahltag1Key)![1].wahltagID,
            beschreibung: mapWithDtosToMap.get(wahltag1Key)![1].beschreibung,
            nummer: mapWithDtosToMap.get(wahltag1Key)![1].nummer,
          },
        ],
      };
      const expectedWahltag2: Wahltag = {
        wahltag: wahltag2Key,
        events: [
          {
            wahltagID: mapWithDtosToMap.get(wahltag2Key)![0].wahltagID,
            beschreibung: mapWithDtosToMap.get(wahltag2Key)![0].beschreibung,
            nummer: mapWithDtosToMap.get(wahltag2Key)![0].nummer,
          },
          {
            wahltagID: mapWithDtosToMap.get(wahltag2Key)![1].wahltagID,
            beschreibung: mapWithDtosToMap.get(wahltag2Key)![1].beschreibung,
            nummer: mapWithDtosToMap.get(wahltag2Key)![1].nummer,
          },
          {
            wahltagID: mapWithDtosToMap.get(wahltag2Key)![2].wahltagID,
            beschreibung: mapWithDtosToMap.get(wahltag2Key)![2].beschreibung,
            nummer: mapWithDtosToMap.get(wahltag2Key)![2].nummer,
          },
        ],
      };

      expect(result.length).toStrictEqual(2);
      expect(result[0]).toStrictEqual(expectedWahltag1);
      expect(result[1]).toStrictEqual(expectedWahltag2);
    });
  });
});
