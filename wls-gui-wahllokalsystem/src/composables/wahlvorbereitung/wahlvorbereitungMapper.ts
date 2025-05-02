import type {
  UrnenwahlSchliessungsUhrzeitDTO,
  UrnenwahlSchliessungsUhrzeitWriteDTO,
} from "@/api/wls-clients/generated-wahlvorbereitung-api";
import type { UrnenwahlSchliessungsuhrzeit } from "@/types/wahlvorbereitung/UrnenwahlSchliessungsuhrzeit.ts";

import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";

const { applyLocalTimezoneOffset } = useDateTimeFormatter();

export function useWahlvorbereitungMapper() {
  function toModel(
    schliessungsuhrzeitDTO: UrnenwahlSchliessungsUhrzeitDTO
  ): UrnenwahlSchliessungsuhrzeit {
    return { schliessungsuhrzeit: schliessungsuhrzeitDTO.schliessungsuhrzeit };
  }

  function toDTO(
    schliessungsuhrzeit: string
  ): UrnenwahlSchliessungsUhrzeitWriteDTO {
    const mappedUhrzeit = applyLocalTimezoneOffset(schliessungsuhrzeit);
    return {
      schliessungsuhrzeit: mappedUhrzeit.toISOString(),
    };
  }

  return { toModel, toDTO };
}
