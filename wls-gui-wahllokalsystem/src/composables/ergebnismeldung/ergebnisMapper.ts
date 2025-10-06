import type {
  BegruendungDTO,
  BezirkUndWahlIDStapelartDTO,
  ErgebnisDTO,
  ErgebnisseDTO,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { Begruendung } from "@/types/ergebnisermittlung/Begruendung.ts";
import type { BezirkUndWahlIDStapelArt } from "@/types/ergebnismeldung/BezirkUndWahlIDStapelArt.ts";
import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";
import type { Ergebnisse } from "@/types/ergebnismeldung/Ergebnisse.ts";

import {
  BezirkUndWahlIDStapelartDTOStapelartEnum,
  GetErgebnisseStapelartEnum,
  PostErgebnisseStapelartEnum,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

export function useErgebnisMapper() {
  function toModel(dto: ErgebnisseDTO): Ergebnisse {
    return {
      bezirkUndWahlIDStapelart: _dtoBezirkUndWahlIDStapelartToModel(
        dto.bezirkUndWahlIDStapelart
      ),
      ergebnisse: _dtoErgebnisseToModel(dto.ergebnisse),
    };
  }

  function toDto(model: Ergebnisse): ErgebnisseDTO {
    return {
      bezirkUndWahlIDStapelart: _modelBezirkUndWahlIDStapelartToDto(
        model.bezirkUndWahlIDStapelart
      ),
      ergebnisse: _modelErgebnisseToDto(model.ergebnisse),
    };
  }

  function toGetErgebnisseStapelartEnum(
    model: StapelArtEnum
  ): GetErgebnisseStapelartEnum {
    switch (model) {
      case StapelArtEnum.ObwA:
        return GetErgebnisseStapelartEnum.ObwA;
      case StapelArtEnum.ObwBLeer:
        return GetErgebnisseStapelartEnum.ObwBLeer;
      case StapelArtEnum.ObwBUngekennzeichnet:
        return GetErgebnisseStapelartEnum.ObwBUngekennzeichnet;
      case StapelArtEnum.ObwCGueltig:
        return GetErgebnisseStapelartEnum.ObwCGueltig;
      case StapelArtEnum.ObwCUngueltig:
        return GetErgebnisseStapelartEnum.ObwCUngueltig;
      case StapelArtEnum.SrwBawA:
        return GetErgebnisseStapelartEnum.SrwBawA;
      case StapelArtEnum.SrwBawB:
        return GetErgebnisseStapelartEnum.SrwBawB;
      case StapelArtEnum.SrwBawAB:
        return GetErgebnisseStapelartEnum.SrwBawAB;
      case StapelArtEnum.SrwBawDUngueltig:
        return GetErgebnisseStapelartEnum.SrwBawDUngueltig;
      case StapelArtEnum.SrwBawBC:
        return GetErgebnisseStapelartEnum.SrwBawBC;
      case StapelArtEnum.MbwA:
        return GetErgebnisseStapelartEnum.MbwA;
      case StapelArtEnum.MbwB:
        return GetErgebnisseStapelartEnum.MbwB;
      case StapelArtEnum.MbwD:
        return GetErgebnisseStapelartEnum.MbwD;
      case StapelArtEnum.MbwAB:
        return GetErgebnisseStapelartEnum.MbwAB;
      case StapelArtEnum.MbwBC:
        return GetErgebnisseStapelartEnum.MbwBC;
      default:
        throw new Error("Stapelart nicht gefunden");
    }
  }

  function toPostErgebnisseStapelartEnum(
    model: StapelArtEnum
  ): PostErgebnisseStapelartEnum {
    switch (model) {
      case StapelArtEnum.ObwA:
        return PostErgebnisseStapelartEnum.ObwA;
      case StapelArtEnum.ObwBLeer:
        return PostErgebnisseStapelartEnum.ObwBLeer;
      case StapelArtEnum.ObwBUngekennzeichnet:
        return PostErgebnisseStapelartEnum.ObwBUngekennzeichnet;
      case StapelArtEnum.ObwCGueltig:
        return PostErgebnisseStapelartEnum.ObwCGueltig;
      case StapelArtEnum.ObwCUngueltig:
        return PostErgebnisseStapelartEnum.ObwCUngueltig;
      case StapelArtEnum.SrwBawA:
        return PostErgebnisseStapelartEnum.SrwBawA;
      case StapelArtEnum.SrwBawB:
        return PostErgebnisseStapelartEnum.SrwBawB;
      case StapelArtEnum.SrwBawAB:
        return PostErgebnisseStapelartEnum.SrwBawAB;
      case StapelArtEnum.SrwBawDUngueltig:
        return PostErgebnisseStapelartEnum.SrwBawDUngueltig;
      case StapelArtEnum.SrwBawBC:
        return PostErgebnisseStapelartEnum.SrwBawBC;
      case StapelArtEnum.MbwA:
        return PostErgebnisseStapelartEnum.MbwA;
      case StapelArtEnum.MbwB:
        return PostErgebnisseStapelartEnum.MbwB;
      case StapelArtEnum.MbwD:
        return PostErgebnisseStapelartEnum.MbwD;
      case StapelArtEnum.MbwAB:
        return PostErgebnisseStapelartEnum.MbwAB;
      case StapelArtEnum.MbwBC:
        return PostErgebnisseStapelartEnum.MbwBC;
      default:
        throw new Error("Stapelart nicht gefunden");
    }
  }

  function toBegruendungModel(dto: BegruendungDTO): Begruendung {
    const bezirkUndWahlIdStapelart = _dtoBezirkUndWahlIDStapelartToModel(
      dto.bezirkUndWahlIDStapelart
    );
    return {
      wahlID: bezirkUndWahlIdStapelart.wahlID,
      stapelart: bezirkUndWahlIdStapelart.stapelArt,
      grund: dto.grund,
      nachzaehlung: dto.nachzaehlung,
      unstimmigkeiten: dto.unstimmigkeiten,
    };
  }

  function _dtoBezirkUndWahlIDStapelartToModel(
    dto: BezirkUndWahlIDStapelartDTO
  ): BezirkUndWahlIDStapelArt {
    return {
      wahlID: dto.wahlID,
      wahlbezirkID: dto.wahlbezirkID,
      stapelArt: _dtoStapelArtEnumToModel(dto.stapelart),
    };
  }

  function _dtoStapelArtEnumToModel(
    dto: BezirkUndWahlIDStapelartDTOStapelartEnum
  ): StapelArtEnum {
    switch (dto) {
      case BezirkUndWahlIDStapelartDTOStapelartEnum.ObwA:
        return StapelArtEnum.ObwA;
      case BezirkUndWahlIDStapelartDTOStapelartEnum.ObwBLeer:
        return StapelArtEnum.ObwBLeer;
      case BezirkUndWahlIDStapelartDTOStapelartEnum.ObwBUngekennzeichnet:
        return StapelArtEnum.ObwBUngekennzeichnet;
      case BezirkUndWahlIDStapelartDTOStapelartEnum.ObwCGueltig:
        return StapelArtEnum.ObwCGueltig;
      case BezirkUndWahlIDStapelartDTOStapelartEnum.ObwCUngueltig:
        return StapelArtEnum.ObwCUngueltig;
      case BezirkUndWahlIDStapelartDTOStapelartEnum.SrwBawA:
        return StapelArtEnum.SrwBawA;
      case BezirkUndWahlIDStapelartDTOStapelartEnum.SrwBawB:
        return StapelArtEnum.SrwBawB;
      case BezirkUndWahlIDStapelartDTOStapelartEnum.SrwBawAB:
        return StapelArtEnum.SrwBawAB;
      case BezirkUndWahlIDStapelartDTOStapelartEnum.SrwBawDUngueltig:
        return StapelArtEnum.SrwBawDUngueltig;
      case BezirkUndWahlIDStapelartDTOStapelartEnum.SrwBawBC:
        return StapelArtEnum.SrwBawBC;
      case BezirkUndWahlIDStapelartDTOStapelartEnum.MbwA:
        return StapelArtEnum.MbwA;
      case BezirkUndWahlIDStapelartDTOStapelartEnum.MbwAB:
        return StapelArtEnum.MbwAB;
      case BezirkUndWahlIDStapelartDTOStapelartEnum.MbwB:
        return StapelArtEnum.MbwB;
      case BezirkUndWahlIDStapelartDTOStapelartEnum.MbwBC:
        return StapelArtEnum.MbwBC;
      case BezirkUndWahlIDStapelartDTOStapelartEnum.MbwD:
        return StapelArtEnum.MbwD;
      case BezirkUndWahlIDStapelartDTOStapelartEnum.StimmzettelUmschlaege:
        return StapelArtEnum.StimmzettelUmschlaege;
      default:
        throw new Error("Stapelart nicht gefunden");
    }
  }

  function _dtoErgebnisseToModel(dtos: ErgebnisDTO[]): Ergebnis[] {
    return dtos.map((dto) => ({
      wahlvorschlagID: dto.wahlvorschlagID ?? null,
      kandidatID: dto.kandidatID ?? null,
      wahlvorschlagsOrdnungszahl: dto.wahlvorschlagsordnungszahl ?? null,
      ergebnis: dto.ergebnis,
      numIndex: dto.numIndex ?? null,
    }));
  }

  function _modelBezirkUndWahlIDStapelartToDto(
    model: BezirkUndWahlIDStapelArt
  ): BezirkUndWahlIDStapelartDTO {
    return {
      wahlID: model.wahlID,
      wahlbezirkID: model.wahlbezirkID,
      stapelart: _modelStapelArtEnumToDto(model.stapelArt),
    };
  }

  function _modelStapelArtEnumToDto(
    model: StapelArtEnum
  ): BezirkUndWahlIDStapelartDTOStapelartEnum {
    switch (model) {
      case StapelArtEnum.ObwA:
        return BezirkUndWahlIDStapelartDTOStapelartEnum.ObwA;
      case StapelArtEnum.ObwBLeer:
        return BezirkUndWahlIDStapelartDTOStapelartEnum.ObwBLeer;
      case StapelArtEnum.ObwBUngekennzeichnet:
        return BezirkUndWahlIDStapelartDTOStapelartEnum.ObwBUngekennzeichnet;
      case StapelArtEnum.ObwCGueltig:
        return BezirkUndWahlIDStapelartDTOStapelartEnum.ObwCGueltig;
      case StapelArtEnum.ObwCUngueltig:
        return BezirkUndWahlIDStapelartDTOStapelartEnum.ObwCUngueltig;
      case StapelArtEnum.SrwBawA:
        return BezirkUndWahlIDStapelartDTOStapelartEnum.SrwBawA;
      case StapelArtEnum.SrwBawB:
        return BezirkUndWahlIDStapelartDTOStapelartEnum.SrwBawB;
      case StapelArtEnum.SrwBawAB:
        return BezirkUndWahlIDStapelartDTOStapelartEnum.SrwBawAB;
      case StapelArtEnum.SrwBawDUngueltig:
        return BezirkUndWahlIDStapelartDTOStapelartEnum.SrwBawDUngueltig;
      case StapelArtEnum.SrwBawBC:
        return BezirkUndWahlIDStapelartDTOStapelartEnum.SrwBawBC;
      case StapelArtEnum.StimmzettelUmschlaege:
        return BezirkUndWahlIDStapelartDTOStapelartEnum.StimmzettelUmschlaege;
      default:
        throw new Error("Stapelart nicht gefunden");
    }
  }

  function _modelErgebnisseToDto(models: Ergebnis[]): ErgebnisDTO[] {
    return models.map((model) => ({
      wahlvorschlagID: model.wahlvorschlagID ?? undefined,
      kandidatID: model.kandidatID ?? undefined,
      wahlvorschlagsordnungszahl: model.wahlvorschlagsOrdnungszahl ?? undefined,
      ergebnis: model.ergebnis ?? 0,
      numIndex: model.numIndex ?? undefined,
    }));
  }

  return {
    toModel,
    toDto,
    toGetErgebnisseStapelartEnum,
    toPostErgebnisseStapelartEnum,
    toBegruendungModel,
  };
}
