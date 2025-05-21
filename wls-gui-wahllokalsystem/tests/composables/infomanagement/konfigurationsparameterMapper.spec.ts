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
        {
          beschreibung: dto[0].beschreibung,
          schluessel: dto[0].schluessel,
          standardwert: dto[0].standardwert,
          wert: dto[0].wert,
        },
        {
          beschreibung: dto[1].beschreibung,
          schluessel: dto[1].schluessel,
          standardwert: dto[1].standardwert,
          wert: dto[1].wert,
        },
        {
          beschreibung: dto[2].beschreibung,
          schluessel: dto[2].schluessel,
          standardwert: dto[2].standardwert,
          wert: dto[2].wert,
        },
      ];

      const result = toModel(dto);

      expect(result).toStrictEqual(expectedModel);
    });
  });
});
