import type { WahltagDTO } from "@/api/wls-clients/generated-admin-api";

import { useWahltagTestDataFactory } from "@tests/types/wahltag/WahltagTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useWahltagDtoUtils } from "@/composables/wahltag/wahltagDtoUtils.ts";

const { prepareWahltagDtoComplete } = useWahltagTestDataFactory();

const unitUnderTest = useWahltagDtoUtils();

describe("WahltagDtoUtils.ts", () => {
  describe("groupWahltagDtosByWahltag", () => {
    it("should_returnAMapWithWahltagAsKey_when_dtosWithEqualWahltagAreGiven", () => {
      const wahltag1 = "wahltag1";
      const wahltag2 = "wahltag2";
      const wahltag3 = "wahltag3";

      const dto1 = prepareWahltagDtoComplete().wahltag(wahltag1).build();
      const dto2 = prepareWahltagDtoComplete().wahltag(wahltag2).build();
      const dto3 = prepareWahltagDtoComplete().wahltag(wahltag1).build();
      const dto4 = prepareWahltagDtoComplete().wahltag(wahltag1).build();
      const dto5 = prepareWahltagDtoComplete().wahltag(wahltag3).build();
      const dto6 = prepareWahltagDtoComplete().wahltag(wahltag2).build();
      const dtos = [dto1, dto2, dto3, dto4, dto5, dto6];

      const result = unitUnderTest.groupWahltagDtosByWahltag(dtos);

      expect(result.size).toStrictEqual(3);
      assertThatEntryOnlyContains(result.get(wahltag1), dto1, dto3, dto4);
      assertThatEntryOnlyContains(result.get(wahltag2), dto2, dto6);
      assertThatEntryOnlyContains(result.get(wahltag3), dto5);
    });

    it("should_returnEmptyMap_when_dtoArrayIsEmpty", () => {
      expect(unitUnderTest.groupWahltagDtosByWahltag([])).toStrictEqual(
        new Map<string, WahltagDTO>([])
      );
    });

    function assertThatEntryOnlyContains(
      entry: WahltagDTO[] | undefined,
      ...valuesToContain: WahltagDTO[]
    ) {
      expect(entry?.length).to.equal(valuesToContain.length);
      valuesToContain.forEach((expectedWahltag) =>
        expect(entry).toContain(expectedWahltag)
      );
    }
  });
});
