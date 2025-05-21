import type { KonfigurationDTO } from "@/api/wls-clients/generated-infomanagement-api";
import type { Konfigurationsparameter } from "@/types/infomanagement/Konfigurationsparameter.ts";

import { useCommonTestDataFactory } from "@tests/utils/common/CommonTestDataFactory.ts";

const { generateRandomString } = useCommonTestDataFactory();

export function useKonfigurationsparameterTestDataFactory() {
  function createKonfigurationDtoList(length: number): KonfigurationDTO[] {
    const dtoList: KonfigurationDTO[] = [];
    for (let i = 0; i < length; i++) {
      dtoList.push({
        beschreibung: generateRandomString(10),
        schluessel: generateRandomString(10),
        standardwert: generateRandomString(10),
        wert: generateRandomString(10),
      });
    }
    return dtoList;
  }

  function mapDtosToModel(dtos: KonfigurationDTO[]): Konfigurationsparameter[] {
    const model: Konfigurationsparameter[] = [];
    for (const dto of dtos) {
      model.push({
        beschreibung: dto.beschreibung,
        schluessel: dto.schluessel,
        standardwert: dto.standardwert,
        wert: dto.wert,
      });
    }
    return model;
  }

  return { createKonfigurationDtoList, mapDtosToModel };
}
