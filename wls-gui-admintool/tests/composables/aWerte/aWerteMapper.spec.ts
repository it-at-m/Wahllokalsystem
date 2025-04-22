import type { AWerteInitProgress } from "@/types/aWerte/AWerteInitProgress.ts";

import { useAWerteTestDataFactory } from "@tests/types/aWerte/AWerteTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useAWerteMapper } from "@/composables/aWerte/aWerteMapper.ts";

const { createAsyncProgressDTOComplete } = useAWerteTestDataFactory();

const unitUnderTest = useAWerteMapper();

describe("aWerteMapper.ts", () => {
  describe("useAWerteMapper", () => {
    describe("asyncProgressDtoToAWerteInitProgress", () => {
      it("should_mapToType_when_dtoIsGiven", () => {
        const dtoToMap = createAsyncProgressDTOComplete();

        const result =
          unitUnderTest.asyncProgressDtoToAWerteInitProgress(dtoToMap);

        const expectedResult: AWerteInitProgress = {
          active: dtoToMap.aWerteLoadingActive,
          lastFinishTime: dtoToMap.lastFinishTime,
          lastStartTime: dtoToMap.lastStartTime,
          total: dtoToMap.aWerteTotal,
          next: dtoToMap.aWerteNext,
          finished: dtoToMap.aWerteFinished,
        };

        expect(result).toStrictEqual(expectedResult);
      });
    });
  });
});
