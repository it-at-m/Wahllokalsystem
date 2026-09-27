import type { AusdruckWriteDTO } from "@/api/wls-clients/generated-ergebnismeldung-api";
import type { MeldungsartEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";

import { PostAusdruckDokumentartEnum } from "@/api/wls-clients/generated-ergebnismeldung-api";
import { MeldungsArtEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";

export function useAusdruckMapper() {
  const meldungsartToDtoMappings: Record<
    MeldungsartEnum,
    PostAusdruckDokumentartEnum
  > = {
    [MeldungsArtEnum.Beschlussentscheidungen]:
      PostAusdruckDokumentartEnum.Beschlussentscheidungen,
    [MeldungsArtEnum.Schnellmeldung]: PostAusdruckDokumentartEnum.V3,
    [MeldungsArtEnum.Niederschrift]: PostAusdruckDokumentartEnum.V1,
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
