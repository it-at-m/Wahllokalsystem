/* eslint-disable  @typescript-eslint/no-non-null-assertion */
/* when we access a null field the test will fail */
import type { WahltagDTO } from "@/api/wls-clients/generated-admin-api";
import type { Wahltag } from "@/types/wahltag/Wahltag.ts";

import { useWahltagTestDataFactory } from "@tests/types/wahltag/WahltagTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useWahltagMapper } from "@/composables/wahltag/wahltagMapper.ts";

const { prepareWahltagDtoComplete } = useWahltagTestDataFactory();

const unitUnderTest = useWahltagMapper();

describe("wahltagMapper.ts", () => {
  describe("mapGroupedWahltagDtosToWahltage", () => {
    it("should_returnArrayOfWahltagType_when_mapWithDtosIsGiven", () => {
      const wahltag1Key = "2025-04-04";
      const wahltag2Key = "2025-01-04";

      const mapWithDtosToMap = new Map<string, WahltagDTO[]>([
        [
          wahltag1Key,
          [
            prepareWahltagDtoComplete().build(),
            prepareWahltagDtoComplete().build(),
          ],
        ],
        [
          wahltag2Key,
          [
            prepareWahltagDtoComplete().build(),
            prepareWahltagDtoComplete().build(),
            prepareWahltagDtoComplete().build(),
          ],
        ],
      ]);

      const result =
        unitUnderTest.mapGroupedWahltagDtosToWahltage(mapWithDtosToMap);

      const { expectedWahltag1, expectedWahltag2 } = getExpectedWahltage(
        wahltag1Key,
        wahltag2Key,
        mapWithDtosToMap
      );

      expect(result.length).toStrictEqual(2);
      expect(result[0]).toStrictEqual(expectedWahltag1);
      expect(result[1]).toStrictEqual(expectedWahltag2);
    });
  });
});

function getExpectedWahltage(
  wahltag1Key: string,
  wahltag2Key: string,
  mapWithDtosToMap: Map<string, WahltagDTO[]>
) {
  const expectedWahltag1: Wahltag = {
    wahltag: new Date(wahltag1Key),
    events: [
      {
        wahltagID: mapWithDtosToMap.get(wahltag1Key)![0]!.wahltagID,
        beschreibung: mapWithDtosToMap.get(wahltag1Key)![0]!.beschreibung,
        nummer: mapWithDtosToMap.get(wahltag1Key)![0]!.nummer,
      },
      {
        wahltagID: mapWithDtosToMap.get(wahltag1Key)![1]!.wahltagID,
        beschreibung: mapWithDtosToMap.get(wahltag1Key)![1]!.beschreibung,
        nummer: mapWithDtosToMap.get(wahltag1Key)![1]!.nummer,
      },
    ],
  };
  const expectedWahltag2: Wahltag = {
    wahltag: new Date(wahltag2Key),
    events: [
      {
        wahltagID: mapWithDtosToMap.get(wahltag2Key)![0]!.wahltagID,
        beschreibung: mapWithDtosToMap.get(wahltag2Key)![0]!.beschreibung,
        nummer: mapWithDtosToMap.get(wahltag2Key)![0]!.nummer,
      },
      {
        wahltagID: mapWithDtosToMap.get(wahltag2Key)![1]!.wahltagID,
        beschreibung: mapWithDtosToMap.get(wahltag2Key)![1]!.beschreibung,
        nummer: mapWithDtosToMap.get(wahltag2Key)![1]!.nummer,
      },
      {
        wahltagID: mapWithDtosToMap.get(wahltag2Key)![2]!.wahltagID,
        beschreibung: mapWithDtosToMap.get(wahltag2Key)![2]!.beschreibung,
        nummer: mapWithDtosToMap.get(wahltag2Key)![2]!.nummer,
      },
    ],
  };
  return { expectedWahltag1, expectedWahltag2 };
}
