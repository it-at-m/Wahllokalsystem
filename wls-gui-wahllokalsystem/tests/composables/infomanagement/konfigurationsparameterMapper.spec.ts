import type { KonfigurationDTO } from "@/api/wls-clients/generated-infomanagement-api";
import type { Konfigurationsparameter } from "@/types/infomanagement/Konfigurationsparameter.ts";

import { useKonfigurationsparameterTestDataFactory } from "@tests/utils/infomanagement/KonfigurationsparameterTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useKonfigurationsparameterMapper } from "@/composables/infomanagement/konfigurationsparameterMapper.ts";

describe("konfigurationsparameterMapper.ts", () => {
  const { toModel } = useKonfigurationsparameterMapper();
  const { createKonfigurationDtoList } =
    useKonfigurationsparameterTestDataFactory();

  describe("toModel", () => {
    it("should_returnEmptyArray_when_givenEmptyArray", () => {
      expect(toModel([])).toStrictEqual([]);
    });

    it("should_returnModel_when_givenDto", () => {
      const dto: KonfigurationDTO[] = createKonfigurationDtoList(3);

      const expectedModel: Konfigurationsparameter[] = [
        /* eslint-disable @typescript-eslint/no-non-null-assertion -- disabling because only valid dto is given as input */
        { schluessel: dto[0]!.schluessel, wert: dto[0]!.wert },
        { schluessel: dto[1]!.schluessel, wert: dto[1]!.wert },
        { schluessel: dto[2]!.schluessel, wert: dto[2]!.wert },
        /* eslint-enable @typescript-eslint/no-non-null-assertion */
      ];

      const result = toModel(dto);

      expect(result).toStrictEqual(expectedModel);
    });

    it("should_returnModelWithDefaultValues_when_givenDtoWithMissingProperties", () => {
      const dto: KonfigurationDTO[] = [{ schluessel: "123" }];
      const expectedModel: Konfigurationsparameter[] = [
        { schluessel: "123", wert: "" },
      ];

      const result = toModel(dto);

      expect(result).toStrictEqual(expectedModel);
    });
  });
});
