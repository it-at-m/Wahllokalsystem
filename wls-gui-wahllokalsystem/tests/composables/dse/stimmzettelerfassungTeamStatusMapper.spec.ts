import type { StimmzettelerfassungTeamStatus } from "@/types/dse/StimmzettelerfassungTeamStatus.ts";

import { useStimmzettelerfassungTeamStatusTestDataFactory } from "@tests/utils/dse/StimmzettelerfassungTeamStatusTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { StimmzettelerfassungTeamStatusDTOStatusEnum } from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useStimmzettelerfassungTeamStatusMapper } from "@/composables/dse/stimmzettelerfassungTeamStatusMapper";
import { StimmzettelerfassungTeamStatusEnum } from "@/types/dse/StimmzettelerfassungTeamStatusEnum.ts";

const { prepareStimmzettelerfassungTeamStatusDTO } =
  useStimmzettelerfassungTeamStatusTestDataFactory();

describe("stimmzettelerfassungTeamStatusMapper.ts", () => {
  const { dtoToModel } = useStimmzettelerfassungTeamStatusMapper();

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
});
