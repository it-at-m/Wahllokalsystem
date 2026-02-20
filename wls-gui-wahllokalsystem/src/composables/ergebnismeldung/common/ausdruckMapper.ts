import type { AusdruckWriteDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { MeldungsartEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";

import { PostAusdruckMeldungsartEnum } from "@/api/wls-clients/generated-ergebnismeldung-api";
import { MeldungsArtEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";

export function useAusdruckMapper() {
  const meldungsartToDtoMappings: Record<
    MeldungsartEnum,
    PostAusdruckMeldungsartEnum
  > = {
    [MeldungsArtEnum.Schnellmeldung]: PostAusdruckMeldungsartEnum.V3,
    [MeldungsArtEnum.Niederschrift]: PostAusdruckMeldungsartEnum.V1,
  };

  function meldungsartEnumToDto(meldungsart: MeldungsartEnum) {
    return meldungsartToDtoMappings[meldungsart];
  }

  function toAusdruckWriteDTO(content: string): AusdruckWriteDTO {
    return {
      content: content,
    };
  }

  return {
    meldungsartEnumToDto,
    toAusdruckWriteDTO,
  };
}
