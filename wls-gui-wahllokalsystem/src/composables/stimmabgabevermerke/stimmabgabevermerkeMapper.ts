import type {
  EingenommenerWahlscheinDTO,
  StimmabgabevermerkeDTO,
  StimmzettelDTO,
  VermerkDTO,
  WahldatenDTO,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { Stimmabgabevermerke } from "@/types/stimmabgabevermerke/Stimmabgabevermerke.ts";
import type { Stimmzettel } from "@/types/stimmabgabevermerke/Stimmzettel.ts";
import type { Vermerke } from "@/types/stimmabgabevermerke/Vermerke.ts";
import type { Wahldaten } from "@/types/stimmabgabevermerke/Wahldaten.ts";

import {
  EingenommenerWahlscheinDTOStimmzettelartEnum,
  StimmzettelDTOStimmzettelartEnum,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { EingenommenerWahlscheinStimmzettelartEnum } from "@/types/stimmabgabevermerke/EingenommenerWahlscheinStimmzettelartEnum.ts";
import { StimmzettelStimmzettelartEnum } from "@/types/stimmabgabevermerke/StimmzettelStimmzettelartEnum.ts";

export function useStimmabgabevermerkeMapper() {
  function toModel(dto: StimmabgabevermerkeDTO): Stimmabgabevermerke {
    return {
      anzahlBlaetter: dto.anzahlBlaetter,
      waehlerverzeichnisNummer: dto.waehlerverzeichnisNummer,
      wahlbezirkID: dto.wahlbezirkID,
      wahldaten: _toWahldatenModel(dto.wahldaten),
    };
  }

  function _toWahldatenModel(dto: Set<WahldatenDTO>): Wahldaten[] {
    const arrayOfWahldaten: Wahldaten[] = [];
    dto.forEach((wahldatenDto) => {
      arrayOfWahldaten.push({
        eingenommeneWahlscheine: _toEingenommeneWahlscheineModel(
          wahldatenDto.eingenommeneWahlscheine
        ),
        vermerke: _toVermerkModel(wahldatenDto.vermerke),
        waehlerverzeichnisNummer: wahldatenDto.waehlerverzeichnisNummer,
        wahlID: wahldatenDto.wahlID,
        wahlbezirkID: wahldatenDto.wahlbezirkID,
      });
    });

    return arrayOfWahldaten;
  }

  function _toEingenommeneWahlscheineModel(
    dto: Set<EingenommenerWahlscheinDTO>
  ) {
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

  function _toVermerkModel(dto: Set<VermerkDTO>): Vermerke[] {
    const vermerke: Vermerke[] = [];
    dto.forEach((vermerkDto) => {
      vermerke.push({
        blattnummer: vermerkDto.blattnummer,
        stimmzettel: _toStimmzettelModel(vermerkDto.stimmzettel),
      });
    });
    return vermerke.sort((a, b) => a.blattnummer - b.blattnummer);
  }

  function _toStimmzettelModel(dto: Set<StimmzettelDTO>): Stimmzettel[] {
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

  return {
    toModel,
  };
}
