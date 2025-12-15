import type {
  BegruendungDTO,
  BezirkUndWahlID,
  BezirkUndWahlIDStapelartDTO,
  ErgebnisDTO,
  ErgebnisseDTO,
  StimmzettelumschlaegeDTO,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { Begruendung } from "@/types/ergebnismeldung/common/Begruendung.ts";
import type { BezirkUndWahlIDStapelArt } from "@/types/ergebnismeldung/common/BezirkUndWahlIDStapelArt.ts";
import type { Ergebnis } from "@/types/ergebnismeldung/common/Ergebnis.ts";
import type { Ergebnisse } from "@/types/ergebnismeldung/common/Ergebnisse.ts";
import type { Stimmzettelumschlaege } from "@/types/ergebnismeldung/common/Stimmzettelumschlaege.ts";

import {
  BezirkUndWahlIDStapelartDTOStapelartEnum,
  GetErgebnisseStapelartEnum,
  PostErgebnisseStapelartEnum,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useDateTimeFormatter } from "@/composables/common/dateTimeFormatter.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";

const { toYyyyMmDdWithTimeWithoutTimezoneOffset } = useDateTimeFormatter();

export function useErgebnisMapper() {
  function toErgebnisseModel(dto: ErgebnisseDTO): Ergebnisse {
    return {
      bezirkUndWahlIDStapelart: _dtoBezirkUndWahlIDStapelartToModel(
        dto.bezirkUndWahlIDStapelart
      ),
      ergebnisse: _dtoErgebnisseToModel(dto.ergebnisse),
    };
  }

  function toErgebnisseDto(model: Ergebnisse): ErgebnisseDTO {
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
      case StapelArtEnum.MbwDUngueltig:
        return GetErgebnisseStapelartEnum.MbwDUngueltig;
      case StapelArtEnum.MbwAB:
        return GetErgebnisseStapelartEnum.MbwAB;
      case StapelArtEnum.MbwBC:
        return GetErgebnisseStapelartEnum.MbwBC;
      case StapelArtEnum.StimmzettelUmschlaege:
        return GetErgebnisseStapelartEnum.StimmzettelUmschlaege;
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
      case StapelArtEnum.MbwDUngueltig:
        return PostErgebnisseStapelartEnum.MbwDUngueltig;
      case StapelArtEnum.MbwAB:
        return PostErgebnisseStapelartEnum.MbwAB;
      case StapelArtEnum.MbwBC:
        return PostErgebnisseStapelartEnum.MbwBC;
      case StapelArtEnum.StimmzettelUmschlaege:
        return PostErgebnisseStapelartEnum.StimmzettelUmschlaege;
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

  function toBegruendungDto(
    model: Begruendung,
    wahlbezirkID: string
  ): BegruendungDTO {
    return {
      bezirkUndWahlIDStapelart: {
        wahlID: model.wahlID,
        wahlbezirkID: wahlbezirkID,
        stapelart: _modelStapelArtEnumToDto(model.stapelart),
      },
      grund: model.grund,
      nachzaehlung: model.nachzaehlung,
      unstimmigkeiten: model.unstimmigkeiten,
    };
  }

  function toStimmzettelumschlaegeDto(
    model: Stimmzettelumschlaege,
    wahlID: string,
    wahlbezirkID: string
  ): StimmzettelumschlaegeDTO {
    const dto: StimmzettelumschlaegeDTO = {
      bezirkUndWahlID: _wahlIDAndWahlbezirkIDToBezirkUndWahlID(
        wahlID,
        wahlbezirkID
      ),
      anzahlWaehler: model.anzahlWaehler != null ? model.anzahlWaehler : 0,
    };

    if (model.urneneroeffnungsUhrzeit) {
      dto.urneneroeffnungsUhrzeit = toYyyyMmDdWithTimeWithoutTimezoneOffset(
        model.urneneroeffnungsUhrzeit
      );
    }

    return dto;
  }

  function toStimmzettelumschlaegeModel(
    dto: StimmzettelumschlaegeDTO
  ): Stimmzettelumschlaege {
    const model: Stimmzettelumschlaege = {
      anzahlWaehler: dto.anzahlWaehler != null ? dto.anzahlWaehler : 0,
    };

    if (dto.urneneroeffnungsUhrzeit) {
      model.urneneroeffnungsUhrzeit = new Date(dto.urneneroeffnungsUhrzeit);
    }

    return model;
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
      case BezirkUndWahlIDStapelartDTOStapelartEnum.MbwDUngueltig:
        return StapelArtEnum.MbwDUngueltig;
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
      case StapelArtEnum.MbwA:
        return BezirkUndWahlIDStapelartDTOStapelartEnum.MbwA;
      case StapelArtEnum.MbwAB:
        return BezirkUndWahlIDStapelartDTOStapelartEnum.MbwAB;
      case StapelArtEnum.MbwB:
        return BezirkUndWahlIDStapelartDTOStapelartEnum.MbwB;
      case StapelArtEnum.MbwBC:
        return BezirkUndWahlIDStapelartDTOStapelartEnum.MbwBC;
      case StapelArtEnum.MbwDUngueltig:
        return BezirkUndWahlIDStapelartDTOStapelartEnum.MbwDUngueltig;
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

  function _wahlIDAndWahlbezirkIDToBezirkUndWahlID(
    wahlID: string,
    wahlbezirkID: string
  ): BezirkUndWahlID {
    return {
      wahlID: wahlID,
      wahlbezirkID: wahlbezirkID,
    };
  }

  return {
    toErgebnisseModel,
    toErgebnisseDto,
    toGetErgebnisseStapelartEnum,
    toPostErgebnisseStapelartEnum,
    toBegruendungModel,
    toBegruendungDto,
    toStimmzettelumschlaegeDto,
    toStimmzettelumschlaegeModel,
  };
}
