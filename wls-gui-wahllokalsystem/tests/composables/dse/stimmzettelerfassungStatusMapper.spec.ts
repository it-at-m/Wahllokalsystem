import type { StimmzettelerfassungStatusDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { StimmzettelerfassungStatus } from "@/types/dse/StimmzettelerfassungStatus.ts";

import { useStimmzettelerfassungStatusTestDataFactory } from "@tests/utils/dse/StimmzettelerfassungStatusTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { StimmzettelerfassungStatusDTOStatusEnum } from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useStimmzettelerfassungStatusMapper } from "@/composables/dse/stimmzettelerfassungStatusMapper.ts";
import { StimmzettelerfassungStatusEnum } from "@/types/dse/StimmzettelerfassungStatusEnum.ts";

const {
  prepareStimmzettelerfassungStatusDTO,
  prepareStimmzettelerfassungStatus,
} = useStimmzettelerfassungStatusTestDataFactory();

describe("stimmzettelerfassungStatusMapper.ts", () => {
  const { dtoToModel, modelToDto } = useStimmzettelerfassungStatusMapper();

  const testdata = [
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
  ];

  describe("dtoToModel", () => {
    it.each(testdata)(
      "should_returnStimmzettelerfassungStatus_when_dtoIsGiven",
      (data) => {
        const dtoToMap = prepareStimmzettelerfassungStatusDTO()
          .status(data.dtoStatus)
          .build();

        const result = dtoToModel(dtoToMap);

        const expectedResult: StimmzettelerfassungStatus = {
          status: data.modelStatus,
        };
        expect(result).toStrictEqual(expectedResult);
      }
    );
  });

  describe("modelToDto", () => {
    it.each(testdata)(
      "should_returnStimmzettelerfassungStatusDto_when_modelIsGiven",
      (data) => {
        const modelToMap = prepareStimmzettelerfassungStatus()
          .status(data.modelStatus)
          .build();

        const result = modelToDto(modelToMap);

        const expectedResult: StimmzettelerfassungStatusDTO = {
          status: data.dtoStatus,
        };
        expect(result).toStrictEqual(expectedResult);
      }
    );
  });
});
