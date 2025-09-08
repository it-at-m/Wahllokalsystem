import type { Ergebnisse } from "@/types/ergebnismeldung/Ergebnisse.ts";
import type { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

import {
  Configuration,
  ErgebnisseControllerApi,
} from "@/api/wls-clients/generated-ergebnismeldung-api";
import { useCommonApiUtils } from "@/composables/common/commonApiUtils.ts";
import { useErgebnisMapper } from "@/composables/ergebnismeldung/ergebnisMapper.ts";
import { ERGEBNISMELDUNG_SERVICE_API_URL } from "@/constants.ts";

const {
  toModel,
  toDto,
  toGetErgebnisseStapelartEnum,
  toPostErgebnisseStapelartEnum,
} = useErgebnisMapper();
const { getNullOn204OrElseResponseData } = useCommonApiUtils();

export function useErgebnisService() {
  const ergebnisseControllerAPI = new ErgebnisseControllerApi(
    new Configuration({ basePath: ERGEBNISMELDUNG_SERVICE_API_URL })
  );

  async function getErgebnisse(
    wahlbezirkID: string,
    wahlID: string,
    stapelArt: StapelArtEnum
  ) {
    try {
      const response = await ergebnisseControllerAPI.getErgebnisse(
        wahlbezirkID,
        wahlID,
        toGetErgebnisseStapelartEnum(stapelArt)
      );

      const responseData = getNullOn204OrElseResponseData(response);
      return responseData ? toModel(responseData) : null;
    } catch {
      throw new Error(`Get Ergebnisse for Stapelart ${stapelArt} failed.`);
    }
  }

  async function postErgebnisse(
    wahlbezirkID: string,
    wahlID: string,
    stapelArt: StapelArtEnum,
    ergebnisse: Ergebnisse
  ) {
    try {
      await ergebnisseControllerAPI.postErgebnisse(
        wahlbezirkID,
        wahlID,
        toPostErgebnisseStapelartEnum(stapelArt),
        toDto(ergebnisse)
      );
    } catch {
      throw new Error(`Post Ergebnisse for Stapelart ${stapelArt} failed.`);
    }
  }

  return { getErgebnisse, postErgebnisse };
}
