import type { KonfigurationDTO } from "@/api/wls-clients/generated-infomanagement-api";
import type { Konfigurationsparameter } from "@/types/infomanagement/Konfigurationsparameter.ts";

export function useKonfigurationsparameterMapper() {
  function toModel(dtos: KonfigurationDTO[]): Konfigurationsparameter[] {
    const konfigurationsparameter: Konfigurationsparameter[] = [];

    for (const dto of dtos) {
      const model: Konfigurationsparameter = {
        schluessel: dto.schluessel ?? "",
        wert: dto.wert ?? "",
      };
      konfigurationsparameter.push(model);
    }
    return konfigurationsparameter;
  }

  return { toModel };
}
