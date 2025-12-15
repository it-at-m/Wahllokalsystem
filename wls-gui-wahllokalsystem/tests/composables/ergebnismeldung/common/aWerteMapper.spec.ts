import type { AWerte } from "@/types/ergebnisermittlung/AWerte.ts";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { useAWerteTestDataFactory } from "@tests/utils/ergebnismeldung/common/aWerteTestDataFactory.ts";
import { beforeEach, describe, expect, it } from "vitest";

import { useAWerteMapper } from "@/composables/ergebnismeldung/common/aWerteMapper.ts";

const { prepareAWerteDTO } = useAWerteTestDataFactory();
const { generateRandomNumber } = useCommonTestDataFactory();

describe("aWerteMapper.ts", () => {
  let unitUnderTest: ReturnType<typeof useAWerteMapper>;

  beforeEach(() => {
    unitUnderTest = useAWerteMapper();
  });

  describe("toModel", () => {
    it("should_returnModel_when_dtoWithA2DtoIsGiven", () => {
      const a2 = generateRandomNumber(4);
      const aWerteDto = prepareAWerteDTO().a2(a2).build();

      const result = unitUnderTest.toModel(aWerteDto);

      const expectedResult: AWerte = {
        bezirkUndWahlID: {
          wahlID: aWerteDto.bezirkUndWahlID.wahlID,
          wahlbezirkID: aWerteDto.bezirkUndWahlID.wahlbezirkID,
        },
        a1: aWerteDto.a1,
        a2: a2,
      };
      expect(result).toStrictEqual(expectedResult);
    });

    it("should_returnModelWithA2Null_when_dtoWithA2UndefinedIsGiven", () => {
      const aWerteDto = prepareAWerteDTO().a2(undefined).build();

      const result = unitUnderTest.toModel(aWerteDto);

      const expectedResult: AWerte = {
        bezirkUndWahlID: {
          wahlID: aWerteDto.bezirkUndWahlID.wahlID,
          wahlbezirkID: aWerteDto.bezirkUndWahlID.wahlbezirkID,
        },
        a1: aWerteDto.a1,
        a2: null,
      };
      expect(result).toStrictEqual(expectedResult);
    });
  });
});
