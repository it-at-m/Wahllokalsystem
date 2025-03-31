import type { WahlbezirkEreignisse } from "@/types/vorfaelleundvorkommnisse/WahlbezirkEreignisse.ts";

import {
  Configuration,
  EreignisControllerApi,
} from "@/api/wls-clients/generated-vorfaelleundvorkommnisse-api";
import { useEreignisMapper } from "@/composables/vorfaelleundvorkommnisse/ereignisMapper.ts";
import { VORFAELLEUNDVORKOMMNISSE_SERVICE_API_URL } from "@/constants";

const { toModel, toDto } = useEreignisMapper();

export function useEreignisService() {
  const ereignisControllerApi = new EreignisControllerApi(
    new Configuration({
      basePath: VORFAELLEUNDVORKOMMNISSE_SERVICE_API_URL,
    })
  );

  function getEreignisse(wahlbezirkID: string): Promise<WahlbezirkEreignisse> {
    return ereignisControllerApi
      .getEreignisse(wahlbezirkID)
      .then((response) => toModel(response.data));
  }

  async function saveEreignisse(
    wahlbezirkID: string,
    ereignisse: WahlbezirkEreignisse
  ): Promise<void> {
    const ereignisseWriteDto = toDto(ereignisse);

    await ereignisControllerApi.postEreignisse(
      wahlbezirkID,
      ereignisseWriteDto
    );
  }

  return {
    getEreignisse,
    saveEreignisse,
  };
}
