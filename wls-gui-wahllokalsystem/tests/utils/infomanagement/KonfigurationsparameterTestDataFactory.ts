import type { KonfigurationDTO } from "@/api/wls-clients/generated-infomanagement-api";
import type { Konfigurationsparameter } from "@/types/infomanagement/Konfigurationsparameter.ts";
import type { Builder } from "@tests/utils/Builder.ts";

import { proxyBuilder } from "@tests/utils/Builder.ts";
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

  function createKonfigurationsparameter(): Konfigurationsparameter {
    return {
      schluessel: generateRandomString(10),
      wert: generateRandomString(10),
    };
  }

  function createKonfigurationsparameterList(
    length: number
  ): Konfigurationsparameter[] {
    const modelList: Konfigurationsparameter[] = [];
    for (let i = 0; i < length; i++) {
      modelList.push(createKonfigurationsparameter());
    }
    return modelList;
  }

  function mapDtosToModel(dtos: KonfigurationDTO[]): Konfigurationsparameter[] {
    const model: Konfigurationsparameter[] = [];
    for (const dto of dtos) {
      model.push({
        schluessel: dto.schluessel ?? "",
        wert: dto.wert ?? "",
      });
    }
    return model;
  }

  function prepareKonfigurationsparameter(): Builder<Konfigurationsparameter> {
    return proxyBuilder<Konfigurationsparameter>(
      createKonfigurationsparameter()
    );
  }

  return {
    createKonfigurationDtoList,
    createKonfigurationsparameter,
    createKonfigurationsparameterList,
    mapDtosToModel,
    prepareKonfigurationsparameter,
  };
}
