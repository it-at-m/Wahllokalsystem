import type { BedenklicherStimmzettelDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { BedenklicherStimmzettel } from "@/types/ergebnismeldung/MBW/BedenklicherStimmzettel.ts";

import { useBedenklicherStimmzettelTestDataFactory } from "@tests/utils/ergebnismeldung/MBW/bedenklicherStimmzettelTestDataFactory.ts";
import { beforeEach, describe, expect, it } from "vitest";

import {
  BedenklicherStimmzettelDTOSupplementsEnum,
  BedenklicherStimmzettelDTOValidityEnum,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useBedenklicherStimmzettelMapper } from "@/composables/ergebnismeldung/MBW/bedenklicherStimmzettelMapper.ts";
import { SupplementEnum } from "@/types/ergebnismeldung/MBW/SupplementEnum.ts";
import { ValidityEnum } from "@/types/ergebnismeldung/MBW/ValidityEnum.ts";

const { prepareBedenklicherStimmzettelDTO, prepareBedenklicherStimmzettel } =
  useBedenklicherStimmzettelTestDataFactory();

describe("bedenklicherStimmzettelMapper.ts", () => {
  let unitUnderTest: ReturnType<typeof useBedenklicherStimmzettelMapper>;

  beforeEach(() => {
    unitUnderTest = useBedenklicherStimmzettelMapper();
  });

  describe("toModel", () => {
    it("should_createModel_when_dtoIsGiven", () => {
      const dtoToMap = prepareBedenklicherStimmzettelDTO()
        .supplements([
          BedenklicherStimmzettelDTOSupplementsEnum.SingleKandidatVotes,
          BedenklicherStimmzettelDTOSupplementsEnum.Listenkreuze,
        ])
        .validity(BedenklicherStimmzettelDTOValidityEnum.PartialValid)
        .build();

      const result = unitUnderTest.toModel(dtoToMap);

      const expectedResult: BedenklicherStimmzettel = {
        orderIndex: dtoToMap.orderIndex,
        supplements: [
          SupplementEnum.TOO_MANY_SINGLE_KANDIDAT_VOTES,
          SupplementEnum.TOO_MANY_LISTENKREUZE,
        ],
        validity: ValidityEnum.PARTIAL_VALID,
      };
      expect(result).toStrictEqual(expectedResult);
    });

    const supplementMappingTestcases = [
      {
        dtoValue: BedenklicherStimmzettelDTOSupplementsEnum.SingleKandidatVotes,
        expectedModelValue: SupplementEnum.TOO_MANY_SINGLE_KANDIDAT_VOTES,
      },
      {
        dtoValue: BedenklicherStimmzettelDTOSupplementsEnum.Listenkreuze,
        expectedModelValue: SupplementEnum.TOO_MANY_LISTENKREUZE,
      },
    ];
    it.each(supplementMappingTestcases)(
      "should_mapToModelSupplementValue_when_dtoSupplementValueIsGiven'$dtoValue'",
      (testcaseArguments) => {
        const dtoToMap = prepareBedenklicherStimmzettelDTO()
          .supplements([testcaseArguments.dtoValue])
          .validity(BedenklicherStimmzettelDTOValidityEnum.PartialValid)
          .build();

        const result = unitUnderTest.toModel(dtoToMap);

        expect(result.supplements).toStrictEqual([
          testcaseArguments.expectedModelValue,
        ]);
      }
    );

    it("should_coverAllDTOSupplementValues_when_supplementTestcasesAreDefined", () => {
      Object.values(BedenklicherStimmzettelDTOSupplementsEnum).forEach(
        (dtoEnumValue) => {
          expect(
            supplementMappingTestcases.some(
              (testcase) => testcase.dtoValue === dtoEnumValue
            )
          ).toStrictEqual(true);
        }
      );
    });

    const validityTestcases = [
      {
        dtoValue: BedenklicherStimmzettelDTOValidityEnum.Valid,
        expectedModelValue: ValidityEnum.VALID,
      },
      {
        dtoValue: BedenklicherStimmzettelDTOValidityEnum.PartialValid,
        expectedModelValue: ValidityEnum.PARTIAL_VALID,
      },
      {
        dtoValue: BedenklicherStimmzettelDTOValidityEnum.Invalid,
        expectedModelValue: ValidityEnum.INVALID,
      },
    ];

    it.each(validityTestcases)(
      "should_mapToModelValidityValue_when_dtoValidityValueIsGiven'$dtoValue'",
      (testcaseArguments) => {
        const dtoToMap = prepareBedenklicherStimmzettelDTO()
          .validity(testcaseArguments.dtoValue)
          .build();

        const result = unitUnderTest.toModel(dtoToMap);

        expect(result.validity).toStrictEqual(
          testcaseArguments.expectedModelValue
        );
      }
    );

    it("should_coverAllDTOSValidityValues_when_validityTestcasesAreDefined", () => {
      Object.values(BedenklicherStimmzettelDTOValidityEnum).forEach(
        (dtoEnumValue) => {
          expect(
            validityTestcases.some(
              (testcase) => testcase.dtoValue === dtoEnumValue
            )
          ).toStrictEqual(true);
        }
      );
    });
  });

  describe("toDTO", () => {
    it("should_createModel_when_dtoIsGiven", () => {
      const modelToMap = prepareBedenklicherStimmzettel()
        .supplements([
          SupplementEnum.TOO_MANY_LISTENKREUZE,
          SupplementEnum.TOO_MANY_SINGLE_KANDIDAT_VOTES,
        ])
        .validity(ValidityEnum.VALID)
        .build();

      const result = unitUnderTest.toDTO(modelToMap);

      const expectedResult: BedenklicherStimmzettelDTO = {
        orderIndex: modelToMap.orderIndex,
        supplements: [
          BedenklicherStimmzettelDTOSupplementsEnum.Listenkreuze,
          BedenklicherStimmzettelDTOSupplementsEnum.SingleKandidatVotes,
        ],
        validity: BedenklicherStimmzettelDTOValidityEnum.Valid,
      };
      expect(result).toStrictEqual(expectedResult);
    });

    const supplementMappingTestcases = [
      {
        modelValue: SupplementEnum.TOO_MANY_SINGLE_KANDIDAT_VOTES,
        expectedDTOValue:
          BedenklicherStimmzettelDTOSupplementsEnum.SingleKandidatVotes,
      },
      {
        modelValue: SupplementEnum.TOO_MANY_LISTENKREUZE,
        expectedDTOValue:
          BedenklicherStimmzettelDTOSupplementsEnum.Listenkreuze,
      },
    ];
    it.each(supplementMappingTestcases)(
      "should_mapToModelSupplementValue_when_dtoSupplementValueIsGiven'$dtoValue'",
      (testcaseArguments) => {
        const dtoToMap = prepareBedenklicherStimmzettel()
          .supplements([testcaseArguments.modelValue])
          .validity(BedenklicherStimmzettelDTOValidityEnum.PartialValid)
          .build();

        const result = unitUnderTest.toDTO(dtoToMap);

        expect(result.supplements).toStrictEqual([
          testcaseArguments.expectedDTOValue,
        ]);
      }
    );

    it("should_coverAllDTOSupplementValues_when_supplementTestcasesAreDefined", () => {
      Object.values(SupplementEnum).forEach((modelEnumValue) => {
        expect(
          supplementMappingTestcases.some(
            (testcase) => testcase.modelValue === modelEnumValue
          )
        ).toStrictEqual(true);
      });
    });

    const validityTestcases = [
      {
        modelValue: ValidityEnum.VALID,
        expectedDTOValue: BedenklicherStimmzettelDTOValidityEnum.Valid,
      },
      {
        modelValue: ValidityEnum.PARTIAL_VALID,
        expectedDTOValue: BedenklicherStimmzettelDTOValidityEnum.PartialValid,
      },
      {
        modelValue: ValidityEnum.INVALID,
        expectedDTOValue: BedenklicherStimmzettelDTOValidityEnum.Invalid,
      },
    ];

    it.each(validityTestcases)(
      "should_mapToModelValidityValue_when_dtoValidityValueIsGiven'$dtoValue'",
      (testcaseArguments) => {
        const modelToMap = prepareBedenklicherStimmzettel()
          .validity(testcaseArguments.modelValue)
          .build();

        const result = unitUnderTest.toDTO(modelToMap);

        expect(result.validity).toStrictEqual(
          testcaseArguments.expectedDTOValue
        );
      }
    );

    it("should_coverAllDTOSValidityValues_when_validityTestcasesAreDefined", () => {
      Object.values(ValidityEnum).forEach((modelEnumValue) => {
        expect(
          validityTestcases.some(
            (testcase) => testcase.modelValue === modelEnumValue
          )
        ).toStrictEqual(true);
      });
    });
  });
});
