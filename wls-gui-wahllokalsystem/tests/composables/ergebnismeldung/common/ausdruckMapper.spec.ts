import type { AusdruckWriteDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";
import { beforeEach, describe, expect, it } from "vitest";

import { PostAusdruckMeldungsartEnum } from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useAusdruckMapper } from "@/composables/ergebnismeldung/common/ausdruckMapper.ts";
import { MeldungsArtEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";

const { generateRandomString } = useCommonTestDataFactory();

describe("ausdruckMapper.ts", () => {
  let unitUnderTest: ReturnType<typeof useAusdruckMapper>;

  beforeEach(() => {
    unitUnderTest = useAusdruckMapper();
  });

  describe("meldungsartEnumToDto", () => {
    const testCases = [
      {
        expectedResult: PostAusdruckMeldungsartEnum.V3,
        input: MeldungsArtEnum.Schnellmeldung,
      },
      {
        expectedResult: PostAusdruckMeldungsartEnum.V1,
        input: MeldungsArtEnum.Niederschrift,
      },
    ];

    it.each(testCases)(
      "should_return'$expectedResult'_when_'$input'IsGiven",
      (testcaseArguments) => {
        const result = unitUnderTest.meldungsartEnumToDto(
          testcaseArguments.input
        );
        expect(result).toStrictEqual(testcaseArguments.expectedResult);
      }
    );

    it("should_fail_when_testcasesDontCoverAllPossibleInputs", () => {
      Object.values(MeldungsArtEnum).forEach((meldungsart) =>
        expect(
          testCases.some((testcase) => testcase.input === meldungsart),
          `${meldungsart} not covered by testcases`
        )
      );
    });
  });

  describe("toAusdruckWriteDTO", () => {
    it("should_returnAusdruckWriteDTO_when_contentIsGiven", () => {
      const content = generateRandomString(100);
      const result = unitUnderTest.toAusdruckWriteDTO(content);

      const expectedResult: AusdruckWriteDTO = {
        content,
      };
      expect(result).toStrictEqual(expectedResult);
    });
  });
});
