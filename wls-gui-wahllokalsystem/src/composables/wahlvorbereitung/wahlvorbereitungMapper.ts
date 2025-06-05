import type {
  EroeffnungsUhrzeitWriteDTO,
  UrnenwahlSchliessungsUhrzeitDTO,
  UrnenwahlSchliessungsUhrzeitWriteDTO,
  UrnenwahlvorbereitungDTO,
  UrnenwahlvorbereitungWriteDTO,
  WahlurneDTO,
} from "@/api/wls-clients/generated-wahlvorbereitung-api";
import type { UrnenwahlSchliessungsuhrzeit } from "@/types/wahlvorbereitung/UrnenwahlSchliessungsuhrzeit.ts";
import type { Urnenwahlvorbereitung } from "@/types/wahlvorbereitung/Urnenwahlvorbereitung.ts";
import type { Wahlurne } from "@/types/wahlvorbereitung/Wahlurne.ts";

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

  function toUrnenwahlvorbereitungModel(
    urnenwahlvorbereitungDTO: UrnenwahlvorbereitungDTO
  ): Urnenwahlvorbereitung {
    const urnenAnzahlModel =
      urnenwahlvorbereitungDTO.urnenAnzahl?.map((wahlurneDTO) =>
        toWahlurneModel(wahlurneDTO)
      ) ?? [];
    return {
      wahlbezirkID: urnenwahlvorbereitungDTO.wahlbezirkID,
      anzahlWahlkabinen: urnenwahlvorbereitungDTO.anzahlWahlkabinen,
      anzahlWahltische: urnenwahlvorbereitungDTO.anzahlWahltische,
      anzahlNebenraeume: urnenwahlvorbereitungDTO.anzahlNebenraeume,
      urnenAnzahl: urnenAnzahlModel,
    };
  }

  function toWahlurneModel(wahlurneDto: WahlurneDTO): Wahlurne {
    return {
      wahlID: wahlurneDto.wahlID,
      anzahl: wahlurneDto.anzahl,
      urneVersiegelt: wahlurneDto.urneVersiegelt,
    };
  }

  function toUrnenwahlvorbereitungWriteDto(
    urnenwahlvorbereitung: Urnenwahlvorbereitung
  ): UrnenwahlvorbereitungWriteDTO {
    const urnenAnzahlDto =
      urnenwahlvorbereitung.urnenAnzahl?.map((wahlurneDTO) =>
        toWahlurneDto(wahlurneDTO)
      ) ?? [];
    return {
      anzahlWahlkabinen: urnenwahlvorbereitung.anzahlWahlkabinen ?? 0,
      anzahlWahltische: urnenwahlvorbereitung.anzahlWahltische ?? 0,
      anzahlNebenraeume: urnenwahlvorbereitung.anzahlNebenraeume ?? 0,
      urnenAnzahl: urnenAnzahlDto,
    };
  }

  function toWahlurneDto(wahlurne: Wahlurne): WahlurneDTO {
    return {
      wahlID: wahlurne.wahlID,
      anzahl: wahlurne.anzahl ?? 0,
      urneVersiegelt: wahlurne.urneVersiegelt,
    };
  }

  return {
    toEroeffnungsuhrzeitWriteDTO,
    toUrnenwahlSchliessungsuhrzeitModel,
    toUrnenwahlSchliessungsuhrzeitDTO,
    toUrnenwahlvorbereitungModel,
    toUrnenwahlvorbereitungWriteDto,
  };
}
