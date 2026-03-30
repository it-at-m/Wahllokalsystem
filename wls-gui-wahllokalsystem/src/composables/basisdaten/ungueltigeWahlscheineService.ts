import type { UngueltigerWahlschein } from "@/types/wahlbezirk/UngueltigerWahlschein.ts";
import type { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

import {
  Configuration,
  UngueltigeWahlscheineControllerApi,
} from "@/api/wls-clients/generated-basisdaten-api";
import { useCommonApiUtils } from "@/composables/api/commonApiUtils.ts";
import { useUngueltigeWahlscheineMapper } from "@/composables/basisdaten/ungueltigeWahlscheineMapper.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { BASISDATEN_SERVICE_API_URL } from "@/constants.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";

export function useUngueltigeWahlscheineService() {
  const ungueltigeWahlscheineControllerApi =
    new UngueltigeWahlscheineControllerApi(
      new Configuration({
        basePath: BASISDATEN_SERVICE_API_URL,
      })
    );

  const { addNotification } = useUserNotificationService();
  const { toModel } = useUngueltigeWahlscheineMapper();
  const { axiosConfigWrapper } = useCommonApiUtils();

  async function getUngueltigeWahlscheine(
    wahltagID: string,
    wahlbezirksArt: WahlbezirksArtEnum,
    sendNotification = true
  ): Promise<UngueltigerWahlschein[]> {
    try {
      const ungueltigeWahlscheineCSVString = (
        await ungueltigeWahlscheineControllerApi.getUngueltigeWahlscheine(
          wahltagID,
          wahlbezirksArt,
          axiosConfigWrapper().requestAsOnlineFirst()
        )
      ).data;

      if (sendNotification) {
        addNotification(
          "Liste ungültiger Wahlscheine erfolgreich geladen",
          UserNotificationCategoryEnum.SUCCESS
        );
      }

      return toModel(ungueltigeWahlscheineCSVString);
    } catch (error) {
      if (sendNotification) {
        addNotification(
          "Fehler beim Laden der ungültigen Wahlscheine",
          UserNotificationCategoryEnum.ERROR
        );
      }
      throw error;
    }
  }

  return { getUngueltigeWahlscheine };
}
