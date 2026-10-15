import type { StimmzettelerfassungTeamStatusEntryDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { StimmzettelerfassungTeamStatus } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatus.ts";

import { useStimmzettelerfassungTeamStatusTestDataFactory } from "@tests/utils/dse/StimmzettelerfassungTeamStatusTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { StimmzettelerfassungTeamStatusDTOStatusEnum } from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useStimmzettelerfassungTeamStatusMapper } from "@/composables/dse/stimmzettelerfassungTeamStatus/stimmzettelerfassungTeamStatusMapper.ts";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/stimmzettelerfassungTeamStatus/StimmzettelerfassungTeamStatusEnum.ts";

const {
  createStimmzettelerfassungTeamStatusModel,
  prepareStimmzettelerfassungTeamStatusDTO,
} = useStimmzettelerfassungTeamStatusTestDataFactory();

describe("stimmzettelerfassungTeamStatusMapper.ts", () => {
  const {
    dtoToModel,
    modelToDto,
    dtoEntryToModelEntry,
    statusModelEnumToDisplayString,
    statusConfig,
  } = useStimmzettelerfassungTeamStatusMapper();

  describe("statusConfig", () => {
    it.each(Object.values(StimmzettelerfassungTeamStatusEnum))(
      "should_returnIconAndColor_when_enumIs%s",
      (enumValue) => {
        const entry =
          statusConfig[enumValue as unknown as keyof typeof statusConfig];
        expect(entry).toBeDefined();
        expect(typeof entry.icon).toBe("string");
        expect(entry.icon.length).toBeGreaterThan(0);
        expect(typeof entry.color).toBe("string");
        expect(entry.color.length).toBeGreaterThan(0);
      }
    );
  });

  describe("dtoToModel", () => {
    it.each([
      {
        dtoStatus: StimmzettelerfassungTeamStatusDTOStatusEnum.Registriert,
        modelStatus: StimmzettelerfassungTeamStatusEnum.REGISTRIERT,
      },
      {
        dtoStatus: StimmzettelerfassungTeamStatusDTOStatusEnum.InBearbeitung,
        modelStatus: StimmzettelerfassungTeamStatusEnum.IN_BEARBEITUNG,
      },
      {
        dtoStatus: StimmzettelerfassungTeamStatusDTOStatusEnum.Unterbrochen,
        modelStatus: StimmzettelerfassungTeamStatusEnum.UNTERBROCHEN,
      },
      {
        dtoStatus: StimmzettelerfassungTeamStatusDTOStatusEnum.Abgeschlossen,
        modelStatus: StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN,
      },
    ])(
      "should_returnStimmzettelerfassungTeamStatus_when_dtoIsGiven",
      (testdata) => {
        const dtoToMap = prepareStimmzettelerfassungTeamStatusDTO()
          .status(testdata.dtoStatus)
          .build();

        const result = dtoToModel(dtoToMap);

        const expectedResult: StimmzettelerfassungTeamStatus = {
          status: testdata.modelStatus,
        };
        expect(result).toStrictEqual(expectedResult);
      }
    );
  });

  describe("modelToDto", () => {
    it.each([
      {
        modelStatus: StimmzettelerfassungTeamStatusEnum.REGISTRIERT,
        dtoStatus: StimmzettelerfassungTeamStatusDTOStatusEnum.Registriert,
      },
      {
        modelStatus: StimmzettelerfassungTeamStatusEnum.IN_BEARBEITUNG,
        dtoStatus: StimmzettelerfassungTeamStatusDTOStatusEnum.InBearbeitung,
      },
      {
        modelStatus: StimmzettelerfassungTeamStatusEnum.UNTERBROCHEN,
        dtoStatus: StimmzettelerfassungTeamStatusDTOStatusEnum.Unterbrochen,
      },
      {
        modelStatus: StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN,
        dtoStatus: StimmzettelerfassungTeamStatusDTOStatusEnum.Abgeschlossen,
      },
    ])(
      "should_returnStimmzettelerfassungTeamStatusDTOStatusEnum_when_modelIsGiven",
      (testdata) => {
        const model = createStimmzettelerfassungTeamStatusModel(
          testdata.modelStatus
        );

        const result = modelToDto(model);

        const expectedResult: StimmzettelerfassungTeamStatus = {
          status: testdata.dtoStatus,
        };
        expect(result).toStrictEqual(expectedResult);
      }
    );
  });

  describe("dtoEntryToModelEntry", () => {
    it.each([
      {
        dtoStatus: StimmzettelerfassungTeamStatusDTOStatusEnum.Registriert,
        modelStatus: StimmzettelerfassungTeamStatusEnum.REGISTRIERT,
      },
      {
        dtoStatus: StimmzettelerfassungTeamStatusDTOStatusEnum.InBearbeitung,
        modelStatus: StimmzettelerfassungTeamStatusEnum.IN_BEARBEITUNG,
      },
      {
        dtoStatus: StimmzettelerfassungTeamStatusDTOStatusEnum.Unterbrochen,
        modelStatus: StimmzettelerfassungTeamStatusEnum.UNTERBROCHEN,
      },
      {
        dtoStatus: StimmzettelerfassungTeamStatusDTOStatusEnum.Abgeschlossen,
        modelStatus: StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN,
      },
    ])(
      "should_returnStimmzettelerfassungTeamStatusEntry_when_dtoEntryIsGiven",
      (testdata) => {
        const dtoEntry = {
          teamID: "team1",
          status: testdata.dtoStatus,
        } as StimmzettelerfassungTeamStatusEntryDTO;

        const result = dtoEntryToModelEntry(dtoEntry);

        const expectedResult = {
          teamID: "team1",
          status: testdata.modelStatus,
        };
        expect(result).toStrictEqual(expectedResult);
      }
    );
  });

  describe("statusModelEnumToDisplayString", () => {
    it.each([
      {
        enumValue: StimmzettelerfassungTeamStatusEnum.ABGESCHLOSSEN,
        expected: "abgeschlossen",
      },
      {
        enumValue: StimmzettelerfassungTeamStatusEnum.IN_BEARBEITUNG,
        expected: "in Bearbeitung",
      },
      {
        enumValue: StimmzettelerfassungTeamStatusEnum.REGISTRIERT,
        expected: "registriert",
      },
      {
        enumValue: StimmzettelerfassungTeamStatusEnum.UNTERBROCHEN,
        expected: "unterbrochen",
      },
      { enumValue: null, expected: "" },
    ])(
      "should_return_'$expected'_when_enumIs_'$enumValue'",
      ({ enumValue, expected }) => {
        const result = statusModelEnumToDisplayString(enumValue);
        expect(result).toBe(expected);
      }
    );
  });
});
