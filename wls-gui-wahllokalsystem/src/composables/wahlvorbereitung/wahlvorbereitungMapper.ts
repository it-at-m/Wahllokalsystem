import type {
  EroeffnungsUhrzeitWriteDTO,
  UrnenwahlSchliessungsUhrzeitDTO,
  UrnenwahlSchliessungsUhrzeitWriteDTO,
} from "@/api/wls-clients/generated-wahlvorbereitung-api";
import type { UrnenwahlSchliessungsuhrzeit } from "@/types/wahlvorbereitung/UrnenwahlSchliessungsuhrzeit.ts";

import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";

const { applyLocalTimezoneOffset } = useDateTimeFormatter();

export function useWahlvorbereitungMapper() {
  function toEroeffnungsuhrzeitWriteDTO(
    eroeffnungsuhrzeit: Date
  ): EroeffnungsUhrzeitWriteDTO {
    return {
      eroeffnungsuhrzeit:
        applyLocalTimezoneOffset(eroeffnungsuhrzeit).toISOString(),
    };
  }

  function toUrnenwahlSchliessungsuhrzeitModel(
    schliessungsuhrzeitDTO: UrnenwahlSchliessungsUhrzeitDTO
  ): UrnenwahlSchliessungsuhrzeit {
    return { schliessungsuhrzeit: schliessungsuhrzeitDTO.schliessungsuhrzeit };
  }

  function toUrnenwahlSchliessungsuhrzeitDTO(
    schliessungsuhrzeit: Date
  ): UrnenwahlSchliessungsUhrzeitWriteDTO {
    const mappedUhrzeit = applyLocalTimezoneOffset(schliessungsuhrzeit);
    return {
      schliessungsuhrzeit: mappedUhrzeit.toISOString(),
    };
  }

  return {
    toEroeffnungsuhrzeitWriteDTO,
    toUrnenwahlSchliessungsuhrzeitModel,
    toUrnenwahlSchliessungsuhrzeitDTO,
  };
}
