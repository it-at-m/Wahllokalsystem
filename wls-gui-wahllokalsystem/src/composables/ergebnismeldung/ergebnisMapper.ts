import type {
  BezirkUndWahlIDStapelartDTO,
  ErgebnisDTO,
  ErgebnisseDTO,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { BezirkUndWahlIDStapelart } from "@/types/ergebnismeldung/BezirkUndWahlIDStapelart.ts";
import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";
import type { Ergebnisse } from "@/types/ergebnismeldung/Ergebnisse.ts";

import { BezirkUndWahlIDStapelartDTOStapelartEnum } from "@/api/wls-clients/generated-ergebnismeldung-api";
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

  function _dtoBezirkUndWahlIDStapelartToModel(
    dto: BezirkUndWahlIDStapelartDTO
  ): BezirkUndWahlIDStapelart {
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
      case BezirkUndWahlIDStapelartDTOStapelartEnum.SrwBawD:
        return StapelArtEnum.SrwBawD;
      case BezirkUndWahlIDStapelartDTOStapelartEnum.SrwBawDUngueltig:
        return StapelArtEnum.SrwBawDUngueltig;
      case BezirkUndWahlIDStapelartDTOStapelartEnum.SrwBawBC:
        return StapelArtEnum.SrwBawBC;
      default:
        throw new Error("Stapelart nicht gefunden");
    }
  }

  function _dtoErgebnisseToModel(dtos: ErgebnisDTO[]): Ergebnis[] {
    const models: Ergebnis[] = [];

    dtos.forEach((dto) => {
      models.push({
        wahlvorschlagID: dto.wahlvorschlagID ?? null,
        kandidatID: dto.kandidatID ?? null,
        wahlvorschlagsOrdnungszahl: dto.wahlvorschlagsordnungszahl ?? null,
        ergebnis: dto.ergebnis,
        numIndex: dto.numIndex ?? null,
      });
    });
    return models;
  }
  return { toModel };
}
