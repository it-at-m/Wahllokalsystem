import type { BasisdatenInitProgress } from "@/types/basisdaten/BasisdatenInitProgress.ts";

import useBasisdatenTestDataFactory from "@tests/types/basisdaten/BasisdatenTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useBasisdatenMapper } from "@/composables/basisdaten/basisdatenMapper.ts";

const { createCompleteAsyncProgressDTO } = useBasisdatenTestDataFactory();

const unitUnderTest = useBasisdatenMapper();

describe("basisdatenMapper.ts", () => {
  describe("useBasisdatenMapper", () => {
    describe("mapAsyncProgressDtoToBasisdatenInitProgress", () => {
      it("should_mapToType_when_dtoIsGiven", () => {
        const dtoToMap = createCompleteAsyncProgressDTO();

        const result =
          unitUnderTest.mapAsyncProgressDtoToBasisdatenInitProgress(dtoToMap);

        const expectedResult: BasisdatenInitProgress = {
          lastStartTime: dtoToMap.lastStartTime,
          lastFinishTime: dtoToMap.lastFinishTime,
          wahlvorschlaege: {
            active: dtoToMap.wahlvorschlaegeLoadingActive,
            total: dtoToMap.wahlvorschlaegeTotal,
            next: dtoToMap.wahlvorschlaegeNext,
            finished: dtoToMap.wahlvorschlageFinished,
          },
          referendumvorlagen: {
            active: dtoToMap.referendumLoadingActive,
            next: dtoToMap.referendumVorlagenNext,
            finished: dtoToMap.referendumVorlagenFinished,
            total: dtoToMap.referendumVorlagenTotal,
          },
        };

        expect(result).toStrictEqual(expectedResult);
      });
    });
  });
});
