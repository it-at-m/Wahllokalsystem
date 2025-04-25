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
    const mappedUhrzeit = new Date(
      schliessungsuhrzeitModel.schliessungsuhrzeit
    );
    // TODO: auslagern
    mappedUhrzeit.setHours(
      mappedUhrzeit.getHours() -
        Math.trunc(mappedUhrzeit.getTimezoneOffset() / 60)
    );
    return {
      schliessungsuhrzeit: mappedUhrzeit.toISOString(),
    };
  }

  return { toModel, toDTO };
}
