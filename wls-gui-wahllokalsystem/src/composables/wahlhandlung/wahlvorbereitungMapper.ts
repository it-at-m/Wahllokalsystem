import type {
  BriefwahlvorbereitungDTO,
  BriefwahlvorbereitungWriteDTO,
  EroeffnungsUhrzeitWriteDTO,
  UrnenwahlSchliessungsUhrzeitDTO,
  UrnenwahlSchliessungsUhrzeitWriteDTO,
  UrnenwahlvorbereitungDTO,
  UrnenwahlvorbereitungWriteDTO,
  WahlurneDTO,
} from "@/api/wls-clients/generated-wahlvorbereitung-api";
import type { UrnenwahlSchliessungsuhrzeit } from "@/types/wahlhandlung/UrnenwahlSchliessungsuhrzeit.ts";
import type { Urnenwahlvorbereitung } from "@/types/wahlhandlung/Urnenwahlvorbereitung.ts";
import type { Wahlurne } from "@/types/wahlhandlung/Wahlurne.ts";
import type { Wahlvorbereitung } from "@/types/wahlhandlung/Wahlvorbereitung.ts";

import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";

const { toYyyyMmDdWithTimeWithoutTimezoneOffset } = useDateTimeFormatter();

export function useWahlvorbereitungMapper() {
  function toEroeffnungsuhrzeitWriteDTO(
    eroeffnungsuhrzeit: Date
  ): EroeffnungsUhrzeitWriteDTO {
    return {
      eroeffnungsuhrzeit:
        toYyyyMmDdWithTimeWithoutTimezoneOffset(eroeffnungsuhrzeit),
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
    console.debug(`schliessungsuhrzeit > ${schliessungsuhrzeit}`);
    const mappedUhrzeit =
      toYyyyMmDdWithTimeWithoutTimezoneOffset(schliessungsuhrzeit);
    return {
      schliessungsuhrzeit: mappedUhrzeit,
    };
  }

  function toUrnenwahlvorbereitungModel(
    urnenwahlvorbereitungDTO: UrnenwahlvorbereitungDTO
  ): Urnenwahlvorbereitung {
    const urnenAnzahlModel =
      urnenwahlvorbereitungDTO.urnenAnzahl?.map((wahlurneDTO) =>
        _toWahlurneModel(wahlurneDTO)
      ) ?? [];
    return {
      wahlbezirkID: urnenwahlvorbereitungDTO.wahlbezirkID,
      anzahlWahlkabinen: urnenwahlvorbereitungDTO.anzahlWahlkabinen,
      anzahlWahltische: urnenwahlvorbereitungDTO.anzahlWahltische,
      anzahlNebenraeume: urnenwahlvorbereitungDTO.anzahlNebenraeume,
      urneVersiegelt: _areAllUrnenVersiegelt(
        urnenwahlvorbereitungDTO.urnenAnzahl
      ),
      urnenAnzahl: urnenAnzahlModel,
    };
  }

  function toUrnenwahlvorbereitungWriteDto(
    urnenwahlvorbereitung: Urnenwahlvorbereitung
  ): UrnenwahlvorbereitungWriteDTO {
    const urnenAnzahlDto =
      urnenwahlvorbereitung.urnenAnzahl?.map((wahlurneDTO) =>
        _toWahlurneDto(wahlurneDTO, urnenwahlvorbereitung.urneVersiegelt)
      ) ?? [];
    return {
      anzahlWahlkabinen: urnenwahlvorbereitung.anzahlWahlkabinen ?? 0,
      anzahlWahltische: urnenwahlvorbereitung.anzahlWahltische ?? 0,
      anzahlNebenraeume: urnenwahlvorbereitung.anzahlNebenraeume ?? 0,
      urnenAnzahl: urnenAnzahlDto,
    };
  }

  function toBriefwahlvorbereitungModel(
    briefwahlvorbereitungDTO: BriefwahlvorbereitungDTO
  ): Wahlvorbereitung {
    const urnenAnzahlModel =
      briefwahlvorbereitungDTO.urnenAnzahl?.map((wahlurneDTO) =>
        _toWahlurneModel(wahlurneDTO)
      ) ?? [];
    return {
      wahlbezirkID: briefwahlvorbereitungDTO.wahlbezirkID,
      urneVersiegelt: _areAllUrnenVersiegelt(
        briefwahlvorbereitungDTO.urnenAnzahl
      ),
      urnenAnzahl: urnenAnzahlModel,
    };
  }

  function toBriefwahlvorbereitungWriteDto(
    briefwahlvorbereitung: Wahlvorbereitung
  ): BriefwahlvorbereitungWriteDTO {
    const urnenAnzahlDto = briefwahlvorbereitung.urnenAnzahl.map(
      (wahlurneDTO) =>
        _toWahlurneDto(wahlurneDTO, briefwahlvorbereitung.urneVersiegelt)
    );
    return {
      urnenAnzahl: urnenAnzahlDto,
    };
  }

  function _toWahlurneModel(wahlurneDto: WahlurneDTO): Wahlurne {
    return {
      wahlID: wahlurneDto.wahlID,
      anzahl: wahlurneDto.anzahl,
    };
  }

  function _toWahlurneDto(
    wahlurne: Wahlurne,
    urneVersiegelt: boolean
  ): WahlurneDTO {
    return {
      wahlID: wahlurne.wahlID,
      anzahl: wahlurne.anzahl ?? 0,
      urneVersiegelt: urneVersiegelt,
    };
  }

  function _areAllUrnenVersiegelt(wahlurneDTO: WahlurneDTO[]) {
    if (!Array.isArray(wahlurneDTO) || wahlurneDTO.length === 0) {
      return false;
    }
    return wahlurneDTO.every((urne) => urne.urneVersiegelt === true);
  }

  return {
    toEroeffnungsuhrzeitWriteDTO,
    toUrnenwahlSchliessungsuhrzeitModel,
    toUrnenwahlSchliessungsuhrzeitDTO,
    toUrnenwahlvorbereitungModel,
    toUrnenwahlvorbereitungWriteDto,
    toBriefwahlvorbereitungModel,
    toBriefwahlvorbereitungWriteDto,
  };
}
