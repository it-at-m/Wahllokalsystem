import type {
  BezirkUndWahlID as BezirkUndWahlIdDTO,
  MeldungDTO,
  StatusDTO,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { BezirkUndWahlID } from "@/types/ergebnismeldung/BezirkUndWahlID.ts";
import type { Meldung } from "@/types/ergebnismeldung/Meldung.ts";
import type { Status } from "@/types/ergebnismeldung/Status.ts";

import { MeldungDTOValidierungsstatusEnum } from "@/api/wls-clients/generated-ergebnismeldung-api";
import { MeldungValidierungsstatusEnum } from "@/types/ergebnismeldung/MeldungValidierungsstatusEnum.ts";

export function useStatusMapper() {
  function toModel(dto: StatusDTO): Status {
    return {
      bezirkUndWahlID: _toBezirkUndWahlIDModel(dto.bezirkUndWahlID),
      schnellmeldung: _toMeldungModel(dto.schnellmeldung),
      niederschrift: _toMeldungModel(dto.niederschrift),
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

  function _toBezirkUndWahlIDModel(dto: BezirkUndWahlIdDTO): BezirkUndWahlID {
    return {
      wahlID: dto.wahlID,
      wahlbezirkID: dto.wahlbezirkID,
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

  return {
    toModel,
  };
}
