import type {
  UrnenwahlSchliessungsUhrzeitDTO,
  UrnenwahlSchliessungsUhrzeitWriteDTO,
} from "@/api/wls-clients/generated-wahlvorbereitung-api";
import type { UrnenwahlSchliessungsuhrzeit } from "@/types/wahlvorbereitung/UrnenwahlSchliessungsuhrzeit.ts";

export function useWahlvorbereitungMapper() {
  function toModel(
    schliessungsuhrzeitDTO: UrnenwahlSchliessungsUhrzeitDTO
  ): UrnenwahlSchliessungsuhrzeit {
    return { schliessungsuhrzeit: schliessungsuhrzeitDTO.schliessungsuhrzeit };
  }

  function toDTO(
    schliessungsuhrzeitModel: UrnenwahlSchliessungsuhrzeit
  ): UrnenwahlSchliessungsUhrzeitWriteDTO {
    return {
      schliessungsuhrzeit: schliessungsuhrzeitModel.schliessungsuhrzeit,
    };
  }

  return { toModel, toDTO };
}
