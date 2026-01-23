import type {
  BezirkUndWahlID as BezirkUndWahlIdDTO,
  MeldungDTO,
  StatusDTO,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { BezirkUndWahlID } from "@/types/ergebnismeldung/common/BezirkUndWahlID.ts";
import type { Meldung } from "@/types/ergebnismeldung/common/Meldung.ts";
import type { Status } from "@/types/ergebnismeldung/common/Status.ts";

import { MeldungDTOValidierungsstatusEnum } from "@/api/wls-clients/generated-ergebnismeldung-api";
import { MeldungValidierungsstatusEnum } from "@/types/ergebnismeldung/common/MeldungValidierungsstatusEnum.ts";

export function useStatusMapper() {
  function toModel(dto: StatusDTO): Status {
    return {
      bezirkUndWahlID: _toBezirkUndWahlIDModel(dto.bezirkUndWahlID),
      schnellmeldung: _toMeldungModel(dto.schnellmeldung),
      niederschrift: _toMeldungModel(dto.niederschrift),
      stepsDone: {},
    };
  }

  function toDto(model: Status): StatusDTO {
    return {
      bezirkUndWahlID: _toBezirkUndWahlIDDto(model.bezirkUndWahlID),
      schnellmeldung: _toMeldungDto(model.schnellmeldung),
      niederschrift: _toMeldungDto(model.niederschrift),
    };
  }

  function _toMeldungModel(dto: MeldungDTO): Meldung {
    return {
      validierungsstatus:
        _dtoValidierungsstatusEnumToModelMapping[dto.validierungsstatus],
      gedruckt: dto.gedruckt,
      uebermittelt: dto.uebermittelt,
      sendeuhrzeit: dto.sendeuhrzeit,
    };
  }

  function _toMeldungDto(model: Meldung): MeldungDTO {
    return {
      validierungsstatus:
        _modelValidierungsstatusEnumToDtoMapping[model.validierungsstatus],
      gedruckt: model.gedruckt,
      uebermittelt: model.uebermittelt,
      sendeuhrzeit: model.sendeuhrzeit,
    };
  }

  function _toBezirkUndWahlIDModel(dto: BezirkUndWahlIdDTO): BezirkUndWahlID {
    return {
      wahlID: dto.wahlID,
      wahlbezirkID: dto.wahlbezirkID,
    };
  }

  function _toBezirkUndWahlIDDto(model: BezirkUndWahlID): BezirkUndWahlIdDTO {
    return {
      wahlID: model.wahlID,
      wahlbezirkID: model.wahlbezirkID,
    };
  }

  const _dtoValidierungsstatusEnumToModelMapping: Record<
    MeldungDTOValidierungsstatusEnum,
    MeldungValidierungsstatusEnum
  > = {
    [MeldungDTOValidierungsstatusEnum.Invalide]:
      MeldungValidierungsstatusEnum.Invalide,
    [MeldungDTOValidierungsstatusEnum.NichtGesendet]:
      MeldungValidierungsstatusEnum.NichtGesendet,
    [MeldungDTOValidierungsstatusEnum.NichtValidiert]:
      MeldungValidierungsstatusEnum.NichtValidiert,
    [MeldungDTOValidierungsstatusEnum.Valide]:
      MeldungValidierungsstatusEnum.Valide,
  };

  const _modelValidierungsstatusEnumToDtoMapping: Record<
    MeldungValidierungsstatusEnum,
    MeldungDTOValidierungsstatusEnum
  > = {
    [MeldungValidierungsstatusEnum.Invalide]:
      MeldungDTOValidierungsstatusEnum.Invalide,
    [MeldungValidierungsstatusEnum.NichtGesendet]:
      MeldungDTOValidierungsstatusEnum.NichtGesendet,
    [MeldungValidierungsstatusEnum.NichtValidiert]:
      MeldungDTOValidierungsstatusEnum.NichtValidiert,
    [MeldungValidierungsstatusEnum.Valide]:
      MeldungDTOValidierungsstatusEnum.Valide,
  };

  return {
    toModel,
    toDto,
  };
}
