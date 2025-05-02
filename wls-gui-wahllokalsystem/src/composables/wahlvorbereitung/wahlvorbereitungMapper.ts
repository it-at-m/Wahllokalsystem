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
    schliessungsuhrzeitModel: UrnenwahlSchliessungsuhrzeit
  ): UrnenwahlSchliessungsUhrzeitWriteDTO {
    const mappedUhrzeit = applyLocalTimezoneOffset(
      schliessungsuhrzeitModel.schliessungsuhrzeit
    );
    return {
      schliessungsuhrzeit: mappedUhrzeit.toISOString(),
    };
  }

  return { toModel, toDTO };
}
