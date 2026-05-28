import type {
  EingenommenerWahlscheinDTO,
  StimmabgabevermerkeDTO,
  StimmzettelDTO,
  VermerkDTO,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { Stimmabgabevermerke } from "@/types/stimmabgabevermerke/Stimmabgabevermerke.ts";
import type { Stimmzettel } from "@/types/stimmabgabevermerke/Stimmzettel.ts";
import type { Vermerke } from "@/types/stimmabgabevermerke/Vermerke.ts";

import {
  EingenommenerWahlscheinDTOStimmzettelartEnum,
  StimmzettelDTOStimmzettelartEnum,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { EingenommenerWahlscheinStimmzettelartEnum } from "@/types/stimmabgabevermerke/EingenommenerWahlscheinStimmzettelartEnum.ts";
import { StimmzettelStimmzettelartEnum } from "@/types/stimmabgabevermerke/StimmzettelStimmzettelartEnum.ts";

export function useStimmabgabevermerkeMapper() {
  function toModel(dto: StimmabgabevermerkeDTO): Stimmabgabevermerke {
    return {
      eingenommeneWahlscheine: _toEingenommeneWahlscheineModel(
        dto.eingenommeneWahlscheine
      ),
      vermerke: _toVermerkModel(dto.vermerke),
      waehlerverzeichnisNummer: dto.waehlerverzeichnisNummer,
      wahlID: dto.wahlID,
      wahlbezirkID: dto.wahlbezirkID,
    };
  }

  function toDto(model: Stimmabgabevermerke): StimmabgabevermerkeDTO {
    return {
      wahlbezirkID: model.wahlbezirkID,
      eingenommeneWahlscheine: _toEingenommeneWahlscheinDTO(
        model.eingenommeneWahlscheine
      ),
      vermerke: _toVermerkDTO(model.vermerke),
      waehlerverzeichnisNummer: model.waehlerverzeichnisNummer,
      wahlID: model.wahlID,
    };
  }

  function _toEingenommeneWahlscheineModel(dto: EingenommenerWahlscheinDTO[]) {
    const resultMap = new Map<
      EingenommenerWahlscheinStimmzettelartEnum,
      number
    >();

    for (const entry of dto) {
      resultMap.set(
        _toEingenommenerWahlscheinStimmzettelartEnum(entry.stimmzettelart),
        entry.anzahl
      );
    }
    return resultMap;
  }

  function _toEingenommeneWahlscheinDTO(
    model: Map<EingenommenerWahlscheinStimmzettelartEnum, number>
  ): EingenommenerWahlscheinDTO[] {
    const dtoArray: EingenommenerWahlscheinDTO[] = [];

    for (const [key, value] of model.entries()) {
      dtoArray.push({
        stimmzettelart: _toEingenommenerWahlscheinDTOStimmzettelartEnum(key),
        anzahl: value,
      });
    }

    return dtoArray;
  }

  function _toVermerkModel(dto: VermerkDTO[]): Vermerke[] {
    const vermerke: Vermerke[] = [];
    dto.forEach((vermerkDto) => {
      vermerke.push({
        blattnummer: vermerkDto.blattnummer,
        stimmzettel: _toStimmzettelModel(vermerkDto.stimmzettel),
      });
    });
    return vermerke.sort((a, b) => a.blattnummer - b.blattnummer);
  }

  function _toVermerkDTO(model: Vermerke[]): VermerkDTO[] {
    const vermerkDTOArray: VermerkDTO[] = [];

    model.forEach((vermerk) => {
      vermerkDTOArray.push({
        blattnummer: vermerk.blattnummer,
        stimmzettel: _toStimmzettelDTO(vermerk.stimmzettel),
      });
    });

    return vermerkDTOArray;
  }

  function _toStimmzettelModel(dto: StimmzettelDTO[]): Stimmzettel[] {
    const stimmzettel: Stimmzettel[] = [];
    dto.forEach((stimmzettelDto) => {
      stimmzettel.push({
        anzahl: stimmzettelDto.anzahl,
        stimmzettelart: _toStimmzettelStimmzettelartEnum(
          stimmzettelDto.stimmzettelart
        ),
      });
    });
    return stimmzettel;
  }

  function _toStimmzettelDTO(model: Stimmzettel[]): StimmzettelDTO[] {
    const stimmzettelDTOArray: StimmzettelDTO[] = [];
    model.forEach((stimmzettel) => {
      stimmzettelDTOArray.push({
        anzahl: stimmzettel.anzahl ?? 0,
        stimmzettelart: _toStimmzettelStimmzettelartDTOEnum(
          stimmzettel.stimmzettelart
        ),
      });
    });

    return stimmzettelDTOArray;
  }

  function _toEingenommenerWahlscheinDTOStimmzettelartEnum(
    value: EingenommenerWahlscheinStimmzettelartEnum
  ): EingenommenerWahlscheinDTOStimmzettelartEnum {
    switch (value) {
      case EingenommenerWahlscheinStimmzettelartEnum.Gross:
        return EingenommenerWahlscheinDTOStimmzettelartEnum.Gross;
      case EingenommenerWahlscheinStimmzettelartEnum.Klein:
        return EingenommenerWahlscheinDTOStimmzettelartEnum.Klein;
      case EingenommenerWahlscheinStimmzettelartEnum.Beide:
        return EingenommenerWahlscheinDTOStimmzettelartEnum.Beide;
    }
  }

  function _toEingenommenerWahlscheinStimmzettelartEnum(
    value: EingenommenerWahlscheinDTOStimmzettelartEnum
  ): EingenommenerWahlscheinStimmzettelartEnum {
    switch (value) {
      case EingenommenerWahlscheinDTOStimmzettelartEnum.Gross:
        return EingenommenerWahlscheinStimmzettelartEnum.Gross;
      case EingenommenerWahlscheinDTOStimmzettelartEnum.Klein:
        return EingenommenerWahlscheinStimmzettelartEnum.Klein;
      case EingenommenerWahlscheinDTOStimmzettelartEnum.Beide:
        return EingenommenerWahlscheinStimmzettelartEnum.Beide;
    }
  }

  function _toStimmzettelStimmzettelartEnum(
    value: StimmzettelDTOStimmzettelartEnum
  ): StimmzettelStimmzettelartEnum {
    switch (value) {
      case StimmzettelDTOStimmzettelartEnum.Gross:
        return StimmzettelStimmzettelartEnum.Gross;
      case StimmzettelDTOStimmzettelartEnum.Klein:
        return StimmzettelStimmzettelartEnum.Klein;
      case StimmzettelDTOStimmzettelartEnum.Beide:
        return StimmzettelStimmzettelartEnum.Beide;
    }
  }

  function _toStimmzettelStimmzettelartDTOEnum(
    value: StimmzettelStimmzettelartEnum
  ): StimmzettelDTOStimmzettelartEnum {
    switch (value) {
      case StimmzettelStimmzettelartEnum.Gross:
        return StimmzettelDTOStimmzettelartEnum.Gross;
      case StimmzettelStimmzettelartEnum.Klein:
        return StimmzettelDTOStimmzettelartEnum.Klein;
      case StimmzettelStimmzettelartEnum.Beide:
        return StimmzettelDTOStimmzettelartEnum.Beide;
    }
  }

  return {
    toModel,
    toDto,
  };
}
