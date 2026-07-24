import type { StimmzettelerfassungStatus } from "@/types/dse/stimmzettelerfassungStatus.ts";

import { useStimmzettelerfassungStatusTestDataFactory } from "@tests/utils/dse/StimmzettelerfassungStatusTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { StimmzettelerfassungStatusDTOStatusEnum } from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useStimmzettelerfassungStatusMapper } from "@/composables/dse/stimmzettelerfassungStatusMapper.ts";
import { StimmzettelerfassungStatusEnum } from "@/types/dse/StimmzettelerfassungStatusEnum.ts";

const { prepareStimmzettelerfassungStatusDTO } =
  useStimmzettelerfassungStatusTestDataFactory();

describe("stimmzettelerfassungStatusMapper.ts", () => {
  const { dtoToModel } = useStimmzettelerfassungStatusMapper();

  describe("dtoToModel", () => {
    it.each([
      {
        dtoStatus: StimmzettelerfassungStatusDTOStatusEnum.SteBearbeitung,
        modelStatus: StimmzettelerfassungStatusEnum.SteBearbeitung,
      },
      {
        dtoStatus: StimmzettelerfassungStatusDTOStatusEnum.SteAbgeschlossen,
        modelStatus: StimmzettelerfassungStatusEnum.SteAbgeschlossen,
      },
      {
        dtoStatus: StimmzettelerfassungStatusDTOStatusEnum.BeAbgeschlossen,
        modelStatus: StimmzettelerfassungStatusEnum.BeAbgeschlossen,
      },
    ])(
      "should_returnStimmzettelerfassungStatus_when_dtoIsGiven",
      (testdata) => {
        const dtoToMap = prepareStimmzettelerfassungStatusDTO()
          .status(testdata.dtoStatus)
          .build();

        const result = dtoToModel(dtoToMap);

        const expectedResult: StimmzettelerfassungStatus = {
          status: testdata.modelStatus,
        };
        expect(result).toStrictEqual(expectedResult);
      }
    );
  });
});
